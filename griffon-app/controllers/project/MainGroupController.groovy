package project

import org.jdesktop.swingx.plaf.BusyLabelUI
import util.BusyLayerUI

import javax.swing.*
import java.awt.*
import java.awt.event.*
import griffon.util.GriffonNameUtils

class MainGroupController {

    def model
    def view
    def groupId

    def switchPage = { ActionEvent event ->
        // destroying previous MVCGroup before switching to a new one
        if (groupId) {
            app.mvcGroupManager.destroyMVCGroup(groupId)
        }

        BusyLayerUI.instance.show()
        groupId = event.actionCommand

        // destroying current MVCGroup if it was not destroyed properly before
        if (app.mvcGroupManager.findConfiguration(groupId)) {
            app.mvcGroupManager.destroyMVCGroup(groupId)
        }

        def (m,v,c) = app.mvcGroupManager.createMVCGroup(groupId, groupId)

        execInsideUIAsync {
            v.mainPanel.visible = false
            view.mainPanel.add(v.mainPanel, groupId)
            view.cardLayout.show(view.mainPanel, groupId)
            view.mainFrame.title = "${app.config.application.title} ${app.metadata.getApplicationVersion()}: ${GriffonNameUtils.getNaturalName(groupId)}"
            BusyLayerUI.instance.hide()
        }
    }

}