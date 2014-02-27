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

import domain.StatusPekerjaan

import javax.swing.JOptionPane
import java.awt.event.KeyEvent

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*
import org.jdesktop.swingx.prompt.PromptSupport

application(title: 'Work Order',
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
            label("Cari")
            textField(id: 'nomorSearch', columns: 10, text: bind('nomorSearch', target: model, mutual: true), actionPerformed: controller.search)
            textField(id: 'pelangganSearch', columns: 10, text: bind('pelangganSearch', target: model, mutual: true), actionPerformed: controller.search)
            comboBox(id: 'jenisJadwalSearch', model: model.jenisJadwalSearch)
            label("Tanggal")
            dateTimePicker(id: 'tanggalMulaiSearch', localDate: bind('tanggalMulaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiSearch', localDate: bind('tanggalSelesaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search, mnemonic: KeyEvent.VK_C)
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label('Menampilkan work order yang telah diterima dan belum dikerjakan')
            }
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.workOrderList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Nomor', property: 'nomor', width: 120)
                    glazedColumn(name: 'Tanggal', property: 'tanggal', width: 100) {
                        templateRenderer("\${it.toString('dd-MM-yyyy')}")
                    }
                    glazedColumn(name: 'Pelanggan', property: 'pelanggan') {
                        templateRenderer('${it.nama}')
                    }
                    glazedColumn(name: 'Pembayaran', property: 'pembayaran') {
                        templateRenderer(templateExpression: { it?.getNamaDeskripsi()?: '-'})
                    }
                    glazedColumn(name: 'Lunas?', expression: {it.pembayaran?.isLunas()}, width: 60) {
                        templateRenderer(templateExpression: { it?'Y':'-'})
                    }
                    glazedColumn(name: 'Express?', property: 'express', width: 70) {
                        templateRenderer(templateExpression: {it?'Y': 'N'})
                    }
                    glazedColumn(name: 'Estimasi Selesai', property: 'estimasiSelesai', width: 120) {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy'): '-'}")
                    }
                    glazedColumn(name: 'Jumlah Pakaian', expression: {it.jumlahPakaian()}, columnClass: Integer)
                    glazedColumn(name: 'Keterangan', property: 'keterangan')
                    glazedColumn(name: 'Status', property: 'statusTerakhir') {
                        templateRenderer('${it}') {
                            condition(if_: {it==StatusPekerjaan.DITERIMA}, then_property_: 'foreground', is_: Color.BLUE, else_is_: Color.GRAY)
                            condition(if_: {isSelected}, then_property_: 'foreground', is_: Color.WHITE)
                        }
                    }
                    glazedColumn(name: 'Diskon', property: 'diskon', columnClass: Integer)
                    glazedColumn(name: 'Jumlah Diskon', expression: {it.hitungDiskon()}, columnClass: Integer) {
                        templateRenderer("\${it==0?'-':currencyFormat(it)}", horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Total', expression: {it.total}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('hidemode 2', '[right][left][left,grow]', ''), visible: bind { table.isRowSelected }, constraints: PAGE_END) {

            label('Tanggal Mulai Dikerjakan:')
            dateTimePicker(id: 'tanggal', localDateTime: bind('tanggal', target: model, mutual: true), errorPath: 'tanggal')
            errorLabel(path: 'tanggal', constraints: 'wrap')

            label('Tanggal Estimasi Selesai:')
            dateTimePicker(id: 'estimasiSelesai', localDate: bind('estimasiSelesai', target: model, mutual: true), errorPath: 'estimasiSelesai', dateVisible: true, timeVisible: false)
            errorLabel(path: 'estimasiSelesai', constraints: 'wrap')

            label('Keterangan:')
            textField(id: 'keterangan', columns: 60, text: bind('keterangan', target: model, mutual: true), errorPath: 'keterangan')
            errorLabel(path: 'keterangan', constraints: 'wrap')


            label('Isi:')
            mvcPopupButton(id: 'itemWorkOrders', text: 'Klik Disini Untuk Melihat Rincian Order...',
                    errorPath: 'itemWorkOrders', mvcGroup: 'itemWorkOrderAsChild',
                    args: { [parentList: view.table.selectionModel.selected[0].itemWorkOrders,
                             parentWorkOrder: view.table.selectionModel.selected[0],
                             editable: false] },
                    dialogProperties: [title: 'Item Work Orders', size: new Dimension(900,420)])
            errorLabel(path: 'itemWorkOrders', constraints: 'wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Proses Order Ini Menjadi Sedang Dikerjakan...', actionPerformed: controller.prosesCuci)
                mvcPopupButton(id: 'detailStatus', text: 'Detail Status...', mvcGroup: 'eventPekerjaanAsChild',
                        args: {[parentWorkOrder: view.table.selectionModel.selected[0]]}, dialogProperties: [title: 'Detail Status'],
                        visible: bind {table.isRowSelected}, onFinish: { m, v, c ->
                    view.table.selectionModel.selected[0] = controller.findWorkOrderById(view.table.selectionModel.selected[0].id)
                })
                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear)
            }
        }
    }
}

PromptSupport.setPrompt("Nomor WO", nomorSearch)
PromptSupport.setPrompt("Pelanggan", pelangganSearch)