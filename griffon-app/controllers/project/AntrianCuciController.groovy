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
        List workOrderResult = findAllWorkOrderByTanggalBetweenAndStatusTerakhirNe(model.tanggalMulaiSearch,
            model.tanggalSelesaiSearch, StatusPekerjaan.DIAMBIL, [orderBy: 'nomor,tanggal'])

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
            statusTerakhir ne(StatusPekerjaan.DIAMBIL)
            if (model.jenisJadwalSearch.selectedItem && model.jenisJadwalSearch.selectedItem!=JenisJadwalSearch.SEMUA) {
                and()
                express eq(model.jenisJadwalSearch.selectedItem==JenisJadwalSearch.EXPRESS)
            }
        }

        execInsideUISync { model.workOrderList.addAll(result) }
    }

    def prosesCuci = {

        if (model.statusTerakhir != StatusPekerjaan.DITERIMA) {
            JOptionPane.showMessageDialog(view.mainPanel, "Tidak dapat mengubah work order ini karena statusnya tidak memungkinkan untuk diproses!",
                    "Update Tidak Diperbolehkan", JOptionPane.ERROR_MESSAGE)
            return
        }

        // validasi
        if (model.tanggal.toLocalDate().isBefore(view.table.selectionModel.selected[0].tanggal)) {
            model.errors['tanggal'] = 'Tanggal mulai dkerjakan harus setelah tanggal yang tertera di WO'
        }
        if (model.estimasiSelesai.isBefore(model.tanggal.toLocalDate())) {
            model.errors['estimasiSelesai'] = 'Tanggal estimasi selesai harus setelah tanggal mulai dikerjakan'
        }
        if (model.hasError()) return

        // konfirmasi
        if (JOptionPane.showConfirmDialog(view.mainPanel, "Apakah Anda yakin order ini sudah memasuki proses pencucian?",
                'Konfirmasi Pencucian', JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return
        }

        WorkOrder workOrder = merge(view.table.selectionModel.selected[0])
        if (model.keterangan && !model.keterangan.isEmpty()) {
            workOrder.keterangan = model.keterangan
        }

        workOrder.dicuci(model.tanggal)
        workOrder.estimasiSelesai = model.estimasiSelesai
        execInsideUISync {
            view.table.selectionModel.selected[0] = workOrder
            model.statusTerakhir = workOrder.statusTerakhir
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.keterangan = null
            model.estimasiSelesai = null
            model.statusTerakhir = null
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
                model.statusTerakhir = selected.statusTerakhir
            }
        }
    }
}