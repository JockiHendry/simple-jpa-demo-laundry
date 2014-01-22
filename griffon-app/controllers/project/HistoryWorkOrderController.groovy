package project

import domain.PembayaranSignedBill
import domain.StatusPekerjaan
import domain.WorkOrder
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction

import javax.swing.JOptionPane
import javax.swing.event.ListSelectionEvent
import java.text.NumberFormat

@Transaction
class HistoryWorkOrderController {

    HistoryWorkOrderModel model
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

        execInsideUISync {
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
            if (model.jenisJadwalSearch.selectedItem && model.jenisJadwalSearch.selectedItem!=JenisJadwalSearch.SEMUA) {
                and()
                express eq(model.jenisJadwalSearch.selectedItem==JenisJadwalSearch.EXPRESS)
            }
        }

        // Inisialisasi eventpekerjaan
        for (WorkOrder order: result) {
            order.eventPekerjaans.size()
        }

        execInsideUISync { model.workOrderList.addAll(result) }

    }
}
