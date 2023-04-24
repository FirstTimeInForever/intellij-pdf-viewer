package com.firsttimeinforever.intellij.pdf.viewer.settings

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.model.SidebarViewMode
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.observable.util.not
import com.intellij.ui.ColorPanel
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.*
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel

class PdfViewerSettingsForm : JPanel() {
  private val settings
    get() = PdfViewerSettings.instance

  val enableDocumentAutoReload = PropertyGraph().property(settings.enableDocumentAutoReload)
  val defaultSidebarViewMode = PropertyGraph().property(settings.defaultSidebarViewMode)

  private val generalSettingsGroup = panel {
    group(PdfViewerBundle.message("pdf.viewer.settings.group.general")) {
      row {
        checkBox(PdfViewerBundle.message("pdf.viewer.settings.reload.document"))
          .bindSelected(enableDocumentAutoReload)
      }
      row(PdfViewerBundle.message("pdf.viewer.settings.sidebar.viewer.default")) {
        val renderer = SimpleListCellRenderer.create<SidebarViewMode> { label, value, _ ->
          label.text = when (value) {
            SidebarViewMode.NONE -> "Closed"
            SidebarViewMode.THUMBNAILS -> "Thumbnails"
            // SidebarViewMode.OUTLINE -> "Outline (document structure)"
            SidebarViewMode.ATTACHMENTS -> "Attachments"
            else -> "Outline (document structure)"
          }
        }
        comboBox(DefaultComboBoxModel(SidebarViewMode.values()), renderer)
          .bindItem(defaultSidebarViewMode)
      }
    }
  }

  val invertDocumentColorsWithTheme = PropertyGraph().property(settings.invertColorsWithTheme)
  val invertDocumentColors = PropertyGraph().property(settings.invertDocumentColors)
  val documentColorsInvertIntensity = PropertyGraph().property(settings.documentColorsInvertIntensity)

  private val invertColorsGroup = panel {
    group(PdfViewerBundle.message("pdf.viewer.settings.group.colors.document")) {
      row {
        checkBox(PdfViewerBundle.message("pdf.viewer.settings.colors.document.with.theme"))
          .bindSelected(invertDocumentColorsWithTheme)
          .comment(PdfViewerBundle.message("pdf.viewer.settings.colors.document.with.theme.comment"))
      }
      row {
        checkBox(PdfViewerBundle.message("pdf.viewer.settings.colors.document.invert"))
          .bindSelected(invertDocumentColors)
          .enabledIf(invertDocumentColorsWithTheme.not())
      }
      row(PdfViewerBundle.message("pdf.viewer.settings.colors.document.invert.intensity")) {
        intTextField(1..100, 1)
          .bindIntText(documentColorsInvertIntensity)
        rowComment(PdfViewerBundle.message("pdf.viewer.settings.colors.document.invert.intensity.comment"))
      }
    }
  }

  val useCustomColors = PropertyGraph().property(settings.useCustomColors)
  val customBackgroundColor = PropertyGraph().property(settings.customBackgroundColor)
  val customForegroundColor = PropertyGraph().property(settings.customForegroundColor)
  val customIconColor = PropertyGraph().property(settings.customIconColor)

  private val backgroundColorPanel = ColorPanel().apply {
    selectedColor = Color(customBackgroundColor.get())
    addActionListener {
      selectedColor?.let { customBackgroundColor.set(it.rgb) }
    }
  }
  private val foregroundColorPanel = ColorPanel().apply {
    addActionListener {
      selectedColor?.let { customForegroundColor.set(it.rgb) }
    }
  }
  private val iconColorPanel = ColorPanel().apply {
    addActionListener {
      selectedColor?.let { customIconColor.set(it.rgb) }
    }
  }

  private val customColorsGroup = panel {
    group(PdfViewerBundle.message("pdf.viewer.settings.group.colors.viewer")) {
      row {
        checkBox(PdfViewerBundle.message("pdf.viewer.settings.viewer.colors"))
          .bindSelected(useCustomColors)
          .comment(PdfViewerBundle.message("pdf.viewer.settings.group.colors.viewer.comment"))
      }
      indent {
          panel {
            row(PdfViewerBundle.message("pdf.viewer.settings.foreground")) {
              cell(foregroundColorPanel)
            }
            row(PdfViewerBundle.message("pdf.viewer.settings.background")) {
              cell(backgroundColorPanel)
            }
            row(PdfViewerBundle.message("pdf.viewer.settings.icons")) {
              cell(iconColorPanel)
              rowComment(PdfViewerBundle.message("pdf.viewer.settings.icons.color.notice"))
            }
            row {
              link(PdfViewerBundle.message("pdf.viewer.settings.set.current.theme")) {
                resetViewerColorsToTheme()
              }
            }
          }.enabledIf(useCustomColors)
      }
    }
  }

  init {
    layout = BorderLayout()
    add(panel {
      row { cell(generalSettingsGroup).align(AlignX.FILL) }
      row { cell(invertColorsGroup).align(AlignX.FILL) }
      row { cell(customColorsGroup).align(AlignX.FILL) }
    })
  }

  fun reset() {
    enableDocumentAutoReload.set(settings.enableDocumentAutoReload)
    defaultSidebarViewMode.set(settings.defaultSidebarViewMode)
    invertDocumentColorsWithTheme.set(settings.invertColorsWithTheme)
    invertDocumentColors.set(settings.invertDocumentColors)
    documentColorsInvertIntensity.set(settings.documentColorsInvertIntensity)
    useCustomColors.set(settings.useCustomColors)
    customForegroundColor.set(settings.customForegroundColor)
    customBackgroundColor.set(settings.customBackgroundColor)
    customIconColor.set(settings.customIconColor)
  }

  fun resetViewerColorsToTheme() {
    PdfViewerSettings.run {
      backgroundColorPanel.selectedColor = defaultBackgroundColor
      customBackgroundColor.set(defaultBackgroundColor.rgb)
      foregroundColorPanel.selectedColor = defaultForegroundColor
      customForegroundColor.set(defaultForegroundColor.rgb)
      iconColorPanel.selectedColor = defaultIconColor
      customIconColor.set(defaultIconColor.rgb)
    }
  }
}
