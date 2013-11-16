package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class EventPekerjaanAsChildModel {

    WorkOrder parentWorkOrder

    @Bindable Long id
    @Bindable LocalDate tanggal
    EnumComboBoxModel<StatusPekerjaan> status = new EnumComboBoxModel<StatusPekerjaan>(StatusPekerjaan.class)

    BasicEventList<EventPekerjaan> eventPekerjaanList = new BasicEventList<>()

}