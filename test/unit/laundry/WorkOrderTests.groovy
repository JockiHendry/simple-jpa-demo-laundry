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

import domain.Diskon
import domain.EventPekerjaan
import domain.ItemPakaian
import domain.ItemWorkOrder
import domain.JenisWork
import domain.Pelanggan
import domain.PembayaranCash
import domain.PilihanDiskon
import domain.StatusPekerjaan
import domain.Work
import domain.WorkOrder
import griffon.test.*
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class WorkOrderTests extends GriffonUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTambahItem() {
        Pelanggan pelangganCorporate = new Pelanggan(corporate: true)
        Pelanggan pelangganOutsider = new Pelanggan()

        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'))
        w1.hargaOutsider = 22500.0
        w1.hargaCorporate = 20500.0
        Work w2 = new Work(new ItemPakaian('Kebaya Suits'), new JenisWork('Dry Cleaning'))
        w2.hargaOutsider = 19500.0
        w2.hargaCorporate = 19000.0

        WorkOrder woCorporate = new WorkOrder(pelanggan: pelangganCorporate)

        ItemWorkOrder item1 = new ItemWorkOrder(w1, w1.hargaCorporate)
        woCorporate.tambahItem(item1)
        assertTrue(woCorporate.itemWorkOrders.contains(item1))
        assertEquals(1, woCorporate.itemWorkOrders.size())

        woCorporate.tambahItem(w2)
        assertEquals(w2, woCorporate.itemWorkOrders[1].work)
        assertEquals(1, woCorporate.itemWorkOrders[1].jumlah)
        assertEquals(w2.hargaCorporate, woCorporate.itemWorkOrders[1].harga)
        assertEquals(2, woCorporate.itemWorkOrders.size())

        WorkOrder woOutsider = new WorkOrder(pelanggan: pelangganOutsider)

        item1 = new ItemWorkOrder(w1, w1.hargaOutsider)
        woOutsider.tambahItem(item1)
        assertTrue(woOutsider.itemWorkOrders.contains(item1))
        assertEquals(1, woOutsider.itemWorkOrders.size())

        woOutsider.tambahItem(w2)
        assertEquals(w2, woOutsider.itemWorkOrders[1].work)
        assertEquals(1, woOutsider.itemWorkOrders[1].jumlah)
        assertEquals(w2.hargaOutsider, woOutsider.itemWorkOrders[1].harga)
        assertEquals(2, woOutsider.itemWorkOrders.size())
    }

    void testEvents() {
        WorkOrder wo = new WorkOrder()
        DateTimeFormatter formatter = DateTimeFormat.forPattern('dd-MM-yyyy HH:mm')

        EventPekerjaan e1 = new EventPekerjaan(LocalDateTime.parse('01-11-2013 10:00', formatter), StatusPekerjaan.DITERIMA, wo)
        wo.diterima(LocalDateTime.parse('01-11-2013 10:00', formatter))
        assertEquals(1, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e1))
        assertEquals(StatusPekerjaan.DITERIMA, wo.statusTerakhir)

        EventPekerjaan e2 = new EventPekerjaan(LocalDateTime.parse('03-11-2013 08:00', formatter), StatusPekerjaan.DICUCI, wo)
        wo.dicuci(LocalDateTime.parse('03-11-2013 08:00', formatter))
        assertEquals(2, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e2))
        assertEquals(StatusPekerjaan.DICUCI, wo.statusTerakhir)

        EventPekerjaan e3 = new EventPekerjaan(LocalDateTime.parse('04-11-2013 12:00', formatter), StatusPekerjaan.DISELESAIKAN, wo)
        wo.diselesaikan(LocalDateTime.parse('04-11-2013 12:00', formatter))
        assertEquals(3, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e3))
        assertEquals(StatusPekerjaan.DISELESAIKAN, wo.statusTerakhir)

        EventPekerjaan e4 = new EventPekerjaan(LocalDateTime.parse('05-11-2013 09:00', formatter), StatusPekerjaan.DIAMBIL, wo)
        wo.diambil("the solid snake", LocalDateTime.parse('05-11-2013 09:00', formatter))
        assertEquals(4, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e4))
        assertEquals(StatusPekerjaan.DIAMBIL, wo.statusTerakhir)

        // test untuk penghapusan
        wo.hapusEvent(e2)
        assertEquals(3, wo.eventPekerjaans.size())
        assertEquals(false, wo.eventPekerjaans.contains(e2))
        assertEquals(StatusPekerjaan.DIAMBIL, wo.statusTerakhir)

        wo.hapusEvent(e4)
        assertEquals(2, wo.eventPekerjaans.size())
        assertEquals(false, wo.eventPekerjaans.contains(e4))
        assertEquals(StatusPekerjaan.DISELESAIKAN, wo.statusTerakhir)
    }

    void testTotal() {
        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'))
        w1.hargaCorporate = 22500
        w1.hargaOutsider = 21000
        Work w2 = new Work(new ItemPakaian('Kebaya Suits'), new JenisWork('Dry Cleaning'))
        w2.hargaCorporate = 19500
        w2.hargaOutsider = 18000
        Work w3 = new Work(new ItemPakaian('Long Coat'), new JenisWork('Pressing'))
        w3.hargaCorporate = 17500
        w3.hargaOutsider = 17000

        WorkOrder woCorporate = new WorkOrder(pelanggan: new Pelanggan(corporate: true))
        woCorporate.tambahItem(w1)
        woCorporate.tambahItem(w2)
        woCorporate.tambahItem(w3)
        assertEquals(59500, woCorporate.hitungSubtotal())
        assertEquals(0, woCorporate.hitungSurcharge())
        assertEquals(59500, woCorporate.total)

        WorkOrder woOutsider = new WorkOrder(pelanggan: new Pelanggan())
        woOutsider.tambahItem(w1)
        woOutsider.tambahItem(w2)
        woOutsider.tambahItem(w3)
        assertEquals(56000, woOutsider.hitungSubtotal())
        assertEquals(0, woOutsider.hitungSurcharge())
        assertEquals(56000, woOutsider.total)

        WorkOrder woCorporateExpress = new WorkOrder(pelanggan: new Pelanggan(corporate: true), express: true)
        woCorporateExpress.tambahItem(w1)
        woCorporateExpress.tambahItem(w2)
        woCorporateExpress.tambahItem(w3)
        assertEquals(59500, woCorporateExpress.hitungSubtotal())
        assertEquals(59500, woCorporateExpress.hitungSurcharge())
        assertEquals(59500 * 2, woCorporateExpress.total)
    }

    void testTotalWithDiskon() {
        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'))
        w1.hargaCorporate = 22500
        w1.hargaOutsider = 21000
        Work w2 = new Work(new ItemPakaian('Kebaya Suits'), new JenisWork('Dry Cleaning'))
        w2.hargaCorporate = 19500
        w2.hargaOutsider = 18000
        Work w3 = new Work(new ItemPakaian('Long Coat'), new JenisWork('Pressing'))
        w3.hargaCorporate = 17500
        w3.hargaOutsider = 17000

        // Diskon 10%
        WorkOrder woCorporate = new WorkOrder(pelanggan: new Pelanggan(corporate: true), diskon: new Diskon(pilihanPersen: PilihanDiskon.SEPULUH_PERSEN))
        woCorporate.tambahItem(w1)
        woCorporate.tambahItem(w2)
        woCorporate.tambahItem(w3)
        assertEquals(59500, woCorporate.hitungSubtotal())
        assertEquals(0, woCorporate.hitungSurcharge())
        assertEquals(53550, woCorporate.total)

        // Diskon Rp 5000
        WorkOrder woOutsider = new WorkOrder(pelanggan: new Pelanggan(), diskon: new Diskon(nominal: 5000))
        woOutsider.tambahItem(w1)
        woOutsider.tambahItem(w2)
        woOutsider.tambahItem(w3)
        assertEquals(56000, woOutsider.hitungSubtotal())
        assertEquals(0, woOutsider.hitungSurcharge())
        assertEquals(51000, woOutsider.total)

        // Diskon 10% + Rp 5000
        WorkOrder woCorporateExpress = new WorkOrder(pelanggan: new Pelanggan(corporate: true), express: true, diskon: new Diskon(PilihanDiskon.SEPULUH_PERSEN, 5000))
        woCorporateExpress.tambahItem(w1)
        woCorporateExpress.tambahItem(w2)
        woCorporateExpress.tambahItem(w3)
        assertEquals(59500, woCorporateExpress.hitungSubtotal())
        assertEquals(59500, woCorporateExpress.hitungSurcharge())
        assertEquals(102100, woCorporateExpress.total)

        // Setiap item diskon 5%, untuk total diskon 10%
        WorkOrder woPromo = new WorkOrder(pelanggan: new Pelanggan(), diskon: new Diskon(pilihanPersen: PilihanDiskon.SEPULUH_PERSEN))
        woPromo.tambahItem(new ItemWorkOrder(w1, w1.hargaCorporate, 2, new Diskon(pilihanPersen: PilihanDiskon.LIMA_PERSEN))) // Total: 2 * 22500 - 5% == 42750
        woPromo.tambahItem(new ItemWorkOrder(w2, w2.hargaCorporate, 1, new Diskon(pilihanPersen: PilihanDiskon.LIMA_PERSEN))) // Total: 1 * 19500 - 5% == 18525
        woPromo.tambahItem(new ItemWorkOrder(w3, w3.hargaCorporate, 3, new Diskon(pilihanPersen: PilihanDiskon.LIMA_PERSEN))) // Total: 3 * 17500 - 5% == 49875
        assertEquals(111150, woPromo.hitungSubtotal())
        assertEquals(0, woPromo.hitungSurcharge())
        assertEquals(100035, woPromo.total)
    }

    void testTotalExpress() {
        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'))
        w1.hargaCorporate = 22500
        w1.hargaOutsider = 21000
        Work w2 = new Work(new ItemPakaian('Kebaya Suits'), new JenisWork('Dry Cleaning'))
        w2.hargaCorporate = 19500
        w2.hargaOutsider = 18000
        Work w3 = new Work(new ItemPakaian('Long Coat'), new JenisWork('Pressing'))
        w3.hargaCorporate = 17500
        w3.hargaOutsider = 17000

        WorkOrder woCorporate = new WorkOrder(pelanggan: new Pelanggan(corporate: true), express: true)
        woCorporate.tambahItem(w1)
        woCorporate.tambahItem(w2)
        woCorporate.tambahItem(w3)
        assertEquals(59500 * 2, woCorporate.total)

        WorkOrder woOutsider = new WorkOrder(pelanggan: new Pelanggan(), express: true)
        woOutsider.tambahItem(w1)
        woOutsider.tambahItem(w2)
        woOutsider.tambahItem(w3)
        assertEquals(56000 * 2, woOutsider.total)
    }

}
