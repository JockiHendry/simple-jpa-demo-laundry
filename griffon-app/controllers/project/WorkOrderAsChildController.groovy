package project

import org.joda.time.LocalDate
import simplejpa.transaction.Transaction

@Transaction
class WorkOrderAsChildController {

    WorkOrderAsChildModel model
    def view

    void mvcGroupInit(Map args) {
        model.parentPelanggan = args.'parentPelanggan'
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
        List workOrderResult = findAllWorkOrderByPelangganAndTanggalBetween(model.parentPelanggan,
            model.tanggalMulaiSearch, model.tanggalSelesaiSearch, [orderBy: 'nomor,tanggal'])

        execInsideUISync {
            model.workOrderList.addAll(workOrderResult)
            view.statusSearch.selectedItem = null
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync {
            model.workOrderList.clear()
        }
        List result = findAllWorkOrderByDsl([orderBy: 'nomor,tanggal']) {
            pelanggan eq(model.parentPelanggan)
            if (model.statusSearch.selectedItem) {
                and()
                statusTerakhir eq(model.statusSearch.selectedItem)
            }
            and()
            tanggal between(model.tanggalMulaiSearch, model.tanggalSelesaiSearch)
        }
        execInsideUISync {
            model.workOrderList.addAll(result)
        }
    }

}
