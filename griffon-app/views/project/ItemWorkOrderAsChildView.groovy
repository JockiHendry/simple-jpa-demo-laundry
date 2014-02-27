/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package project

import javax.swing.BorderFactory
import javax.swing.SwingConstants
import java.awt.event.KeyEvent
import java.text.NumberFormat

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*

application(title: 'Item Work Order',
        preferredSize: [520, 340],
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {

    panel(id: 'mainPanel') {
        borderLayout()

        panel(constraints: CENTER) {
            borderLayout()
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.itemWorkOrderList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Item', property: 'work')
                    glazedColumn(name: 'Qty', property: 'jumlah', columnClass: Integer, width: [80,80,200])
                    glazedColumn(name: 'Harga', property: 'harga', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Keterangan', property: 'keterangan')
                    glazedColumn(name: 'Diskon', property: 'diskon', columnClass: Integer)
                    glazedColumn(name: 'Total Diskon', expression: {it.diskon?.jumlahDiskon(it.harga)?.multiply(it.jumlah)}, columnClass: Integer) {
                        templateRenderer("\${it==null?'-':currencyFormat(it)}", horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Total', expression: {it.total()}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                }
            }
            label(constraints: PAGE_END, horizontalAlignment: SwingConstants.RIGHT,
                border: BorderFactory.createRaisedSoftBevelBorder(), text: bind{model.informasi})
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), visible: bind { model.editable }, constraints: PAGE_END) {
            label('Item Pekerjaan:')
            panel {
                label(text: bind {model.selectedWork?: '- kosong -'})
                button('Cari Di Daftar Pekerjaan...', id: 'cariPekerjaan', errorPath: 'work', mnemonic: KeyEvent.VK_P,
                    enabled: bind { model.parentPelanggan != null}, actionPerformed: controller.showDaftarPekerjaan)
                label('Anda harus memilih pelanggan terlebih dahulu!', visible: bind { model.parentPelanggan == null})
            }
            errorLabel(path: 'work', constraints: 'wrap')
            label('Jumlah:')
            numberTextField(id: 'jumlah', columns: 10, bindTo: 'jumlah', errorPath: 'jumlah')
            errorLabel(path: 'jumlah', constraints: 'wrap')
            label('Harga:')
            label(text: bind('harga', source: model, converter: {it? NumberFormat.getCurrencyInstance().format(it): ''}), errorPath: 'harga')
            errorLabel(path: 'harga', constraints: 'wrap')
            label('Diskon:')
            panel(layout: new FlowLayout(FlowLayout.LEADING, 0, 0)) {
                comboBox(id: 'pilihanPersen', model: model.pilihanPersen, errorPath: 'pilihanPersen')
                label('% dan Nominal Rp')
                numberTextField(id: 'diskonNominal', columns: 20, bindTo: 'diskonNominal', errorPath: 'diskonNominal')
            }
            errorLabel(path: 'diskon', constraints: 'wrap')
            label('Keterangan:')
            textField(id: 'keterangan', columns: 50, text: bind('keterangan', target: model, mutual: true), errorPath: 'keterangan')
            errorLabel(path: 'keterangan', constraints: 'wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Simpan', actionPerformed: {
                    if (!view.table.selectionModel.selectionEmpty) {
                        if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                                app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                            return
                        }
                    }
                    controller.save()
                    cariPekerjaan.requestFocusInWindow()
                }, mnemonic: KeyEvent.VK_S)
                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear, mnemonic: KeyEvent.VK_B)
                button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind { table.isRowSelected }, actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                            app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        controller.delete()
                    }
                }, mnemonic: KeyEvent.VK_H)
                button(app.getMessage("simplejpa.dialog.close.button"), actionPerformed: {
                    SwingUtilities.getWindowAncestor(mainPanel)?.dispose()
                }, mnemonic: KeyEvent.VK_T)
            }
        }
    }
}
