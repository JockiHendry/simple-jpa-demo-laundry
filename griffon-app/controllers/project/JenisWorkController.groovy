package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class JenisWorkController {

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
            model.jenisWorkList.clear()
        }

        List jenisWorkResult = findAllJenisWork()

        execInsideUISync {
            model.jenisWorkList.addAll(jenisWorkResult)
            model.namaSearch = null
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
        }
    }

    @Transaction(newSession = true)
    def search = {
        if (model.namaSearch?.length() > 0) {
            execInsideUISync { model.jenisWorkList.clear() }
            List result = findAllJenisWorkByNamaLike("%${model.namaSearch}%")
            execInsideUISync {
                model.jenisWorkList.addAll(result)
                model.searchMessage = app.getMessage("simplejpa.search.result.message", ['Nama', model.namaSearch])
            }
        }
    }

    def save = {
        JenisWork jenisWork = new JenisWork('nama': model.nama, 'keterangan': model.keterangan)

        if (!validate(jenisWork)) return

        if (model.id == null) {
            // Insert operation
            if (findJenisWorkByNama(jenisWork.nama)) {
                model.errors['nama'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }
            jenisWork = merge(jenisWork)
            execInsideUISync {
                model.jenisWorkList << jenisWork
                view.table.changeSelection(model.jenisWorkList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            JenisWork selectedJenisWork = view.table.selectionModel.selected[0]
            selectedJenisWork.nama = model.nama
            selectedJenisWork.keterangan = model.keterangan
            selectedJenisWork = merge(selectedJenisWork)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedJenisWork }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        JenisWork jenisWork = view.table.selectionModel.selected[0]
        remove(jenisWork)
        execInsideUISync {
            model.jenisWorkList.remove(jenisWork)
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
                JenisWork selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.nama = selected.nama
                model.keterangan = selected.keterangan
            }
        }
    }

}