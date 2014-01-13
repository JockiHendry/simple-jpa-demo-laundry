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
        source << args.'workOrder'
        String fileReport = args.'fileReport'

        Map parameters = ['logo': ImageIO.read(getResourceAsStream("report/logo.jpg"))]

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(source)
        JasperPrint jasperPrint = JasperFillManager.fillReport(
            getResourceAsStream("report/${fileReport}.jasper"), parameters, dataSource)

        view.mainPanel.clear()
        view.mainPanel.add(new JRViewer(jasperPrint), BorderLayout.CENTER)
    }

}
