package $packageName

import ${domainPackage}.*
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class $className {

    @Bindable Long id
<% fields.each { field ->
        if (["BASIC_TYPE", "DATE"].contains(field.info)) {
            out << "\t@Bindable ${field.type} ${field.name}\n"
        } else if ("UNKNOWN".equals(field.info)){
            out << "\t// ${field.name} is not supported by generator.  You will need to code it manually.\n"
            out << "\t@Bindable ${field.type} ${field.name}\n"
        } else if (isOneToOne(field) && !isMappedBy(field)) {
            out << "\t@Bindable ${field.type} ${field.name}\n"
        } else if (isEnumerated(field)) {
            out << "\tEnumComboBoxModel<${field.type}> ${field.name} = new EnumComboBoxModel<${field.type}>(${field.type}.class)\n"
        } else if (isManyToOne(field)) {
            out << "\tBasicEventList<${field.type}> ${field.name}List = new BasicEventList<>()\n"
            out << "\t@Bindable DefaultEventComboBoxModel<${field.type}> ${field.name} =\n"
            out << "\t\tGlazedListsSwing.eventComboBoxModelWithThreadProxyList(${field.name}List)\n"
        } else if (isOneToMany(field)) {
            out << "\tList<${field.info}> ${field.name} = []\n"
        } else if (isManyToMany(field)) {
            out << "\tTagChooserModel ${field.name} = new TagChooserModel()\n"
        }
    }
%>

    @Bindable ${domainClass} ${domainClassAsProp}

}