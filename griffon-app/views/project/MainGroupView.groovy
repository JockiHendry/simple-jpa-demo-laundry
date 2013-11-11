package project

import org.jdesktop.swingx.JXStatusBar
import javax.swing.*
import javax.swing.border.*
import java.awt.*
import java.awt.event.*

def popupMaintenance = {
    maintenancePopup.show(maintenanceButton, 0, maintenanceButton.getHeight())
}

actions {
//	action(id: 'eventPekerjaan', name: 'Event Pekerjaan', actionCommandKey: 'eventPekerjaan', closure: controller.switchPage)
//	action(id: 'itemWorkOrder', name: 'Item Work Order', actionCommandKey: 'itemWorkOrder', closure: controller.switchPage)
/*
	action(id: 'pelanggan', name: 'Pelanggan', actionCommandKey: 'pelanggan', closure: controller.switchPage)
	action(id: 'pembayaran', name: 'Pembayaran', actionCommandKey: 'pembayaran', closure: controller.switchPage)
	action(id: 'pembayaranCash', name: 'Pembayaran Cash', actionCommandKey: 'pembayaranCash', closure: controller.switchPage)
	action(id: 'pembayaranCompliant', name: 'Pembayaran Compliant', actionCommandKey: 'pembayaranCompliant', closure: controller.switchPage)
	action(id: 'pembayaranKartuDebit', name: 'Pembayaran Kartu Debit', actionCommandKey: 'pembayaranKartuDebit', closure: controller.switchPage)
	action(id: 'pembayaranSignedBill', name: 'Pembayaran Signed Bill', actionCommandKey: 'pembayaranSignedBill', closure: controller.switchPage)
	action(id: 'workOrder', name: 'Work Order', actionCommandKey: 'workOrder', closure: controller.switchPage)
*/
    action(id: 'maintenance', name: 'Maintenance', actionCommandKey: 'maintenance', mnemonic: KeyEvent.VK_M,
        smallIcon: imageIcon('/menu_maintenance.png'), closure: popupMaintenance)
    action(id: 'kategori', name: 'Kategori', actionCommandKey: 'kategori', mnemonic: KeyEvent.VK_K,
        smallIcon: imageIcon('/menu_maintenance_kategori.png'), closure: controller.switchPage)
    action(id: 'itemPakaian', name: 'Item Pakaian', actionCommandKey: 'itemPakaian', mnemonic: KeyEvent.VK_P,
        smallIcon: imageIcon('/menu_maintenance_itemPakaian.png'), closure: controller.switchPage)
    action(id: 'jenisWork', name: 'Jenis Pekerjaan', actionCommandKey: 'jenisWork', mnemonic: KeyEvent.VK_J,
        smallIcon: imageIcon('/menu_maintenance_jenisWork.png'), closure: controller.switchPage)
    action(id: 'work', name: 'Daftar Harga', actionCommandKey: 'work', mnemonic: KeyEvent.VK_W,
        smallIcon: imageIcon('/menu_maintenance_work.png'), closure: controller.switchPage)
}

application(id: 'mainFrame',
  title: "${app.config.application.title} ${app.metadata.getApplicationVersion()}",
  extendedState: JFrame.MAXIMIZED_BOTH,
  pack: true,
  iconImage:   imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
        imageIcon('/griffon-icon-32x32.png').image,
        imageIcon('/griffon-icon-16x16.png').image],
  locationByPlatform: true) {

    popupMenu(id: "maintenancePopup") {
        menuItem(action: work)
        menuItem(action: itemPakaian)
        menuItem(action: kategori)
        menuItem(action: jenisWork)
    }

    borderLayout()

    toolBar(constraints: BorderLayout.PAGE_START, floatable: false) {
        buttonGroup(id: 'buttons')
//		toggleButton(buttonGroup: buttons, action: eventPekerjaan, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: itemWorkOrder, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: pelanggan, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: pembayaran, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: pembayaranCash, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: pembayaranCompliant, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: pembayaranKartuDebit, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: pembayaranSignedBill, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
//		toggleButton(buttonGroup: buttons, action: workOrder, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        toggleButton(buttonGroup: buttons, action: maintenance, id: 'maintenanceButton', verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
    }

    panel(id: "mainPanel", constraints: BorderLayout.CENTER) {
        cardLayout(id: "cardLayout")
    }

    statusBar(constraints: BorderLayout.PAGE_END, border: BorderFactory.createBevelBorder(BevelBorder.LOWERED)) {
        busyLabel(id: "busyLabel", busy: true, visible: false)
    }
}
