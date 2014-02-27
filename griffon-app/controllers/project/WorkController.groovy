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
import simplejpa.transaction.Transaction

import javax.swing.event.ListSelectionEvent

@Transaction
class WorkController {

    final static String SEMUA = "- Semua Item  -"

    WorkModel model
    def view

    void mvcGroupInit(Map args) {
        if (args.'popup') {
            model.popupMode = true
            listAllInPopup()
        } else {
            listAll()
        }
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction
    def listAllInPopup = {
        execInsideUISync {
            model.kategoriSearchList.clear()
            model.jenisWorkSearchList.clear()
        }

        List kategoriResult = findAllKategori([orderBy: 'nama'])
        List jenisWorkResult = findAllJenisWork([orderBy: 'nama'])

        execInsideUISync {
            model.kategoriSearchList << SEMUA
            model.kategoriSearchList.addAll(kategoriResult)
            model.kategoriSearch.selectedItem = SEMUA

            model.jenisWorkSearchList << SEMUA
            model.jenisWorkSearchList.addAll(jenisWorkResult)
            model.jenisWorkSearch.selectedItem = SEMUA

            view.itemPakaianSearch.requestFocusInWindow()
        }
    }

    @Transaction
    def listAll = {
        execInsideUISync {
            model.workList.clear()
            model.kategoriSearchList.clear()
            model.jenisWorkSearchList.clear()
        }

        List kategoriResult = findAllKategori([orderBy: 'nama'])
        List jenisWorkResult = findAllJenisWork([orderBy: 'nama'])
        List workResult = findAllWork()

        // Menambah Work baru bila belum terdaftar
        List itemPakaianResult = findAllItemPakaian([orderBy: 'nama'])
        for (ItemPakaian itemPakaian: itemPakaianResult) {
            for (JenisWork jenisWork: jenisWorkResult) {
                if (!workResult.find {it.itemPakaian == itemPakaian && it.jenisWork == jenisWork}) {
                    Work work = new Work(itemPakaian, jenisWork)
                    work.hargaOutsider = 0
                    work.hargaCorporate = 0
                    persist(work)
                    workResult << work
                }
            }
        }

        execInsideUISync {
            model.workList.addAll(workResult.sort())
            model.searchMessage = app.getMessage("simplejpa.search.all.message")

            model.kategoriSearchList << SEMUA
            model.kategoriSearchList.addAll(kategoriResult)
            model.kategoriSearch.selectedItem = SEMUA

            model.jenisWorkSearchList << SEMUA
            model.jenisWorkSearchList.addAll(jenisWorkResult)
            model.jenisWorkSearch.selectedItem = SEMUA

            view.itemPakaianSearch.requestFocusInWindow()
        }
    }

    @Transaction
    def search = {
        execInsideUISync { model.workList.clear() }

        List result = findAllWorkByDsl() {
            itemPakaian__nama like("%${model.itemPakaianSearch}%")
            if (model.kategoriSearch.selectedItem!=SEMUA) {
                and()
                itemPakaian__kategori eq(model.kategoriSearch.selectedItem)
            }
            if (model.jenisWorkSearch.selectedItem!=SEMUA) {
                and()
                jenisWork eq(model.jenisWorkSearch.selectedItem)
            }
        }.sort()

        execInsideUISync {
            model.workList.addAll(result)
            if (result.size() > 0) {
                view.table.requestFocusInWindow()
            }
        }
    }

    def save = {
        if (model.id != null) {
            Work selectedWork = view.table.selectionModel.selected[0]
            selectedWork.hargaOutsider = model.hargaOutsider
            selectedWork.hargaCorporate = model.hargaCorporate
            if (!validate(selectedWork)) return
            selectedWork = merge(selectedWork)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedWork }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        Work work = view.table.selectionModel.selected[0]
        remove(work)
        execInsideUISync {
            model.workList.remove(work)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.hargaOutsider = null
            model.hargaCorporate = null

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
                Work selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.hargaOutsider = selected.hargaOutsider
                model.hargaCorporate = selected.hargaCorporate
            }
        }
    }

}