package laundry

import org.joda.time.LocalDate
import project.MembershipSearch
import simplejpa.transaction.Transaction

import javax.swing.SwingUtilities

@Transaction
class KriteriaPendapatanPerPakaianController {

    KriteriaPendapatanPerPakaianModel model
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
        StringBuilder query = new StringBuilder('''
            SELECT
                i.work.itemPakaian.kategori.nama AS kategori,
                i.work.itemPakaian.nama AS itemPakaian,
                SUM(i.jumlah) AS jumlah,
                SUM(i.hargaSetelahDiskon) AS harga
            FROM
                ItemWorkOrder i
''')
        query.append(' WHERE (i.workOrder.tanggal BETWEEN :tanggalMulai AND :tanggalSelesai) ')
        Map params = ['tanggalMulai': model.tanggalMulaiCari, 'tanggalSelesai': model.tanggalSelesaiCari]

        if (model.membershipSearch.selectedItem == MembershipSearch.CORPORATE) {
            query.append(' AND i.workOrder.pelanggan.corporate = true ')
        } else if (model.membershipSearch.selectedItem == MembershipSearch.OUTSIDER) {
            query.append(' AND i.workOrder.pelanggan.corporate = false ')
        }

        if (!model.kategori.trim().isEmpty()) {
            query.append(' AND (i.work.itemPakaian.kategori.nama LIKE :kategori) ')
            params['kategori'] = '%' + model.kategori.trim() + '%'
        }

        if (!model.itemPakaian.trim().isEmpty()) {
            query.append(' AND (i.work.itemPakaian.nama LIKE :itemPakaian) ')
            params['itemPakaian'] = '%' + model.itemPakaian.trim() + '%'
        }

        query.append(' GROUP BY i.work.itemPakaian.nama ORDER BY kategori, itemPakaian')

        def result = executeQuery(query.toString(), [:], params)

        model.result = []
        for(Object[] o: result) {
            model.result << ['kategori': o[0], 'itemPakaian': o[1], 'jumlah': o[2], 'harga': o[3],
                    'total': ((o[3] as BigDecimal)?.multiply(o[2]))?: 0]
        }

        SwingUtilities.getWindowAncestor(view.mainPanel).visible = false
    }

    @Transaction(Transaction.Policy.SKIP)
    def batal = {
        model.batal = true
        SwingUtilities.getWindowAncestor(view.mainPanel).visible = false
    }

}
