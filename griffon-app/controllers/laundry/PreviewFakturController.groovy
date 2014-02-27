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

import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.swing.JRViewer

import javax.imageio.ImageIO
import java.awt.BorderLayout

class PreviewFakturController {

    def model
    def view

    void mvcGroupInit(Map args) {
        List source = []
        source << args.'source'
        String fileReport = args.'fileReport'

        Map parameters = ['logo': ImageIO.read(getResourceAsStream("report/logo.jpg"))]

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(source)
        JasperPrint jasperPrint = JasperFillManager.fillReport(
            getResourceAsStream("report/${fileReport}.jasper"), parameters, dataSource)

        view.mainPanel.clear()
        view.mainPanel.add(new JRViewer(jasperPrint), BorderLayout.CENTER)
    }

}
