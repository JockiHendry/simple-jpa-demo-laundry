package project

import ca.odell.glazedlists.BasicEventList
import domain.WorkOrder
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate

class PencucianModel {

    @Bindable String nomorSearch
    @Bindable String pelangganSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<JenisJadwalSearch> jenisJadwalSearch = new EnumComboBoxModel<JenisJadwalSearch>(JenisJadwalSearch.class)

    @Bindable LocalDate tanggal
    @Bindable String keterangan

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

}