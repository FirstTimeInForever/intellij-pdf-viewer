package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorLeftToolbarActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorRightToolbarActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorToolbarSearchActionGroup
import com.intellij.ide.DataManager
import com.intellij.ide.ui.UISettings
import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.actionSystem.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ControlPanel: JPanel(), UISettingsListener {
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
        leftToolbar.component.border = null
        searchToolbar.component.border = null
        rightToolbar.component.border = null
        add(leftToolbar.component, Component.LEFT_ALIGNMENT)

        rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
        rightPanel.add(searchToolbar.component)
        searchToolbar.setTargetComponent(rightPanel)
        searchToolbar.adjustTheSameSize(true)
        rightPanel.add(findTextArea)
        findTextArea.preferredSize = Dimension(200, 24)
        findTextArea.minimumSize = Dimension(100, 24)

        rightPanel.add(rightToolbar.component)
        rightToolbar.adjustTheSameSize(true)

        rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
        add(rightPanel, Component.RIGHT_ALIGNMENT)
        setupSearchAreaKeybindings()
    }

    private fun setupSearchAreaKeybindings() {
        val inputMap = findTextArea.getInputMap(JComponent.WHEN_FOCUSED)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK, true), ENTER_SHIFT_KEY_EVENT)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_KEY_EVENT)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_KEY_EVENT)
        val actionMap = findTextArea.actionMap
        putAction(actionMap, ESCAPE_KEY_EVENT) {
            findTextArea.text = ""
            findTextArea.transferFocusUpCycle()
        }
        putAction(actionMap, ENTER_SHIFT_KEY_EVENT) {
            findTextAreaSearchAction(FIND_PREVIOUS_ACTION_ID)
        }
        putAction(actionMap, ENTER_KEY_EVENT) {
            findTextAreaSearchAction(FIND_NEXT_ACTION_ID)
        }
        findTextArea.document.addDocumentListener(object: DocumentListener {
            override fun changedUpdate(event: DocumentEvent?) = Unit

            override fun insertUpdate(event: DocumentEvent?) {
                if (event == null) {
                    return
                }
                findTextAreaSearchAction(FIND_NEXT_ACTION_ID)
            }

            override fun removeUpdate(event: DocumentEvent?) {
                insertUpdate(event)
            }
        })
    }

    private fun putAction(actionMap: ActionMap, key: String, action: (ActionEvent) -> Unit) {
        actionMap.put(key, object: AbstractAction() {
            override fun actionPerformed(event: ActionEvent?) {
                if (event == null) {
                    return
                }
                action(event)
            }
        })
    }

    private fun findTextAreaSearchAction(targetActionId: String) {
        val action = ActionManager.getInstance().getAction(targetActionId)
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

    private var presentationModeState = false

    var presentationModeEnabled
        set(value: Boolean) {
            presentationModeState = value
            findTextArea.isEnabled = !value
        }
        get() = presentationModeState

    override fun uiSettingsChanged(settings: UISettings) {
        // Action buttons will be hidded by their update
        findTextArea.isVisible = !settings.presentationMode
        findTextArea.isEnabled = !settings.presentationMode
        if (presentationModeEnabled) {
            findTextArea.isEnabled = false
        }
    }

    companion object {
        private const val ENTER_SHIFT_KEY_EVENT = "Enter+Shift"
        private const val ENTER_KEY_EVENT = "Enter"
        private const val ESCAPE_KEY_EVENT = "Escape"

        private const val FIND_NEXT_ACTION_ID =
            "com.firsttimeinforever.intellij.pdf.viewer.actions.common.FindNextInDocumentAction"

        private const val FIND_PREVIOUS_ACTION_ID =
            "com.firsttimeinforever.intellij.pdf.viewer.actions.common.FindPreviousInDocumentAction"
    }
}
