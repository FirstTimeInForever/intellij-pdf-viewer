package com.firsttimeinforever.intellij.pdf.viewer.settings

import com.firsttimeinforever.intellij.pdf.viewer.model.SidebarViewMode
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.util.registry.Registry
import com.intellij.util.messages.Topic
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean

@State(name = "PdfViewerSettings", storages = [(Storage("pdf_viewer.xml"))])
class PdfViewerSettings : PersistentStateComponent<PdfViewerSettings> {
  var useCustomColors = false
  var customBackgroundColor: Int = defaultBackgroundColor.rgb
  var customForegroundColor: Int = defaultForegroundColor.rgb
  var customIconColor: Int = defaultIconColor.rgb
  var enableDocumentAutoReload = true
  var documentColorsInvertIntensity: Int = defaultDocumentColorsInvertIntensity
  var invertDocumentColors = false
  var invertColorsWithTheme = false

  var defaultSidebarViewMode: SidebarViewMode = SidebarViewMode.THUMBNAILS

  fun notifyListeners() {
    ApplicationManager.getApplication().messageBus.syncPublisher(TOPIC).settingsChanged(this)
  }

  override fun getState() = this

  override fun loadState(state: PdfViewerSettings) {
    copyBean(state, this)
  }

  companion object {
    val TOPIC = Topic(PdfViewerSettingsListener::class.java)

    val instance: PdfViewerSettings
      get() = service()

    val defaultBackgroundColor
      get() = EditorColorsManager.getInstance().globalScheme.defaultBackground

    val defaultForegroundColor
      get() = EditorColorsManager.getInstance().globalScheme.defaultForeground

    val defaultIconColor
      get() = defaultForegroundColor

    const val defaultDocumentColorsInvertIntensity = 85

    val enableExperimentalFeatures: Boolean
      get() = Registry.`is`("pdf.viewer.enableExperimentalFeatures")

    val isDebugMode: Boolean
      get() = Registry.`is`("pdf.viewer.debug", false)
  }
}
