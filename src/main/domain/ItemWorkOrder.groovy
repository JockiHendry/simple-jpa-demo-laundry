/*
 * Copyright 2013 Jocki Hendry.
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
import validation.Pengisian

import javax.persistence.*
import org.hibernate.annotations.Type
import javax.validation.constraints.*
import org.hibernate.validator.constraints.*
import org.joda.time.*

import javax.validation.groups.Default

@NamedQueries([
    @NamedQuery(name='ItemWorkOrder.LaporanBulanan', query='''
        SELECT DAY(wo.tanggal) AS tanggal, i.jumlah, i.work.itemPakaian.nama AS nama
        FROM WorkOrder wo JOIN wo.itemWorkOrders i
        WHERE wo.tanggal BETWEEN :tanggalMulaiCari AND :tanggalSelesaiCari
        ORDER BY tanggal, nama
    '''),
    @NamedQuery(name='ItemWorkOrder.LaporanPemasukan', query='''
        SELECT  i.work.itemPakaian.nama AS nama, SUM(i.harga *  i.jumlah) AS total
        FROM WorkOrder wo JOIN wo.itemWorkOrders i
        WHERE wo.tanggal BETWEEN :tanggalMulaiCari AND :tanggalSelesaiCari
        GROUP BY nama
        ORDER BY nama
    '''),
    @NamedQuery(name='ItemWorkOrder.LaporanPelanggan', query='''
        SELECT wo.pelanggan.nama AS pelanggan, i.jumlah, i.work.itemPakaian.nama AS nama
        FROM WorkOrder wo JOIN wo.itemWorkOrders i
        WHERE wo.tanggal BETWEEN :tanggalMulaiCari AND :tanggalSelesaiCari
        ORDER BY tanggal, nama
    ''')
])
@DomainClass @Entity @Canonical
class ItemWorkOrder {

    @NotNull(groups=[Pengisian, Default]) @ManyToOne
    Work work

    @NotNull(groups=[Pengisian, Default])
    BigDecimal harga

    @NotNull(groups=[Pengisian, Default]) @Min(0l)
    Integer jumlah

    String keterangan

    @NotNull @ManyToOne
    WorkOrder workOrder

    BigDecimal total() {
        jumlah * harga
    }
}


