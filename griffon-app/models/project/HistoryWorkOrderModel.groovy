package project

import ca.odell.glazedlists.BasicEventList
import domain.WorkOrder
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class HistoryWorkOrderModel {

    @Bindable String nomorSearch
    @Bindable String pelangganSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<JenisJadwalSearch> jenisJadwalSearch = new EnumComboBoxModel<>(JenisJadwalSearch.class)

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()
}