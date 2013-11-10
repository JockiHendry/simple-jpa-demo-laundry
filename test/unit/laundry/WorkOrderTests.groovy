package laundry

import domain.EventPekerjaan
import domain.ItemPakaian
import domain.ItemWorkOrder
import domain.JenisWork
import domain.PembayaranCash
import domain.StatusPekerjaan
import domain.Work
import domain.WorkOrder
import griffon.test.*
import org.joda.time.LocalDate
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
        WorkOrder wo = new WorkOrder()
        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'), 22500)
        Work w2 = new Work(new ItemPakaian('Kebaya Suits'), new JenisWork('Dry Cleaning'), 19500)

        ItemWorkOrder item1 = new ItemWorkOrder(w1, w1.harga)
        wo.tambahItem(item1)
        assertEquals(true, wo.itemWorkOrders.contains(item1))
        assertEquals(1, wo.itemWorkOrders.size())

        ItemWorkOrder item2 = new ItemWorkOrder(w2, w2.harga)
        wo.tambahItem(w2, w2.harga)
        assertEquals(true, wo.itemWorkOrders.contains(item1))
        assertEquals(2, wo.itemWorkOrders.size())
    }

    void testEvents() {
        WorkOrder wo = new WorkOrder()
        DateTimeFormatter formatter = DateTimeFormat.forPattern('dd-MM-yyyy')

        EventPekerjaan e1 = new EventPekerjaan(LocalDate.parse('01-11-2013', formatter), StatusPekerjaan.DITERIMA, wo)
        wo.diterima(LocalDate.parse('01-11-2013', formatter))
        assertEquals(1, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e1))

        EventPekerjaan e2 = new EventPekerjaan(LocalDate.parse('03-11-2013', formatter), StatusPekerjaan.DICUCI, wo)
        wo.dicuci(LocalDate.parse('03-11-2013', formatter))
        assertEquals(2, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e2))

        EventPekerjaan e3 = new EventPekerjaan(LocalDate.parse('04-11-2013', formatter), StatusPekerjaan.DISELESAIKAN, wo)
        wo.diselesaikan(LocalDate.parse('04-11-2013', formatter))
        assertEquals(3, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e3))

        EventPekerjaan e4 = new EventPekerjaan(LocalDate.parse('05-11-2013', formatter), StatusPekerjaan.DIAMBIL, wo)
        wo.bayar(new PembayaranCash(tanggal: LocalDate.parse('05-11-2013', formatter)))
        assertEquals(4, wo.eventPekerjaans.size())
        assertEquals(true, wo.eventPekerjaans.contains(e4))
        assertEquals(wo.total(), wo.pembayaran.total())
    }

    void testTotal() {
        WorkOrder wo = new WorkOrder()
        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'), 22500)
        Work w2 = new Work(new ItemPakaian('Kebaya Suits'), new JenisWork('Dry Cleaning'), 19500)
        Work w3 = new Work(new ItemPakaian('Long Coat'), new JenisWork('Pressing'), 17500)
        wo.tambahItem(w1)
        wo.tambahItem(w2)
        wo.tambahItem(w3)
        assertEquals(59500, wo.total())
    }

}
