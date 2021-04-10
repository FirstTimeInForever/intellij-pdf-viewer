package com.firsttimeinforever.intellij.pdf.viewer.utility

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.ActionMap

internal object SwingUtilities {
  fun ActionMap.put(key: String, action: (ActionEvent) -> Unit) {
    put(key, object : AbstractAction() {
      override fun actionPerformed(event: ActionEvent) {
        action(event)
      }
    })
  }
}

