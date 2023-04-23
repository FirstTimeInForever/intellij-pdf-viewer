package com.firsttimeinforever.intellij.pdf.viewer.settings

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PdfViewerConfigurable : Configurable {
  private var settingsForm: PdfViewerSettingsForm? = null
  private val settings = PdfViewerSettings.instance

  override fun isModified(): Boolean {
    return settingsForm?.run {
      settings.enableDocumentAutoReload != enableDocumentAutoReload.get() ||
        settings.defaultSidebarViewMode != defaultSidebarViewMode.get() ||
        settings.invertColorsWithTheme != invertDocumentColorsWithTheme.get() ||
        settings.invertDocumentColors != invertDocumentColors.get() ||
        settings.documentColorsInvertIntensity != documentColorsInvertIntensity.get() ||
        settings.useCustomColors != useCustomColors.get() ||
        settings.customForegroundColor != customForegroundColor.get() ||
        settings.customBackgroundColor != customBackgroundColor.get() ||
        settings.customIconColor != customIconColor.get()
    } ?: false
  }

  override fun getDisplayName(): String = PdfViewerBundle.message("pdf.viewer.settings.display.name")

  override fun apply() {
    val wasModified = isModified
    settings.run {
      enableDocumentAutoReload = settingsForm?.enableDocumentAutoReload?.get() ?: enableDocumentAutoReload
      defaultSidebarViewMode = settingsForm?.defaultSidebarViewMode?.get() ?: defaultSidebarViewMode
      invertColorsWithTheme = settingsForm?.invertDocumentColorsWithTheme?.get() ?: invertColorsWithTheme
      invertDocumentColors = settingsForm?.invertDocumentColors?.get() ?: invertDocumentColors
      documentColorsInvertIntensity = settingsForm?.documentColorsInvertIntensity?.get() ?: documentColorsInvertIntensity
      useCustomColors = settingsForm?.useCustomColors?.get() ?: useCustomColors
      customBackgroundColor = settingsForm?.customBackgroundColor?.get() ?: customBackgroundColor
      customForegroundColor = settingsForm?.customForegroundColor?.get() ?: customForegroundColor
      customIconColor = settingsForm?.customIconColor?.get() ?: customIconColor
    }
    if (wasModified) {
      settings.notifyListeners()
    }
  }

  override fun reset() {
    settingsForm?.reset()
  }

  override fun createComponent(): JComponent? {
    settingsForm = settingsForm ?: PdfViewerSettingsForm()
    return settingsForm
  }

  override fun disposeUIResources() {
    settingsForm = null
  }
}
