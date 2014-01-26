package project

import domain.PembayaranSignedBill
import domain.StatusPekerjaan
import domain.WorkOrder
import org.joda.time.LocalDate
import simplejpa.transaction.Transaction

import javax.swing.JOptionPane
import javax.swing.event.ListSelectionEvent
import java.text.NumberFormat

@Transaction
class PengambilanController {
    PengambilanModel model
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
        List workOrderResult = findAllWorkOrderByTanggalBetweenAndStatusTerakhirEq(model.tanggalMulaiSearch,
            model.tanggalSelesaiSearch, StatusPekerjaan.DISELESAIKAN, [orderBy: 'nomor,tanggal'])

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            model.nomorSearch = null
            model.pelangganSearch = null
            view.jenisJadwalSearch.selectedItem = JenisJadwalSearch.SEMUA
            view.statusPekerjaanSearch.selectedItem = StatusPekerjaanPengambilanSearch.DISELESAIKAN
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
            if (model.statusPekerjaanSearch.selectedItem != StatusPekerjaanPengambilanSearch.SEMUA) {
                and()
                statusTerakhir eq(model.statusPekerjaanSearch.selectedItem.convertToStatusPekerjaan())
            }
            if (model.jenisJadwalSearch.selectedItem && model.jenisJadwalSearch.selectedItem!=JenisJadwalSearch.SEMUA) {
                and()
                express eq(model.jenisJadwalSearch.selectedItem==JenisJadwalSearch.EXPRESS)
            }
        }

        execInsideUISync { model.workOrderList.addAll(result) }

    }

    def prosesPengambilan = {

        WorkOrder workOrder = merge(view.table.selectionModel.selected[0])

        // validasi
        if (model.tanggal.isBefore(workOrder.getEvent(StatusPekerjaan.DISELESAIKAN).tanggal)) {
            model.errors['tanggal'] = 'Tanggal pengambilan harus setelah tanggal WO selesai dikerjakan'
            return
        }

        // konfirmasi
        if (JOptionPane.showConfirmDialog(view.mainPanel, "Apakah Anda yakin order ini diambil pada tanggal ${model.tanggal.toString('dd-MM-yyyy')}?",
                'Konfirmasi Selesai Dicuci', JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
            return
        }

        if (model.keterangan && !model.keterangan.isEmpty()) {
            workOrder.keterangan = model.keterangan
        }
        workOrder.diambil(model.namaPenerima, model.tanggal)
        if (!workOrder.pembayaran.isLunas()) {
            if (JOptionPane.showConfirmDialog(view.mainPanel, "Order ini masih belum lunas.  Apakah pelunasan sebesar ${NumberFormat.currencyInstance.format(workOrder.pembayaran.total())} telah diterima?",
                    'Konfirmasi Pembayaran', JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                // pelunasan signed bill
                if (workOrder.pembayaran instanceof PembayaranSignedBill) {
                    workOrder.pembayaran.tanggalPelunasan = model.tanggal.toLocalDate()
                }
            }
        }
        execInsideUISync {
            model.workOrderList.remove(view.table.selectionModel.selected[0])
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.namaPenerima = null
            model.keterangan = null
            model.tanggal = null
            model.adaSisaPembayaran = Boolean.FALSE
            model.jumlahDibayarDimuka = null
            model.sisa = null
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
                model.namaPenerima = selected.namaPenerima
                model.keterangan = selected.keterangan
                if (selected.pembayaran instanceof PembayaranSignedBill) {
                    PembayaranSignedBill p = (PembayaranSignedBill) selected.pembayaran
                    model.adaSisaPembayaran = Boolean.TRUE
                    if (p.jumlahBayarDimuka) {
                        model.jumlahDibayarDimuka = NumberFormat.currencyInstance.format(p.jumlahBayarDimuka)
                    }
                    if (p.total()) {
                        model.sisa = NumberFormat.currencyInstance.format(p.total())
                    }
                } else {
                    model.adaSisaPembayaran = Boolean.FALSE
                }
            }
        }
    }

}
