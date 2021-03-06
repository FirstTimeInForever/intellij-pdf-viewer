package com.firsttimeinforever.intellij.pdf.viewer.settings

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.model.SidebarViewMode
import com.intellij.openapi.observable.properties.GraphPropertyImpl.Companion.graphProperty
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.ui.ColorPanel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.intellij.ui.layout.selected
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class PdfViewerSettingsForm : JPanel() {
  private val settings
    get() = PdfViewerSettings.instance

  private val enableDocumentAutoReloadCheckBox = JCheckBox(
    PdfViewerBundle.message("pdf.viewer.settings.reload.document"),
    settings.enableDocumentAutoReload
  )

  private val useCustomColorsCheckBox: JCheckBox = JCheckBox(
    PdfViewerBundle.message("pdf.viewer.settings.use.custom.colors"),
    settings.useCustomColors
  ).apply {
    addItemListener {
      backgroundColorPanel.isEnabled = isSelected
      foregroundColorPanel.isEnabled = isSelected
      iconColorPanel.isEnabled = isSelected
    }
  }

  private val backgroundColorPanel = ColorPanel()
  private val foregroundColorPanel = ColorPanel()
  private val iconColorPanel = ColorPanel()

  private val documentColorsInvertIntensityField = JTextField(
    "Invert Colors Intensity",
    settings.documentColorsInvertIntensity
  ).also {
    it.isEnabled = PdfViewerSettings.enableExperimentalFeatures
  }

  val enableDocumentAutoReload = PropertyGraph().graphProperty { settings.enableDocumentAutoReload }

  val useCustomColors
    get() = useCustomColorsCheckBox.isSelected

  val customBackgroundColor
    get() = backgroundColorPanel.selectedColor

  val customForegroundColor
    get() = foregroundColorPanel.selectedColor

  val customIconColor
    get() = iconColorPanel.selectedColor

  var documentColorsInvertIntensity = settings.documentColorsInvertIntensity
    private set

  val defaultSidebarViewMode = PropertyGraph().graphProperty { settings.defaultSidebarViewMode }

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
    documentColorsInvertIntensityField.text = settings.documentColorsInvertIntensity.toString()
    defaultSidebarViewMode.set(settings.defaultSidebarViewMode)
  }

  init {
    layout = BorderLayout()
    add(panel {
      titledRow(PdfViewerBundle.message("pdf.viewer.settings.general")) {
        row {
          checkBox(PdfViewerBundle.message("pdf.viewer.settings.reload.document"), enableDocumentAutoReload)
        }
        row {
          label("Default sidebar view mode")
          val renderer = SimpleListCellRenderer.create<SidebarViewMode> { label, value, _ ->
            label.text = when (value) {
              SidebarViewMode.NONE -> "Closed"
              SidebarViewMode.THUMBNAILS -> "Thumbnails"
              // SidebarViewMode.OUTLINE -> "Outline (document structure)"
              SidebarViewMode.ATTACHMENTS -> "Attachments"
              else -> "Outline (document structure)"
            }
          }
          comboBox(DefaultComboBoxModel(SidebarViewMode.values()), defaultSidebarViewMode, renderer)
        }
      }
      titledRow(PdfViewerBundle.message("pdf.viewer.settings.viewer.colors")) {
        row {
          useCustomColorsCheckBox()
        }
        row {
          JPanel(GridBagLayout()).apply {
            GridBagConstraints().also {
              it.anchor = GridBagConstraints.LINE_START
              it.ipadx = 8
              add(JLabel(PdfViewerBundle.message("pdf.viewer.settings.background")), it)
              add(backgroundColorPanel, it)
            }
            GridBagConstraints().also {
              it.gridy = 1
              it.anchor = GridBagConstraints.LINE_START
              it.ipadx = 8
              add(JLabel(PdfViewerBundle.message("pdf.viewer.settings.foreground")), it)
              add(foregroundColorPanel, it)
            }
            GridBagConstraints().also {
              it.gridy = 2
              it.anchor = GridBagConstraints.LINE_START
              it.ipadx = 8
              add(JLabel(PdfViewerBundle.message("pdf.viewer.settings.icons")), it)
              add(iconColorPanel, it)
            }
          }()
        }
        row {
          link(PdfViewerBundle.message("pdf.viewer.settings.set.current.theme")) {
            PdfViewerSettings.run {
              backgroundColorPanel.selectedColor = defaultBackgroundColor
              foregroundColorPanel.selectedColor = defaultForegroundColor
              iconColorPanel.selectedColor = defaultIconColor
            }
          }.enableIf(useCustomColorsCheckBox.selected)
        }
        row {
          label(PdfViewerBundle.message("pdf.viewer.settings.icons.color.notice"))
        }
      }
      if (PdfViewerSettings.enableExperimentalFeatures) {
        titledRow("Experimental Features") {
          row {
            object : JPanel(GridBagLayout()) {
              init {
                GridBagConstraints().also {
                  it.anchor = GridBagConstraints.LINE_START
                  it.ipadx = 8
                  add(JLabel("Colors invert intensity:"), it)
                  val field = JBTextField(documentColorsInvertIntensity.toString(), 5)
                  field.document.addDocumentListener(object : DocumentListener {
                    override fun insertUpdate(e: DocumentEvent?) {
                      documentColorsInvertIntensity = field.text.toIntOrNull() ?: 0
                    }

                    override fun removeUpdate(e: DocumentEvent?) {
                      documentColorsInvertIntensity = field.text.toIntOrNull() ?: 0
                    }

                    override fun changedUpdate(e: DocumentEvent?) {
                      documentColorsInvertIntensity = field.text.toIntOrNull() ?: 0
                    }
                  })
                  add(field, it)
                }
              }
            }()
          }
        }
        // This is a preferred way for implementing this UI component.
        // Unfortunately, this is not working due to unstable UI DSL.
        // titledRow("Experimental Features") {
        //     row {
        //         label("Please note, that this features are experimental and may not work as expected.")
        //     }
        //     row {
        //         label("Colors invert intensity")
        //         intTextField(::documentColorsInvertIntensity, 1, 0..100)
        //     }
        // }
      }
    })
    loadSettings()
  }
}
