package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class PelangganModel {

    @Bindable Long id
    @Bindable String nama
    @Bindable String alamat
    @Bindable String nomorTelepon

    @Bindable String namaSearch
    @Bindable String searchMessage

    BasicEventList<Pelanggan> pelangganList = new BasicEventList<>()

    @Bindable boolean popupMode = false
}