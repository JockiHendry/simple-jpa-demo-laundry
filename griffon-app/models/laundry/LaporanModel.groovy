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

    LAPORAN_HARIAN('Laporan Harian', 'laporan_harian', 'ItemWorkOrder.LaporanBulanan'),
    LAPORAN_PELANGGAN('Laporan Transaksi Pelanggan', 'laporan_pelanggan', 'ItemWorkOrder.LaporanPelanggan'),
    LAPORAN_PELANGGAN_CORPORATE('Laporan Transaksi Pelanggan (Corporate)', 'laporan_pelanggan', 'ItemWorkOrder.LaporanPelangganCorporate'),
    LAPORAN_PELANGGAN_OUTSIDER('Laporan Transaksi Pelanggan (Outsider)', 'laporan_pelanggan', 'ItemWorkOrder.LaporanPelangganOutsider'),
    LAPORAN_PEMASUKAN('Laporan Pemasukan', 'laporan_pemasukan', 'ItemWorkOrder.LaporanPemasukan')

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