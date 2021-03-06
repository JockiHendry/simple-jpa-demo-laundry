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
import simplejpa.swing.WrapLayout

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

        panel(constraints: PAGE_START, layout: new WrapLayout(WrapLayout.LEADING)) {
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
                    glazedColumn(name: 'Pembayaran', property: 'pembayaran', width: 200) {
                        templateRenderer(templateExpression: { it?.getNamaDeskripsi()?: '-'})
                    }
                    glazedColumn(name: 'Lunas?', expression: {it.pembayaran?.isLunas()}, width: 60) {
                        templateRenderer(templateExpression: { it?'Y':'-'})
                    }
                    glazedColumn(name: 'Express?', property: 'express', width: 70) {
                        templateRenderer(templateExpression: {it?'Y': 'N'})
                    }
                    glazedColumn(name: 'Keterangan', property: 'keterangan')
                    glazedColumn(name: 'Status', property: 'statusTerakhir') {
                        templateRenderer('${it}') {
                            condition(if_: {it==StatusPekerjaan.DITERIMA}, then_property_: 'foreground', is_: Color.BLUE, else_is_: Color.GRAY)
                            condition(if_: {isSelected}, then_property_: 'foreground', is_: Color.WHITE)
                        }
                    }
                    glazedColumn(name: 'Diskon', property: 'diskon', columnClass: Integer)
                    glazedColumn(name: 'Jumlah Diskon', property: 'jumlahDiskon', columnClass: Integer) {
                        templateRenderer("\${it==0?'-':currencyFormat(it)}", horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Total', expression: {it.total}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }

                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('hidemode 2', '[right][left][left,grow]', ''), constraints: PAGE_END) {
            label('Nomor:')
            textField(id: 'nomor', columns: 20, text: bind('nomor', target: model, mutual: true), errorPath: 'nomor')
            errorLabel(path: 'nomor', constraints: 'wrap')
            label('Tanggal:')
            dateTimePicker(id: 'tanggal', localDate: bind('tanggal', target: model, mutual: true), errorPath: 'tanggal', dateVisible: true, timeVisible: false)
            errorLabel(path: 'tanggal', constraints: 'wrap')
            label('Pelanggan:')
            panel {
                label(text: bind { model.selectedPelanggan? model.selectedPelanggan.nama: '- kosong -'})
                mvcPopupButton('Cari Pelanggan...', id: 'cariPelanggan', mvcGroup: 'pelanggan', errorPath: 'pelanggan', dialogProperties: [
                    title: 'Cari Pelanggan',
                    size: new Dimension(800, 420)
                ], onFinish: { PelangganModel m, v, c ->
                    if (v.table.selectionModel.isSelectionEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, "Tidak ada pelanggan yang dipilih!", 'Cari Pelanggan', JOptionPane.ERROR_MESSAGE)
                    } else {
                        model.selectedPelanggan = controller.findPelangganById(v.table.selectionModel.selected[0].id)
                    }
                })
            }
            errorLabel(path: 'pelanggan', constraints: 'wrap')
            label('Isi:')
            panel {
                label(text: bind {model.informasi})
                mvcPopupButton(id: 'itemWorkOrders', text: 'Klik Disini Untuk Melihat atau Mengisi Rincian Order...',
                        errorPath: 'itemWorkOrders', mvcGroup: 'itemWorkOrderAsChild',
                        args: { [parentList: model.itemWorkOrders, parentWorkOrder: view.table.selectionModel.selected[0], parentPelanggan: model.selectedPelanggan] },
                        dialogProperties: [title: 'Item Work Orders', size: new Dimension(900,420)], onFinish: { m, v, c ->
                    model.itemWorkOrders.clear()
                    model.itemWorkOrders.addAll(m.itemWorkOrderList)
                    controller.refreshInformasi()
                })
            }
            errorLabel(path: 'itemWorkOrders', constraints: 'wrap')

            label('Keterangan:')
            textField(id: 'keterangan', columns: 50, text: bind('keterangan', target: model, mutual: true), errorPath: 'keterangan')
            errorLabel(path: 'keterangan', constraints: 'wrap')

            label('Diskon:')
            panel(layout: new FlowLayout(FlowLayout.LEADING, 0, 0)) {
                comboBox(id: 'pilihanPersen', model: model.pilihanPersen, errorPath: 'pilihanPersen')
                label('% dan Nominal Rp')
                numberTextField(id: 'diskonNominal', columns: 20, bindTo: 'diskonNominal', errorPath: 'diskonNominal')
            }
            errorLabel(path: 'diskon', constraints: 'wrap')

            label('Express:')
            checkBox(id: 'express', selected: bind('express', target: model, mutual: true), errorPath: 'express')
            errorLabel(path: 'express', constraints: 'wrap')

            label('Pembayaran:')
            pembayaranGroup = buttonGroup()
            panel {
                radioButton('Tunai', id: 'pembayaranCash', buttonGroup: pembayaranGroup, actionPerformed: { mainPanel.revalidate() },
                    selected: bind('pembayaranCash', target: model, mutual: true))
                radioButton('Signed Bill', id: 'pembayaranSignedBill', buttonGroup: pembayaranGroup, actionPerformed: { mainPanel.revalidate() },
                    selected: bind('pembayaranSignedBill', target: model, mutual: true))
                radioButton('Kartu Debit', id: 'pembayaranKartuDebit', buttonGroup: pembayaranGroup, actionPerformed: { mainPanel.revalidate() },
                    selected: bind('pembayaranKartuDebit', target: model, mutual: true))
                radioButton('Kartu Kredit', id: 'pembayaranKartuKredit', buttonGroup: pembayaranGroup, actionPerformed: { mainPanel.revalidate() },
                        selected: bind('pembayaranKartuKredit', target: model, mutual: true))
                radioButton('Compliment', id: 'pembayaranCompliant', buttonGroup: pembayaranGroup, actionPerformed: { mainPanel.revalidate() },
                    selected: bind('pembayaranCompliant', target: model, mutual: true))
            }
            errorLabel(path: 'pembayaran', constraints: 'wrap')

            label('Ket. Pembayaran:')
            textField(id: 'keteranganPembayaran', columns: 20, text: bind('keteranganPembayaran', target: model, mutual: true),
                constraints: 'wrap')

            label('Downpayment:', visible: bind{model.pembayaranSignedBill}, constraints: 'hidemode 3')
            numberTextField(id: 'jumlahBayarDimuka', columns: 20, bindTo: 'jumlahBayarDimuka', nfParseBigDecimal: true,
                visible: bind {model.pembayaranSignedBill}, constraints: 'hidemode 3, wrap')

            label('Nomor Kartu:', visible: bind{model.pembayaranKartuDebit}, constraints: 'hidemode 3')
            textField(id: 'nomorKartuDebit', columns: 20, text: bind('nomorKartuDebit', target: model, mutual: true),
                visible: bind {model.pembayaranKartuDebit}, constraints: 'hidemode 3,wrap')

            label('Nomor Kartu:', visible: bind{model.pembayaranKartuKredit}, constraints: 'hidemode 3')
            textField(id: 'nomorKartuKredit', columns: 20, text: bind('nomorKartuKredit', target: model, mutual: true),
                    visible: bind {model.pembayaranKartuKredit}, constraints: 'hidemode 3, wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button(app.getMessage("simplejpa.dialog.save.button"), actionPerformed: {
                    if (model.id != null) {
                        if (model.statusTerakhir != StatusPekerjaan.DITERIMA) {
                            JOptionPane.showMessageDialog(mainPanel, "Tidak dapat mengubah work order ini karena work order telah diproses ke tahap berikutnya!",
                                "Update Tidak Diperbolehkan", JOptionPane.ERROR_MESSAGE)
                            return
                        }
                        if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                                app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                            return
                        }
                    }
                    controller.save()
                    nomor.requestFocusInWindow()
                })

                mvcPopupButton('Cetak Bukti Penerimaan', mvcGroup: 'previewFaktur', args: {[
                        source: view.table.selectionModel.selected[0],
                        fileReport: 'bukti_terima'
                    ]}, dialogProperties: [
                        title: 'Preview Bukti Penerimaan', size: new Dimension(840,600)
                    ], visible: bind { table.isRowSelected } )

                mvcPopupButton(id: 'detailStatus', text: 'Detail Status...', mvcGroup: 'eventPekerjaanAsChild',
                        args: {[parentWorkOrder: view.table.selectionModel.selected[0]]}, dialogProperties: [title: 'Detail Status'],
                        visible: bind {table.isRowSelected}, onFinish: { m, v, c ->
                    view.table.selectionModel.selected[0] = controller.findWorkOrderById(view.table.selectionModel.selected[0].id)
                })
                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear)
                button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind { table.isRowSelected }, actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                            app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        controller.delete()
                    }
                })
            }
        }
    }
}
PromptSupport.setPrompt("Nomor WO", nomorSearch)
PromptSupport.setPrompt("Pelanggan", pelangganSearch)