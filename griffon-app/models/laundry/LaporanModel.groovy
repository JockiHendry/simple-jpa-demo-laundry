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
    LAPORAN_PENDAPATAN_PER_PELANGGAN('Laporan Pendapatan Per Pelanggan', 'laporan_pendapatan_per_pelanggan', 'kriteriaPendapatanPerPelanggan')

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