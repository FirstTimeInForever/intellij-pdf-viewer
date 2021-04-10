package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfActionUtils.createActionToolbar
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation.PdfPresentationController
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation.PdfPresentationModeListener
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view.salvaged.SearchTextField
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
  Disposable {
  private val leftToolbar = createActionToolbar("pdf.viewer.LeftToolbarActionGroup")

  //    private val searchToolbar = createActionToolbar("PdfEditorToolbarSearchActionGroup")
  private val rightToolbar = createActionToolbar("pdf.viewer.RightToolbarActionGroup")
  private val searchTextField = SearchTextField()
  private val rightPanel = JPanel()
  private val messageBusConnection = project.messageBus.connect()

  init {
    Disposer.register(this, messageBusConnection)
    leftToolbar.component.border = null
//        searchToolbar.component.border = null
    rightToolbar.component.border = null
    add(leftToolbar.component, Component.LEFT_ALIGNMENT)

    rightPanel.layout = FlowLayout(FlowLayout.RIGHT, 0, 0)
    // rightPanel.add(searchToolbar.component)
//        searchToolbar.setTargetComponent(rightPanel)
//        searchToolbar.adjustTheSameSize(true)
//        rightPanel.add(searchTextField)
//        searchTextField.preferredSize = Dimension(200, 24)
//        searchTextField.minimumSize = Dimension(100, 24)
//
    rightPanel.add(rightToolbar.component)
    rightToolbar.adjustTheSameSize(true)
    rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
    add(rightPanel, Component.RIGHT_ALIGNMENT)
//
    messageBusConnection.subscribe(UISettingsListener.TOPIC, this)
    messageBusConnection.subscribe(PdfPresentationModeListener.TOPIC, this)
//        with(presentationModeController) {
//            addEnterListener {
//                presentationModeEnabled = true
//                false
//            }
//            addExitListener {
//                presentationModeEnabled = false
//                false
//            }
//        }
  }

  private var presentationModeEnabled = false
    set(value) {
      field = value
      searchTextField.isEnabled = !value
    }

  override fun uiSettingsChanged(settings: UISettings) {
    // Action buttons will be hidded by their update
    searchTextField.isVisible = !settings.presentationMode
    searchTextField.isEnabled = !settings.presentationMode
    if (presentationModeEnabled) {
      searchTextField.isEnabled = false
    }
  }

  override fun presentationModeChanged(controller: PdfPresentationController) {
    TODO("Not yet implemented")
  }

  override fun dispose() = Unit
}
