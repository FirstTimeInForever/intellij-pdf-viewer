package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.controls

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfActionUtils.createActionToolbar
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation.PdfPresentationController
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation.PdfPresentationModeListener
import com.intellij.ide.ui.UISettings
import com.intellij.ide.ui.UISettingsListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JPanel

class PdfEditorControlPanel(project: Project) : JPanel(GridLayout()), Disposable {
  private val leftToolbar = createActionToolbar("pdf.viewer.LeftToolbarActionGroup")
  private val rightToolbar = createActionToolbar("pdf.viewer.RightToolbarActionGroup")
  private val rightPanel = JPanel()
  private val messageBusConnection = project.messageBus.connect()

  init {
    Disposer.register(this, messageBusConnection)
    leftToolbar.component.border = null
    rightToolbar.component.border = null
    add(leftToolbar.component, Component.LEFT_ALIGNMENT)

    rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)

    rightPanel.add(rightToolbar.component)
    rightToolbar.adjustTheSameSize(true)
    rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
    add(rightPanel, Component.RIGHT_ALIGNMENT)
  }

  override fun dispose() = Unit
}
