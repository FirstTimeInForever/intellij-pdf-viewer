package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.controls

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfActionUtils.performAction
import com.firsttimeinforever.intellij.pdf.viewer.utility.DocumentListenerAdapter
import com.firsttimeinforever.intellij.pdf.viewer.utility.SwingUtilities.put
import org.jetbrains.annotations.ApiStatus
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.KeyStroke.getKeyStroke
import javax.swing.event.DocumentEvent

@ApiStatus.Internal
internal class SearchTextField : JTextField() {
  init {
    with(getInputMap(JComponent.WHEN_FOCUSED)) {
      put(
        getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK, true),
        ENTER_SHIFT_KEY
      )
      put(getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_KEY)
      put(getKeyStroke(KeyEvent.VK_ESCAPE, 0), ESCAPE_KEY)
    }
    actionMap.put(ESCAPE_KEY) {
      text = ""
      transferFocusUpCycle()
    }
    actionMap.put(ENTER_SHIFT_KEY) {
      performAction(SEARCH_BACKWARD_ACTION_ID, this)
    }
    actionMap.put(ENTER_KEY) {
      performAction(SEARCH_FORWARD_ACTION_ID, this)
    }
    document.addDocumentListener(object : DocumentListenerAdapter() {
      override fun insertUpdate(event: DocumentEvent) {
        performAction(SEARCH_FORWARD_ACTION_ID, this@SearchTextField)
      }

      override fun removeUpdate(event: DocumentEvent) = insertUpdate(event)
    })
  }

  companion object {
    private const val ENTER_SHIFT_KEY = "Enter+Shift"
    private const val ENTER_KEY = "Enter"
    private const val ESCAPE_KEY = "Escape"

    private const val SEARCH_FORWARD_ACTION_ID = "pdf.viewer.FindForwardAction"
    private const val SEARCH_BACKWARD_ACTION_ID = "pdf.viewer.FindBackwardAction"
  }
}
