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

class PdfEditorControlPanel(project: Project) :
  JPanel(GridLayout()),
  UISettingsListener,
  PdfPresentationModeListener,
  Disposable
{
  private val leftToolbar = createActionToolbar("pdf.viewer.LeftToolbarActionGroup")
  private val searchToolbar = createActionToolbar("pdf.viewer.PdfEditorToolbarSearchActionGroup")
  private val rightToolbar = createActionToolbar("pdf.viewer.RightToolbarActionGroup")
  private val searchTextField = SearchTextField()
  private val rightPanel = JPanel()
  private val messageBusConnection = project.messageBus.connect()

  init {
    Disposer.register(this, messageBusConnection)
    leftToolbar.component.border = null
    searchToolbar.component.border = null
    rightToolbar.component.border = null
    add(leftToolbar.component, Component.LEFT_ALIGNMENT)

    rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
    rightPanel.add(searchTextField)
    searchTextField.preferredSize = Dimension(200, 24)
    searchTextField.minimumSize = Dimension(100, 24)
    rightPanel.add(searchToolbar.component)
    searchToolbar.setTargetComponent(rightPanel)
    searchToolbar.adjustTheSameSize(true)

    rightPanel.add(rightToolbar.component)
    rightToolbar.adjustTheSameSize(true)
    rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
    add(rightPanel, Component.RIGHT_ALIGNMENT)

    messageBusConnection.subscribe(UISettingsListener.TOPIC, this)
    messageBusConnection.subscribe(PdfPresentationModeListener.TOPIC, this)
  }

  var searchText: String
    get() = searchTextField.text
    set(value) {
      searchTextField.text = value
    }

  override fun uiSettingsChanged(settings: UISettings) {
    // Action buttons will be hidden by their update
    searchTextField.isVisible = !settings.presentationMode
    searchTextField.isEnabled = !settings.presentationMode
  }

  override fun presentationModeChanged(controller: PdfPresentationController) {
    searchTextField.isVisible = !controller.isPresentationModeActive
    searchTextField.isEnabled = !controller.isPresentationModeActive
  }

  override fun dispose() = Unit
}
