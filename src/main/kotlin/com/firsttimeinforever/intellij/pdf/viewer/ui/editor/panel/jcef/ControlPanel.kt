package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorLeftToolbarActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorRightToolbarActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.actions.PdfEditorToolbarSearchActionGroup
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.ActionToolbarUtils.createToolbarForGroup
import com.intellij.ide.ui.UISettings
import com.intellij.ide.ui.UISettingsListener
import com.intellij.util.messages.MessageBus
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.JPanel

class ControlPanel(
    messageBus: MessageBus,
    presentationModeController: PresentationModeController
): JPanel(), UISettingsListener {
    private val leftToolbar =
        createToolbarForGroup<PdfEditorLeftToolbarActionGroup>("PdfEditorLeftToolbarActionGroup")
    private val searchToolbar =
        createToolbarForGroup<PdfEditorToolbarSearchActionGroup>("PdfEditorToolbarSearchActionGroup")
    private val rightToolbar =
        createToolbarForGroup<PdfEditorRightToolbarActionGroup>("PdfEditorRightToolbarActionGroup")

    val searchTextField = SearchTextField()
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
        rightPanel.add(searchTextField)
        searchTextField.preferredSize = Dimension(200, 24)
        searchTextField.minimumSize = Dimension(100, 24)

        rightPanel.add(rightToolbar.component)
        rightToolbar.adjustTheSameSize(true)

        rightPanel.preferredSize = Dimension(Int.MAX_VALUE, 24)
        add(rightPanel, Component.RIGHT_ALIGNMENT)
        messageBus.connect().subscribe(UISettingsListener.TOPIC, this)
        with(presentationModeController) {
            addEnterListener {
                presentationModeEnabled = true
                false
            }
            addExitListener {
                presentationModeEnabled = false
                false
            }
        }
    }

    private var presentationModeState = false

    var presentationModeEnabled
        set(value) {
            presentationModeState = value
            searchTextField.isEnabled = !value
        }
        get() = presentationModeState

    override fun uiSettingsChanged(settings: UISettings) {
        // Action buttons will be hidded by their update
        searchTextField.isVisible = !settings.presentationMode
        searchTextField.isEnabled = !settings.presentationMode
        if (presentationModeEnabled) {
            searchTextField.isEnabled = false
        }
    }
}
