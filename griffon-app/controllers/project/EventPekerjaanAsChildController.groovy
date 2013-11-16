package project

import domain.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class EventPekerjaanAsChildController {

    EventPekerjaanAsChildModel model
    def view

    void mvcGroupInit(Map args) {
        model.parentWorkOrder = args.'parentWorkOrder'
        listAll()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    def listAll = {
        execInsideUISync { model.eventPekerjaanList.clear() }
        model.parentWorkOrder = merge(model.parentWorkOrder)
        List eventPekerjaans = model.parentWorkOrder.eventPekerjaans
        execInsideUISync { model.eventPekerjaanList.addAll(eventPekerjaans) }
    }

    def save = {
        model.parentWorkOrder = merge(model.parentWorkOrder)
        EventPekerjaan eventPekerjaan = new EventPekerjaan('tanggal': model.tanggal, 'status': model.status.selectedItem, 'workOrder': model.parentWorkOrder)
        if (!validate(eventPekerjaan)) return_failed()

        if (view.table.selectionModel.selectionEmpty) {
            // Insert operation
            model.parentWorkOrder.tambahEvent(eventPekerjaan)
            execInsideUISync {
                model.eventPekerjaanList << eventPekerjaan
                view.table.changeSelection(model.eventPekerjaanList.size() - 1, 0, false, false)
            }
        } else {
            // Update operation
            EventPekerjaan selectedEventPekerjaan = view.table.selectionModel.selected[0]
            selectedEventPekerjaan.tanggal = model.tanggal
            selectedEventPekerjaan.status = model.status.selectedItem
            selectedEventPekerjaan.workOrder = model.parentWorkOrder

        }
        execInsideUISync { clear() }
    }

    def delete = {
        model.parentWorkOrder = merge(model.parentWorkOrder)
        EventPekerjaan eventPekerjaan = view.table.selectionModel.selected[0]
        model.parentWorkOrder.hapusEvent(eventPekerjaan)
        execInsideUISync {
            model.eventPekerjaanList.remove(eventPekerjaan)
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
            model.tanggal = null
            model.status.selectedItem = null
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
                EventPekerjaan selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
                model.tanggal = selected.tanggal
                model.status.selectedItem = selected.status
            }
        }
    }

}