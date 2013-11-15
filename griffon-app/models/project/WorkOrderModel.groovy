package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class WorkOrderModel {

    @Bindable Long id
    @Bindable String nomor
    @Bindable LocalDate tanggal

    @Bindable String nomorSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<StatusPekerjaan> statusSearch = new EnumComboBoxModel<StatusPekerjaan>(StatusPekerjaan.class)

    @Bindable Pelanggan selectedPelanggan
    @Bindable StatusPekerjaan statusTerakhir

    List<ItemWorkOrder> itemWorkOrders = []

    @Bindable Pembayaran pembayaran

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

}