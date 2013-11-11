package $packageName

import org.jdesktop.swingx.JXStatusBar
import javax.swing.*
import javax.swing.border.*
import java.awt.*
import java.awt.event.*

actions {
<%
   domainClassLists.each { String name ->
        out << "\taction(id: '${prop(name)}', name: '${natural(name)}', actionCommandKey: '${prop(name)}', closure: controller.switchPage)\n"
   }
%>
}

application(id: 'mainFrame',
  title: app.config.application.title,
  extendedState: JFrame.MAXIMIZED_BOTH,
  pack: true,
  locationByPlatform: true) {

    borderLayout()

    toolBar(constraints: BorderLayout.PAGE_START, floatable: false) {
        buttonGroup(id: 'buttons')
<%
    domainClassLists.each { String name ->
        out << "\t\ttoggleButton(buttonGroup: buttons, action: ${prop(name)}, verticalTextPosition: SwingConstants.BOTTOM, horizontalTextPosition: SwingConstants.CENTER)\n"
    }
%>
    }

    panel(id: "mainPanel", constraints: BorderLayout.CENTER) {
        cardLayout(id: "cardLayout")
    }

    statusBar(constraints: BorderLayout.PAGE_END, border: BorderFactory.createBevelBorder(BevelBorder.LOWERED)) {
        busyLabel(id: "busyLabel", busy: true, visible: false)
    }
}
