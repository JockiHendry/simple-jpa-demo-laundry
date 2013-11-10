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
import javax.persistence.*
import org.hibernate.annotations.Type
import javax.validation.constraints.*
import org.hibernate.validator.constraints.*
import org.joda.time.*

@DomainClass @Entity @Canonical(excludes='itemWorkOrders,eventPekerjaans')
class WorkOrder {

    @NotBlank @Size(min=12, max=12)
    String nomor

    @NotNull @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    LocalDate tanggal

    @NotNull @ManyToOne
    Pelanggan pelanggan

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="workOrder")
    List<ItemWorkOrder> itemWorkOrders = []

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="workOrder")
    List<EventPekerjaan> eventPekerjaans = []

    @NotNull @Enumerated
    StatusPekerjaan statusTerakhir

    @OneToOne
    Pembayaran pembayaran

    void tambahItem(Work work) {
        tambahItem(work, work.harga)
    }

    void tambahItem(Work work, BigDecimal harga) {
        tambahItem(new ItemWorkOrder(work, harga))
    }

    void tambahItem(ItemWorkOrder itemWorkOrder) {
        itemWorkOrder.workOrder = this
        itemWorkOrders << itemWorkOrder
    }

    void tambahEvent(LocalDate tanggal, StatusPekerjaan status) {
        tambahEvent(new EventPekerjaan(tanggal, status))
    }

    void tambahEvent(EventPekerjaan eventPekerjaan) {
        eventPekerjaan.workOrder = this
        eventPekerjaans << eventPekerjaan
    }

    void diterima(LocalDate tanggal = LocalDate.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DITERIMA)
    }

    void dicuci(LocalDate tanggal = LocalDate.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DICUCI)
    }

    void diselesaikan(LocalDate tanggal = LocalDate.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DISELESAIKAN)
    }

    void diambil(LocalDate tanggal = LocalDate.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DIAMBIL)
    }

    void bayar(Pembayaran pembayaran) {
        this.pembayaran = pembayaran
        this.pembayaran.tagihan = total()
        diambil(this.pembayaran.tanggal)
    }

    boolean isSelesai() {
        statusTerakhir == StatusPekerjaan.DISELESAIKAN
    }

    BigDecimal total() {
        itemWorkOrders.sum { it.harga }
    }

}

