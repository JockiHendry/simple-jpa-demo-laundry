package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class KategoriModel {

    @Bindable Long id
	@Bindable String nama
	@Bindable String keterangan

    @Bindable String namaSearch
    @Bindable String searchMessage

    BasicEventList<Kategori> kategoriList = new BasicEventList<>()

}