package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.StaticServer
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorPanel
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.ui.DialogBuilder
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
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.BoxLayout

class PdfFileEditorJcefPanel: PdfFileEditorPanel(), EditorColorsListener {
    private val browserPanel = JCEFHtmlPanel("about:blank")
    private val logger = logger<PdfFileEditorJcefPanel>()
    private lateinit var virtualFile: VirtualFile
    private val jsonSerializer = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    private val eventReceiver =
        MessageEventReceiver.fromList(browserPanel, SubscribableEventType.values().asList())
    private val eventSender = MessageEventSender(browserPanel, jsonSerializer)
    val presentationModeController =
        PresentationModeController(this, browserPanel, eventReceiver, eventSender)
    private var currentPageNumberHolder = 0
    private val controlPanel = ControlPanel()
    private var currentScrollDirectionHorizontal = true

    fun isCurrentScrollDirectionHorizontal() = currentScrollDirectionHorizontal

    private val pageNavigationKeyListener = object: KeyListener {
        override fun keyPressed(event: KeyEvent?) {
            when (event?.keyCode) {
                KeyEvent.VK_LEFT -> previousPage()
                KeyEvent.VK_RIGHT -> nextPage()
            }
        }
        override fun keyTyped(event: KeyEvent?) = Unit
        override fun keyReleased(event: KeyEvent?) = Unit
    }

    init {
        Disposer.register(this, browserPanel)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(controlPanel)
        add(browserPanel.component)
        eventReceiver.run {
            addHandler(SubscribableEventType.PAGE_CHANGED) {
                val result = jsonSerializer.parse(PageChangeEventDataObject.serializer(), it)
                currentPageNumberHolder = result.pageNumber
            }
            addHandler(SubscribableEventType.DOCUMENT_INFO) {
                val result = jsonSerializer.parse(DocumentInfoDataObject.serializer(), it)
                ApplicationManager.getApplication().invokeLater {
                    showDocumentInfoDialog(result)
                }
            }
            addHandler(SubscribableEventType.FRAME_FOCUSED) {
                grabFocus()
            }
        }
        addKeyListener(pageNavigationKeyListener)
        presentationModeController.run {
            addEnterListener {
                controlPanel.presentationModeEnabled = true
                false
            }
            addExitListener {
                controlPanel.presentationModeEnabled = false
                false
            }
        }
    }

    override fun increaseScale() = eventSender.trigger(TriggerableEventType.INCREASE_SCALE)
    override fun decreaseScale() = eventSender.trigger(TriggerableEventType.DECREASE_SCALE)
    override fun nextPage() = eventSender.trigger(TriggerableEventType.GOTO_NEXT_PAGE)
    override fun previousPage() = eventSender.trigger(TriggerableEventType.GOTO_PREVIOUS_PAGE)

    fun togglePdfjsToolbar() = eventSender.trigger(TriggerableEventType.TOGGLE_PDFJS_TOOLBAR)
    fun getDocumentInfo() = eventSender.trigger(TriggerableEventType.GET_DOCUMENT_INFO)
    fun toggleSidebar() = eventSender.trigger(TriggerableEventType.TOGGLE_SIDEBAR)
    fun printDocument() = eventSender.trigger(TriggerableEventType.PRINT_DOCUMENT)
    fun toggleSpreadEvenPages() = eventSender.trigger(TriggerableEventType.TOGGLE_SPREAD_EVEN_PAGES)
    fun toggleSpreadOddPages() = eventSender.trigger(TriggerableEventType.TOGGLE_SPREAD_ODD_PAGES)
    fun rotateClockwise() = eventSender.trigger(TriggerableEventType.ROTATE_CLOCKWISE)
    fun rotateCounterclockwise() = eventSender.trigger(TriggerableEventType.ROTATE_COUNTERCLOCKWISE)

    fun toggleScrollDirection(): Boolean {
        eventSender.trigger(TriggerableEventType.TOGGLE_SCROLL_DIRECTION)
        currentScrollDirectionHorizontal = !currentScrollDirectionHorizontal
        return currentScrollDirectionHorizontal
    }

    fun openDevtools() = browserPanel.openDevtools()

    override fun findNext() {
        if (!controlPanel.findTextArea.isFocusOwner) {
            controlPanel.findTextArea.grabFocus()
        }
        val searchTarget = controlPanel.findTextArea.text ?: return
        eventSender.triggerWith(
            TriggerableEventType.FIND_NEXT,
            SearchDataObject(searchTarget),
            SearchDataObject.serializer()
        )
    }

    override fun findPrevious() {
        if (!controlPanel.findTextArea.isFocusOwner) {
            controlPanel.findTextArea.grabFocus()
        }
        val searchTarget = controlPanel.findTextArea.text ?: return
        eventSender.triggerWith(
            TriggerableEventType.FIND_PREVIOUS,
            SearchDataObject(searchTarget),
            SearchDataObject.serializer()
        )
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

    private fun showDocumentInfoDialog(documentInfo: DocumentInfoDataObject) =
        DialogBuilder().centerPanel(DocumentInfoPanel(documentInfo)).showModal(true)

    override fun openDocument(file: VirtualFile) {
        virtualFile = file
        addUpdateHandler()
        reloadDocument()
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
                eventReceiver.injectSubscriptions()
                setCurrentPageNumber(currentPageNumberHolder)
                setBackgroundColor(EditorColorsManager.getInstance().globalScheme.defaultBackground)
            }
        }, browserPanel.cefBrowser)
        browserPanel.loadURL(targetUrl)
    }

    private fun setBackgroundColor(color: Color) {
        eventSender.triggerWith(
            TriggerableEventType.SET_BACKGROUND_COLOR,
            SetBackgroundColorDataObject.from(color),
            SetBackgroundColorDataObject.serializer()
        )
    }

    override fun getCurrentPageNumber(): Int = currentPageNumberHolder

    override fun setCurrentPageNumber(page: Int) {
        currentPageNumberHolder = page
        eventSender.triggerWith(
            TriggerableEventType.SET_PAGE,
            PageChangeEventDataObject(page),
            PageChangeEventDataObject.serializer()
        )
    }

    override fun dispose() = Unit

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        if (scheme == null) {
            return
        }
        setBackgroundColor(scheme.defaultBackground)
    }
}
