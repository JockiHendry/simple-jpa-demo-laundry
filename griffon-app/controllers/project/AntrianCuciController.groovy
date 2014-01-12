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

    AntrianCuciModel model
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

        model.tanggalMulaiSearch = LocalDate.now().minusMonths(1)
        model.tanggalSelesaiSearch = LocalDate.now()
        List workOrderResult = findAllWorkOrderByTanggalBetweenAndStatusTerakhir(model.tanggalMulaiSearch,
            model.tanggalSelesaiSearch, StatusPekerjaan.DITERIMA, [orderBy: 'nomor,tanggal'])

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            model.nomorSearch = null
            model.pelangganSearch = null
            view.jenisJadwalSearch.selectedItem = JenisJadwalSearch.SEMUA
        }
    }

    @Transaction(newSession = true)
    def search = {

        execInsideUISync { model.workOrderList.clear() }

        List result = findAllWorkOrderByDsl([orderBy: 'nomor,tanggal']) {
            tanggal between(model.tanggalMulaiSearch, model.tanggalSelesaiSearch)
            and()
            nomor like("%${model.nomorSearch?:''}%")
            and()
            pelanggan__nama like("%${model.pelangganSearch?:''}%")
            and()
            statusTerakhir eq(StatusPekerjaan.DITERIMA)
            if (model.jenisJadwalSearch.selectedItem && model.jenisJadwalSearch.selectedItem!=JenisJadwalSearch.SEMUA) {
                and()
                express eq(model.jenisJadwalSearch.selectedItem==JenisJadwalSearch.EXPRESS)
            }
        }

        execInsideUISync { model.workOrderList.addAll(result) }
    }

    def prosesCuci = {
        WorkOrder workOrder = merge(view.table.selectionModel.selected[0])
        if (model.keterangan && !model.keterangan.isEmpty()) {
            workOrder.keterangan = model.keterangan
        }
        workOrder.dicuci(model.tanggal)
        workOrder.estimasiSelesai = model.estimasiSelesai
        execInsideUISync {
            model.workOrderList.remove(view.table.selectionModel.selected[0])
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.keterangan = null
            model.estimasiSelesai = null
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