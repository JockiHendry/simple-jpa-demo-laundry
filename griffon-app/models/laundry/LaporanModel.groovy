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

import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate

class LaporanModel {

    @Bindable LocalDate tanggalMulaiCari
    @Bindable LocalDate tanggalSelesaiCari

    EnumComboBoxModel<JenisLaporan> jenisLaporanSearch = new EnumComboBoxModel<JenisLaporan>(JenisLaporan)

}

enum JenisLaporan {

    LAPORAN_KUANTITAS_PER_PELANGGAN('Laporan Kuantitas Per Pelanggan', 'laporan_kuantitas_per_pelanggan', 'kriteriaKuantitasPerPelanggan'),
    LAPORAN_PENDAPATAN_PER_PELANGGAN('Laporan Pendapatan Per Pelanggan', 'laporan_pendapatan_per_pelanggan', 'kriteriaPendapatanPerPelanggan'),
    LAPORAN_KUANTITAS_PER_PAKAIAN('Laporan Kuantitas Per Pakaian', 'laporan_kuantitas_per_pakaian', 'kriteriaKuantitasPerPakaian'),
    LAPORAN_PENDAPATAN_PER_PAKAIAN('Laporan Pendapatan Per Pakaian', 'laporan_pendapatan_per_pakaian', 'kriteriaPendapatanPerPakaian'),
    LAPORAN_TRANSAKSI_HARIAN_PER_PELANGGAN('Laporan Transaksi Harian Per Pelanggan', 'laporan_transaksi_harian_per_pelanggan', 'kriteriaTransaksiHarianPerPelanggan'),
    LAPORAN_PENDAPATAN_HARIAN('Laporan Pendapatan Harian', 'laporan_pendapatan_harian', 'kriteriaLaporanPendapatanHarian')

    String keterangan
    String namaLaporan
    String namaMVC

    public JenisLaporan(String keterangan, String namaLaporan, String namaMVC) {
        this.keterangan = keterangan
        this.namaLaporan = namaLaporan
        this.namaMVC = namaMVC
    }

    @Override
    String toString() {
        keterangan
    }
}