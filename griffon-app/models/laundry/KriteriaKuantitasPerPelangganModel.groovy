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