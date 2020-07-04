package com.firsttimeinforever.intellij.pdf.viewer.settings;

import com.intellij.ui.ColorPanel
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected
import java.awt.*
import javax.swing.JCheckBox
import javax.swing.JLabel
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
            iconColorPanel.isEnabled = it.isSelected
        }
    }

    private val backgroundColorPanel = ColorPanel()
    private val foregroundColorPanel = ColorPanel()
    private val iconColorPanel = ColorPanel()

    val enableDocumentAutoReload
        get() = enableDocumentAutoReloadCheckBox.isSelected

    val useCustomColors
        get() = useCustomColorsCheckBox.isSelected

    val customBackgroundColor
        get() = backgroundColorPanel.selectedColor

    val customForegroundColor
        get() = foregroundColorPanel.selectedColor

    val customIconColor
        get() = iconColorPanel.selectedColor

    fun loadSettings() {
        enableDocumentAutoReloadCheckBox.isSelected = settings.enableDocumentAutoReload
        useCustomColorsCheckBox.isSelected = settings.useCustomColors
        backgroundColorPanel.selectedColor = Color(settings.customBackgroundColor)
        foregroundColorPanel.selectedColor = Color(settings.customForegroundColor)
        iconColorPanel.selectedColor = Color(settings.customIconColor)
        useCustomColorsCheckBox.run {
            backgroundColorPanel.isEnabled = isSelected
            foregroundColorPanel.isEnabled = isSelected
            iconColorPanel.isEnabled = isSelected
        }
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
                    object: JPanel(GridBagLayout()) {
                        init {
                            GridBagConstraints().also {
                                it.anchor = GridBagConstraints.LINE_START
                                it.ipadx = 8
                                add(JLabel("Background:"), it)
                                add(backgroundColorPanel, it)
                            }
                            GridBagConstraints().also {
                                it.gridy = 1
                                it.anchor = GridBagConstraints.LINE_START
                                it.ipadx = 8
                                add(JLabel("Foreground:"), it)
                                add(foregroundColorPanel, it)
                            }
                            GridBagConstraints().also {
                                it.gridy = 2
                                it.anchor = GridBagConstraints.LINE_START
                                it.ipadx = 8
                                add(JLabel("Icons:"), it)
                                add(iconColorPanel, it)
                            }
                        }
                    }()
                }
                row {
                    link("Set current theme colors") {
                        PdfViewerSettings.run {
                            backgroundColorPanel.selectedColor = defaultBackgroundColor
                            foregroundColorPanel.selectedColor = defaultForegroundColor
                            iconColorPanel.selectedColor = defaultIconColor
                        }
                    }.enableIf(useCustomColorsCheckBox.selected)
                }
                row {
                    label("Please pay extra attention to icons color, as it can be incorrectly inherited from the current theme.")
                }
            }
        })
        loadSettings()
    }
}
