package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class KategoriController {

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
            model.kategoriList.clear()
        }

        List kategoriResult = findAllKategori()

        execInsideUISync {
            model.kategoriList.addAll(kategoriResult)
            model.namaSearch = null
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
        }
    }

    @Transaction(newSession = true)
    def search = {
        if (model.namaSearch?.length() > 0) {
            execInsideUISync { model.kategoriList.clear() }
            List result = findAllKategoriByNamaLike("%${model.namaSearch}%")
            execInsideUISync {
                model.kategoriList.addAll(result)
                model.searchMessage = app.getMessage("simplejpa.search.result.message", ['Nama', model.namaSearch])
            }
        }
    }

    def save = {
        Kategori kategori = new Kategori('nama': model.nama, 'keterangan': model.keterangan)

        if (!validate(kategori)) return

        if (model.id == null) {
            // Insert operation
            if (findKategoriByNama(kategori.nama)) {
                model.errors['nama'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }
            kategori = merge(kategori)
            execInsideUISync {
                model.kategoriList << kategori
                view.table.changeSelection(model.kategoriList.size()-1, 0, false, false)
            }
        } else {
            // Update operation
            Kategori selectedKategori = view.table.selectionModel.selected[0]
			selectedKategori.nama = model.nama
			selectedKategori.keterangan = model.keterangan
            selectedKategori = merge(selectedKategori)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedKategori }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        Kategori kategori = view.table.selectionModel.selected[0]
		remove(kategori)
        execInsideUISync {
            model.kategoriList.remove(kategori)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
			model.nama = null
			model.keterangan = null
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
                Kategori selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
				model.nama = selected.nama
				model.keterangan = selected.keterangan
            }
        }
    }

}