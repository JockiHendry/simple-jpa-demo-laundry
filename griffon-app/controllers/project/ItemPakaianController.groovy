package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class ItemPakaianController {

    final static String SEMUA_KATEGORI = "- Semua Kategori -"
    final static String SEMUA_BAHAN = "- Semua Bahan -"

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
            model.bahanList.clear()
            model.kategoriSearchList.clear()
            model.bahanSearchList.clear()
        }

        List itemPakaianResult = findAllItemPakaian()
        List kategoriResult = findAllKategori()
        List bahanResult = findAllBahan()

        execInsideUISync {
            model.namaSearch = null
            model.itemPakaianList.addAll(itemPakaianResult)

            model.kategoriSearchList << SEMUA_KATEGORI
            model.kategoriSearchList.addAll(kategoriResult)
            model.kategoriSearch.selectedItem = SEMUA_KATEGORI

            model.bahanSearchList << SEMUA_BAHAN
            model.bahanSearchList.addAll(bahanResult)
            model.bahanSearch.selectedItem = SEMUA_BAHAN

            model.kategoriList.addAll(kategoriResult)
            model.bahanList.addAll(bahanResult)
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
            if (model.bahanSearch.selectedItem!=SEMUA_BAHAN) {
                and()
                bahan eq(model.bahanSearch.selectedItem)
            }
        }
        execInsideUISync {
            model.itemPakaianList.addAll(result)
        }
    }

    def save = {
        ItemPakaian itemPakaian = new ItemPakaian('nama': model.nama, 'kategori': model.kategori.selectedItem, 'bahan': model.bahan.selectedItem)

        if (!validate(itemPakaian)) return

        // Sebuah `ItemPakaian` yang memiliki `kategori` tidak boleh memiliki `bahan` (dan sebaliknya).
        // Strange?
        if (itemPakaian.kategori && itemPakaian.bahan) {
            JOptionPane.showMessageDialog(view.mainPanel, 'Item tidak boleh memiliki kategori dan bahan.  Silahkan pilih salah satu.',
                'Kesalahan Validasi', JOptionPane.ERROR_MESSAGE)
            return
        }

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
            selectedItemPakaian.bahan = model.bahan.selectedItem
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
            model.bahan.selectedItem = null
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
                model.bahan.selectedItem = selected.bahan
            }
        }
    }

}