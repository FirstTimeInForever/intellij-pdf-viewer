package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.StaticServer
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorPanel
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.messages.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
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
import java.awt.MouseInfo
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.BoxLayout
import javax.swing.FocusManager

class PdfFileEditorJcefPanel: PdfFileEditorPanel() {
    private val browserPanel = JCEFHtmlPanel("about:blank")
    private val logger = logger<PdfFileEditorJcefPanel>()
    private lateinit var virtualFile: VirtualFile
    private val eventSubscriptionsManager =
        MessageEventSubscriptionsManager.fromList(
            browserPanel,
            listOf("pageChanged", "documentInfo", "presentationModeEnterReady", "frameFocused")
        )
    private var currentPageNumberHolder = 0
    private val jsonSerializer = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    private val controlPanel = ControlPanel()
    private var currentScrollDirectionHorizontal = true
    private var presentationModeActive = false

    fun isCurrentScrollDirectionHorizontal() = currentScrollDirectionHorizontal

    fun isPresentationModeActive() = presentationModeActive

    private val presentationModeExitKeyListener = object: KeyListener {
        override fun keyPressed(event: KeyEvent?) {
            if (event == null) {
                return
            }
            if (event.keyCode == KeyEvent.VK_ESCAPE) {
                togglePresentationMode()
                removeKeyListener(this)
            }
        }
        override fun keyTyped(event: KeyEvent?) = Unit
        override fun keyReleased(event: KeyEvent?) = Unit
    }

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
        eventSubscriptionsManager.addHandler("pageChanged") {
            val result = jsonSerializer.parse(PageChangeEventDataObject.serializer(), it)
            logger.debug(result.toString())
            currentPageNumberHolder = result.pageNumber
        }
        eventSubscriptionsManager.addHandler("documentInfo") {
            val result = jsonSerializer.parse(DocumentInfoDataObject.serializer(), it)
            logger.debug(result.toString())
            ApplicationManager.getApplication().invokeLater {
                showDocumentInfoDialog(result)
            }
        }
        eventSubscriptionsManager.addHandler("presentationModeEnterReady") {
            presentationModeActive = true
            clickInBrowserWindow()
            onPresentationModeEnter()
        }
        eventSubscriptionsManager.addHandler("frameFocused") {
            this.grabFocus()
        }
        addKeyListener(pageNavigationKeyListener)
    }

    private fun clickInBrowserWindow() {
        val originalPosition = MouseInfo.getPointerInfo().location
        val originalFocusOwner = FocusManager.getCurrentManager().focusOwner;
        val robot = Robot();
        val location = browserPanel.component.locationOnScreen
        val xcenter = browserPanel.component.width / 2
        val ycenter = browserPanel.component.height / 2
        robot.mouseMove(location.x + xcenter, location.y + ycenter);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseMove(originalPosition.x, originalPosition.y)
        originalFocusOwner?.requestFocus()
    }

    private fun onPresentationModeEnter() {
        addKeyListener(presentationModeExitKeyListener)
    }

    private fun onPresentationModeExit() {
        removeKeyListener(presentationModeExitKeyListener)
    }

    private fun triggerMessageEvent(eventName: String, data: String = "{}") {
        browserPanel.cefBrowser.executeJavaScript("triggerMessageEvent('$eventName', $data)", null, 0)
    }

    override fun increaseScale() = triggerMessageEvent("increaseScale")
    override fun decreaseScale() = triggerMessageEvent("decreaseScale")
    override fun nextPage() = triggerMessageEvent("nextPage")
    override fun previousPage() = triggerMessageEvent("previousPage")

    fun togglePdfjsToolbar() = triggerMessageEvent("toggleToolbar")
    fun getDocumentInfo() = triggerMessageEvent("getDocumentInfo")
    fun toggleSidebar() = triggerMessageEvent("toggleSidebar")
    fun printDocument() = triggerMessageEvent("printDocument")

    fun toggleScrollDirection(): Boolean {
        triggerMessageEvent("toggleScrollDirection")
        currentScrollDirectionHorizontal = !currentScrollDirectionHorizontal
        return currentScrollDirectionHorizontal
    }

    fun toggleSpreadEvenPages() = triggerMessageEvent("toggleSpreadEvenPages")
    fun toggleSpreadOddPages() = triggerMessageEvent("toggleSpreadOddPages")

    fun rotateClockwise() = triggerMessageEvent("rotateClockwise")
    fun rotateCounterclockwise() = triggerMessageEvent("rotateCounterclockwise")

    fun openDevtools() = browserPanel.openDevtools()

    fun togglePresentationMode() {
        if (presentationModeActive) {
            presentationModeActive = false
            onPresentationModeExit()
        }
        controlPanel.presentationModeEnabled = !controlPanel.presentationModeEnabled
        triggerMessageEvent("togglePresentationMode")
    }

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

    private fun showDocumentInfoDialog(documentInfo: DocumentInfoDataObject) =
        DialogBuilder().centerPanel(DocumentInfoPanel(documentInfo)).showModal(true)

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
                eventSubscriptionsManager.injectSubscriptions()
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
