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

import domain.StatusPekerjaan
import domain.WorkOrder
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction

import javax.swing.JOptionPane
import javax.swing.event.ListSelectionEvent

@Transaction
class PencucianController {
    PencucianModel model
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

        execInsideUISync { model.workOrderList.addAll(result) }
    }

    def prosesSelesaiCuci = {

        if (model.statusTerakhir != StatusPekerjaan.DICUCI) {
            JOptionPane.showMessageDialog(view.mainPanel, "Tidak dapat mengubah work order ini karena statusnya tidak memungkinkan untuk diproses!",
                    "Update Tidak Diperbolehkan", JOptionPane.ERROR_MESSAGE)
            return
        }

        // validasi
        WorkOrder workOrder = merge(view.table.selectionModel.selected[0])
        if (model.tanggal.isBefore(workOrder.getEvent(StatusPekerjaan.DICUCI).tanggal)) {
            model.errors['tanggal'] = 'Tanggal selesai dkerjakan harus setelah tanggal mulai dikerjakan'
            return
        }

        // konfirmasi
        if (JOptionPane.showConfirmDialog(view.mainPanel, "Apakah Anda yakin order ini telah selesai dicuci?",
                'Konfirmasi Selesai Dicuci', JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return
        }

        if (model.keterangan && !model.keterangan.isEmpty()) {
            workOrder.keterangan = model.keterangan
        }
        workOrder.diselesaikan(model.tanggal)
        execInsideUISync {
            view.table.selectionModel.selected[0] = workOrder
            model.statusTerakhir = workOrder.statusTerakhir
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.keterangan = null
            model.tanggal = null
            model.statusTerakhir = null
            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def tableSelectionChanged = { ListSelectionEvent event ->
        execInsideUISync {
            if (view.table.selectionModel.isSelectionEmpty()) {
                clear()
            } else {
                WorkOrder selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.keterangan = selected.keterangan
                model.statusTerakhir = selected.statusTerakhir
            }
        }
    }
}
