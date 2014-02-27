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
