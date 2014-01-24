package laundry

import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate
import project.MembershipSearch

class KriteriaTransaksiHarianPerPelangganModel {

    @Bindable LocalDate tanggalMulaiCari

    @Bindable LocalDate tanggalSelesaiCari

    @Bindable String namaPelanggan

    @Bindable String kategori

    EnumComboBoxModel<MembershipSearch> membershipSearch = new EnumComboBoxModel<>(MembershipSearch)

    @Bindable List result

    boolean batal = false


}