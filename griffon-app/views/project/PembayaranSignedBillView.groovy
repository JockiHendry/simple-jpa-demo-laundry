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

import javax.swing.JOptionPane
import java.text.NumberFormat

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*
import org.jdesktop.swingx.prompt.PromptSupport

application(title: 'Pembayaran',
        preferredSize: [520, 340],
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {

    panel(id: 'mainPanel') {
        borderLayout()

        panel(constraints: PAGE_START) {
            flowLayout(alignment: FlowLayout.LEADING)
            label("Nomor")
            textField(id: 'nomorSearch', columns: 20, text: bind('nomorSearch', target: model, mutual: true), actionPerformed: controller.search)
            label("Tanggal")
            dateTimePicker(id: 'tanggalMulaiSearch', localDate: bind('tanggalMulaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiSearch', localDate: bind('tanggalSelesaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search)
            button(app.getMessage('simplejpa.search.all.label'), actionPerformed: controller.listAll)
        }

        panel(constraints: CENTER) {
            borderLayout()
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.pembayaranSignedBillList, sortingStrategy: SINGLE_COLUMN) {
                    glazedColumn(name: 'Nomor', expression: {it.workOrder?.nomor}, width: 120)
                    glazedColumn(name: 'Tanggal', property: 'tanggal', width: 100) {
                        templateRenderer("\${it.toString('dd-MM-yyyy')}")
                    }
                    glazedColumn(name: 'Pelanggan', expression: {it.workOrder?.pelanggan}) {
                        templateRenderer('${it?.nama}')
                    }
                    glazedColumn(name: 'Status', expression: {it.workOrder?.statusTerakhir}, width: 150)
                    glazedColumn(name: 'Lunas?', expression: {it.isLunas()}, width: 60) {
                        templateRenderer(templateExpression: { it?'Y':'-'})
                    }
                    glazedColumn(name: 'Tanggal Pelunasan', property: 'tanggalPelunasan') {
                        templateRenderer("\${it?it.toString('dd-MM-yyyy'): '-'}")
                    }
                    glazedColumn(name: 'Downpayment', property: 'jumlahBayarDimuka', columnClass: Integer) {
                        templateRenderer(templateExpression: {it? currencyFormat(it): '-'}, horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Tagihan', expression: {it.total}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Keterangan', property: 'keterangan')
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), constraints: PAGE_END) {
            label('Tanggal Pelunasan: ')
            dateTimePicker(id: 'tanggalPelunasan', localDate: bind('tanggalPelunasan', target: model, mutual: true), errorPath: 'tanggalPelunasan', dateVisible: true, timeVisible: false)
            errorLabel(path: 'tanggalPelunasan', constraints: 'wrap')
            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Proses Pembayaran', visible: bind{table.isRowSelected}, actionPerformed: {
                    if (view.table.selectionModel.selected[0].isLunas()) {
                        JOptionPane.showMessageDialog(mainPanel, 'Tidak dapat memproses pembayaran untuk tagihan yang sudah lunas!',
                            'Kesalahan Proses Pembayaran', JOptionPane.ERROR_MESSAGE)
                        return
                    } else if (JOptionPane.showConfirmDialog(mainPanel, "Anda yakin pembayaran sebesar ${NumberFormat.currencyInstance.format(view.table.selectionModel.selected[0].total)} untuk tagihan ini telah diterima?",
                            'Konfirmasi Proses Pembayaran', JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return
                    }
                    controller.prosesPembayaran()
                })
            }
        }
    }
}
PromptSupport.setPrompt("Ketik kata kunci pencarian disini!", nomorSearch)