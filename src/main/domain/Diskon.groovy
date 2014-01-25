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

import java.text.NumberFormat

@Embeddable @Canonical
class Diskon implements Comparable {

    @Enumerated
    PilihanDiskon pilihanPersen = PilihanDiskon.NOL_PERSEN;

    @Min(0l)
    BigDecimal nominal = 0;

    BigDecimal hasilDiskon(BigDecimal nilai) {
        BigDecimal hasil = nilai
        if (pilihanPersen.persen > 0) {
            hasil -= ((pilihanPersen.persen/100) * nilai)
        }
        if (nominal) {
            hasil -= nominal
        }
        hasil
    }

    BigDecimal jumlahDiskon(BigDecimal nilai) {
        BigDecimal hasil = 0
        if (pilihanPersen.persen > 0) {
            hasil += (pilihanPersen.persen/100) * nilai
        }
        if (nominal) {
            hasil += nominal
        }
        hasil
    }

    String toString() {
        List result = []
        if (pilihanPersen.persen > 0) {
            result << "${NumberFormat.numberInstance.format(pilihanPersen.persen.doubleValue())} %"
        }
        if (nominal) {
            result << NumberFormat.numberInstance.format(nominal.doubleValue())
        }
        result.join(' + ')
    }

    int compareTo(Object o) {
        if (!o) return -1
        if (!o instanceof Diskon) return -1
        (pilihanPersen.persen - o.pilihanPersen.persen)
    }
}

public enum PilihanDiskon {
    NOL_PERSEN(0),
    LIMA_PERSEN(5),
    SEPULUH_PERSEN(10),
    LIMA_BELAS_PERSEN(15),
    DUA_PULUH_PERSEN(20),
    DUA_PULUH_LIMA_PERSEN(25)

    BigDecimal persen

    public PilihanDiskon(BigDecimal persen) {
        this.persen = persen
    }

    @Override
    String toString() {
        if (persen==0) {
            return "-"
        } else {
            return NumberFormat.instance.format(persen)
        }
    }
}

