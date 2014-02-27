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

import ca.odell.glazedlists.BasicEventList
import domain.StatusPekerjaan
import domain.WorkOrder
import groovy.beans.Bindable
import org.jdesktop.swingx.combobox.EnumComboBoxModel
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class PengambilanModel {

    @Bindable String nomorSearch
    @Bindable String pelangganSearch
    @Bindable String searchMessage
    @Bindable LocalDate tanggalMulaiSearch
    @Bindable LocalDate tanggalSelesaiSearch
    EnumComboBoxModel<JenisJadwalSearch> jenisJadwalSearch = new EnumComboBoxModel<>(JenisJadwalSearch.class)
    EnumComboBoxModel<StatusPekerjaanPengambilanSearch> statusPekerjaanSearch = new EnumComboBoxModel<>(StatusPekerjaanPengambilanSearch.class)

    @Bindable LocalDateTime tanggal
    @Bindable String namaPenerima
    @Bindable String keterangan

    @Bindable Boolean adaSisaPembayaran
    @Bindable String jumlahDibayarDimuka
    @Bindable String sisa

    BasicEventList<WorkOrder> workOrderList = new BasicEventList<>()

}

enum StatusPekerjaanPengambilanSearch {
    SEMUA("Semua"), DITERIMA("Diterima"), DICUCI("Sedang Dikerjakan"), DISELESAIKAN("Selesai"), DIAMBIL("Diambil")

    String description

    StatusPekerjaanPengambilanSearch(String description) {
        this.description = description
    }

    StatusPekerjaan convertToStatusPekerjaan() {
        if (this == StatusPekerjaanPengambilanSearch.DITERIMA) {
            return StatusPekerjaan.DITERIMA
        } else if (this == StatusPekerjaanPengambilanSearch.DICUCI) {
            return StatusPekerjaan.DICUCI
        } else if (this == StatusPekerjaanPengambilanSearch.DISELESAIKAN) {
            return StatusPekerjaan.DISELESAIKAN
        } else if (this == StatusPekerjaanPengambilanSearch.DIAMBIL) {
            return StatusPekerjaan.DIAMBIL
        }
        return null
    }

    @Override
    String toString() {
        description
    }
}