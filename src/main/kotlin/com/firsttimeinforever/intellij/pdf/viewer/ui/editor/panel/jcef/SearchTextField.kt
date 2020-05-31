package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class SearchTextField: JTextField() {
    init {
        val inputMap = getInputMap(JComponent.WHEN_FOCUSED)
        inputMap.put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK, true),
            ENTER_SHIFT_KEY_EVENT
        )
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_KEY_EVENT)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_KEY_EVENT)
        putAction(actionMap, ESCAPE_KEY_EVENT) {
            text = ""
            transferFocusUpCycle()
        }
        putAction(actionMap, ENTER_SHIFT_KEY_EVENT) {
            findTextAreaSearchAction(FIND_PREVIOUS_ACTION_ID)
        }
        putAction(actionMap, ENTER_KEY_EVENT) {
            findTextAreaSearchAction(FIND_NEXT_ACTION_ID)
        }
        document.addDocumentListener(object: DocumentListener {
            override fun insertUpdate(event: DocumentEvent?) {
                if (event == null) {
                    return
                }
                findTextAreaSearchAction(FIND_NEXT_ACTION_ID)
            }

            override fun removeUpdate(event: DocumentEvent?) = insertUpdate(event)

            override fun changedUpdate(event: DocumentEvent?) = Unit
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
