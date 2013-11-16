package project

import domain.*
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
        model.isUpdateMode = (model.parentWorkOrder?.id != null)
        refreshInformasi()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(Transaction.Policy.SKIP)
    def refreshInformasi = {
        def jumlahItem = model.itemWorkOrderList.size()
        def total = model.itemWorkOrderList.sum { it.harga }?: 0
        model.informasi = "Jumlah Item ${jumlahItem}   Total ${NumberFormat.currencyInstance.format(total)}"
    }

    def save = {
        ItemWorkOrder itemWorkOrder = new ItemWorkOrder('work': model.selectedWork, 'harga': model.harga, 'jumlah': model.jumlah)
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
    def clear = {
        execInsideUISync {
            model.id = null
            model.selectedWork = null
            model.harga = null
            model.jumlah = null

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
            }
        }
    }

}