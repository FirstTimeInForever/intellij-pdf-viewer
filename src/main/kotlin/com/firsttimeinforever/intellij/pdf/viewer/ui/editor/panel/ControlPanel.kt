package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorLeftToolbarActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorRightToolbarActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorToolbarSearchActionGroup
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JPanel
import javax.swing.JTextField

class ControlPanel: JPanel() {
    private val leftToolbar =
        createToolbarForGroup<PdfEditorLeftToolbarActionGroup>("PdfEditorLeftToolbarActionGroup")
    private val searchToolbar =
        createToolbarForGroup<PdfEditorToolbarSearchActionGroup>("PdfEditorToolbarSearchActionGroup")
    private val rightToolbar =
        createToolbarForGroup<PdfEditorRightToolbarActionGroup>("PdfEditorRightToolbarActionGroup")

    val findTextArea = JTextField()
    private val rightPanel = JPanel()

    init {
        layout = GridLayout()
        size = Dimension(Int.MAX_VALUE, 24)
        maximumSize = Dimension(Int.MAX_VALUE, 24)
        preferredSize = Dimension(Int.MAX_VALUE, 24)
        leftToolbar.component.border = null
        searchToolbar.component.border = null
        rightToolbar.component.border = null
        add(leftToolbar.component, Component.LEFT_ALIGNMENT)

        rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
        rightPanel.add(searchToolbar.component)
        searchToolbar.setTargetComponent(rightPanel)
        searchToolbar.adjustTheSameSize(true)
        setActionToolbarSize(searchToolbar, Dimension(26 * 2, 24))
        rightPanel.add(findTextArea)
        findTextArea.preferredSize = Dimension(200, 24)
        findTextArea.minimumSize = Dimension(100, 24)

        setActionToolbarSize(rightToolbar, Dimension(26, 24))
        rightPanel.add(rightToolbar.component)
        rightToolbar.adjustTheSameSize(true)

        rightPanel.size = Dimension(Int.MAX_VALUE, 24)
        rightPanel.maximumSize = Dimension(Int.MAX_VALUE, 24)
        rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
        add(rightPanel, Component.RIGHT_ALIGNMENT)
        findTextArea.addActionListener(this::findTextAreaSearchAction)
        findTextArea.addKeyListener(object: KeyListener {
            override fun keyTyped(e: KeyEvent?) = Unit
            override fun keyReleased(e: KeyEvent?) = Unit

            override fun keyPressed(e: KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_ESCAPE) {
                    findTextArea.text = ""
                    findTextAreaSearchAction()
                    findTextArea.transferFocusUpCycle()
                }
            }
        })
    }

    private fun findTextAreaSearchAction(actionEvent: ActionEvent? = null) {
        val action = ActionManager.getInstance().getAction(FIND_NEXT_ACTION_ID)
        val dataContext = DataManager.getInstance().getDataContext(this)
        val event = AnActionEvent(
            null,
            dataContext,
            ActionPlaces.UNKNOWN,
            Presentation(),
            ActionManager.getInstance(),
            0
        )
        action.actionPerformed(event)
    }

    companion object {
        private const val FIND_NEXT_ACTION_ID =
            "com.firsttimeinforever.intellij.pdf.viewer.actions.FindNextInDocumentAction"
    }
}
