package project

import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import java.awt.event.ItemEvent
import java.awt.event.KeyEvent

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*

actions {
    action(id: 'pilih', name: 'Pilih', mnemonic: KeyEvent.VK_P, closure: {
        if (model.popupMode) {
            SwingUtilities.getWindowAncestor(mainPanel).visible = false
        }
    })
    action(id: 'cari', name: 'Cari', mnemonic: KeyEvent.VK_C, closure: controller.search)
    action(id: 'lihatSemua', name: 'Lihat Semua', mnemonic: KeyEvent.VK_L, closure: controller.listAll)
}
application(title: 'Work',
        preferredSize: [520, 340],
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {

    panel(id: 'mainPanel') {
        borderLayout()

        panel(constraints: PAGE_START) {
            borderLayout()
            label('<html><b>Petunjuk:</b> <i>Cari dan pilih jenis pekerjaan di tabel, kemudian klik tombol Pilih untuk selesai!</i><html>',
                visible: bind{model.popupMode}, horizontalAlignment: SwingConstants.CENTER, constraints: PAGE_START)
            panel(constraints: CENTER) {
                flowLayout(alignment: FlowLayout.LEADING)
                label("Jenis Pakaian")
                textField(id: 'itemPakaianSearch', columns: 20, text: bind('itemPakaianSearch', target: model, mutual: true),
                    actionPerformed: controller.search)
                label("Kategori")
                comboBox(id: 'kategoriSearch', model: model.kategoriSearch, itemStateChanged: { ItemEvent e ->
                    if (e.stateChange == ItemEvent.SELECTED) {
                        if (e.item != controller.SEMUA) {
                            model.bahanSearch.selectedItem = controller.SEMUA
                        }
                    }
                })
                label("Bahan")
                comboBox(id: 'bahanSearch', model: model.bahanSearch, itemStateChanged: { ItemEvent e ->
                    if (e.stateChange == ItemEvent.SELECTED) {
                        if (e.item != controller.SEMUA) {
                            model.kategoriSearch.selectedItem = controller.SEMUA
                        }
                    }
                })
                label("Jenis Pekerjaan")
                comboBox(id: 'jenisWorkSearch', model: model.jenisWorkSearch)
                button(app.getMessage('simplejpa.search.label'), action: cari)
            }
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label(text: bind('searchMessage', source: model))
            }
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.workList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Item Pakaian', property: 'itemPakaian')
                    glazedColumn(name: 'Jenis Work', property: 'jenisWork')
                    glazedColumn(name: 'Kategori', expression: {it.itemPakaian.kategori}) {
                        templateRenderer("\${it?:'-'}")
                    }
                    glazedColumn(name: 'Bahan', expression: {it.itemPakaian.bahan}) {
                        templateRenderer("\${it?:'-'}")
                    }
                    glazedColumn(name: 'Harga Outsider (Rp)', property: 'hargaOutsider', columnClass: Integer) {
                        templateRenderer('${numberFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Harga Corporate (Rp)', property: 'hargaCorporate', columnClass: Integer) {
                        templateRenderer('${numberFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    keyStrokeAction(actionKey: 'pilih', condition: JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                        keyStroke: KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), action: pilih)
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), visible: bind {!model.popupMode}, constraints: PAGE_END) {
            label('Harga Outsider:')
            numberTextField(id: 'hargaOutsider', columns: 20, bindTo: 'hargaOutsider', nfParseBigDecimal: true, errorPath: 'hargaOutsider')
            errorLabel(path: 'hargaOutsider', constraints: 'wrap')

            label('Harga Corporate:')
            numberTextField(id: 'hargaCorporate', columns: 20, bindTo: 'hargaCorporate', nfParseBigDecimal: true, errorPath: 'hargaCorporate')
            errorLabel(path: 'hargaCorporate', constraints: 'wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Pilih', visible: bind('isRowSelected', source: table, converter: {it && model.popupMode}), action: pilih)
                button(app.getMessage("simplejpa.dialog.save.button"), visible: bind('isRowSelected', source: table, converter: {it && !model.popupMode}), actionPerformed: {
                    controller.save()
                    hargaOutsider.requestFocusInWindow()
                }, mnemonic: KeyEvent.VK_S)
                button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind('isRowSelected', source: table, converter: {it && !model.popupMode}), actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                            app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        controller.delete()
                    }
                }, mnemonic: KeyEvent.VK_H)
            }
        }
    }
}