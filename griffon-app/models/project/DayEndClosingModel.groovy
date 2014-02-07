package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class DayEndClosingModel {

    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch

    @Bindable Long id
    @Bindable LocalDate tanggal
    @Bindable BigDecimal aktualTunai

    BasicEventList<DayEndClosing> dayEndClosingList = new BasicEventList<>()

}