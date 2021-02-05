package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef

import com.firsttimeinforever.intellij.pdf.viewer.PdfViewerBundle
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettingsListener
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
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.util.ui.UIUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import java.awt.Color
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.concurrent.atomic.AtomicReference
import javax.swing.BoxLayout
import kotlin.math.max
import kotlin.math.min

class PdfFileEditorJcefPanel(project: Project, virtualFile: VirtualFile):
    PdfFileEditorPanel<JcefPanelPreviewState>(virtualFile),
    EditorColorsListener,
    PdfViewerSettingsListener,
    DumbAware
{
    private val browserPanel = JCEFHtmlPanel("about:blank")
    private val documentLoadErrorPanel by lazy { DocumentLoadErrorPanel() }
    private val messageBusConnection = project.messageBus.connect(this)

    private val eventSender = MessageEventSender(browserPanel, jsonSerializer)
    private val eventReceiver = MessageEventReceiver.fromList(
        browserPanel,
        SubscribableEventType.values().asList()
    )

    val presentationModeController = PresentationModeController(
        this,
        browserPanel.component,
        eventReceiver,
        eventSender
    )

    private val controlPanel = ControlPanel(project.messageBus, presentationModeController)

    private val propertiesHolder = AtomicReference(JcefPanelPreviewProperties())
    override val properties: JcefPanelPreviewProperties
        get() = propertiesHolder.get()

    private var state = JcefPanelPreviewState()
    private val fileListener = FileListener()

    init {
        Disposer.register(this, browserPanel)
        Disposer.register(this, eventReceiver)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(controlPanel)
        add(browserPanel.component)
        with(eventReceiver) {
            addHandler(SubscribableEventType.PAGE_CHANGED) {
                val result = jsonSerializer.decodeFromString<PageChangeDataObject>(it)
                state.pageNumber = result.pageNumber
                pageStateChanged()
            }
            addHandler(SubscribableEventType.DOCUMENT_INFO) {
                val result = jsonSerializer.decodeFromString<DocumentInfoDataObject>(it)
                ApplicationManager.getApplication().invokeLater {
                    showDocumentInfoDialog(result)
                }
            }
            addHandler(SubscribableEventType.FRAME_FOCUSED) {
                grabFocus()
            }
            addHandler(SubscribableEventType.PAGES_COUNT) {
                try {
                    val result = jsonSerializer.decodeFromString<PagesCountDataObject>(it)
                    updateAtomicReference(propertiesHolder) { current ->
                        current.copy(pagesCount = result.count)
                    }
                    pageStateChanged()
                } catch (exception: Exception) {
                    // FIXME: Find out proper way of handling new exceptions
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
                    showDocumentLoadErrorNotification(this@PdfFileEditorJcefPanel)
                }
            }
            addHandler(SubscribableEventType.SIDEBAR_VIEW_STATE_CHANGED) {
                val result = jsonSerializer.decodeFromString<SidebarViewStateChangeDataObject>(it)
                state.sidebarViewState = result.state
            }
            addHandler(SubscribableEventType.SIDEBAR_AVAILABLE_VIEWS_CHANGED) {
                val result = jsonSerializer.decodeFromString<SidebarAvailableViewModesChangedDataObject>(it)
                updateAtomicReference(propertiesHolder) { current ->
                    current.copy(sidebarAvailableViewModes = result)
                }
            }
        }
        addKeyListener(object: KeyAdapter() {
            override fun keyPressed(event: KeyEvent?) {
                when (event?.keyCode) {
                    KeyEvent.VK_LEFT -> previousPage()
                    KeyEvent.VK_RIGHT -> nextPage()
                }
            }
        })
        messageBusConnection.subscribe(PdfViewerSettings.TOPIC, this)
        messageBusConnection.subscribe(EditorColorsManager.TOPIC, this)
        openDocument()
    }

    val sidebarAvailableViewModes
        get() = properties.sidebarAvailableViewModes

    val isCurrentScrollDirectionHorizontal
        get() = state.currentScrollDirectionHorizontal

    val sidebarViewState: SidebarViewState
        get() = state.sidebarViewState

    fun setSidebarViewMode(mode: SidebarViewMode) {
        state.sidebarViewState = SidebarViewState(mode, state.sidebarViewState.hidden)
        eventSender.triggerWith(
            TriggerableEventType.SET_SIDEBAR_VIEW_MODE,
            SidebarViewModeChangeDataObject.from(mode)
        )
    }

    override var currentPageNumber: Int
        get() = state.pageNumber
        set(value) {
            state.pageNumber = value
            updatePageNumber(value)
        }

    private fun updatePageNumber(value: Int) {
        eventSender.triggerWith(TriggerableEventType.SET_PAGE, PageChangeDataObject(value))
    }

    override fun setScale(value: Double) {
        state.scale = min(max(value, MIN_SCALE), MAX_SCALE)
        eventSender.triggerWith(TriggerableEventType.SET_SCALE, ScaleChangeDataObject(state.scale))
    }

    override fun nextPage() = eventSender.trigger(TriggerableEventType.GOTO_NEXT_PAGE)
    override fun previousPage() = eventSender.trigger(TriggerableEventType.GOTO_PREVIOUS_PAGE)

    fun showDocumentInfo() = eventSender.trigger(TriggerableEventType.GET_DOCUMENT_INFO)
    fun toggleSidebar() = eventSender.trigger(TriggerableEventType.TOGGLE_SIDEBAR)
    fun printDocument() = eventSender.trigger(TriggerableEventType.PRINT_DOCUMENT)
    fun rotateClockwise() = eventSender.trigger(TriggerableEventType.ROTATE_CLOCKWISE)
    fun rotateCounterClockwise() = eventSender.trigger(TriggerableEventType.ROTATE_COUNTERCLOCKWISE)

    var pageSpreadState
        get() = state.pageSpreadState
        set(value) {
            if (state.pageSpreadState == value) {
                return
            }
            state.pageSpreadState = value
            eventSender.trigger(when (value) {
                PageSpreadState.NONE -> TriggerableEventType.SPREAD_NONE
                PageSpreadState.EVEN -> TriggerableEventType.SPREAD_EVEN_PAGES
                PageSpreadState.ODD -> TriggerableEventType.SPREAD_ODD_PAGES
            })
        }

    override val previewState: JcefPanelPreviewState
        get() = state

    override val currentScaleValue: Double
        get() = state.scale

    fun toggleScrollDirection(): Boolean {
        eventSender.trigger(TriggerableEventType.TOGGLE_SCROLL_DIRECTION)
        state.currentScrollDirectionHorizontal = !state.currentScrollDirectionHorizontal
        return state.currentScrollDirectionHorizontal
    }

    fun openDevtools() = browserPanel.openDevtools()

    override fun findNext() {
        if (!controlPanel.searchTextField.isFocusOwner) {
            controlPanel.searchTextField.grabFocus()
        }
        val searchTarget = controlPanel.searchTextField.text ?: return
        eventSender.triggerWith(TriggerableEventType.FIND_NEXT, SearchDataObject(searchTarget))
    }

    override fun findPrevious() {
        if (!controlPanel.searchTextField.isFocusOwner) {
            controlPanel.searchTextField.grabFocus()
        }
        val searchTarget = controlPanel.searchTextField.text ?: return
        eventSender.triggerWith(TriggerableEventType.FIND_PREVIOUS, SearchDataObject(searchTarget))
    }

    private fun showDocumentInfoDialog(documentInfo: DocumentInfoDataObject) =
        DialogBuilder().centerPanel(DocumentInfoPanel(documentInfo)).showModal(true)

    private fun openDocument() {
        fileListener.enabled = PdfViewerSettings.instance.enableDocumentAutoReload
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
                setScale(currentScaleValue)
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
        val colorInvertIntensity = if(PdfViewerSettings.instance.invertDocumentColors) {
            PdfViewerSettings.instance.documentColorsInvertIntensity
        } else 0
        eventSender.triggerWith(
            TriggerableEventType.SET_THEME_COLORS,
            PdfViewerSettings.instance.run {
                if (useCustomColors) {
                    SetThemeColorsDataObject(
                        Color(customBackgroundColor),
                        Color(customForegroundColor),
                        Color(customIconColor),
                        colorInvertIntensity
                    )
                } else {
                    SetThemeColorsDataObject(
                        background,
                        foreground,
                        PdfViewerSettings.defaultIconColor,
                        colorInvertIntensity
                    )
                }
            }
        )
    }

    override fun globalSchemeChange(scheme: EditorColorsScheme?) {
        setThemeColors()
    }

    override fun settingsChanged(settings: PdfViewerSettings) {
        setThemeColors()
        fileListener.enabled = settings.enableDocumentAutoReload
    }

    private inner class FileListener: BulkFileListener {
        var enabled = false

        init {
            messageBusConnection.subscribe(VirtualFileManager.VFS_CHANGES, this)
        }

        override fun after(events: MutableList<out VFileEvent>) {
            if (enabled && events.any { it == virtualFile }) {
                logger.debug("Target file (${virtualFile.path}) changed. Reloading page!")
                val targetUrl = StaticServer.instance.getFilePreviewUrl(virtualFile.path)
                browserPanel.loadURL(targetUrl.toExternalForm())
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.25
        private const val MAX_SCALE = 10.0

        private val logger = thisLogger()

        private val jsonSerializer = Json {
            ignoreUnknownKeys = true
        }

        private const val reloadActionId =
            "com.firsttimeinforever.intellij.pdf.viewer.actions.common.ReloadDocumentAction"

        private fun showDocumentLoadErrorNotification(panel: PdfFileEditorJcefPanel) {
            val reloadAction = ActionManager.getInstance().getAction(reloadActionId) ?:
                error("Could not get document reload action")
            val notification = Notification(
                PdfViewerBundle.message("pdf.viewer.notifications.group.id"),
                PdfViewerBundle.message("pdf.viewer.editor.panel.notifications.document.open.failed.title"),
                PdfViewerBundle.message("pdf.viewer.editor.panel.notifications.document.open.failed.content"),
                NotificationType.ERROR
            )
            notification.addAction(with(reloadAction.templatePresentation) {
                object: AnAction(text, description, icon) {
                    override fun actionPerformed(event: AnActionEvent) {
                        if (!panel.browserPanel.isDisposed) {
                            panel.reloadDocument()
                        }
                    }
                }
            })
            Notifications.Bus.notify(notification)
        }

        private fun <T> updateAtomicReference(reference: AtomicReference<T>, transform: (T) -> T) {
            while (true) {
                val current = reference.get()
                if (reference.compareAndSet(current, transform(current))) {
                    break
                }
            }
        }
    }
}
