package project

import domain.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class WorkOrderModel {

    def controller

    public WorkOrderModel() {
        addPropertyChangeListener('express', { controller.refreshInformasi() } as PropertyChangeListener)
    }

    @Bindable Long id
    @Bindable String nomor
    @Bindable LocalDate tanggal
    @Bindable String keterangan
    @Bindable Boolean express
    @Bindable boolean pembayaranCash
    @Bindable boolean pembayaranSignedBill
    @Bindable boolean pembayaranKartuDebit
    @Bindable boolean pembayaranCompliant
    @Bindable String keteranganPembayaran
    @Bindable BigDecimal jumlahBayarDimuka
    @Bindable String nomorKartu
    @Bindable EnumComboBoxModel<PilihanDiskon> pilihanPersen = new EnumComboBoxModel<>(PilihanDiskon)
    @Bindable BigDecimal diskonNominal

    @Bindable String nomorSearch
    @Bindable String pelangganSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<JenisJadwalSearch> jenisJadwalSearch = new EnumComboBoxModel<JenisJadwalSearch>(JenisJadwalSearch.class)

    @Bindable Pelanggan selectedPelanggan
    @Bindable StatusPekerjaan statusTerakhir

    List<ItemWorkOrder> itemWorkOrders = []

    @Bindable Pembayaran pembayaran

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

    @Bindable String informasi

}

public enum JenisJadwalSearch {
    SEMUA("Semua"), EXPRESS("Express"), NORMAL("Normal")

    String text

    public JenisJadwalSearch(String text) {
        this.text = text
    }

    @Override
    String toString() {
        text
    }
}