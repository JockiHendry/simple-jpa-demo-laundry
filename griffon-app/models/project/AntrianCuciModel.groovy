package project

import ca.odell.glazedlists.BasicEventList
import domain.StatusPekerjaan
import domain.WorkOrder
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class AntrianCuciModel {

    @Bindable String nomorSearch
    @Bindable String pelangganSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<JenisJadwalSearch> jenisJadwalSearch = new EnumComboBoxModel<JenisJadwalSearch>(JenisJadwalSearch.class)

    @Bindable LocalDateTime tanggal
    @Bindable LocalDate estimasiSelesai
    @Bindable String keterangan
    @Bindable StatusPekerjaan statusTerakhir

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()
}