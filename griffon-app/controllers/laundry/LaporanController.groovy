package laundry

import domain.ItemPakaian
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import net.sf.jasperreports.swing.JRViewer
import org.joda.time.LocalDate
import simplejpa.swing.DialogUtils
import simplejpa.transaction.Transaction
import util.BusyLayerUI

import javax.swing.JOptionPane
import javax.swing.JPanel
import java.awt.BorderLayout

@Transaction
class LaporanController {

    LaporanModel model
    def view

    @Transaction(Transaction.Policy.SKIP)
    def search = {
        JenisLaporan jenisLaporan = model.jenisLaporanSearch.selectedItem
        if (!jenisLaporan) {
            JOptionPane.showMessageDialog(view.mainPanel, "Anda harus memilih jenis laporan yang akan ditampilkan!",
                    "Pesan Kesalahan", JOptionPane.ERROR_MESSAGE)
            return
        }

        def result, batal
        def tanggalMulaiCari, tanggalSelesaiCari
        execInsideUISync {
            DialogUtils.showMVCGroup(jenisLaporan.namaMVC, [:], app, view, [title: 'Pilih Kriteria'], { m, v, c ->
                result = m.result
                batal = m.batal
                tanggalMulaiCari = m.tanggalMulaiCari
                tanggalSelesaiCari = m.tanggalSelesaiCari
            }, {v -> v})

            BusyLayerUI.instance.hide()
        }

        if (!batal) {
            JRDataSource dataSource = new JRMapCollectionDataSource(result)

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                getResourceAsStream("report/${jenisLaporan.namaLaporan}.jasper"),
                ['tanggalMulaiCari': tanggalMulaiCari, 'tanggalSelesaiCari': tanggalSelesaiCari], dataSource)

            execInsideUISync {
                view.content.clear()
                view.content.add(new JRViewer(jasperPrint), BorderLayout.CENTER)
            }
        }
    }


}
