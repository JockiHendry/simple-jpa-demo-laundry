package project

import domain.*
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class PembayaranSignedBillController {

    PembayaranSignedBillModel model
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
            model.pembayaranSignedBillList.clear()
        }

        model.tanggalMulaiSearch = LocalDate.now().minusMonths(1)
        model.tanggalSelesaiSearch = LocalDate.now()
        List pembayaranSignedBillResult = findAllPembayaranSignedBillByTanggalBetween(model.tanggalMulaiSearch,
            model.tanggalSelesaiSearch, [orderBy: 'tanggal'])

        execInsideUISync {
            model.pembayaranSignedBillList.addAll(pembayaranSignedBillResult)
            model.nomorSearch = null
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.pembayaranSignedBillList.clear() }
        List result = findAllPembayaranSignedBillByDsl([orderBy: 'tanggal']) {
            if (model.nomorSearch?.trim()?.length() > 0) {
                workOrder__nomor like("%${model.nomorSearch}%")
            } else {
                tanggal between(model.tanggalMulaiSearch, model.tanggalSelesaiSearch)
            }
        }
        execInsideUISync {
            model.pembayaranSignedBillList.addAll(result)
        }
    }

    def prosesPembayaran = {
        PembayaranSignedBill pembayaranSignedBill = merge(view.table.selectionModel.selected[0])
        pembayaranSignedBill.tanggalPelunasan = model.tanggalPelunasan
        execInsideUISync {
            view.table.selectionModel.selected[0] = pembayaranSignedBill
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.tanggalPelunasan = null
            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
    }

}