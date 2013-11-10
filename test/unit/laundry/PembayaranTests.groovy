package laundry

import domain.Pembayaran
import domain.PembayaranCash
import domain.PembayaranCompliant
import domain.PembayaranKartuDebit
import domain.PembayaranSignedBill
import griffon.test.*

class PembayaranTests extends GriffonUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testPembayaranCash() {
        Pembayaran pembayaranCash = new PembayaranCash()
        pembayaranCash.tagihan = 150000
        assertEquals(150000, pembayaranCash.total())
    }

    void testPembayaranSignedBill() {
        Pembayaran pembayaranSignedBill1 = new PembayaranSignedBill()
        pembayaranSignedBill1.tagihan = 150000
        assertEquals(150000, pembayaranSignedBill1.total())
        pembayaranSignedBill1.jumlahBayarDimuka = 50000
        assertEquals(100000, pembayaranSignedBill1.total())
    }

    void testPembayaranKartuDebit() {
        Pembayaran pembayaranKartuDebit = new PembayaranKartuDebit()
        pembayaranKartuDebit.tagihan = 150000
        assertEquals(150000, pembayaranKartuDebit.total())
    }

    void testPembayaranCompliant() {
        Pembayaran pembayaranCompliant = new PembayaranCompliant()
        pembayaranCompliant.tagihan = 150000
        assertEquals(0, pembayaranCompliant.total())
    }
}
