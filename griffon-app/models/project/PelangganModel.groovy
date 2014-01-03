package project

import domain.*
import ca.odell.glazedlists.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class PelangganModel {

    @Bindable Long id
    @Bindable String nama
    @Bindable String alamat
    @Bindable String nomorTelepon
    @Bindable Boolean corporate
    @Bindable Boolean outsider

    @Bindable String namaSearch
    @Bindable String searchMessage
    EnumComboBoxModel<MembershipSearch> membershipSearch = new EnumComboBoxModel<>(MembershipSearch.class)

    BasicEventList<Pelanggan> pelangganList = new BasicEventList<>()

    @Bindable boolean popupMode = false

}

public enum MembershipSearch {
    SEMUA("Semua"), CORPORATE("Corporate"), OUTSIDER("Outsider")

    String text

    public MembershipSearch(String text) {
        this.text = text
    }

    @Override
    String toString() {
        text;
    }
}