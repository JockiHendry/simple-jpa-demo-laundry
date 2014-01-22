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

package util

import domain.ItemPakaian
import domain.ItemWorkOrder
import domain.JenisWork
import domain.Kategori
import domain.Pelanggan
import domain.PembayaranCash
import domain.PembayaranSignedBill
import domain.StatusPekerjaan
import domain.Work
import domain.WorkOrder
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class ReportTest {

    static List getDataWorkOrder() {
        Pelanggan pelanggan = new Pelanggan("Edi Yi Wei", "Jl. Sungai Raya Dalam", "0813524455", false)

        ItemPakaian itemPakaian1 = new ItemPakaian(nama: "Bath Towel", kategori: new Kategori("Linen - Cloth"))
        ItemPakaian itemPakaian2 = new ItemPakaian(nama: "Napkin", kategori: new Kategori("F & B Linen"))
        ItemPakaian itemPakaian3 = new ItemPakaian(nama: "Pijamas", kategori: new Kategori("Gentlemen"))
        ItemPakaian itemPakaian4 = new ItemPakaian(nama: "Skirts", kategori: new Kategori("Ladies"))

        JenisWork laundry = new JenisWork("Laundry")
        JenisWork dryCleaning = new JenisWork("Dry Cleaning")
        JenisWork pressing = new JenisWork("Pressing")

        Work work1 = new Work(itemPakaian: itemPakaian1, jenisWork: laundry, hargaOutsider: 4500.0, hargaCorporate: 4300.0)
        Work work2 = new Work(itemPakaian: itemPakaian2, jenisWork: laundry, hargaOutsider: 2200.0, hargaCorporate: 2100.0)
        Work work3 = new Work(itemPakaian: itemPakaian3, jenisWork: dryCleaning, hargaOutsider: 5000.0, hargaCorporate: 4000.0)
        Work work4 = new Work(itemPakaian: itemPakaian4, jenisWork: pressing, hargaOutsider: 1000.0, hargaCorporate: 1500.0)

        WorkOrder workOrderBiasa = new WorkOrder(nomor: "2014/WO/0001", tanggal: LocalDate.now().minusDays(5), pelanggan: pelanggan)
        workOrderBiasa.tambahItem(new ItemWorkOrder(work1, work1.getHarga(pelanggan), 5))
        workOrderBiasa.tambahItem(new ItemWorkOrder(work2, work2.getHarga(pelanggan), 3))
        workOrderBiasa.tambahItem(new ItemWorkOrder(work3, work3.getHarga(pelanggan), 2))
        workOrderBiasa.tambahItem(new ItemWorkOrder(work4, work4.getHarga(pelanggan), 1))
        workOrderBiasa.pembayaran = new PembayaranCash(tanggal: LocalDate.now().minusDays(5), tagihan: workOrderBiasa.total)
        workOrderBiasa.diterima(LocalDateTime.now().minusDays(5))

        WorkOrder workOrderSignedBill = new WorkOrder(nomor: "2014/WO/0002", tanggal: LocalDate.now().minusDays(5), pelanggan: pelanggan)
        workOrderSignedBill.tambahItem(new ItemWorkOrder(work1, work1.getHarga(pelanggan), 5))
        workOrderSignedBill.tambahItem(new ItemWorkOrder(work2, work2.getHarga(pelanggan), 3))
        workOrderSignedBill.tambahItem(new ItemWorkOrder(work3, work3.getHarga(pelanggan), 2))
        workOrderSignedBill.tambahItem(new ItemWorkOrder(work4, work4.getHarga(pelanggan), 1))
        workOrderSignedBill.pembayaran = new PembayaranSignedBill(tanggal: LocalDate.now().minusDays(5),
            tagihan: workOrderSignedBill.total, jumlahBayarDimuka: 3000)
        workOrderSignedBill.diterima(LocalDateTime.now().minusDays(5))

        WorkOrder workOrderExpress = new WorkOrder(nomor: "2014/WO/0003", tanggal: LocalDate.now().minusDays(5), pelanggan: pelanggan)
        workOrderExpress.tambahItem(new ItemWorkOrder(work1, work1.getHarga(pelanggan), 5))
        workOrderExpress.tambahItem(new ItemWorkOrder(work2, work2.getHarga(pelanggan), 3))
        workOrderExpress.tambahItem(new ItemWorkOrder(work3, work3.getHarga(pelanggan), 2))
        workOrderExpress.tambahItem(new ItemWorkOrder(work4, work4.getHarga(pelanggan), 1))
        workOrderExpress.express = true
        workOrderExpress.pembayaran = new PembayaranCash(tanggal: LocalDate.now().minusDays(5), tagihan: workOrderExpress.total)
        workOrderExpress.diterima(LocalDateTime.now().minusDays(5))

        [workOrderBiasa, workOrderExpress, workOrderSignedBill]
    }
}
