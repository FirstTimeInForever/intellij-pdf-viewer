package com.firsttimeinforever.intellij.pdf.viewer.settings.form;

import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.intellij.ui.ColorPanel
import com.intellij.ui.layout.panel
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JCheckBox
import javax.swing.JPanel

class PdfViewerSettingsForm: JPanel() {
    private val settings = PdfViewerSettings.instance

    private val enableDocumentAutoReloadCheckBox = JCheckBox(
        "Reload document on change",
        settings.enableDocumentAutoReload
    )

    private val useCustomColorsCheckBox: JCheckBox = JCheckBox(
        "Use custom colors",
        settings.useCustomColors
    ).also {
        it.addItemListener { _ ->
            backgroundColorPanel.isEnabled = it.isSelected
            foregroundColorPanel.isEnabled = it.isSelected
        }
    }

    private val backgroundColorPanel = ColorPanel()
    private val foregroundColorPanel = ColorPanel()

    val enableDocumentAutoReload
        get() = enableDocumentAutoReloadCheckBox.isSelected

    val useCustomColors
        get() = useCustomColorsCheckBox.isSelected

    val customBackgroundColor
        get() = backgroundColorPanel.selectedColor

    val customForegroundColor
        get() = foregroundColorPanel.selectedColor

    fun loadSettings() {
        enableDocumentAutoReloadCheckBox.isSelected = settings.enableDocumentAutoReload
        useCustomColorsCheckBox.isSelected = settings.useCustomColors
        backgroundColorPanel.selectedColor = Color(settings.customBackgroundColor)
        foregroundColorPanel.selectedColor = Color(settings.customForegroundColor)
        backgroundColorPanel.isEnabled = useCustomColorsCheckBox.isSelected
        foregroundColorPanel.isEnabled = useCustomColorsCheckBox.isSelected
    }

    init {
        layout = BorderLayout()
        add(panel {
            titledRow("General") {
                row {
                    enableDocumentAutoReloadCheckBox()
                }
            }
            titledRow("Viewer colors") {
                row {
                    useCustomColorsCheckBox()
                }
                row {
                    label("Background:")
                    backgroundColorPanel()
                }
                row {
                    label("Foreground:")
                    foregroundColorPanel()
                }
            }
        })
        loadSettings()
    }
}
