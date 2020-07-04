package com.firsttimeinforever.intellij.pdf.viewer.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager.getService
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import com.intellij.util.xmlb.annotations.Transient

@State(name = "PdfViewerSettings", storages = [(Storage("pdf_viewer.xml"))])
class PdfViewerSettings: PersistentStateComponent<PdfViewerSettings> {
    var useCustomColors = false
    var customBackgroundColor: Int = defaultBackgroundColor.rgb
    var customForegroundColor: Int = defaultForegroundColor.rgb
    var customIconColor: Int = defaultIconColor.rgb
    var enableDocumentAutoReload = true

    @Transient
    private val changeListenersHolder = mutableListOf<(PdfViewerSettings) -> Unit>()

    val changeListeners
        get() = changeListenersHolder.toList()
    
    fun addChangeListener(listener: (settings: PdfViewerSettings) -> Unit) {
        changeListenersHolder.add(listener)
    }

    fun removeChangeListener(listener: (settings: PdfViewerSettings) -> Unit) {
        changeListenersHolder.remove(listener)
    }

    override fun getState() = this

    override fun loadState(state: PdfViewerSettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: PdfViewerSettings
            get() = getService(PdfViewerSettings::class.java)

        val defaultBackgroundColor
            get() = EditorColorsManager.getInstance().globalScheme.defaultBackground

        val defaultForegroundColor
            get() = EditorColorsManager.getInstance().globalScheme.defaultForeground

        val defaultIconColor
            get() = defaultForegroundColor
    }
}
