package laundry

import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate
import project.MembershipSearch

class KriteriaKuantitasPerPelangganModel {

    @Bindable LocalDate tanggalMulaiCari

    @Bindable LocalDate tanggalSelesaiCari

    @Bindable String namaPelanggan

    @Bindable String kategori

    @Bindable String itemPakaian

    EnumComboBoxModel<MembershipSearch> membershipSearch = new EnumComboBoxModel<>(MembershipSearch)

    @Bindable List result

    boolean batal = false
}