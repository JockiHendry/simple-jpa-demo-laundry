package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class PelangganController {

    def model
    def view

    void mvcGroupInit(Map args) {
        if (args.'popup') model.popupMode = true
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(newSession = true)
    def listAll = {
        execInsideUISync {
            model.pelangganList.clear()
        }

        List pelangganResult = findAllPelanggan()

        execInsideUISync {
            model.pelangganList.addAll(pelangganResult)
            model.namaSearch = null
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
        }
    }

    @Transaction(newSession = true)
    def search = {
        if (model.namaSearch?.length() > 0) {
            execInsideUISync { model.pelangganList.clear() }
            List result = findAllPelangganByNamaLike("%${model.namaSearch}%")
            execInsideUISync {
                model.pelangganList.addAll(result)
                model.searchMessage = app.getMessage("simplejpa.search.result.message", ['Nama', model.namaSearch])
            }
        }
    }

    def save = {
        Pelanggan pelanggan = new Pelanggan('nama': model.nama, 'alamat': model.alamat, 'nomorTelepon': model.nomorTelepon)

        if (!validate(pelanggan)) return

        if (model.id == null) {
            // Insert operation
            if (findPelangganByNama(pelanggan.nama)) {
                model.errors['nama'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }
            pelanggan = merge(pelanggan)
            execInsideUISync {
                model.pelangganList << pelanggan
                view.table.changeSelection(model.pelangganList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            Pelanggan selectedPelanggan = view.table.selectionModel.selected[0]
            selectedPelanggan.nama = model.nama
            selectedPelanggan.alamat = model.alamat
            selectedPelanggan.nomorTelepon = model.nomorTelepon

            selectedPelanggan = merge(selectedPelanggan)
            execInsideUISync { view.table.selectionModel.selected[0] = selectedPelanggan }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        Pelanggan pelanggan = view.table.selectionModel.selected[0]
        remove(pelanggan)
        execInsideUISync {
            model.pelangganList.remove(pelanggan)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.nama = null
            model.alamat = null
            model.nomorTelepon = null

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
                Pelanggan selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.nama = selected.nama
                model.alamat = selected.alamat
                model.nomorTelepon = selected.nomorTelepon
            }
        }
    }

}