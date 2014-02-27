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

@DomainClass @Entity @Canonical(excludes='itemWorkOrders,eventPekerjaans')
class WorkOrder {

    @NotBlank @Size(min=12, max=12)
    String nomor

    @NotNull @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    LocalDate tanggal

    @NotNull @ManyToOne
    Pelanggan pelanggan

    @NotEmpty @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="workOrder", fetch=FetchType.EAGER)
    List<ItemWorkOrder> itemWorkOrders = []

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy="workOrder")
    List<EventPekerjaan> eventPekerjaans = []

    @NotNull @Enumerated
    StatusPekerjaan statusTerakhir

    @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    Pembayaran pembayaran

    @Embedded
    Diskon diskon

    BigDecimal jumlahDiskon

    String keterangan

    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    LocalDate estimasiSelesai

    @NotNull
    Boolean express = Boolean.FALSE

    String namaPenerima

    BigDecimal total

    void tambahItem(Work work) {
        tambahItem(work, pelanggan.corporate? work.hargaCorporate: work.hargaOutsider)
    }

    void tambahItem(Work work, BigDecimal harga) {
        tambahItem(new ItemWorkOrder(work, harga, 1))
    }

    void tambahItem(ItemWorkOrder itemWorkOrder) {
        itemWorkOrder.workOrder = this
        itemWorkOrders << itemWorkOrder
    }

    void tambahEvent(LocalDateTime tanggal, StatusPekerjaan status) {
        tambahEvent(new EventPekerjaan(tanggal, status))
    }

    void tambahEvent(EventPekerjaan eventPekerjaan) {
        eventPekerjaan.workOrder = this
        eventPekerjaans << eventPekerjaan
        statusTerakhir = eventPekerjaan.status
    }

    void hapusEvent(EventPekerjaan eventPekerjaan) {
        eventPekerjaans.remove(eventPekerjaan)
        if (eventPekerjaans.isEmpty()) {
            statusTerakhir = null
        } else {
            statusTerakhir = eventPekerjaans.last().status
        }
    }

    void diterima(LocalDateTime tanggal = LocalDateTime.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DITERIMA)
    }

    void dicuci(LocalDateTime tanggal = LocalDateTime.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DICUCI)
    }

    void diselesaikan(LocalDateTime tanggal = LocalDateTime.now()) {
        tambahEvent(tanggal, StatusPekerjaan.DISELESAIKAN)
    }

    void diambil(String namaPenerima, LocalDateTime tanggal = LocalDateTime.now()) {
        if (namaPenerima && namaPenerima.trim().length() > 0) {
            this.namaPenerima = namaPenerima
        }
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

    BigDecimal getTotal() {
        BigDecimal total = hitungSubtotal() + hitungSurcharge()
        this.total = diskon ? diskon.hasilDiskon(total): total
    }

    BigDecimal getJumlahDiskon() {
        BigDecimal total = hitungSubtotal() + hitungSurcharge()
        this.jumlahDiskon = diskon ? diskon.jumlahDiskon(total): 0
    }

    LocalDate getEstimasiSelesaiSementara() {
        LocalDate hasil
        if (estimasiSelesai) {
            hasil = estimasiSelesai
        } else {
            if (express) {
                hasil = LocalDate.now()
            } else {
                hasil = LocalDate.now().plusDays(3)
            }
        }
        return hasil
    }

    EventPekerjaan getEvent(StatusPekerjaan statusPekerjaan) {
        eventPekerjaans.find { it.status == statusPekerjaan}
    }

    BigDecimal hitungSubtotal() {
        itemWorkOrders.sum { it.total() }
    }

    BigDecimal hitungSurcharge() {
        if (express==Boolean.TRUE) {
            return hitungSubtotal()
        } else {
            return 0
        }
    }

    Integer jumlahPakaian() {
        itemWorkOrders.sum { ItemWorkOrder i -> i.jumlah }
    }

    BigDecimal hitungDiskon() {
        diskon?.jumlahDiskon(hitungSubtotal() + hitungSurcharge())?:0
    }

}

