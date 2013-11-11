package $packageName

import ${domainPackage}.*
import simplejpa.transaction.Transaction
import javax.swing.*
import javax.swing.event.ListSelectionEvent

@Transaction
class $className {

    def model
    def view

    void mvcGroupInit(Map args) {
        args.'parentList'.each { model.${domainClassAsProp}List << it }
        listAll()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    def listAll = {
<%
    fields.each { field ->
        if (isManyToOne(field) && !field.type.toString().equals(parentDomainClass)) {
            out << "\t\t\texecInsideUISync {model.${field.name}List.clear() }\n"
        }
    }

    fields.each { field ->
        if (isManyToOne(field) && !field.type.toString().equals(parentDomainClass)) {
            out << "\t\tList ${field.name}Result = findAll${field.type}()\n"
        } else if (isManyToMany(field)) {
            out << "\t\tList ${field.name}Result = findAll${field.info}()\n"
        } else if (field.info=="UNKNOWN") {
            out << "\t\t// ${field.name} isn't supported! It must be coded manually!\n"
        }
    }

    fields.each { field ->
        if (isManyToOne(field) && !field.type.toString().equals(parentDomainClass)) {
            out << "\t\t\texecInsideUISync{ model.${field.name}List.addAll(${field.name}Result) }\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\texecInsideUISync{ model.${field.name}.replaceValues(${field.name}Result) }\n"
        }
    }
%>    }

    def save = {
        ${domainClass} ${domainClassAsProp} = new ${domainClass}(<%
    out << fields.findAll{ !(isOneToOne(it) && isMappedBy(it)) &&
                           !(isManyToOne(it) && it.type.toString().equals(parentDomainClass))}.collect { field ->
        if (isManyToOne(field) && !field.type.toString().equals(parentDomainClass)) {
            return "'${field.name}': model.${field.name}.selectedItem"
        } else if (isEnumerated(field)) {
            return "'${field.name}': model.${field.name}.selectedItem"
        } else if (isOneToMany(field)) {
            return "'${field.name}': new ArrayList(model.${field.name})"
        } else if (isManyToMany(field)) {
            return "'${field.name}': model.${field.name}.selectedValues"
        } else {
            return "'${field.name}': model.${field.name}"
        }
    }.join(", ")
%>)
        if (!validate(${domainClassAsProp})) return_failed()

        if (view.table.selectionModel.selectionEmpty) {
            // Insert operation
            execInsideUISync {
                model.${domainClassAsProp}List << ${domainClassAsProp}
                view.table.changeSelection(model.${domainClassAsProp}List.size()-1, 0, false, false)
            }
        } else {
            // Update operation
            ${domainClass} selected${domainClass} = view.table.selectionModel.selected[0]
<%
    fields.each { field ->
        if (isManyToOne(field) && field.type.toString().equals(parentDomainClass)) return

        if (isManyToOne(field) && !field.type.toString().equals(parentDomainClass)) {
            out << "\t\t\tselected${domainClass}.${field.name} = model.${field.name}.selectedItem\n"
        } else if (isEnumerated(field)) {
            out << "\t\t\tselected${domainClass}.${field.name} = model.${field.name}.selectedItem\n"
        } else if (isOneToMany(field)) {
            out << "\t\t\tselected${domainClass}.${field.name}.clear()\n"
            out << "\t\t\tselected${domainClass}.${field.name}.addAll(model.${field.name})\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\tselected${domainClass}.${field.name}.clear()\n"
            out << "\t\t\tselected${domainClass}.${field.name}.addAll(model.${field.name}.selectedValues)\n"
        } else {
            out << "\t\t\tselected${domainClass}.${field.name} = model.${field.name}\n"
        }
    }
%>
        }
        execInsideUISync { clear() }
    }

    def delete = {
        ${domainClass} ${domainClassAsProp} = view.table.selectionModel.selected[0]
        execInsideUISync {
            model.${domainClassAsProp}List.remove(${domainClassAsProp})
             clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
<% fields.each { field ->
        if (isOneToOne(field) && isMappedBy(field)) return
        if (isManyToOne(field) && field.type.toString().equals(parentDomainClass)) return

        if (["BASIC_TYPE", "DATE"].contains(field.info)) {
            if (["Boolean", "boolean"].contains(field.type as String)) {
                out << "\t\t\tmodel.${field.name} = false\n"
            } else {
                out << "\t\t\tmodel.${field.name} = null\n"
            }
        } else if (isOneToOne(field)) {
            out << "\t\t\tmodel.${field.name} = null\n"
        } else if (isOneToMany(field)) {
            out << "\t\t\tmodel.${field.name}.clear()\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\tmodel.${field.name}.clearSelectedValues()\n"
        } else if (isManyToOne(field) || isEnumerated(field)) {
            out << "\t\t\tmodel.${field.name}.selectedItem = null\n"
        } else if (field.info=="UNKNOWN") {
            out << "\t\t\t// ${field.name} is not supported by generator.  You will need to code it manually.\n"
            out << "\t\t\tmodel.${field.name} = null\n"
        }
   }
%>
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
                ${domainClass} selected = view.table.selectionModel.selected[0]
                model.errors.clear()
                model.id = selected.id
<%
    fields.each { field ->
        if (isOneToOne(field) && isMappedBy(field)) return
        if (isManyToOne(field) && field.type.toString().equals(parentDomainClass)) return

        if (["BASIC_TYPE", "DATE"].contains(field.info)) {
            out << "\t\t\t\tmodel.${field.name} = selected.${field.name}\n"
        } else if (isOneToOne(field)) {
            out << "\t\t\t\tmodel.${field.name} = selected.${field.name}\n"
        } else if (isOneToMany(field)) {
            out << "\t\t\t\tmodel.${field.name}.clear()\n"
            out << "\t\t\t\tmodel.${field.name}.addAll(selected.${field.name})\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\t\tmodel.${field.name}.replaceSelectedValues(selected.${field.name})\n"
        } else if (isManyToOne(field) || isEnumerated(field)) {
            out << "\t\t\t\tmodel.${field.name}.selectedItem = selected.${field.name}\n"
        } else if (field.info=="UNKNOWN") {
            out << "\t\t\t\t// ${field.name} is not supported by generator.  You will need to code it manually.\n"
            out << "\t\t\t\tmodel.${field.name} = selected.${field.name}\n"
        }
    }
%>            }
        }
    }

}