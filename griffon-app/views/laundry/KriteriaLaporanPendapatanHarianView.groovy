/*
 * Copyright 2014 Jocki Hendry.
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



package laundry

import net.miginfocom.swing.MigLayout

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

        panel(constraints: 'span, growx, wrap') {
            button('OK', actionPerformed: controller.tampilkanLaporan)
            button('Batal', actionPerformed: controller.batal)
        }
    }

}
