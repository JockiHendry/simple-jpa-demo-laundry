package project

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
            label("Nomor")
            textField(id: 'nomorSearch', columns: 20, text: bind('nomorSearch', target: model, mutual: true), actionPerformed: controller.search)
            button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search, mnemonic: KeyEvent.VK_C)
            button(app.getMessage('simplejpa.search.all.label'), actionPerformed: controller.listAll, mnemonic: KeyEvent.VK_L)
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label(text: bind('searchMessage', source: model))
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
                    glazedColumn(name: 'Total', expression: {it.total()}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Estimasi Selesai', property: 'estimasiSelesai', width: 120) {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy'): '-'}")
                    }
                    glazedColumn(name: 'Jumlah Pakaian', expression: {it.itemWorkOrders.size()}, columnClass: Integer)
                    glazedColumn(name: 'Keterangan', property: 'keterangan')
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('hidemode 2', '[right][left][left,grow]', ''), constraints: PAGE_END) {

            label('Tanggal:')
            dateTimePicker(id: 'tanggal', localDate: bind('tanggal', target: model, mutual: true), errorPath: 'tanggal', dateVisible: true, timeVisible: false)
            errorLabel(path: 'tanggal', constraints: 'wrap')
            label('Keterangan:')
            textField(id: 'keterangan', columns: 60, text: bind('keterangan', target: model, mutual: true), errorPath: 'keterangan')
            errorLabel(path: 'keterangan', constraints: 'wrap')


            label('Isi:')
            mvcPopupButton(id: 'itemWorkOrders', text: 'Klik Disini Untuk Melihat Rincian Order...',
                    errorPath: 'itemWorkOrders', mvcGroup: 'itemWorkOrderAsChild',
                    args: { [parentList: view.table.selectionModel.selected[0].itemWorkOrders, parentWorkOrder: view.table.selectionModel.selected[0]] },
                    dialogProperties: [title: 'Item Work Orders', size: new Dimension(900,420)])
            errorLabel(path: 'itemWorkOrders', constraints: 'wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Order Ini Sedang Dicuci...', actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, "Apakah Anda yakin order ini sudah memasuki proses pencucian pada tanggal ${model.tanggal.toString('dd-MM-yyyy')}?",
                            'Konfirmasi Pencucian', JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                        return
                    }
                    controller.prosesCuci()
                })
                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear)
            }
        }
    }
}
PromptSupport.setPrompt("Ketik kata kunci pencarian disini!", nomorSearch)