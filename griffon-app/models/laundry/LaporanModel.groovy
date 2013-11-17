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

    LAPORAN_BULANAN('Laporan Harian', 'laporan_harian', '')

    String keterangan
    String namaLaporan
    String namedQuery

    public JenisLaporan(String keterangan, String namaLaporan, String namedQuery) {
        this.keterangan = keterangan
        this.namaLaporan = namaLaporan
        this.namedQuery = namedQuery
    }

    @Override
    String toString() {
        keterangan
    }
}