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

package domain

import groovy.transform.Canonical
import org.hibernate.validator.constraints.NotBlank
import simplejpa.DomainClass

import javax.persistence.Entity
import javax.validation.constraints.Size

@DomainClass @Entity @Canonical
class PembayaranKartuKredit extends Pembayaran {

    @NotBlank @Size(min=2, max=50)
    String nomorKartu

    @Override
    BigDecimal total() {
        super.tagihan
    }

    @Override
    Boolean isLunas() {
        true
    }

    @Override
    String getNamaDeskripsi() {
        'Kartu Kredit'
    }
}

