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

import groovy.transform.*
import simplejpa.DomainClass
import javax.persistence.*
import org.hibernate.annotations.Type
import javax.validation.constraints.*
import org.hibernate.validator.constraints.*
import org.joda.time.*

@DomainClass @Entity @Canonical(excludes = 'hargaOutsider,hargaCorporate')
class Work implements Comparable {

    @NotNull @ManyToOne
    ItemPakaian itemPakaian

    @NotNull @ManyToOne
    JenisWork jenisWork

    @NotNull
    BigDecimal hargaOutsider

    @NotNull
    BigDecimal hargaCorporate

    BigDecimal getHarga(Pelanggan pelanggan) {
        if (pelanggan==null || pelanggan.corporate) {
            return hargaCorporate
        } else {
            return hargaOutsider
        }
    }

    @Override
    int compareTo(Object o) {
        if (!o instanceof Work) return -1
        if (!itemPakaian && !o.itemPakaian) {
            return 0
        } else if (itemPakaian && !o.itemPakaian) {
            return 1
        } else {
            return itemPakaian.compareTo(o.itemPakaian)
        }
    }

    @Override
    String toString() {
        "${jenisWork.nama} - ${itemPakaian.nama}"
    }
}

