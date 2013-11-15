package project

import javax.swing.JOptionPane

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
            label("Nomor")
            textField(id: 'nomorSearch', columns: 20, text: bind('nomorSearch', target: model, mutual: true), actionPerformed: controller.search)
            label('Status')
            comboBox(id: 'statusSearch', model: model.statusSearch)
            label("Tanggal")
            dateTimePicker(id: 'tanggalMulaiSearch', localDate: bind('tanggalMulaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiSearch', localDate: bind('tanggalSelesaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search)
            button(app.getMessage('simplejpa.search.all.label'), actionPerformed: controller.listAll)
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label(text: bind('searchMessage', source: model))
            }
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.workOrderList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Nomor', property: 'nomor')
                    glazedColumn(name: 'Tanggal', property: 'tanggal') {
                        templateRenderer("\${it.toString('dd-MM-yyyy')}")
                    }
                    glazedColumn(name: 'Pelanggan', property: 'pelanggan') {
                        templateRenderer('${it.nama}')
                    }
                    glazedColumn(name: 'Status', property: 'statusTerakhir')
                    glazedColumn(name: 'Total', expression: {it.total()}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), constraints: PAGE_END) {
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
            mvcPopupButton(id: 'itemWorkOrders', text: 'Klik Disini Untuk Melihat atau Mengisi Rincian Order...',
                    errorPath: 'itemWorkOrders', mvcGroup: 'itemWorkOrderAsChild',
                    args: { [parentList: model.itemWorkOrders, parentWorkOrder: view.table.selectionModel.selected[0]] },
                    dialogProperties: [title: 'Item Work Orders', size: new Dimension(900,420)], onFinish: { m, v, c ->
                model.itemWorkOrders.clear()
                model.itemWorkOrders.addAll(m.itemWorkOrderList)
            })
            errorLabel(path: 'itemWorkOrders', constraints: 'wrap')
//            label('Event Pekerjaans:')
//            mvcPopupButton(id: 'eventPekerjaans', text: 'Event Pekerjaans', errorPath: 'eventPekerjaans', mvcGroup: 'eventPekerjaanAsChild',
//                    args: { [parentList: model.eventPekerjaans] }, dialogProperties: [title: 'Event Pekerjaans'], onFinish: { m, v, c ->
//                model.eventPekerjaans.clear()
//                model.eventPekerjaans.addAll(m.eventPekerjaanList)
//            }
//            )
//            errorLabel(path: 'eventPekerjaans', constraints: 'wrap')
//            label('Status Terakhir:')
//            comboBox(id: 'statusTerakhir', model: model.statusTerakhir, errorPath: 'statusTerakhir')
//            errorLabel(path: 'statusTerakhir', constraints: 'wrap')
//            label('Pembayaran:')
//            mvcPopupButton(id: 'pembayaran', text: 'Pembayaran', errorPath: 'pembayaran', mvcGroup: 'pembayaranAsPair',
//                    args: { [pair: model.pembayaran] }, dialogProperties: [title: 'Pembayaran'], onFinish: { m, v, c ->
//                model.pembayaran = m.pembayaran
//            }
//            )
//            errorLabel(path: 'pembayaran', constraints: 'wrap')
            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button(app.getMessage("simplejpa.dialog.save.button"), actionPerformed: {
                    if (model.id != null) {
                        if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                                app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                            return
                        }
                    }
                    controller.save()
                    nomor.requestFocusInWindow()
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
PromptSupport.setPrompt("Ketik kata kunci pencarian disini!", nomorSearch)