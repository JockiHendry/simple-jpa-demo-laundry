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
