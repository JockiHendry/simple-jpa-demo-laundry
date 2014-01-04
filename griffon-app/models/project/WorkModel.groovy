/*
 * Copyright 2013 Jocki Hendry.
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
import ca.odell.glazedlists.*
import ca.odell.glazedlists.swing.*
import groovy.beans.Bindable
import org.joda.time.*
import javax.swing.event.*
import simplejpa.swing.*
import org.jdesktop.swingx.combobox.EnumComboBoxModel

class WorkModel {

    @Bindable Long id
    @Bindable BigDecimal hargaOutsider
    @Bindable BigDecimal hargaCorporate

    @Bindable String searchMessage

    BasicEventList kategoriSearchList = new BasicEventList()
    @Bindable DefaultEventComboBoxModel kategoriSearch =
        GlazedListsSwing.eventComboBoxModelWithThreadProxyList(kategoriSearchList)
    BasicEventList bahanSearchList = new BasicEventList()
    @Bindable DefaultEventComboBoxModel bahanSearch =
        GlazedListsSwing.eventComboBoxModelWithThreadProxyList(bahanSearchList)
    BasicEventList jenisWorkSearchList = new BasicEventList()
    @Bindable DefaultEventComboBoxModel jenisWorkSearch =
        GlazedListsSwing.eventComboBoxModelWithThreadProxyList(jenisWorkSearchList)
    @Bindable String itemPakaianSearch

    BasicEventList<Work> workList = new BasicEventList<>()

    @Bindable boolean popupMode = false

}