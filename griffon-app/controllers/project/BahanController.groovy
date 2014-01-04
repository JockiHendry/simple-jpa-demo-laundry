package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class BahanController {

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
            model.bahanList.clear()
        }

        List bahanResult = findAllBahan()

        execInsideUISync {
            model.bahanList.addAll(bahanResult)
            model.namaSearch = null
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
        }
    }

    @Transaction(newSession = true)
    def search = {
        if (model.namaSearch?.length() > 0) {
            execInsideUISync { model.bahanList.clear() }
            List result = findAllBahanByNamaLike("%${model.namaSearch}%")
            execInsideUISync {
                model.bahanList.addAll(result)
                model.searchMessage = app.getMessage("simplejpa.search.result.message", ['Nama', model.namaSearch])
            }
        }
    }

    def save = {
        Bahan bahan = new Bahan('nama': model.nama, 'keterangan': model.keterangan)
        if (!validate(bahan)) return

        if (model.id == null) {
            // Insert operation
            if (findBahanByNama(bahan.nama)) {
                model.errors['nama'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }
            bahan = merge(bahan)
            execInsideUISync {
                model.bahanList << bahan
                view.table.changeSelection(model.bahanList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            Bahan selectedBahan = view.table.selectionModel.selected[0]
            selectedBahan.nama = model.nama
            selectedBahan.keterangan = model.keterangan
            selectedBahan = merge(selectedBahan)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedBahan }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        Bahan bahan = view.table.selectionModel.selected[0]
        remove(bahan)
        execInsideUISync {
            model.bahanList.remove(bahan)
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
                Bahan selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.nama = selected.nama
                model.keterangan = selected.keterangan
            }
        }
    }

}