package project

import domain.*
import griffon.transform.Threading
import simplejpa.transaction.Transaction
import validation.Pengisian

import javax.swing.*
import javax.swing.event.ListSelectionEvent
import java.text.NumberFormat

@Transaction
class ItemWorkOrderAsChildController {

    ItemWorkOrderAsChildModel model
    def view

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
            selectedItemWorkOrder.work = model.selectedWork
            selectedItemWorkOrder.harga = model.harga
            selectedItemWorkOrder.jumlah = model.jumlah
            selectedItemWorkOrder.keterangan = model.keterangan
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

    @Threading(Threading.Policy.SKIP)
    def prosesWork = { m, v, c ->
        if (v.table.selectionModel.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(view.mainPanel, 'Tidak ada item pekerjaan yang dipilih!', 'Cari Item Pekerjaan',
                    JOptionPane.ERROR_MESSAGE)
        } else {
            model.selectedWork = findWorkById(v.view.table.selectionModel.selected[0].id)
            model.harga = model.selectedWork.getHarga(model.parentPelanggan)
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.selectedWork = null
            model.harga = null
            model.jumlah = null
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
                model.keterangan = selected.keterangan
            }
        }
    }

}