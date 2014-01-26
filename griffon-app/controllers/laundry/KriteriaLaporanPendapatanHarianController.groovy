package laundry

import org.joda.time.LocalDate
import project.MembershipSearch
import simplejpa.transaction.Transaction

import javax.swing.SwingUtilities

class KriteriaLaporanPendapatanHarianController {

    KriteriaLaporanPendapatanHarianModel model
    def view

    void mvcGroupInit(Map args) {
        model.tanggalMulaiCari = LocalDate.now().withDayOfMonth(1)
        model.tanggalSelesaiCari = LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1)
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(newSession = true)
    def tampilkanLaporan = {

        // Cari semua pendapatan tunai
        Map params = ['tanggalMulai': model.tanggalMulaiCari, 'tanggalSelesai': model.tanggalSelesaiCari]
        def pendapatanTunai = executeQuery('SELECT p.tanggal, SUM(p.tagihan), SUM(p.workOrder.jumlahDiskon) FROM PembayaranCash p WHERE p.tanggal BETWEEN :tanggalMulai AND :tanggalSelesai GROUP BY p.tanggal ORDER BY p.tanggal', [:], params)
        def downpayment = executeQuery('SELECT p.tanggal, SUM(p.jumlahBayarDimuka) FROM PembayaranSignedBill p WHERE p.tanggal BETWEEN :tanggalMulai AND :tanggalSelesai GROUP BY p.tanggal ORDER BY p.tanggal', [:], params)
        def sisa = executeQuery('SELECT p.tanggalPelunasan, SUM(p.tagihan - p.jumlahBayarDimuka) FROM PembayaranSignedBill p WHERE p.tanggalPelunasan BETWEEN :tanggalMulai AND :tanggalSelesai GROUP BY p.tanggalPelunasan ORDER BY p.tanggalPelunasan', [:], params)
        def pendapatanKartuDebit = executeQuery('SELECT p.tanggal, SUM(p.tagihan), SUM(p.workOrder.jumlahDiskon) FROM PembayaranKartuDebit p WHERE p.tanggal BETWEEN :tanggalMulai AND :tanggalSelesai GROUP BY p.tanggal ORDER BY p.tanggal', [:], params)
        def pendapatanKartuKredit = executeQuery('SELECT p.tanggal, SUM(p.tagihan), SUM(p.workOrder.jumlahDiskon) FROM PembayaranKartuKredit p WHERE p.tanggal BETWEEN :tanggalMulai AND :tanggalSelesai GROUP BY p.tanggal ORDER BY p.tanggal', [:], params)
        def pendapatanSignedBill = executeQuery('SELECT p.tanggal, SUM(p.tagihan), SUM(p.workOrder.jumlahDiskon) FROM PembayaranSignedBill p WHERE p.tanggal BETWEEN :tanggalMulai AND :tanggalSelesai GROUP BY p.tanggal ORDER BY p.tanggal', [:], params)

        // Proses hasil query
        model.result = []
        for (LocalDate tanggal = model.tanggalMulaiCari;
             tanggal.isEqual(model.tanggalSelesaiCari) || tanggal.isBefore(model.tanggalSelesaiCari);
             tanggal = tanggal.plusDays(1)) {

            Map barisResult = [:]

            BigDecimal jumlahDiskon = 0

            def itemPendapatanTunai = pendapatanTunai.find { it[0].isEqual(tanggal) }
            if (itemPendapatanTunai) {
                barisResult['tunai'] = itemPendapatanTunai[1] + (itemPendapatanTunai[2]?:0)
                jumlahDiskon += (itemPendapatanTunai[2]?:0)
            }
            def itemDownpayment = downpayment.find { it[0].isEqual(tanggal) }
            if (itemDownpayment) {
                barisResult['tunai'] = (barisResult['tunai']?: 0) + itemDownpayment[1]
            }
            def itemSisa = sisa.find { it[0].isEqual(tanggal) }
            if (itemSisa) {
                barisResult['tunai'] = (barisResult['tunai']?: 0) + itemSisa[1]
            }

            def itemPendapatanKartuDebit = pendapatanKartuDebit.find { it[0].isEqual(tanggal) }
            if (itemPendapatanKartuDebit) {
                barisResult['kartuDebit'] = itemPendapatanKartuDebit[1] + (itemPendapatanKartuDebit[2]?:0)
                jumlahDiskon += (itemPendapatanKartuDebit[2]?:0)
            }

            def itemPendapatanKartuKredit = pendapatanKartuKredit.find { it[0].isEqual(tanggal) }
            if (itemPendapatanKartuKredit) {
                barisResult['kartuKredit'] = itemPendapatanKartuKredit[1] + (itemPendapatanKartuKredit[2]?:0)
                jumlahDiskon += (itemPendapatanKartuKredit[2]?:0)
            }

            def itemPendapatanSignedBill = pendapatanSignedBill.find { it[0].isEqual(tanggal) }
            if (itemPendapatanSignedBill) {
                barisResult['signedBill'] = itemPendapatanSignedBill[1] + (itemPendapatanSignedBill[2]?:0)
                jumlahDiskon += (itemPendapatanSignedBill[2]?:0)
            }

            if (!barisResult.isEmpty()) {
                barisResult.'tanggal' = tanggal
                barisResult.'diskon' = jumlahDiskon
                model.result << barisResult
            }

        }

        SwingUtilities.getWindowAncestor(view.mainPanel).visible = false
    }

    @Transaction(Transaction.Policy.SKIP)
    def batal = {
        model.batal = true
        SwingUtilities.getWindowAncestor(view.mainPanel).visible = false
    }


}
