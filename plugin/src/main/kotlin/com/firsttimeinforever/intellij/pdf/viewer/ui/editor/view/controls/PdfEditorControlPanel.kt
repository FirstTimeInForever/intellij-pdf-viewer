package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.controls

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfActionUtils.createActionToolbar
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JPanel

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
    leftToolbar.component.border = null
    rightToolbar.component.border = null
    add(leftToolbar.component, Component.LEFT_ALIGNMENT)

    rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)

    rightPanel.add(rightToolbar.component)
    rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
    add(rightPanel, Component.RIGHT_ALIGNMENT)
  }

  override fun dispose() = Unit
}
