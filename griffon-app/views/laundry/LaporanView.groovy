package laundry

import java.awt.FlowLayout

application(title: 'laundry',
        preferredSize: [320, 240],
        pack: true,
        //location: [50,50],
        locationByPlatform: true,
        iconImage: imageIcon('/griffon-icon-48x48.png').image,
        iconImages: [imageIcon('/griffon-icon-48x48.png').image,
                imageIcon('/griffon-icon-32x32.png').image,
                imageIcon('/griffon-icon-16x16.png').image]) {

    panel(id: 'mainPanel') {
        borderLayout()

        panel(constraints: PAGE_START) {
            flowLayout(alignment: FlowLayout.LEADING)
            label('Pilih Laporan: ')
            comboBox(id: 'jenisLaporan', model: model.jenisLaporanSearch)
            label("Periode: ")
            dateTimePicker(id: 'tanggalMulaiCari', localDate: bind('tanggalMulaiCari', target: model, mutual: true),
                    dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiCari', localDate: bind('tanggalSelesaiCari', target: model, mutual: true),
                    dateVisible: true, timeVisible: false)
            button('Tampilkan Laporan', actionPerformed: controller.search)
        }

        panel(id: 'content', constraints: CENTER) {
            borderLayout()
        }

    }

}
