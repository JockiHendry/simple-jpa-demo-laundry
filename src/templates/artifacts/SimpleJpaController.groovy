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
        listAll()
    }

    void mvcGroupDestroy() {
        destroyEntityManager()
    }

    @Transaction(newSession = true)
    def listAll = {
        execInsideUISync {
            model.${domainClassAsProp}List.clear()
<%
    fields.each { field ->
        if (isManyToOne(field)) {
            out << "\t\t\tmodel.${field.name}List.clear()\n"
        }
    }
%>        }

        List ${domainClassAsProp}Result = findAll${domainClass}()
<%
    fields.each { field ->
        if (isManyToOne(field)) {
            out << "\t\tList ${field.name}Result = findAll${field.type}()\n"
        } else if (isManyToMany(field)) {
            out << "\t\tList ${field.name}Result = findAll${field.info}()\n"
        } else if (field.info=="UNKNOWN") {
            out << "\t\t// ${field.name} isn't supported! It must be coded manually!\n"
        }
    }  %>
        execInsideUISync {
            model.${domainClassAsProp}List.addAll(${domainClassAsProp}Result)
            model.${firstField}Search = null
            model.searchMessage = app.getMessage("simplejpa.search.all.message")
<%
    fields.each { field ->
        if (isManyToOne(field)) {
            out << "\t\t\tmodel.${field.name}List.addAll(${field.name}Result)\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\tmodel.${field.name}.replaceValues(${field.name}Result)\n"
        }
    }
%>        }
    }

    @Transaction(newSession = true)
    def search = {
        if (model.${firstField}Search?.length() > 0) {
            execInsideUISync { model.${domainClassAsProp}List.clear() }
            List result = findAll${domainClass}By${cls(firstField)}Like("%\${model.${firstField}Search}%")
            execInsideUISync {
                model.${domainClassAsProp}List.addAll(result)
                model.searchMessage = app.getMessage("simplejpa.search.result.message", ['${natural(firstField)}', model.${firstField}Search])
            }
        }
    }

    def save = {
        ${domainClass} ${domainClassAsProp} = new ${domainClass}(<%
    out << fields.findAll{ !(isOneToOne(it) && isMappedBy(it)) }.collect { field ->
        if (isManyToOne(field) || isEnumerated(field)) {
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
<%
    def printTab(int n) {
        n.times { out << "\t" }
    }

    def processOneToManyInSave(List fields, String currentClass, String currentAttribute = null, int numOfTab, String currentField=null) {
        if (!currentAttribute) currentAttribute = currentClass
        fields.findAll{ isOneToMany(it) && isBidirectional(it) }.each { field ->
            if (currentField && field.name.toString()!=currentField) return
            printTab(numOfTab)
            out << "${currentClass}.${field.name}.each { ${field.info} ${prop(field.info)} ->\n"
            printTab(numOfTab+1)
            out << "${prop(field.info)}.${currentAttribute} = ${currentClass}\n"
            processOneToManyInSave(getField(field.info), prop(field.info), numOfTab+1)
            printTab(numOfTab)
            out << "}\n"
        }
    }

    def processManyToManyInSave(List fields, String currentClass, String currentAttribute = null, int numOfTab, String currentField=null) {
        if (!currentAttribute) currentAttribute = currentClass
        fields.findAll{ isManyToMany(it) && isBidirectional(it) && !isMappedBy(it) }.each { field ->
            if (currentField && field.name.toString()!=currentField) return
            printTab(numOfTab)
            out << "${currentClass}.${field.name}.each { ${field.info} ${prop(field.info)} ->\n"
            printTab(numOfTab+1)
            out << "if (!${prop(field.info)}.${linkedAttribute(field).name}.contains(${currentClass})) {\n"
            printTab(numOfTab+2)
            out << "${prop(field.info)}.${linkedAttribute(field).name}.add(${currentClass})\n"
            processManyToManyInSave(getField(field.info), prop(field.info), numOfTab+2)
            printTab(numOfTab+1)
            out << "}\n"
            printTab(numOfTab)
            out << "}\n"
        }
    }

    processOneToManyInSave(fields, domainClassAsProp, 2)
    processManyToManyInSave(fields, domainClassAsProp, 2)
%>
        if (!validate(${domainClassAsProp})) return

        if (model.id == null) {
            // Insert operation
            if (find${domainClass}By${cls(firstField)}(${domainClassAsProp}.${firstField})) {
                model.errors['${firstField}'] = app.getMessage("simplejpa.error.alreadyExist.message")
                return_failed()
            }
            ${domainClassAsProp} = merge(${domainClassAsProp})
            execInsideUISync {
                model.${domainClassAsProp}List << ${domainClassAsProp}
                view.table.changeSelection(model.${domainClassAsProp}List.size()-1, 0, false, false)
            }
        } else {
            // Update operation
            ${domainClass} selected${domainClass} = view.table.selectionModel.selected[0]
<%
    fields.each { field ->
        if (isManyToOne(field) || isEnumerated(field)) {
            out << "\t\t\tselected${domainClass}.${field.name} = model.${field.name}.selectedItem\n"
        } else if (isOneToMany(field)) {
            if (!isCascaded(field)) {
                out << "\t\t\t// You may need to add code here because it seems that you haven't included cascade=CascadeType.ALL and orphanRemoval=true in your domain class's field\n"
            }
            out << "\t\t\tselected${domainClass}.${field.name}.clear()\n"
            out << "\t\t\tselected${domainClass}.${field.name}.addAll(model.${field.name})\n"
            processOneToManyInSave(fields, "selected${domainClass}", domainClassAsProp, 3, field.name.toString())
        } else if (isManyToMany(field)) {
            out << "\t\t\tselected${domainClass}.${field.name}.clear()\n"
            out << "\t\t\tselected${domainClass}.${field.name}.addAll(model.${field.name}.selectedValues)\n"
            processManyToManyInSave(fields, "selected${domainClass}", domainClassAsProp, 3, field.name.toString())
        } else if (isOneToOne(field)) {
            if (!isMappedBy(field)) {
                if (!isCascaded(field)) {
                    out << "\t\t\t// You may need to add code here because it seems that you haven't included cascade=CascadeType.ALL and orphanRemoval=true in your domain class's field\n"
                }
                out << "\t\t\tselected${domainClass}.${field.name} = model.${field.name}\n"
            }
        } else {
            out << "\t\t\tselected${domainClass}.${field.name} = model.${field.name}\n"
        }
    }
%>
            selected${domainClass} = merge(selected${domainClass})
            execInsideUISync { view.table.selectionModel.selected[0] = selected${domainClass} }
        }
        execInsideUISync { clear() }
    }

    def delete = {
        ${domainClass} ${domainClassAsProp} = view.table.selectionModel.selected[0]
<% if (softDelete) {
        out << "\t\tsoftDelete(${domainClassAsProp})\n"
   } else {
        out << "\t\tremove(${domainClassAsProp})"  } %>
        execInsideUISync {
            model.${domainClassAsProp}List.remove(${domainClassAsProp})
            clear()
        }
    }

    @Transaction(Transaction.Policy.SKIP)
    def clear = {
        execInsideUISync {
            model.id = null
<% fields.collect { field ->
        if (isOneToOne(field) && isMappedBy(field)) return

        if (["BASIC_TYPE", "DATE"].contains(field.info) ||
            (field.info=="DOMAIN_CLASS" && field.annotations?.containsAnnotation('OneToOne'))) {
                if (["Boolean", "boolean"].contains(field.type as String)) {
                    out << "\t\t\tmodel.${field.name} = false\n"
                } else {
                    out << "\t\t\tmodel.${field.name} = null\n"
                }
        } else if (isOneToOne(field) || isManyToOne(field) || isEnumerated(field)) {
            out << "\t\t\tmodel.${field.name}.selectedItem = null\n"
        } else if (isOneToMany(field)) {
            out << "\t\t\tmodel.${field.name}.clear()\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\tmodel.${field.name}.clearSelectedValues()\n"
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

        if (["BASIC_TYPE", "DATE"].contains(field.info) ||
            (field.info=="DOMAIN_CLASS" && field.annotations?.containsAnnotation('OneToOne'))) {
            out << "\t\t\t\tmodel.${field.name} = selected.${field.name}\n"
        } else if (isOneToOne(field) || isManyToOne(field) || isEnumerated(field)) {
            out << "\t\t\t\tmodel.${field.name}.selectedItem = selected.${field.name}\n"
        } else if (isOneToMany(field)) {
            out << "\t\t\t\tmodel.${field.name}.clear()\n"
            out << "\t\t\t\tmodel.${field.name}.addAll(selected.${field.name})\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\t\tmodel.${field.name}.replaceSelectedValues(selected.${field.name})\n"
        } else if (field.info=="UNKNOWN") {
            out << "\t\t\t\t// ${field.name} is not supported by generator.  You will need to code it manually.\n"
            out << "\t\t\t\tmodel.${field.name} = selected.${field.name}\n"
        }
    }
%>            }
        }
    }

}