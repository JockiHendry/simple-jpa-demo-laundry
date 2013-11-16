package project

import ca.odell.glazedlists.BasicEventList
import domain.Pelanggan
import domain.StatusPekerjaan
import domain.WorkOrder
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate

class WorkOrderAsChildModel {

    Pelanggan parentPelanggan

    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<StatusPekerjaan> statusSearch = new EnumComboBoxModel<StatusPekerjaan>(StatusPekerjaan)

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

}