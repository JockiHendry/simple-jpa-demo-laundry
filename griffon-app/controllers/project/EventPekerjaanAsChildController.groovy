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