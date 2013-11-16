package project

import domain.StatusPekerjaan
import domain.WorkOrder
import simplejpa.transaction.Transaction

import javax.swing.event.ListSelectionEvent

@Transaction
class PencucianController {
    def model
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
            model.workOrderList.clear()
        }

        List workOrderResult = findAllWorkOrderByStatusTerakhir(StatusPekerjaan.DICUCI)

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            model.nomorSearch = null
        }
    }

    @Transaction(newSession = true)
    def search = {
        if (model.nomorSearch) {
            execInsideUISync { model.workOrderList.clear() }
            List result = findAllWorkOrderByNomorLikeAndStatusTerakhir("%${model.nomorSearch}%", StatusPekerjaan.DICUCI)
            execInsideUISync {
                model.workOrderList.addAll(result)
            }
        }
    }

    def prosesSelesaiCuci = {
        WorkOrder workOrder = merge(view.table.selectionModel.selected[0])
        if (model.keterangan && model.keterangan.isEmpty()) {
            workOrder.keterangan = model.keterangan
        }
        workOrder.diselesaikan(model.tanggal)
        execInsideUISync {
            model.workOrderList.remove(view.table.selectionModel.selected[0])
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.keterangan = null
            model.tanggal = null
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
                WorkOrder selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.keterangan = selected.keterangan
            }
        }
    }
}
