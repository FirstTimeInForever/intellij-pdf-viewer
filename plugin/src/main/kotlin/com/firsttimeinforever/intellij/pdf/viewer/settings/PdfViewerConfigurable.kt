package com.firsttimeinforever.intellij.pdf.viewer.settings

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class PdfViewerConfigurable : Configurable {
  private var settingsForm: PdfViewerSettingsForm? = null
  private val settings = PdfViewerSettings.instance

  override fun isModified(): Boolean {
    return settingsForm?.run {
      settings.enableDocumentAutoReload != enableDocumentAutoReload ||
        settings.useCustomColors != useCustomColors ||
        settings.customBackgroundColor != customBackgroundColor?.rgb ?: settings.customBackgroundColor ||
        settings.customForegroundColor != customForegroundColor?.rgb ?: settings.customForegroundColor ||
        settings.customIconColor != customIconColor?.rgb ?: settings.customIconColor ||
        settings.documentColorsInvertIntensity != documentColorsInvertIntensity
    } ?: false
  }

  override fun getDisplayName(): String = PdfViewerBundle.message("pdf.viewer.settings.display.name")

  override fun apply() {
    val wasModified = isModified
    settings.run {
      enableDocumentAutoReload = settingsForm?.enableDocumentAutoReload ?: enableDocumentAutoReload
      useCustomColors = settingsForm?.useCustomColors ?: useCustomColors
      customBackgroundColor = settingsForm?.customBackgroundColor?.rgb ?: customBackgroundColor
      customForegroundColor = settingsForm?.customForegroundColor?.rgb ?: customForegroundColor
      customIconColor = settingsForm?.customIconColor?.rgb ?: customIconColor
      documentColorsInvertIntensity = settingsForm?.documentColorsInvertIntensity ?: documentColorsInvertIntensity
    }
    if (wasModified) {
      settings.notifyListeners()
    }
  }

  override fun reset() {
    settingsForm?.loadSettings()
  }

  override fun createComponent(): JComponent? {
    settingsForm = settingsForm ?: PdfViewerSettingsForm()
    return settingsForm
  }

  override fun disposeUIResources() {
    settingsForm = null
  }
}
