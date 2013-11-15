package project

import javax.swing.BorderFactory
import javax.swing.SwingConstants
import java.awt.event.KeyEvent

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*

application(title: 'Item Work Order',
        preferredSize: [520, 340],
        pack: true,
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {

    panel(id: 'mainPanel') {
        borderLayout()

        panel(constraints: CENTER) {
            borderLayout()
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.itemWorkOrderList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Item', property: 'work')
                    glazedColumn(name: 'Qty', property: 'jumlah', columnClass: Integer, width: [80,80,200])
                    glazedColumn(name: 'Harga', property: 'harga', columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                }
            }
            label(constraints: PAGE_END, horizontalAlignment: SwingConstants.RIGHT,
                border: BorderFactory.createRaisedSoftBevelBorder(), text: bind{model.informasi})
        }

        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), constraints: PAGE_END) {
            label('Item Pekerjaan:')
            panel {
                label(text: bind {model.selectedWork?: '- kosong -'})
                mvcPopupButton('Cari Di Daftar Pekerjaan...', id: 'cariPekerjaan', mvcGroup: 'work', errorPath: 'work', dialogProperties: [
                    title: 'Cari Item Pekerjaan',
                    size: new Dimension(900, 420)
                ], onFinish: { WorkModel m, v, c ->
                    if (v.table.selectionModel.isSelectionEmpty()) {
                        JOptionPane.showMessageDialog(mainPanel, 'Tidak ada item pekerjaan yang dipilih!', 'Cari Item Pekerjaan',
                            JOptionPane.ERROR_MESSAGE)
                    } else {
                        model.selectedWork = controller.findWorkById(v.table.selectionModel.selected[0].id)
                        model.harga = model.selectedWork.harga
                    }
                }, mnemonic: KeyEvent.VK_P)
            }
            errorLabel(path: 'work', constraints: 'wrap')
            label('Jumlah:')
            numberTextField(id: 'jumlah', columns: 10, bindTo: 'jumlah', errorPath: 'jumlah')
            errorLabel(path: 'jumlah', constraints: 'wrap')
            label('Harga:')
            numberTextField(id: 'harga', columns: 20, bindTo: 'harga', nfParseBigDecimal: true, errorPath: 'harga')
            errorLabel(path: 'harga', constraints: 'wrap')


            panel(constraints: 'span, growx, wrap') {
                flowLayout(alignment: FlowLayout.LEADING)
                button('Simpan', actionPerformed: {
                    if (!view.table.selectionModel.selectionEmpty) {
                        if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.update.message"),
                                app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
                            return
                        }
                    }
                    controller.save()
                    cariPekerjaan.requestFocusInWindow()
                }, mnemonic: KeyEvent.VK_S)
                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear, mnemonic: KeyEvent.VK_B)
                button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind { table.isRowSelected }, actionPerformed: {
                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
                            app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        controller.delete()
                    }
                }, mnemonic: KeyEvent.VK_H)
                button(app.getMessage("simplejpa.dialog.close.button"), actionPerformed: {
                    SwingUtilities.getWindowAncestor(mainPanel)?.dispose()
                }, mnemonic: KeyEvent.VK_T)
            }
        }
    }
}
