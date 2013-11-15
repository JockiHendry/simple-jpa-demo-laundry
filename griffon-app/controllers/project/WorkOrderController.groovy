package project

import domain.*
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent
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
        List workOrderResult = findAllWorkOrderByTanggalBetween(model.tanggalMulaiSearch, model.tanggalSelesaiSearch, [orderBy: 'nomor,tanggal'])
        buatNomorWO()

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            model.nomorSearch = null
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
            view.statusSearch.selectedItem = null
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.workOrderList.clear() }
        List result = findAllWorkOrderByDsl([orderBy: 'nomor,tanggal']) {
            if (model.nomorSearch?.trim()?.length() > 0) {
                nomor like("%${model.nomorSearch}%")
            } else {
                if (model.statusSearch.selectedItem) {
                    statusTerakhir eq(model.statusSearch.selectedItem)
                }
                and()
                tanggal between(model.tanggalMulaiSearch, model.tanggalSelesaiSearch)
            }
        }
        execInsideUISync {
            model.workOrderList.addAll(result)
        }
    }

    def save = {
        WorkOrder workOrder = new WorkOrder('nomor': model.nomor, 'tanggal': model.tanggal, 'pelanggan': model.selectedPelanggan,
            'itemWorkOrders': new ArrayList(model.itemWorkOrders), 'statusTerakhir': model.statusTerakhir)
        workOrder.itemWorkOrders.each { ItemWorkOrder itemWorkOrder ->
            itemWorkOrder.workOrder = workOrder
        }
        if (!workOrder.statusTerakhir) workOrder.diterima()
        if (!validate(workOrder)) return

        if (model.id == null) {
            // Insert operation
            if (findWorkOrderByNomor(workOrder.nomor)) {
                model.errors['nomor'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }

            // Konfirmasi
            if (JOptionPane.showConfirmDialog(view.mainPanel, "Total order adalah ${NumberFormat.currencyInstance.format(workOrder.total())}.\n" +
                    "Anda yakin akan menyimpan order ini?", "Konfirmasi Simpan", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return
            }

            workOrder = merge(workOrder)
            execInsideUISync {
                model.workOrderList << workOrder
                view.table.changeSelection(model.workOrderList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            WorkOrder selectedWorkOrder = view.table.selectionModel.selected[0]
            selectedWorkOrder.nomor = model.nomor
            selectedWorkOrder.tanggal = model.tanggal
            selectedWorkOrder.pelanggan = model.selectedPelanggan
            selectedWorkOrder.itemWorkOrders.clear()
            selectedWorkOrder.itemWorkOrders.addAll(model.itemWorkOrders)
            selectedWorkOrder.itemWorkOrders.each { ItemWorkOrder itemWorkOrder ->
                itemWorkOrder.workOrder = selectedWorkOrder
            }
            selectedWorkOrder = merge(selectedWorkOrder)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedWorkOrder }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        WorkOrder workOrder = view.table.selectionModel.selected[0]
        remove(workOrder)
        execInsideUISync {
            model.workOrderList.remove(workOrder)
            clear()
        }
    }

    def clear = {
        execInsideUISync {
            model.id = null
            model.nomor = null
            model.tanggal = null
            model.selectedPelanggan = null
            model.statusTerakhir = null
            model.itemWorkOrders.clear()
            model.pembayaran = null
            model.errors.clear()
            view.table.selectionModel.clearSelection()
        }
        buatNomorWO()
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
                model.itemWorkOrders.clear()
                model.itemWorkOrders.addAll(selected.itemWorkOrders)
                model.pembayaran = selected.pembayaran
            }
        }
    }

    def buatNomorWO() {
        WorkOrder workOrderTahunIniTerakhir = findWorkOrderByTanggalGt(LocalDate.now().withDayOfYear(1).minusDays(1),
            [pageSize: 1, orderBy: 'nomor', orderDirection: 'desc'])
        long nomor = 1
        if (workOrderTahunIniTerakhir) {
            try {
                nomor = Integer.valueOf(workOrderTahunIniTerakhir.nomor.substring(6))
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(view.mainPanel, "Nomor order tidak sesuai standar: ${workOrderTahunIniTerakhir.nomor}",
                    'Kesalahan Nomor Order', JOptionPane.ERROR_MESSAGE)
            }
            nomor++
        }
        model.nomor = String.format('W%04d-%06d', LocalDate.now().year, nomor)
    }

}