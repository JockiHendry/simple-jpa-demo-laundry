package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class WorkModel {

    @Bindable Long id
    @Bindable BigDecimal harga

    @Bindable String searchMessage
    BasicEventList<ItemPakaian> itemPakaianList = new BasicEventList<>()
    @Bindable DefaultEventComboBoxModel<ItemPakaian> itemPakaian =
        GlazedListsSwing.eventComboBoxModelWithThreadProxyList(itemPakaianList)
    BasicEventList<JenisWork> jenisWorkList = new BasicEventList<>()
    @Bindable DefaultEventComboBoxModel<JenisWork> jenisWork =
        GlazedListsSwing.eventComboBoxModelWithThreadProxyList(jenisWorkList)

    BasicEventList itemPakaianSearchList = new BasicEventList()
    @Bindable DefaultEventComboBoxModel itemPakaianSearch =
        GlazedListsSwing.eventComboBoxModelWithThreadProxyList(itemPakaianSearchList)


    BasicEventList<Work> workList = new BasicEventList<>()

}