package laundry

import domain.Diskon
import domain.ItemPakaian
import domain.ItemWorkOrder
import domain.JenisWork
import domain.PilihanDiskon
import domain.Work
import griffon.test.*

class ItemWorkOrderTests extends GriffonUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testTotal() {
        Work w1 = new Work(new ItemPakaian('Long Coat'),  new JenisWork('Laundry'))
        w1.hargaOutsider = 22500.0
        w1.hargaCorporate = 20500.0

        ItemWorkOrder item

        // Test diskon 10%
        item = new ItemWorkOrder(w1, w1.hargaOutsider, 5, new Diskon(pilihanPersen: PilihanDiskon.SEPULUH_PERSEN), null, null)
        assertEquals(101250, item.total())

        // Test diskon Rp 2000
        item = new ItemWorkOrder(w1, w1.hargaOutsider, 5, new Diskon(nominal: 2000), null, null)
        assertEquals(102500, item.total())

        // Test diskon 10% + Rp 2000
        item = new ItemWorkOrder(w1, w1.hargaOutsider, 5, new Diskon(PilihanDiskon.SEPULUH_PERSEN, 2000), null, null)
        assertEquals(91250, item.total())
    }
}
