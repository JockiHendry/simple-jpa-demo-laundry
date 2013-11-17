package laundry

import domain.ItemPakaian
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import net.sf.jasperreports.swing.JRViewer
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction

import javax.swing.JOptionPane
import java.awt.BorderLayout

@Transaction
class LaporanController {

    LaporanModel model
    def view

    void mvcGroupInit(Map args) {
        model.tanggalMulaiCari = LocalDate.now().withDayOfMonth(1)
        model.tanggalSelesaiCari = LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1)
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(newSession = true)
    def search = {
        JenisLaporan jenisLaporan = model.jenisLaporanSearch.selectedItem
        if (!jenisLaporan) {
            JOptionPane.showMessageDialog(view.mainPanel, "Anda harus memilih jenis laporan yang akan ditampilkan!",
                    "Pesan Kesalahan", JOptionPane.ERROR_MESSAGE)
            return
        }

        Map parameter = ['tanggalMulaiCari': model.tanggalMulaiCari, 'tanggalSelesaiCari': model.tanggalSelesaiCari]
        //List source = executeNamedQuery(jenisLaporan.namedQuery, parameter)
        List queryResult = executeQuery('SELECT DAY(wo.tanggal) AS tanggal, i.jumlah, i.work.itemPakaian.nama AS nama FROM WorkOrder wo JOIN wo.itemWorkOrders i ORDER BY tanggal, nama')
        List source = []
        queryResult.each {
            source << ['tanggal': it[0], 'jumlah': it[1], 'nama': it[2]]
        }

        // data default untuk seluruh jenis pakaian
        findAllItemPakaian().each { ItemPakaian itemPakaian ->
            (1..31).each { source << ['tanggal': it, 'jumlah': 0, 'nama': itemPakaian.nama]}
        }

        JRDataSource dataSource = new JRMapCollectionDataSource(source)
        JasperPrint jasperPrint = JasperFillManager.fillReport(
                getResourceAsStream("report/${jenisLaporan.namaLaporan}.jasper"),
                parameter, dataSource)

        execInsideUIAsync {
            view.content.clear()
            view.content.add(new JRViewer(jasperPrint), BorderLayout.CENTER)
        }
    }


}
