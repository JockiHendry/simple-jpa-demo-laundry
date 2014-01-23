package laundry

import net.miginfocom.swing.MigLayout

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

    panel(id: 'mainPanel', layout: new MigLayout('hidemode 2', '[right][left,grow]', '')) {
        label('Periode')
        panel(constraints: 'wrap') {
            flowLayout()
            dateTimePicker(id: 'tanggalMulaiCari', localDate: bind('tanggalMulaiCari', target: model, mutual: true),
                dateVisible: true, timeVisible: false)
            label(" s/d ")
            dateTimePicker(id: 'tanggalSelesaiCari', localDate: bind('tanggalSelesaiCari', target: model, mutual: true),
                dateVisible: true, timeVisible: false)
        }

        label('Dan', constraints: 'wrap')
        label('Membership Pelanggan')
        comboBox(id: 'membershipSearch', model: model.membershipSearch, constraints: 'wrap')

        label('Dan', constraints: 'wrap')
        label('Nama Pelanggan')
        textField(text: bind('namaPelanggan', target: model, mutual: true), columns: 20, constraints: 'wrap')

        label('Dan', constraints: 'wrap')
        label('Kategori')
        textField(text: bind('kategori', target: model, mutual: true), columns: 20, constraints: 'wrap')

        label('Dan', constraints: 'wrap')
        label('Pakaian')
        textField(text: bind('itemPakaian', target: model, mutual: true), columns: 20, constraints: 'wrap')

        panel(constraints: 'span, growx, wrap') {
            button('OK', actionPerformed: controller.tampilkanLaporan)
            button('Batal', actionPerformed: controller.batal)
        }
    }

}
