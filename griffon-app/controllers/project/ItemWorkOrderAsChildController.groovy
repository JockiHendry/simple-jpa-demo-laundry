package project

import domain.*
import griffon.transform.Threading
import simplejpa.swing.DialogUtils
import simplejpa.transaction.Transaction
import validation.Pengisian

import javax.swing.*
import javax.swing.event.ListSelectionEvent
import java.awt.Dialog
import java.awt.Dimension
import java.awt.Window
import java.text.NumberFormat

@Transaction
class ItemWorkOrderAsChildController {

    ItemWorkOrderAsChildModel model
    def view

    static MVCGroup workPopupMVC

    void mvcGroupInit(Map args) {
        args.'parentList'.each { model.itemWorkOrderList << it }
        model.parentWorkOrder =  args.'parentWorkOrder'
        model.parentPelanggan = args.'parentPelanggan'
        model.isUpdateMode = (model.parentWorkOrder?.id != null)
        model.editable = args.'editable'!=null? args.'editable': true
        refreshInformasi()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(Transaction.Policy.SKIP)
    def refreshInformasi = {
        def jumlahItem = model.itemWorkOrderList.sum { it.jumlah }?: 0
        def total = model.itemWorkOrderList.sum { it.total() }?: 0
        model.informasi = "Jumlah Pakaian ${jumlahItem}   Total ${NumberFormat.currencyInstance.format(total)}"
    }

    def save = {
        ItemWorkOrder itemWorkOrder = new ItemWorkOrder('work': model.selectedWork, 'harga': model.harga, 'jumlah': model.jumlah, 'keterangan': model.keterangan)
        if (model.pilihanPersen.selectedItem?.persen > 0 || model.diskonNominal > 0) {
            Diskon diskon = new Diskon(model.pilihanPersen.selectedItem, model.diskonNominal)
            itemWorkOrder.diskon = diskon
        }
        if (!validate(itemWorkOrder, Pengisian)) return_failed()

        if (view.table.selectionModel.selectionEmpty) {
            // Insert operation
            execInsideUISync {
                model.itemWorkOrderList << itemWorkOrder
                view.table.changeSelection(model.itemWorkOrderList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            if (model.isUpdateMode) {
                JOptionPane.showMessageDialog(view.mainPanel, "Anda tidak boleh mengubah data yang sudah tersimpan.", 'Pesan Kesalahan', JOptionPane.ERROR_MESSAGE)
                return
            }
            ItemWorkOrder selectedItemWorkOrder = view.table.selectionModel.selected[0]
            selectedItemWorkOrder.work = itemWorkOrder.work
            selectedItemWorkOrder.harga = itemWorkOrder.harga
            selectedItemWorkOrder.jumlah = itemWorkOrder.jumlah
            selectedItemWorkOrder.diskon = itemWorkOrder.diskon
            selectedItemWorkOrder.keterangan = itemWorkOrder.keterangan
        }
        execInsideUISync {
            clear()
            refreshInformasi()
        }
    }

    def delete = {
        ItemWorkOrder itemWorkOrder = view.table.selectionModel.selected[0]
        execInsideUISync {
            model.itemWorkOrderList.remove(itemWorkOrder)
            clear()
            refreshInformasi()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def showDaftarPekerjaan = {
        if (!workPopupMVC) {
            workPopupMVC = app.mvcGroupManager.buildMVCGroup('work', 'workPopup', [popup: true])
        }

        def m = workPopupMVC.model
        def v = workPopupMVC.view
        def c = workPopupMVC.controller

        Window thisWindow = SwingUtilities.getWindowAncestor(view.mainPanel)
        JDialog dialog = new JDialog(thisWindow, Dialog.ModalityType.APPLICATION_MODAL)
        if (DialogUtils.defaultContentDecorator) {
            dialog.contentPane = DialogUtils.defaultContentDecorator(v.mainPanel)
        } else {
            dialog.contentPane = v.mainPanel
        }
        dialog.pack()
        dialog.title = "Cari Item Pekerjaan"
        dialog.size = new Dimension(1000, 420)
        dialog.setLocationRelativeTo(thisWindow)
        dialog.setVisible(true)

        // Setelah dialog selesai ditampilkan
        if (v.table.selectionModel.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(view.mainPanel, 'Tidak ada item pekerjaan yang dipilih!', 'Cari Item Pekerjaan',
                    JOptionPane.ERROR_MESSAGE)
        } else {
            model.selectedWork = findWorkById(v.view.table.selectionModel.selected[0].id)
            model.harga = model.selectedWork.getHarga(model.parentPelanggan)
            view.jumlah.requestFocusInWindow()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.selectedWork = null
            model.harga = null
            model.jumlah = null
            model.pilihanPersen.selectedItem = null
            model.diskonNominal = null
            model.keterangan = null

            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def tableSelectionChanged = { ListSelectionEvent event ->
        execInsideUISync {
            if (view.table.selectionModel.isSelectionEmpty()) {
                clear()
            } else {
                ItemWorkOrder selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.selectedWork = selected.work
                model.harga = selected.harga
                model.jumlah = selected.jumlah
                model.pilihanPersen.selectedItem = selected.diskon?.pilihanPersen
                model.diskonNominal = selected.diskon?.nominal
                model.keterangan = selected.keterangan
            }
        }
    }

}