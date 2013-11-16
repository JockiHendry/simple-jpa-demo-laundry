package project

import domain.*
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction

import javax.persistence.FlushModeType
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import java.text.NumberFormat

@Transaction
class AntrianCuciController {

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

        List workOrderResult = findAllWorkOrderByStatusTerakhir(StatusPekerjaan.DITERIMA)

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            model.nomorSearch = null
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.workOrderList.clear() }
        List result = findAllWorkOrderByNomorLikeAndStatusTerakhir("%${model.nomorSearch}%", StatusPekerjaan.DITERIMA)
        execInsideUISync {
            model.workOrderList.addAll(result)
        }
    }

    def prosesCuci = {
        WorkOrder workOrder = merge(view.table.selectionModel.selected[0])
        if (model.keterangan && model.keterangan.isEmpty()) {
            workOrder.keterangan = model.keterangan
        }
        workOrder.dicuci(model.tanggal)
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