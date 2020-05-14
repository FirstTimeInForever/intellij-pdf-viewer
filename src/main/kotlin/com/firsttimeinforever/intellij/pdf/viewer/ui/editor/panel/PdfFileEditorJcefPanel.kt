package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.StaticServer
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.ui.jcef.JCEFHtmlPanel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.Color
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.BoxLayout

class PdfFileEditorJcefPanel: PdfFileEditorPanel() {
    private val browserPanel = JCEFHtmlPanel("about:blank")
    private val logger = logger<PdfFileEditorJcefPanel>()
    private lateinit var virtualFile: VirtualFile
    private val eventSubscriptionsManager =
        MessageEventSubscriptionsManager.fromList(browserPanel, listOf("pageChanged"))
    private var currentPageNumberHolder = 0
    private val jsonSerializer = Json(JsonConfiguration.Stable)
    private val controlPanel = ControlPanel()

    init {
        Disposer.register(this, browserPanel)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(controlPanel)
        add(browserPanel.component)
        eventSubscriptionsManager.addHandler("pageChanged") {
            val result = jsonSerializer.parse(PageChangeEventDataObject.serializer(), it)
            logger.debug(result.toString())
            currentPageNumberHolder = result.pageNumber
            null
        }
    }

    private fun triggerMessageEvent(eventName: String, data: String = "{}") {
        browserPanel.cefBrowser.executeJavaScript("triggerMessageEvent('$eventName', $data)", null, 0)
    }

    override fun increaseScale() = triggerMessageEvent("increaseScale")
    override fun decreaseScale() = triggerMessageEvent("decreaseScale")
    override fun toggleSidebar() = triggerMessageEvent("toggleSidebar")
    override fun printDocument() = triggerMessageEvent("printDocument")
    override fun nextPage() = triggerMessageEvent("nextPage")
    override fun previousPage() = triggerMessageEvent("previousPage")
    fun togglePdfjsToolbar() = triggerMessageEvent("toggleToolbar")

    override fun findNext() {
        if (!controlPanel.findTextArea.isFocusOwner) {
            controlPanel.findTextArea.grabFocus()
        }
        val searchTarget = controlPanel.findTextArea.text ?: return
        triggerMessageEvent("findNext", "{searchTarget: \"$searchTarget\"}")
    }

    override fun findPrevious() {
        if (!controlPanel.findTextArea.isFocusOwner) {
            controlPanel.findTextArea.grabFocus()
        }
        val searchTarget = controlPanel.findTextArea.text ?: return
        triggerMessageEvent("findPrevious", "{searchTarget: \"$searchTarget\"}")
    }

    private fun addUpdateHandler() {
        val fileSystem = LocalFileSystem.getInstance()
        fileSystem.addRootToWatch(virtualFile.path, false)
        fileSystem.addVirtualFileListener(object: VirtualFileListener {
            override fun contentsChanged(event: VirtualFileEvent) {
                logger.debug("Got some events batch")
                if (event.file != virtualFile) {
                    logger.debug("Seems like target file (${virtualFile.path}) is not changed")
                    return
                }
                logger.debug("Target file (${virtualFile.path}) changed. Reloading page!")
                val targetUrl = StaticServer.getInstance()
                    ?.getFilePreviewUrl(virtualFile.path)
                browserPanel.loadURL(targetUrl!!.toExternalForm())
            }
        })
    }

    override fun openDocument(file: VirtualFile) {
        virtualFile = file
        addUpdateHandler()
        reloadDocument()
        controlPanel.addPropertyChangeListener("background", object: PropertyChangeListener {
            override fun propertyChange(event: PropertyChangeEvent?) {
                if (event == null) {
                    return
                }
                setBackgroundColor(event.newValue as Color)
            }
        })
    }

    override fun reloadDocument() {
        val targetUrl = StaticServer.getInstance()
            ?.getFilePreviewUrl(virtualFile.path)!!.toExternalForm()
        logger.debug("Trying to load url: ${targetUrl}")
        browserPanel.jbCefClient.addLoadHandler(object: CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                if (browser!!.url != targetUrl) {
                    return
                }
                setCurrentPageNumber(currentPageNumberHolder)
                setBackgroundColor(controlPanel.background)
            }
        }, browserPanel.cefBrowser)
        browserPanel.loadURL(targetUrl)
    }

    private fun setBackgroundColor(color: Color) {
        val colors = listOf(color.red, color.blue, color.green, color.alpha)
        val colorString = colors.joinToString("", transform = Integer::toHexString)
        triggerMessageEvent("setBackgroundColor", "{color: \"#${colorString}\"}")
    }

    override fun getCurrentPageNumber(): Int = currentPageNumberHolder

    override fun setCurrentPageNumber(page: Int) {
        currentPageNumberHolder = page
        val data = jsonSerializer.toJson(PageChangeEventDataObject.serializer(), PageChangeEventDataObject(page))
        triggerMessageEvent("pageSet", data.toString())
    }

    override fun dispose() = Unit
}
