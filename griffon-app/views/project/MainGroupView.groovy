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
    action(id: 'pelanggan', name: 'Pelanggan', actionCommandKey: 'pelanggan', mnemonic: KeyEvent.VK_P,
        smallIcon: imageIcon('/menu_pelanggan.png'), closure: controller.switchPage)
    action(id: 'workOrder', name: 'Penerimaan', actionCommandKey: 'workOrder', mnemonic: KeyEvent.VK_O,
        smallIcon: imageIcon('/menu_workorder.png'), closure: controller.switchPage)
    action(id: 'pembayaranSignedBill', name: 'Tagihan', actionCommandKey: 'pembayaranSignedBill', mnemonic: KeyEvent.VK_T,
        smallIcon: imageIcon('/menu_tagihan.png'), closure: controller.switchPage)
    action(id: 'antrianCuci', name: 'Mulai Pengerjaan', actionCommandKey: 'antrianCuci', mnemonic: KeyEvent.VK_A,
        smallIcon: imageIcon('/menu_antriancuci.png'), closure: controller.switchPage)
    action(id: 'pencucian', name: 'Selesai Pengerjaan', actionCommandKey: 'pencucian', mnemonic: KeyEvent.VK_C,
        smallIcon: imageIcon('/menu_pencucian.png'), closure: controller.switchPage)
    action(id: 'pengambilan', name: 'Pengambilan', actionCommandKey: 'pengambilan', mnemonic: KeyEvent.VK_B,
        smallIcon: imageIcon('/menu_pengambilan.png'), closure: controller.switchPage)
    action(id: 'laporan', name: 'Laporan', actionCommandKey: 'laporan', mnemonic: KeyEvent.VK_L,
            smallIcon: imageIcon('/menu_report.png'), closure: controller.switchPage)
    action(id: 'maintenance', name: 'Maintenance', actionCommandKey: 'maintenance', mnemonic: KeyEvent.VK_M,
        smallIcon: imageIcon('/menu_maintenance.png'), closure: popupMaintenance)
    action(id: 'kategori', name: 'Kategori', actionCommandKey: 'kategori', mnemonic: KeyEvent.VK_K,
        smallIcon: imageIcon('/menu_maintenance_kategori.png'), closure: controller.switchPage)
    action(id: 'bahan', name: 'Bahan', actionCommandKey: 'bahan', mnemonic: KeyEvent.VK_H,
        smallIcon: imageIcon('/menu_maintenance_bahan.png'), closure: controller.switchPage)
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
  preferredSize: new Dimension(500,500),
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
        menuItem(action: bahan)
        menuItem(action: jenisWork)
    }

    borderLayout()

    toolBar(constraints: BorderLayout.PAGE_START, floatable: false) {
        buttonGroup(id: 'buttons')
		toggleButton(buttonGroup: buttons, action: pelanggan, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
		toggleButton(buttonGroup: buttons, action: workOrder, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        //toggleButton(buttonGroup: buttons, action: pembayaranSignedBill, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        toggleButton(buttonGroup: buttons, action: antrianCuci, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        toggleButton(buttonGroup: buttons, action: pencucian, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        toggleButton(buttonGroup: buttons, action: pengambilan, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        toggleButton(buttonGroup: buttons, action: laporan, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
        toggleButton(buttonGroup: buttons, action: maintenance, id: 'maintenanceButton', verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)
    }

    panel(id: "mainPanel", constraints: BorderLayout.CENTER) {
        cardLayout(id: "cardLayout")
    }

    statusBar(constraints: BorderLayout.PAGE_END, border: BorderFactory.createBevelBorder(BevelBorder.LOWERED)) {
        busyLabel(id: "busyLabel", busy: true, visible: false)
    }
}
