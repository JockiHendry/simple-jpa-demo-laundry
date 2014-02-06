package laundry

import org.joda.time.LocalDate
import project.MembershipSearch
import simplejpa.transaction.Transaction
import javax.swing.SwingUtilities

@Transaction
class KriteriaTransaksiHarianPerPelangganController {

    KriteriaTransaksiHarianPerPelangganModel model
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
                i.workOrder.pelanggan.nama AS nama,
                i.workOrder.tanggal AS tanggal,
                i.work.itemPakaian.kategori.nama AS kategori,
                SUM(i.hargaSetelahDiskon * i.jumlah) AS total
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

        if (!model.namaPelanggan.trim().isEmpty()) {
            query.append(' AND (i.workOrder.pelanggan.nama LIKE :nama) ')
            params['nama'] = '%' + model.namaPelanggan.trim() + '%'
        }

        if (!model.kategori.trim().isEmpty()) {
            query.append(' AND (i.work.itemPakaian.kategori.nama LIKE :kategori) ')
            params['kategori'] = '%' + model.kategori.trim() + '%'
        }

        query.append(' GROUP BY i.workOrder.pelanggan.nama, i.workOrder.tanggal, i.work.itemPakaian.kategori ORDER BY nama, tanggal, kategori')

        def result = executeQuery(query.toString(), [:], params)

        model.result = []
        for(Object[] o: result) {
            model.result << ['nama': o[0], 'tanggal': o[1], 'kategori': o[2], 'harga': o[3]]
        }

        SwingUtilities.getWindowAncestor(view.mainPanel).visible = false
    }

    @Transaction(Transaction.Policy.SKIP)
    def batal = {
        model.batal = true
        SwingUtilities.getWindowAncestor(view.mainPanel).visible = false
    }

}
