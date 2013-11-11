package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class WorkController {

    final static String SEMUA_ITEM_PAKAIAN = "- Semua Item Pakaian -"

    WorkModel model
    def view

    void mvcGroupInit(Map args) {
        listAll()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(newSession = true)
    def listAll = {
        execInsideUISync {
            model.workList.clear()
            model.itemPakaianList.clear()
            model.itemPakaianSearchList.clear()
            model.jenisWorkList.clear()
        }

        List workResult = findAllWork()
        List itemPakaianResult = findAllItemPakaian()
        List jenisWorkResult = findAllJenisWork()

        execInsideUISync {
            model.workList.addAll(workResult)
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
            model.itemPakaianSearchList << SEMUA_ITEM_PAKAIAN
            model.itemPakaianSearchList.addAll(itemPakaianResult)
            model.itemPakaianSearch.selectedItem = SEMUA_ITEM_PAKAIAN
            model.itemPakaianList.addAll(itemPakaianResult)
            model.jenisWorkList.addAll(jenisWorkResult)
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.workList.clear() }
        List result
        if (model.itemPakaianSearch.selectedItem==SEMUA_ITEM_PAKAIAN) {
            result = findAllWork()
        } else {
            result = findAllWorkByItemPakaian(model.itemPakaianSearch.selectedItem)
        }
        execInsideUISync {
            model.workList.addAll(result)
            model.searchMessage = "Menampilkan hasil pencarian item pakaian ${model.itemPakaianSearch.selectedItem}"
        }
    }

    def save = {
        Work work = new Work('itemPakaian': model.itemPakaian.selectedItem, 'jenisWork': model.jenisWork.selectedItem, 'harga': model.harga)

        if (!validate(work)) return

        if (model.id == null) {
            work = merge(work)
            execInsideUISync {
                model.workList << work
                view.table.changeSelection(model.workList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            Work selectedWork = view.table.selectionModel.selected[0]
            selectedWork.itemPakaian = model.itemPakaian.selectedItem
            selectedWork.jenisWork = model.jenisWork.selectedItem
            selectedWork.harga = model.harga

            selectedWork = merge(selectedWork)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedWork }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        Work work = view.table.selectionModel.selected[0]
        remove(work)
        execInsideUISync {
            model.workList.remove(work)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.itemPakaian.selectedItem = null
            model.jenisWork.selectedItem = null
            model.harga = null

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
                Work selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.itemPakaian.selectedItem = selected.itemPakaian
                model.jenisWork.selectedItem = selected.jenisWork
                model.harga = selected.harga
            }
        }
    }

}