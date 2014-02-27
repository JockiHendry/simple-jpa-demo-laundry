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
import java.text.SimpleDateFormat

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*
import org.jdesktop.swingx.prompt.PromptSupport

SimpleDateFormat standardDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm")

application(title: 'Day End Closing',
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
            label("Tanggal")
            dateTimePicker(id: 'tanggalMulaiSearch', localDate: bind('tanggalMulaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiSearch', localDate: bind('tanggalSelesaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search, mnemonic: KeyEvent.VK_C)
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label(text: bind('searchMessage', source: model))
            }
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.dayEndClosingList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Tanggal Closing', property: 'tanggal', width: 120) {
                        templateRenderer("\${it.toString('dd-MM-yyyy')}")
                    }
                    glazedColumn(name: 'Dibuat Pada', property: 'createdDate', width: 150) {
                        templateRenderer(templateExpression: {standardDateFormat.format(it)})
                    }
                    glazedColumn(name: 'Dibuat Oleh', property: 'user')
                    glazedColumn(name: 'Tunai', property: 'tunai', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Aktual Tunai', property: 'aktualTunai', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Signed Bill', property: 'signedBill', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Kartu Kredit', property: 'kartuKredit', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Kartu Debit', property: 'kartuDebit', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Compliment', property: 'compliment', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Total', expression: {it.getTotal()}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Selisih', expression: {it.getTotal() - it.getAktual()}, columnClass: Integer) {
                        templateRenderer("\${it==0?'':currencyFormat(it)}", horizontalAlignment: RIGHT) {
                            condition(if_: {it!=''}, then_property_: 'foreground', is_: Color.RED, else_is_: Color.BLACK)
                        }
                    }
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), constraints: PAGE_END) {
            label('Tanggal Closing:')
            dateTimePicker(id: 'tanggal', localDate: bind('tanggal', target: model, mutual: true), errorPath: 'tanggal', dateVisible: true, timeVisible: false)
            errorLabel(path: 'tanggal', constraints: 'wrap')
            label('Hasil Perhitungan Kas:')
            numberTextField(id: 'aktualTunai', columns: 20, bindTo: 'aktualTunai', nfParseBigDecimal: true, errorPath: 'aktualTunai')
            errorLabel(path: 'aktualTunai', constraints: 'wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button(app.getMessage("simplejpa.dialog.save.button"), actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, 'Day-end closing hanya dapat dilakukan sekali saja dalam sehari.  Apakah Anda yakin ingin melakukan day-end closing?',
                        'Konfirmasi', JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            controller.save()
                            tanggal.requestFocusInWindow()
                    }
                })

                mvcPopupButton('Cetak Nota', mvcGroup: 'previewFaktur', args: {[
                        source: view.table.selectionModel.selected[0],
                        fileReport: 'bukti_closing'
                ]}, dialogProperties: [
                        title: 'Preview Nota Day-End Closing', size: new Dimension(840,600)
                ], visible: bind { table.isRowSelected } )
            }
        }
    }
}