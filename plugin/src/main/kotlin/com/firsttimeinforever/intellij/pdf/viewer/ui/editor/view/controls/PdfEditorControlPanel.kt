package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.controls

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfActionUtils.createActionToolbar
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.BoxLayout
import javax.swing.JPanel

/**
 * The default pdf.js toolbar is hidden using fixes.css, which is inserted in web-view/bootstrap/build.gradle.kts
 */
class PdfEditorControlPanel: JPanel(GridLayout()), Disposable {
  private val leftToolbar = createActionToolbar(
    "pdf.viewer.LeftToolbarActionGroup",
    ActionPlaces.EDITOR_TOOLBAR,
    this
  )

  private val rightToolbar = createActionToolbar(
    "pdf.viewer.RightToolbarActionGroup",
    ActionPlaces.EDITOR_TOOLBAR,
    this
  )

  private val rightPanel = JPanel()

  init {
    // BoxLayout resizes components to fit the container
    layout = BoxLayout(this, BoxLayout.X_AXIS)
    leftToolbar.component.border = null
    rightToolbar.component.border = null
    add(leftToolbar.component)

    // Use a panel to store the right toolbar, to align the actions to the right
    rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
    rightPanel.add(rightToolbar.component)
    add(rightPanel, RIGHT_ALIGNMENT)
  }

  override fun dispose() = Unit
}
