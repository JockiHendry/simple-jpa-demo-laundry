package project

import ca.odell.glazedlists.BasicEventList
import domain.StatusPekerjaan
import domain.WorkOrder
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class PengambilanModel {

    @Bindable String nomorSearch
    @Bindable String pelangganSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<JenisJadwalSearch> jenisJadwalSearch = new EnumComboBoxModel<>(JenisJadwalSearch.class)
    EnumComboBoxModel<StatusPekerjaanPengambilanSearch> statusPekerjaanSearch = new EnumComboBoxModel<>(StatusPekerjaanPengambilanSearch.class)

    @Bindable LocalDateTime tanggal
    @Bindable String namaPenerima
    @Bindable String keterangan

    @Bindable Boolean adaSisaPembayaran
    @Bindable String jumlahDibayarDimuka
    @Bindable String sisa

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

}

enum StatusPekerjaanPengambilanSearch {
    SEMUA("Semua"), DITERIMA("Diterima"), DICUCI("Sedang Dikerjakan"), DISELESAIKAN("Selesai"), DIAMBIL("Diambil")

    String description

    StatusPekerjaanPengambilanSearch(String description) {
        this.description = description
    }

    StatusPekerjaan convertToStatusPekerjaan() {
        if (this == StatusPekerjaanPengambilanSearch.DITERIMA) {
            return StatusPekerjaan.DITERIMA
        } else if (this == StatusPekerjaanPengambilanSearch.DICUCI) {
            return StatusPekerjaan.DICUCI
        } else if (this == StatusPekerjaanPengambilanSearch.DISELESAIKAN) {
            return StatusPekerjaan.DISELESAIKAN
        } else if (this == StatusPekerjaanPengambilanSearch.DIAMBIL) {
            return StatusPekerjaan.DIAMBIL
        }
        return null
    }

    @Override
    String toString() {
        description
    }
}