package project

import javax.swing.JComponent
import javax.swing.KeyStroke
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import java.awt.event.KeyEvent

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*
import org.jdesktop.swingx.prompt.PromptSupport

actions {
    action(id: 'pilih', name: 'Pilih', mnemonic: KeyEvent.VK_P, closure: {
        if (model.popupMode) {
            SwingUtilities.getWindowAncestor(mainPanel).visible = false
        }
    })
}

application(title: 'Pelanggan',
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
            label('<html><b>Petunjuk:</b> <i>Cari dan pilih pelanggan di tabel, kemudian klik tombol Pilih untuk selesai!</i></html>',
                visible: bind {model.popupMode}, horizontalAlignment: CENTER, constraints: PAGE_START)
            panel(constraints: CENTER) {
                flowLayout(alignment: FlowLayout.LEADING)
                label("Nama")
                textField(id: 'namaSearch', columns: 20, text: bind('namaSearch', target: model, mutual: true),
                        actionPerformed: controller.search, keyPressed: { KeyEvent k ->
                    if (k.keyCode==KeyEvent.VK_DOWN) table.requestFocusInWindow()
                })
                label("Membership")
                comboBox(id: 'membershipSearch', model: model.membershipSearch)
                button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search)
            }
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label(text: bind('searchMessage', source: model))
            }
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.pelangganList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Nama', property: 'nama')
                    glazedColumn(name: 'Alamat', property: 'alamat')
                    glazedColumn(name: 'Nomor Telepon', property: 'nomorTelepon')
                    glazedColumn(name: 'Membership', expression: {it.corporate? 'Corporate': 'Outsider'})
                    keyStrokeAction(actionKey: 'pilih', condition: JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
                        keyStroke: KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), action: pilih)
                }
            }
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), visible: bind {!model.popupMode}, constraints: PAGE_END) {
            label('Nama:')
            textField(id: 'nama', columns: 20, text: bind('nama', target: model, mutual: true), errorPath: 'nama')
            errorLabel(path: 'nama', constraints: 'wrap')
            label('Alamat:')
            textField(id: 'alamat', columns: 40, text: bind('alamat', target: model, mutual: true), errorPath: 'alamat')
            errorLabel(path: 'alamat', constraints: 'wrap')
            label('Nomor Telepon:')
            textField(id: 'nomorTelepon', columns: 15, text: bind('nomorTelepon', target: model, mutual: true), errorPath: 'nomorTelepon')
            errorLabel(path: 'nomorTelepon', constraints: 'wrap')
            label('Membership:')
            panel {
                membershipGroup = buttonGroup()
                radioButton(id: 'corporate', text: 'Corporate', selected: bind('corporate', target: model, mutual: true), buttonGroup: membershipGroup)
                radioButton(id: 'outsider', text: 'Outsider', selected: bind('outsider', target: model, mutual: true), buttonGroup: membershipGroup)
            }
            errorLabel(path: 'corporate', constraints: 'wrap')

            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Pilih', visible: bind('isRowSelected', source: table, converter: {it && model.popupMode}), action: pilih)
                button(app.getMessage("simplejpa.dialog.save.button"), actionPerformed: {
                    if (model.id != null) {
                        if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                                app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                            return
                        }
                    }
                    controller.save()
                    nama.requestFocusInWindow()
                })
                mvcPopupButton(id: 'lihatWorkOrder', text: 'Lihat Order...', mvcGroup: 'workOrderAsChild',
                    args: {[parentPelanggan: view.table.selectionModel.selected[0]]},
                    dialogProperties: [title: 'Daftar Order Untuk Pelanggan Ini', size: new Dimension(900,420)],
                    visible: bind{table.isRowSelected})
                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear)
                button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind { table.isRowSelected }, actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                            app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        controller.delete()
                    }
                })
            }
        }
    }
}
PromptSupport.setPrompt("Ketik kata kunci pencarian disini!", namaSearch)