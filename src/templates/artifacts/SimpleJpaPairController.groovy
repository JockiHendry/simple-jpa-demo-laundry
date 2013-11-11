package $packageName

import ${domainPackage}.*
import simplejpa.transaction.Transaction
import javax.swing.*

@Transaction
class $className {

    def model
    def view

    void mvcGroupInit(Map args) {
        model.${domainClassAsProp} = args.'pair'
<%
        fields.each { field ->
            if (isOneToOne(field) && isMappedBy(field)) return

            if (["BASIC_TYPE", "DATE"].contains(field.info)) {
                out << "\t\tmodel.${field.name} = args.'pair'?.${field.name}\n"
            } else if (isOneToOne(field)) {
                out << "\t\tmodel.${field.name} = args.'pair'?.${field.name}\n"
            } else if (isManyToOne(field) || isEnumerated(field)) {
                out << "\t\tmodel.${field.name}.selectedItem = args.'pair'?.${field.name}\n"
            } else if (isOneToMany(field)) {
                out << "\t\tif (args.'pair'?.${field.name}) {\n"
                out << "\t\t\tmodel.${field.name}.clear()\n"
                out << "\t\t\tmodel.${field.name}.addAll(args.'pair'?.${field.name})\n"
                out << "\t\t}\n"
            } else if (isManyToMany(field)) {
                out << "\t\tif (args.'pair'?.${field.name}) {\n"
                out << "\t\t\tmodel.${field.name}.replaceSelectedValues(args.'pair'?.${field.name})\n"
                out << "\t\t}\n"
            } else if (field.info=="UNKNOWN") {
                out << "\t\t// ${field.name} is not supported by generator.  You will need to code it manually.\n"
                out << "\t\tmodel.${field.name} = args.'pair'?.${field.name}\n"
            }
        }
%>
        listAll()
    }

    void mvcGroupDestroy() {
    }

    def listAll = {
<%
    fields.each { field ->
        if ((isManyToOne(field))) {
            out << "\t\t\texecInsideUISync {model.${field.name}List.clear() }\n"
        }
    }

    fields.each { field ->
        if (isManyToOne(field)) {
            out << "\t\tList ${field.name}Result = findAll${field.type}()\n"
        } else if (isManyToMany(field)) {
            out << "\t\tList ${field.name}Result = findAll${field.info}()\n"
        } else if (field.info=="UNKNOWN") {
            out << "\t\t// ${field.name} isn't supported! It must be coded manually!\n"
        }
    }

    fields.each { field ->
        if (isManyToOne(field)) {
            out << "\t\t\texecInsideUISync{ model.${field.name}List.addAll(${field.name}Result) }\n"
        } else if (isManyToMany(field)) {
            out << "\t\t\texecInsideUISync{ model.${field.name}.replaceValues(${field.name}Result) }\n"
        }
    }
%>    }

    def close = {
        execInsideUISync { SwingUtilities.getWindowAncestor(view.mainPanel)?.dispose() }
    }

    def save = {
        ${domainClass} ${domainClassAsProp} = new ${domainClass}(<%
    out << fields.findAll{ !(isOneToOne(it) && isMappedBy(it)) &&
                           !(isManyToOne(it) && it.type.toString().equals(parentDomainClass))}.collect { field ->
        if (isOneToOne(field)) {
            return "'${field.name}': model.${field.name}"
        } else if (isManyToOne(field) || isEnumerated(field)) {
            return "'${field.name}': model.${field.name}.selectedItem"
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

    def processOneToManyInSave(List fields, boolean updateMode = false, int numOfTab, String currentField = null) {
        fields.findAll{ isOneToMany(it) && isBidirectional(it) }.each { field ->
            if (currentField && field.name.toString()!=currentField) return

            printTab(numOfTab)
            out << "${domainClassAsProp}.${field.name}.each { ${field.info} ${prop(field.info)} ->\n"
            printTab(numOfTab+1)
            if (updateMode) {
                out << "${prop(field.info)}.${domainClassAsProp} = model.${domainClassAsProp}\n"
            } else {
                out << "${prop(field.info)}.${domainClassAsProp} = ${domainClassAsProp}\n"
            }
            processOneToManyInSave(getField(field.info), updateMode, numOfTab+1)
            printTab(numOfTab)
            out << "}\n"
        }
    }

    def processManyToManyInSave(List fields, boolean updateMode = false, int numOfTab, String currentField = null) {
        fields.findAll{ isManyToMany(it) && isBidirectional(it) && !isMappedBy(it) }.each { field ->
            if (currentField && field.name.toString()!=currentField) return

            printTab(numOfTab)
            if (updateMode) out << "model."
            out << "${domainClassAsProp}.${field.name}.each { ${field.info} ${prop(field.info)} ->\n"
            printTab(numOfTab+1)
            out << "if (!${prop(field.info)}.${linkedAttribute(field).name}.contains(${domainClassAsProp})) {\n"
            printTab(numOfTab+2)
            out << "${prop(field.info)}.${linkedAttribute(field).name}.add(${domainClassAsProp})\n"
            processManyToManyInSave(getField(field.info), updateMode, numOfTab+2)
            printTab(numOfTab+1)
            out << "}\n"
            printTab(numOfTab)
            out << "}\n"
        }
    }

    processOneToManyInSave(fields, 2)
    processManyToManyInSave(fields, 2)
%>
        if (!validate(${domainClassAsProp})) return_failed()

        if (model.${domainClassAsProp}==null) {
            // Insert operation
            model.${domainClassAsProp}= ${domainClassAsProp}
        } else {
            // Update operation
<%
    fields.each { field ->

        if (field.info=="DOMAIN_CLASS" && field.annotations?.containsAttribute('mappedBy')) return
        if (isManyToOne(field) && field.type.toString().equals(parentDomainClass)) return

        if (isOneToMany(field)) {
            out << "\t\t\tmodel.${domainClassAsProp}.${field.name} = ${domainClassAsProp}.${field.name}\n"
            processOneToManyInSave(fields, true, 3, field.name.toString())
        } else if (isManyToMany(field)) {
            out << "\t\t\tmodel.${domainClassAsProp}.${field.name} = ${domainClassAsProp}.${field.name}\n"
            processManyToManyInSave(fields, true, 3, field.name.toString())
        } else if (field.type.toString()=="List" && field.info!="UNKNOWN") {
            out << "\t\t\tmodel.${domainClassAsProp}.${field.name}.clear()\n"
            out << "\t\t\tmodel.${domainClassAsProp}.${field.name}.addAll(${domainClassAsProp}.${field.name})\n"
        } else {
            out << "\t\t\tmodel.${domainClassAsProp}.${field.name} = ${domainClassAsProp}.${field.name}\n"
        }
    }%>
        }
        close()
    }

    def delete = {
        model.${domainClassAsProp} = null
        close()
    }

}