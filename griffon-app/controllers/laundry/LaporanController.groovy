package laundry

import domain.ItemPakaian
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
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
        List queryResult = executeNamedQuery(jenisLaporan.namedQuery, parameter)
        List source = []

        switch(jenisLaporan) {
            case JenisLaporan.LAPORAN_HARIAN:
                queryResult.each {
                    source << ['tanggal': it[0], 'jumlah': it[1], 'nama': it[2]]
                }
                // data default untuk seluruh jenis pakaian
                findAllItemPakaian().each { ItemPakaian itemPakaian ->
                    (1..31).each { source << ['tanggal': it, 'jumlah': 0, 'nama': itemPakaian.nama]}
                }
                break

            case JenisLaporan.LAPORAN_PELANGGAN:
            case JenisLaporan.LAPORAN_PELANGGAN_CORPORATE:
            case JenisLaporan.LAPORAN_PELANGGAN_OUTSIDER:
                queryResult.each {
                    source << ['pelanggan': it[0], 'jumlah': it[1], 'nama': it[2]]
                }
                break

            case JenisLaporan.LAPORAN_PEMASUKAN:
                queryResult.each {
                    source << ['nama': it[0], 'total': it[1]]
                }
                break
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
