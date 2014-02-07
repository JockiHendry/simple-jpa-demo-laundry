package project

import domain.*
import org.joda.time.LocalDate
import simplejpa.swing.DialogUtils
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import java.awt.Dimension

@Transaction
class DayEndClosingController {

    DayEndClosingModel model
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
            model.dayEndClosingList.clear()
        }

        model.tanggalMulaiSearch = LocalDate.now().minusMonths(1)
        model.tanggalSelesaiSearch = LocalDate.now()
        List dayEndClosingResult = findAllDayEndClosing()

        execInsideUISync {
            model.dayEndClosingList.addAll(dayEndClosingResult)
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.dayEndClosingList.clear() }

        List result = findAllDayEndClosingByTanggalBetween(model.tanggalMulaiSearch, model.tanggalSelesaiSearch)

        execInsideUISync {
            model.dayEndClosingList.addAll(result)
        }

    }

    def save = {

        if (model.tanggal.isAfter(LocalDate.now())) {
            model.errors['tanggal'] = 'Tanggal tidak boleh dimasa depan'
            return
        }

        // Tidak boleh ada closing ganda pada hari yang sama
        if (findDayEndClosingByTanggal(model.tanggal) != null) {
            JOptionPane.showMessageDialog(view.mainPanel, "Day-end closing untuk tanggal ${model.tanggal.toString('dd-MM-YYYY')} sudah dilakukan dan tidak dapat diulangi!",
                'Pesan Kesalahan', JOptionPane.ERROR_MESSAGE)
            return
        }

        // Membuat instance baru dan mengisi nilainya
        DayEndClosing dayEndClosing = new DayEndClosing('tanggal': model.tanggal, 'user': 'ADMIN1', 'aktualTunai': model.aktualTunai)

        Map params = ['tanggal': model.tanggal]
        def pendapatanTunai = executeQuery('SELECT SUM(p.tagihan) FROM PembayaranCash p WHERE p.tanggal = :tanggal', [:], params)[0] ?: 0
        def downpayment = executeQuery('SELECT SUM(p.jumlahBayarDimuka) FROM PembayaranSignedBill p WHERE p.tanggal = :tanggal', [:], params)[0] ?: 0
        def sisa = executeQuery('SELECT SUM(p.tagihan - p.jumlahBayarDimuka) FROM PembayaranSignedBill p WHERE p.tanggalPelunasan = :tanggal', [:], params)[0] ?: 0
        def pendapatanKartuDebit = executeQuery('SELECT SUM(p.tagihan) FROM PembayaranKartuDebit p WHERE p.tanggal = :tanggal', [:], params)[0] ?: 0
        def pendapatanKartuKredit = executeQuery('SELECT SUM(p.tagihan) FROM PembayaranKartuKredit p WHERE p.tanggal = :tanggal', [:], params)[0] ?: 0
        def pendapatanSignedBill = executeQuery('SELECT SUM(p.tagihan) FROM PembayaranSignedBill p WHERE p.tanggal = :tanggal', [:], params)[0] ?: 0
        def pendapatanCompliment = executeQuery('SELECT SUM(p.tagihan) FROM PembayaranCompliant p WHERE p.tanggal = :tanggal', [:], params)[0] ?: 0

        dayEndClosing.tunai = pendapatanTunai + downpayment + sisa
        dayEndClosing.kartuDebit = pendapatanKartuDebit
        dayEndClosing.kartuKredit = pendapatanKartuKredit
        dayEndClosing.signedBill = pendapatanSignedBill
        dayEndClosing.compliment = pendapatanCompliment

        if (!validate(dayEndClosing)) return

        // Insert operation
        persist(dayEndClosing)
        execInsideUISync {
            model.dayEndClosingList << dayEndClosing
            view.table.changeSelection(model.dayEndClosingList.size() - 1, 0, false, false)
        }

        // Mencetak nota
        execInsideUIAsync {
            DialogUtils.showMVCGroup('previewFaktur', [source: dayEndClosing, fileReport: 'bukti_closing'], app, view,
                [title: 'Preview Nota Day-End Closing', size: new Dimension(840,600)])
        }

        execInsideUISync { clear() }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.tanggal = LocalDate.now()
            model.aktualTunai = null
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
                DayEndClosing selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.tanggal = selected.tanggal
                model.aktualTunai = selected.aktualTunai
            }
        }
    }

}