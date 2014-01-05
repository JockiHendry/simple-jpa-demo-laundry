package project

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*

application(title: 'Event Pekerjaan',
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
                glazedTable(id: 'table', list: model.eventPekerjaanList, sortingStrategy: SINGLE_COLUMN, onValueChanged: controller.tableSelectionChanged) {
                    glazedColumn(name: 'Tanggal', property: 'tanggal')
                    glazedColumn(name: 'Status', property: 'status')
                }
            }
        }

//        taskPane(id: "form", layout: new MigLayout('', '[right][left][left,grow]', ''), constraints: PAGE_END) {
//            label('Tanggal:')
//            dateTimePicker(id: 'tanggal', localDate: bind('tanggal', target: model, mutual: true), errorPath: 'tanggal', dateVisible: true, timeVisible: false)
//            errorLabel(path: 'tanggal', constraints: 'wrap')
//            label('Status:')
//            comboBox(id: 'status', model: model.status, errorPath: 'status')
//            errorLabel(path: 'status', constraints: 'wrap')
//
//            panel(constraints: 'span, growx, wrap') {
//                flowLayout(alignment: FlowLayout.LEADING)
//                button(app.getMessage("simplejpa.dialog.save.button"), actionPerformed: {
//                    if (JOptionPane.showConfirmDialog(mainPanel, 'Apakah Anda yakin akan melakukan modifikasi status secara manual tanpa notifikasi dari pihak terkait?',
//                            app.getMessage("simplejpa.dialog.update.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
//                        return
//                    }
//                    controller.save()
//                    tanggal.requestFocusInWindow()
//                })
//                button(app.getMessage("simplejpa.dialog.cancel.button"), visible: bind { table.isRowSelected }, actionPerformed: controller.clear)
//                button(app.getMessage("simplejpa.dialog.delete.button"), visible: bind { table.isRowSelected }, actionPerformed: {
//                    if (JOptionPane.showConfirmDialog(mainPanel, app.getMessage("simplejpa.dialog.delete.message"),
//                            app.getMessage("simplejpa.dialog.delete.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
//                        controller.delete()
//                    }
//                })
//                button(app.getMessage("simplejpa.dialog.close.button"), actionPerformed: {
//                    SwingUtilities.getWindowAncestor(mainPanel)?.dispose()
//                })
//            }
//        }
    }
}
