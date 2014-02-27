/*
 * Copyright 2014 Jocki Hendry.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    @Bindable boolean pembayaranKartuKredit
    @Bindable boolean pembayaranCompliant
    @Bindable String keteranganPembayaran
    @Bindable BigDecimal jumlahBayarDimuka
    @Bindable String nomorKartuDebit
    @Bindable String nomorKartuKredit
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