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
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class ItemPakaianController {

    final static String SEMUA_KATEGORI = "- Semua Kategori -"

    def model
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
            model.itemPakaianList.clear()
            model.kategoriList.clear()
            model.kategoriSearchList.clear()
        }

        List itemPakaianResult = findAllItemPakaian()
        List kategoriResult = findAllKategori()

        execInsideUISync {
            model.namaSearch = null
            model.itemPakaianList.addAll(itemPakaianResult)

            model.kategoriSearchList << SEMUA_KATEGORI
            model.kategoriSearchList.addAll(kategoriResult)
            model.kategoriSearch.selectedItem = SEMUA_KATEGORI

            model.kategoriList.addAll(kategoriResult)
        }
    }

    @Transaction(newSession = true)
    def search = {
        execInsideUISync { model.itemPakaianList.clear() }
        List result = findAllItemPakaianByDsl {
            nama like("%${model.namaSearch ?: ''}%")
            if (model.kategoriSearch.selectedItem!=SEMUA_KATEGORI) {
                and()
                kategori eq(model.kategoriSearch.selectedItem)
            }
        }
        execInsideUISync {
            model.itemPakaianList.addAll(result)
        }
    }

    def save = {
        ItemPakaian itemPakaian = new ItemPakaian('nama': model.nama, 'kategori': model.kategori.selectedItem)

        if (!validate(itemPakaian)) return

        if (model.id == null) {
            // Insert operation
            if (findItemPakaianByNama(itemPakaian.nama)) {
                model.errors['nama'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }
            itemPakaian = merge(itemPakaian)
            execInsideUISync {
                model.itemPakaianList << itemPakaian
                view.table.changeSelection(model.itemPakaianList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            ItemPakaian selectedItemPakaian = view.table.selectionModel.selected[0]
            selectedItemPakaian.nama = model.nama
            selectedItemPakaian.kategori = model.kategori.selectedItem
            selectedItemPakaian = merge(selectedItemPakaian)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedItemPakaian }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        ItemPakaian itemPakaian = view.table.selectionModel.selected[0]
        remove(itemPakaian)
        execInsideUISync {
            model.itemPakaianList.remove(itemPakaian)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.nama = null
            model.kategori.selectedItem = null
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
                ItemPakaian selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.nama = selected.nama
                model.kategori.selectedItem = selected.kategori
            }
        }
    }

}