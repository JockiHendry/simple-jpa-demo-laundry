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

package project

import domain.*
import org.joda.time.LocalDate
import simplejpa.swing.DialogUtils
import simplejpa.transaction.Transaction

import javax.persistence.FlushModeType
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import java.awt.Dimension
import java.text.NumberFormat

@Transaction
class WorkOrderController {

    WorkOrderModel model
    def view

    void mvcGroupInit(Map args) {
        listAll()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(newSession = true)
    def listAll = {
        execInsideUISync {
            model.workOrderList.clear()
        }

        model.tanggalMulaiSearch = LocalDate.now().minusMonths(1)
        model.tanggalSelesaiSearch = LocalDate.now()
        List workOrderResult = findAllWorkOrderByTanggalBetweenAndStatusTerakhirNe(model.tanggalMulaiSearch,
            model.tanggalSelesaiSearch, StatusPekerjaan.DIAMBIL, [orderBy: 'nomor,tanggal'])
        buatNomorWO()
        refreshInformasi()

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            model.nomorSearch = null
            model.pelangganSearch = null
            view.jenisJadwalSearch.selectedItem = JenisJadwalSearch.SEMUA
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.workOrderList.clear() }
        List result = findAllWorkOrderByDsl([orderBy: 'nomor,tanggal']) {
            tanggal between(model.tanggalMulaiSearch, model.tanggalSelesaiSearch)
            and()
            nomor like("%${model.nomorSearch?:''}%")
            and()
            pelanggan__nama like("%${model.pelangganSearch?:''}%")
            and()
            statusTerakhir ne(StatusPekerjaan.DIAMBIL)
            if (model.jenisJadwalSearch.selectedItem && model.jenisJadwalSearch.selectedItem!=JenisJadwalSearch.SEMUA) {
                and()
                express eq(model.jenisJadwalSearch.selectedItem==JenisJadwalSearch.EXPRESS)
            }
        }
        refreshInformasi()
        execInsideUISync {
            model.workOrderList.addAll(result)
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def refreshInformasi = {
        def jumlahItem = model.itemWorkOrders.sum { it.jumlah }?: 0
        def total = model.itemWorkOrders.sum { it.total() }?: 0
        if (model.express) {
            model.informasi = "Jumlah Pakaian ${jumlahItem}   Subtotal ${NumberFormat.currencyInstance.format(total)}    Total (Express) ${NumberFormat.currencyInstance.format(total*2)}"
        } else {
            model.informasi = "Jumlah Pakaian ${jumlahItem}   Total ${NumberFormat.currencyInstance.format(total)}"
        }
    }


    def save = {
        WorkOrder workOrder = new WorkOrder('nomor': model.nomor, 'tanggal': model.tanggal, 'pelanggan': model.selectedPelanggan,
            'itemWorkOrders': new ArrayList(model.itemWorkOrders), 'statusTerakhir': model.statusTerakhir, 'keterangan': model.keterangan,
            'express': model.express)
        if ((model.pilihanPersen.selectedItem && model.pilihanPersen.selectedItem.persen > 0) || model.diskonNominal > 0) {
            Diskon diskon = new Diskon(model.pilihanPersen.selectedItem, model.diskonNominal)
            workOrder.diskon = diskon
        }
        workOrder.itemWorkOrders.each { ItemWorkOrder itemWorkOrder ->
            itemWorkOrder.workOrder = workOrder
        }
        if (!workOrder.statusTerakhir) workOrder.diterima()

        Pembayaran pembayaran
        if (model.pembayaranCash) {
            pembayaran = new PembayaranCash(tanggal: workOrder.tanggal, tagihan: workOrder.total, keterangan: model.keteranganPembayaran)
        } else if (model.pembayaranSignedBill) {
            if (model.jumlahBayarDimuka == null) {
                JOptionPane.showMessageDialog(view.mainPanel, 'Jumlah downpayment (pembayaran di muka) wajib di-isi',
                    'Kesalahan Validasi', JOptionPane.ERROR_MESSAGE)
                return
            }
            if (model.jumlahBayarDimuka >= workOrder.total) {
                JOptionPane.showMessageDialog(view.mainPanel, 'Jumlah downpayment (pembayaran di muka) harus kurang dari harga total',
                    'Kesalahan Validasi', JOptionPane.ERROR_MESSAGE)
                return
            }
            pembayaran = new PembayaranSignedBill(tanggal: workOrder.tanggal, tagihan: workOrder.total,
                keterangan: model.keteranganPembayaran, jumlahBayarDimuka: model.jumlahBayarDimuka)
        } else if (model.pembayaranKartuDebit) {
            if (model.nomorKartuDebit == null || model.nomorKartuDebit.isEmpty()) {
                JOptionPane.showMessageDialog(view.mainPanel, 'Nomor kartu debit wajib di-isi', 'Kesalahan Validasi', JOptionPane.ERROR_MESSAGE)
                return
            }
            pembayaran = new PembayaranKartuDebit(tanggal: workOrder.tanggal, tagihan: workOrder.total,
                keterangan: model.keteranganPembayaran, nomorKartu: model.nomorKartuDebit)
        } else if (model.pembayaranKartuKredit) {
            if (model.nomorKartuKredit == null || model.nomorKartuKredit.isEmpty()) {
                JOptionPane.showMessageDialog(view.mainPanel, 'Nomor kartu kredit wajib di-isi', 'Kesalahan Validasi', JOptionPane.ERROR_MESSAGE)
                return
            }
            pembayaran = new PembayaranKartuKredit(tanggal: workOrder.tanggal, tagihan: workOrder.total,
                keterangan: model.keteranganPembayaran, nomorKartu: model.nomorKartuKredit)
        } else if (model.pembayaranCompliant) {
            pembayaran = new PembayaranCompliant(tanggal: workOrder.tanggal, tagihan: workOrder.total, keterangan: model.keteranganPembayaran)
        }
        if (!pembayaran) {
            JOptionPane.showMessageDialog(view.mainPanel, 'Anda harus memilih salah satu metode pembayaran.', 'Kesalahan Validasi',
                JOptionPane.ERROR_MESSAGE)
            return
        }
        model.pembayaran = pembayaran
        workOrder.pembayaran = pembayaran

        if (workOrder.tanggal.isAfter(LocalDate.now())) {
            model.errors['tanggal'] = 'Tanggal tidak boleh di masa depan (lebih dari hari ini)'
            return
        }
        if (!validate(workOrder)) return

        if (model.id == null) {
            // Insert operation
            if (findWorkOrderByNomor(workOrder.nomor)) {
                model.errors['nomor'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }

            // Konfirmasi
            if (JOptionPane.showConfirmDialog(view.mainPanel, "Total order adalah ${NumberFormat.currencyInstance.format(workOrder.total)}.\n" +
                    "Anda yakin akan menyimpan order ini?", "Konfirmasi Simpan", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return
            }

            pembayaran.workOrder = workOrder
            persist(workOrder)

            execInsideUIAsync {
                DialogUtils.showMVCGroup('previewFaktur', [source: workOrder, fileReport: 'bukti_terima'], app, view,
                        [title: 'Preview Bukti Penerimaan', size: new Dimension(840,600)])
            }

            execInsideUISync {
                model.workOrderList << workOrder
                view.table.changeSelection(model.workOrderList.size() - 1, 0, false, false)
                clear()
            }

        } else {
            // Update operation
            WorkOrder selectedWorkOrder = view.table.selectionModel.selected[0]
            selectedWorkOrder.nomor = model.nomor
            selectedWorkOrder.tanggal = model.tanggal
            selectedWorkOrder.pelanggan = model.selectedPelanggan
            selectedWorkOrder.keterangan = model.keterangan
            selectedWorkOrder.express = model.express
            selectedWorkOrder.diskon = workOrder.diskon
            selectedWorkOrder.itemWorkOrders.clear()
            selectedWorkOrder.itemWorkOrders.addAll(model.itemWorkOrders)
            selectedWorkOrder.itemWorkOrders.each { ItemWorkOrder itemWorkOrder ->
                itemWorkOrder.workOrder = selectedWorkOrder
            }
            selectedWorkOrder = merge(selectedWorkOrder)
            if (selectedWorkOrder.pembayaran.id!=model.pembayaran.id) {
                if (selectedWorkOrder.pembayaran) remove(selectedWorkOrder.pembayaran)
                model.pembayaran.workOrder = selectedWorkOrder
                persist(model.pembayaran)
                selectedWorkOrder.pembayaran = model.pembayaran
            }

            execInsideUISync {
                view.table.selectionModel.selected[0] = selectedWorkOrder
                clear()
            }
        }
    }

    def delete = {
        WorkOrder workOrder = view.table.selectionModel.selected[0]
        Pembayaran pembayaran = workOrder.pembayaran
        remove(workOrder)
        remove(pembayaran)
        execInsideUISync {
            model.workOrderList.remove(workOrder)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.nomor = null
            model.tanggal = null
            model.selectedPelanggan = null
            model.keterangan = null
            model.express = false
            model.statusTerakhir = null
            model.itemWorkOrders.clear()
            model.pembayaran = null
            model.keteranganPembayaran = null
            model.jumlahBayarDimuka = null
            model.pembayaranCash = true
            model.pembayaranSignedBill = false
            model.pembayaranKartuDebit = false
            model.pembayaranKartuKredit = false
            model.pembayaranCompliant = false
            model.nomorKartuDebit = null
            model.nomorKartuKredit = null
            model.pilihanPersen.selectedItem = null
            model.diskonNominal = null
            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
        buatNomorWO()
        refreshInformasi()
    }

    @Transaction(Transaction.Policy.SKIP)
    def tableSelectionChanged = { ListSelectionEvent event ->
        execInsideUISync {
            if (view.table.selectionModel.isSelectionEmpty()) {
                clear()
            } else {
                WorkOrder selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.nomor = selected.nomor
                model.tanggal = selected.tanggal
                model.selectedPelanggan = selected.pelanggan
                model.statusTerakhir = selected.statusTerakhir
                model.keterangan = selected.keterangan
                model.express = selected.express
                model.itemWorkOrders.clear()
                model.itemWorkOrders.addAll(selected.itemWorkOrders)
                refreshInformasi()
                model.pembayaran = selected.pembayaran
                model.pembayaranCash = model.pembayaran instanceof PembayaranCash
                model.pembayaranSignedBill = model.pembayaran instanceof PembayaranSignedBill
                model.pembayaranKartuDebit = model.pembayaran instanceof PembayaranKartuDebit
                model.pembayaranKartuKredit = model.pembayaran instanceof PembayaranKartuKredit
                model.pembayaranCompliant = model.pembayaran instanceof PembayaranCompliant
                if (model.pembayaranSignedBill) {
                    model.jumlahBayarDimuka = selected.pembayaran.jumlahBayarDimuka
                }
                if (model.pembayaranKartuDebit) {
                    model.nomorKartuDebit = selected.pembayaran.nomorKartu
                }
                if (model.pembayaranKartuKredit) {
                    model.nomorKartuKredit = selected.pembayaran.nomorKartu
                }
                model.pilihanPersen.selectedItem = selected.diskon?.pilihanPersen
                model.diskonNominal = selected.diskon?.nominal
                model.keteranganPembayaran = selected.pembayaran?.keterangan
            }
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def buatNomorWO() {
        //List result = executeNativeQuery('SELECT nomor FROM workorder ORDER BY nomor DESC LIMIT 1')
        List result = executeQuery('SELECT w.nomor FROM WorkOrder w ORDER BY w.nomor DESC', [pageSize: 1, flushMode: FlushModeType.COMMIT])
        long nomor = 1
        if (!result.isEmpty()) {
            try {
                nomor = Integer.valueOf(result[0].substring(6))
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(view.mainPanel, "Nomor order tidak sesuai standar: ${result[0]}",
                    'Kesalahan Nomor Order', JOptionPane.ERROR_MESSAGE)
            }
            nomor++
        }
        model.nomor = String.format('W%04d-%06d', LocalDate.now().year, nomor)
    }

}