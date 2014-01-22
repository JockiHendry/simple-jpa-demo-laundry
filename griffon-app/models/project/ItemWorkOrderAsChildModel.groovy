package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class ItemWorkOrderAsChildModel {

    @Bindable Long id
    @Bindable Work selectedWork
    @Bindable Integer jumlah
    @Bindable BigDecimal harga
    @Bindable EnumComboBoxModel<PilihanDiskon> pilihanPersen = new EnumComboBoxModel<>(PilihanDiskon)
    @Bindable BigDecimal diskonNominal
    @Bindable String keterangan

    BasicEventList<ItemWorkOrder> itemWorkOrderList = new BasicEventList<>()

    @Bindable String informasi
    WorkOrder parentWorkOrder
    Pelanggan parentPelanggan

    boolean isUpdateMode
    @Bindable boolean editable
}