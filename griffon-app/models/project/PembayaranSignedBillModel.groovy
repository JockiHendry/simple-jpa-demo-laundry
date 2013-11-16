package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class PembayaranSignedBillModel {

    @Bindable Long id
    @Bindable LocalDate tanggalPelunasan
    @Bindable BigDecimal tagihan
    @Bindable String keterangan

    @Bindable String nomorSearch
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch

    BasicEventList<PembayaranSignedBill> pembayaranSignedBillList = new BasicEventList<>()

}