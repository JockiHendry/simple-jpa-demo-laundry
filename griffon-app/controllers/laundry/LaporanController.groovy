/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
