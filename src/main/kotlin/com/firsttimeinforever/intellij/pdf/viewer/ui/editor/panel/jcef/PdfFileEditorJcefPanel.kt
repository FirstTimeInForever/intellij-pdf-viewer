package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.StaticServer
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.PdfFileEditorPanel
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.MessageEventReceiver
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.MessageEventSender
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.SubscribableEventType
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.TriggerableEventType
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects.*
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.util.ui.UIUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonDecodingException
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.Color
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.BoxLayout

class PdfFileEditorJcefPanel(project: Project, virtualFile: VirtualFile):
    PdfFileEditorPanel(virtualFile), EditorColorsListener
{
    private val browserPanel = JCEFHtmlPanel("about:blank")
    private val logger = logger<PdfFileEditorJcefPanel>()
    private val messageBusConnection = project.messageBus.connect()
    private val jsonSerializer = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true))
    private val eventReceiver =
        MessageEventReceiver.fromList(browserPanel, SubscribableEventType.values().asList())
    private val eventSender = MessageEventSender(browserPanel, jsonSerializer)
    val presentationModeController =
        PresentationModeController(this, browserPanel.component, eventReceiver, eventSender)
    private var currentPageNumberHolder: Int = 1
    private val controlPanel = ControlPanel(project.messageBus)
    private var currentScrollDirectionHorizontal = true
    private var pagesCountHolder = 0
    private var pageSpreadStateHolder = PageSpreadState.NONE
    private val documentLoadErrorPanel = DocumentLoadErrorPanel()
    private var sidebarViewStateHolder = SidebarViewState()
    private var sidebarAvailableViewModesHolder = SidebarAvailableViewModes()

    private fun showDocumentLoadErrorNotification() {
        val reloadAction =  ActionManager.getInstance().getAction(RELOAD_ACTION_ID)?:
            error("Could not get document reload action")
        val notification = Notification(
            PdfViewerBundle.message("pdf.viewer.notifications.group.id"),
            PdfViewerBundle.message("pdf.viewer.editor.panel.notifications.document.open.failed.title"),
            PdfViewerBundle.message("pdf.viewer.editor.panel.notifications.document.open.failed.content"),
            NotificationType.ERROR
        ).addAction(reloadAction.templatePresentation.run {
            object: AnAction(text, description, icon) {
                override fun actionPerformed(event: AnActionEvent) {
                    if (browserPanel.isDisposed) {
                        return
                    }
                    reloadDocument()
                }
            }
        })
        Notifications.Bus.notify(notification)
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

    private val settingsChangeListener = { settings: PdfViewerSettings ->
        setThemeColors()
        if (!settings.enableDocumentAutoReload) {
            removeFileUpdateHandler()
        }
        else if (watchRequest == null) {
            addFileUpdateHandler()
        }
    }

    private var watchRequest: LocalFileSystem.WatchRequest? = null

    private val fileListener = object: VirtualFileListener {
        override fun contentsChanged(event: VirtualFileEvent) {
            logger.debug("Got some events batch")
            if (event.file != virtualFile) {
                logger.debug("Seems like target file (${virtualFile.path}) is not changed")
                return
            }
            logger.debug("Target file (${virtualFile.path}) changed. Reloading page!")
            val targetUrl = StaticServer.instance.getFilePreviewUrl(virtualFile.path)
            browserPanel.loadURL(targetUrl.toExternalForm())
        }
    }

    init {
        Disposer.register(this, browserPanel)
        Disposer.register(this, eventReceiver)
        Disposer.register(this, controlPanel)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(controlPanel)
        add(browserPanel.component)
        with (eventReceiver) {
            addHandler(SubscribableEventType.PAGE_CHANGED) {
                val result = jsonSerializer.parse(PageChangeDataObject.serializer(), it)
                currentPageNumberHolder = result.pageNumber
                pageStateChanged()
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
            addHandler(SubscribableEventType.PAGES_COUNT) {
                try {
                    val result = jsonSerializer.parse(PagesCountDataObject.serializer(), it)
                    pagesCountHolder = result.count
                    pageStateChanged()
                }
                catch (exception: JsonDecodingException) {
                    logger.warn(
                        "Failed to parse PagesCount data object! (This should be fixed at message passing level)",
                        exception
                    )
                }
            }
            addHandler(SubscribableEventType.DOCUMENT_LOAD_ERROR) {
                // For some reason this event triggers with no data
                // This should be impossible, due to passing event data
                // to triggerEvent() in unhandledrejection event handler
                if (it.isNotEmpty()) {
                    browserPanel.component.isVisible = false
                    add(documentLoadErrorPanel)
                    showDocumentLoadErrorNotification()
                }
            }
            addHandler(SubscribableEventType.SIDEBAR_VIEW_STATE_CHANGED) {
                val result = jsonSerializer.parse(
                    SidebarViewStateChangeDataObject.serializer(), it
                )
                sidebarViewStateHolder = result.state
            }
            addHandler(SubscribableEventType.SIDEBAR_AVAILABLE_VIEWS_CHANGED) {
                val result = jsonSerializer.parse(
                    SidebarAvailableViewModesChangedDataObject.serializer(), it
                )
                sidebarAvailableViewModesHolder = result
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
        PdfViewerSettings.instance.addChangeListener(settingsChangeListener)
        messageBusConnection.subscribe(EditorColorsManager.TOPIC, this)
        openDocument()
    }

    val sidebarAvailableViewModes
        get() = sidebarAvailableViewModesHolder

    val isCurrentScrollDirectionHorizontal
        get() = currentScrollDirectionHorizontal

    override val pagesCount
        get() = pagesCountHolder

    val sidebarViewState: SidebarViewState
        get() = sidebarViewStateHolder

    fun setSidebarViewMode(mode: SidebarViewMode) {
        sidebarViewStateHolder = SidebarViewState(mode, sidebarViewStateHolder.hidden)
        eventSender.triggerWith(
            TriggerableEventType.SET_SIDEBAR_VIEW_MODE,
            SidebarViewModeChangeDataObject.from(mode),
            SidebarViewModeChangeDataObject.serializer()
        )
    }

    override var currentPageNumber: Int
        get() = currentPageNumberHolder
        set(value) {
            currentPageNumberHolder = value
            updatePageNumber(value)
        }

    private fun updatePageNumber(value: Int) {
        eventSender.triggerWith(
            TriggerableEventType.SET_PAGE,
            PageChangeDataObject(value),
            PageChangeDataObject.serializer()
        )
    }

    override fun increaseScale() = eventSender.trigger(TriggerableEventType.INCREASE_SCALE)
    override fun decreaseScale() = eventSender.trigger(TriggerableEventType.DECREASE_SCALE)
    override fun nextPage() = eventSender.trigger(TriggerableEventType.GOTO_NEXT_PAGE)
    override fun previousPage() = eventSender.trigger(TriggerableEventType.GOTO_PREVIOUS_PAGE)

    fun getDocumentInfo() = eventSender.trigger(TriggerableEventType.GET_DOCUMENT_INFO)
    fun toggleSidebar() = eventSender.trigger(TriggerableEventType.TOGGLE_SIDEBAR)
    fun printDocument() = eventSender.trigger(TriggerableEventType.PRINT_DOCUMENT)
    fun rotateClockwise() = eventSender.trigger(TriggerableEventType.ROTATE_CLOCKWISE)
    fun rotateCounterclockwise() = eventSender.trigger(TriggerableEventType.ROTATE_COUNTERCLOCKWISE)

    var pageSpreadState
        get() = pageSpreadStateHolder
        set(state) {
            if (pageSpreadStateHolder == state) {
                return
            }
            pageSpreadStateHolder = state
            eventSender.trigger(when (state) {
                PageSpreadState.NONE -> TriggerableEventType.SPREAD_NONE
                PageSpreadState.EVEN -> TriggerableEventType.SPREAD_EVEN_PAGES
                PageSpreadState.ODD -> TriggerableEventType.SPREAD_ODD_PAGES
            })
        }

    fun toggleScrollDirection(): Boolean {
        eventSender.trigger(TriggerableEventType.TOGGLE_SCROLL_DIRECTION)
        currentScrollDirectionHorizontal = !currentScrollDirectionHorizontal
        return currentScrollDirectionHorizontal
    }

    fun openDevtools() = browserPanel.openDevtools()

    override fun findNext() {
        if (!controlPanel.searchTextField.isFocusOwner) {
            controlPanel.searchTextField.grabFocus()
        }
        val searchTarget = controlPanel.searchTextField.text ?: return
        eventSender.triggerWith(
            TriggerableEventType.FIND_NEXT,
            SearchDataObject(searchTarget),
            SearchDataObject.serializer()
        )
    }

    override fun findPrevious() {
        if (!controlPanel.searchTextField.isFocusOwner) {
            controlPanel.searchTextField.grabFocus()
        }
        val searchTarget = controlPanel.searchTextField.text ?: return
        eventSender.triggerWith(
            TriggerableEventType.FIND_PREVIOUS,
            SearchDataObject(searchTarget),
            SearchDataObject.serializer()
        )
    }

    private fun addFileUpdateHandler() {
        LocalFileSystem.getInstance().run {
            watchRequest = addRootToWatch(virtualFile.path, false)
            addVirtualFileListener(fileListener)
        }
    }

    private fun removeFileUpdateHandler() {
        LocalFileSystem.getInstance().run {
            watchRequest?.also {
                removeWatchedRoot(it)
                watchRequest = null
            }
            removeVirtualFileListener(fileListener)
        }
    }

    private fun showDocumentInfoDialog(documentInfo: DocumentInfoDataObject) =
        DialogBuilder().centerPanel(DocumentInfoPanel(documentInfo)).showModal(true)

    private fun openDocument() {
        if (PdfViewerSettings.instance.enableDocumentAutoReload) {
            addFileUpdateHandler()
        }
        addReloadHandler()
        reloadDocument()
    }

    private fun addReloadHandler() {
        val targetUrl = StaticServer.instance.getFilePreviewUrl(virtualFile.path).toExternalForm()
        browserPanel.jbCefClient.addLoadHandler(object: CefLoadHandlerAdapter() {
            override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
                if (browser == null || browser.url != targetUrl) {
                    return
                }
                eventReceiver.injectSubscriptions()
                updatePageNumber(currentPageNumber)
                setThemeColors()
            }
        }, browserPanel.cefBrowser)
    }

    override fun reloadDocument() {
        remove(documentLoadErrorPanel)
        browserPanel.component.isVisible = true
        val targetUrl = StaticServer.instance.getFilePreviewUrl(virtualFile.path).toExternalForm()
        logger.debug("Trying to load url: $targetUrl")
        browserPanel.loadURL(targetUrl)
    }

    private fun setThemeColors(
        background: Color = UIUtil.getPanelBackground(),
        foreground: Color = UIUtil.getLabelForeground()
    ) {
        eventSender.triggerWith(
            TriggerableEventType.SET_THEME_COLORS,
            PdfViewerSettings.instance.run {
                if (useCustomColors) {
                    SetThemeColorsDataObject.from(
                        Color(customBackgroundColor),
                        Color(customForegroundColor),
                        Color(customIconColor)
                    )
                }
                else {
                    SetThemeColorsDataObject.from(
                        background,
                        foreground,
                        PdfViewerSettings.defaultIconColor
                    )
                }
            },
            SetThemeColorsDataObject.serializer()
        )
    }

    override fun dispose() {
        PdfViewerSettings.instance.removeChangeListener(settingsChangeListener)
    }

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        setThemeColors()
    }

    companion object {
        private const val RELOAD_ACTION_ID =
            "com.firsttimeinforever.intellij.pdf.viewer.actions.common.ReloadDocumentAction"
    }
}
