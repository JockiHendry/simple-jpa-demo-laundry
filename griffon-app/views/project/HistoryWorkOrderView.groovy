package project

import domain.StatusPekerjaan

import javax.swing.JOptionPane
import java.awt.event.KeyEvent
import java.text.NumberFormat

import static ca.odell.glazedlists.gui.AbstractTableComparatorChooser.*
import static javax.swing.SwingConstants.*
import net.miginfocom.swing.MigLayout
import org.joda.time.*
import java.awt.*
import org.jdesktop.swingx.prompt.PromptSupport

application(title: 'Work Order',
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
            flowLayout(alignment: FlowLayout.LEADING)
            label("Cari")
            textField(id: 'nomorSearch', columns: 10, text: bind('nomorSearch', target: model, mutual: true), actionPerformed: controller.search)
            textField(id: 'pelangganSearch', columns: 10, text: bind('pelangganSearch', target: model, mutual: true), actionPerformed: controller.search)
            comboBox(id: 'jenisJadwalSearch', model: model.jenisJadwalSearch)
            label("Tanggal")
            dateTimePicker(id: 'tanggalMulaiSearch', localDate: bind('tanggalMulaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiSearch', localDate: bind('tanggalSelesaiSearch', target: model, mutual: true), errorPath: 'tanggalMulaiSearch', dateVisible: true, timeVisible: false)
            button(app.getMessage('simplejpa.search.label'), actionPerformed: controller.search, mnemonic: KeyEvent.VK_C)
        }

        panel(constraints: CENTER) {
            borderLayout()
            panel(constraints: PAGE_START, layout: new FlowLayout(FlowLayout.LEADING)) {
                label(text: bind('searchMessage', source: model))
            }
            scrollPane(constraints: CENTER) {
                glazedTable(id: 'table', list: model.workOrderList, sortingStrategy: SINGLE_COLUMN) {
                    glazedColumn(name: 'Nomor', property: 'nomor', width: 120)
                    glazedColumn(name: 'Tanggal', property: 'tanggal', width: 100) {
                        templateRenderer("\${it.toString('dd-MM-yyyy')}")
                    }
                    glazedColumn(name: 'Pelanggan', property: 'pelanggan') {
                        templateRenderer('${it.nama}')
                    }
                    glazedColumn(name: 'Pembayaran', property: 'pembayaran') {
                        templateRenderer(templateExpression: { it?.getNamaDeskripsi()?: '-'})
                    }
                    glazedColumn(name: 'Express?', property: 'express', width: 70) {
                        templateRenderer(templateExpression: {it?'Y': 'N'})
                    }
                    glazedColumn(name: 'Keterangan', property: 'keterangan')
                    glazedColumn(name: 'Total', expression: {it.total}, columnClass: Integer) {
                        templateRenderer('${currencyFormat(it)}', horizontalAlignment: RIGHT)
                    }
                    glazedColumn(name: 'Diterima', expression: {it.getEvent(StatusPekerjaan.DITERIMA)?.tanggal}) {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy hh:mm'): '-'}")
                    }
                    glazedColumn(name: 'Mulai Dikerjakan', expression: {it.getEvent(StatusPekerjaan.DICUCI)?.tanggal}) {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy hh:mm'): '-'}")
                    }
                    glazedColumn(name: 'Estimasi Selesai', property: 'estimasiSelesai') {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy'): '-'}")
                    }
                    glazedColumn(name: 'Selesai Dicuci', expression: {it.getEvent(StatusPekerjaan.DISELESAIKAN)?.tanggal}) {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy hh:mm'): '-'}")
                    }
                    glazedColumn(name: 'Diambil', expression: {it.getEvent(StatusPekerjaan.DIAMBIL)?.tanggal}) {
                        templateRenderer("\${it? it.toString('dd-MM-yyyy hh:mm'): '-'}")
                    }
                }
            }
        }
    }
}

PromptSupport.setPrompt("Nomor WO", nomorSearch)
PromptSupport.setPrompt("Pelanggan", pelangganSearch)