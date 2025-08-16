package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefBrowserMessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addConsoleMessageListener
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.createDefaultConsoleMessageListener
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.invokeAndWaitForLoadEnd
import com.firsttimeinforever.intellij.pdf.viewer.jcef.PdfStaticServer
import com.firsttimeinforever.intellij.pdf.viewer.model.*
import com.firsttimeinforever.intellij.pdf.viewer.model.ViewThemeUtils.create
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettingsListener
import com.firsttimeinforever.intellij.pdf.viewer.tex.SynctexPreciseLocation
import com.firsttimeinforever.intellij.pdf.viewer.tex.SynctexUtils.isSynctexFileAvailable
import com.firsttimeinforever.intellij.pdf.viewer.tex.SynctexUtils.isSynctexInstalled
import com.firsttimeinforever.intellij.pdf.viewer.tex.TexFileInfo
import com.firsttimeinforever.intellij.pdf.viewer.ui.dialogs.Dialogs
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation.PdfPresentationController
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.util.ui.UIUtil
import io.netty.handler.codec.http.QueryStringDecoder
import kotlinx.serialization.json.JsonElement
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefContextMenuParams
import org.cef.callback.CefMenuModel
import org.cef.handler.CefContextMenuHandlerAdapter
import java.awt.Color
import java.net.URI
import java.util.concurrent.atomic.AtomicBoolean

class PdfJcefPreviewController(val project: Project, val virtualFile: VirtualFile) :
  PdfViewerSettingsListener,
  EditorColorsListener,
  Disposable,
  DumbAware
{
  // TODO: Migrate to OSR when it's ready
  val browser = JCEFHtmlPanel(useOsr, null, "about:blank").apply {
    setOpenLinksInExternalBrowser(true)
  }
  val pipe = JcefBrowserMessagePipe(browser)
  val presentationController = PdfPresentationController(this)
  private val messageBusConnection = project.messageBus.connect(this)

  private val isReloading = AtomicBoolean(false)
  private var viewLoaded = false
  private var firstLoad = true

  /**
   * Current view state of the preview.
   */
  var viewState = ViewState()
    private set

  var viewProperties = ViewProperties()
    private set

  /**
   * Is null if current document does not have bookmarks.
   */
  var outline: PdfOutlineNode? = null
    private set

  private var currentForwardSearchData: SynctexPreciseLocation? = null

  init {
    Disposer.register(this, browser)
    Disposer.register(this, messageBusConnection)

    if (PdfViewerSettings.isDebugMode) {
      browser.addConsoleMessageListener(createDefaultConsoleMessageListener(logger))
    }

    browser.jbCefClient.addContextMenuHandler(object : CefContextMenuHandlerAdapter() {
      private fun getPdfUrl(cefBrowser: CefBrowser?): String? {
        if (!viewLoaded || cefBrowser == null) return null
        val urlDecoder = QueryStringDecoder(cefBrowser.url)
        val file = urlDecoder.parameters()?.get("file")?.get(0) ?: return null
        return URI(cefBrowser.url).resolve(file).toString()
      }

      override fun onBeforeContextMenu(cefBrowser: CefBrowser?, frame: CefFrame?, params: CefContextMenuParams?, model: CefMenuModel?) {
        getPdfUrl(cefBrowser) ?: return
        model?.addItem(CefMenuModel.MenuId.MENU_ID_USER_FIRST, "Open in Embedded Browser")
      }

      override fun onContextMenuCommand(cefBrowser: CefBrowser?, frame: CefFrame?, params: CefContextMenuParams?, commandId: Int, eventFlags: Int): Boolean {
        if (commandId != CefMenuModel.MenuId.MENU_ID_USER_FIRST) return false
        val url = getPdfUrl(cefBrowser) ?: return true
        logger.debug("would load pdf, base url: ${cefBrowser?.url}, pdf url: $url")
        viewLoaded = false
        browser.loadURL(url)
        return true
      }
    }, browser.cefBrowser)

    pipe.subscribe<BrowserMessages.InitialViewProperties> {
      logger.debug(it.toString())
      viewProperties = it.properties
      // TODO: Move to dedicated in-memory stylesheet and serve it as a resource
      updateViewTheme(collectThemeColors())
      pipe.send(IdeMessages.SynctexAvailability(virtualFile.parent != null && virtualFile.isSynctexFileAvailable() && isSynctexInstalled()))
      viewLoaded = true
      firstLoad = false
    }
    pipe.subscribe<BrowserMessages.DocumentOutline> {
      outline = it.outlineNode
      project.messageBus.syncPublisher(PdfOutlineChangedListener.TOPIC).outlineChanged(it.outlineNode)
    }
    pipe.subscribe<BrowserMessages.ViewStateChanged> {
      logger.debug(it.toString())
      viewStateChanged(it.state, it.reason)
    }
    pipe.subscribe<BrowserMessages.DocumentInfoResponse> {
      Dialogs.showDocumentInfoDialog(it.info)
    }

    pipe.subscribe<BrowserMessages.SynctexSyncEditor> {
      TexFileInfo.fromSynctexInfoData(virtualFile, it.coordinates)?.syncEditor(project)
    }
    pipe.subscribe<BrowserMessages.AskForwardSearchData> {
      currentForwardSearchData?.let {
        pipe.send(IdeMessages.SynctexForwardSearch(it))
      }
    }
    pipe.subscribe<BrowserMessages.BeforeReloadViewState> {
      viewStateChanged(it.state, ViewStateChangeReason.UNSPECIFIED)
      doActualReload(tryToPreserveState = true)
    }
    doActualReload(tryToPreserveState = true)
    messageBusConnection.subscribe(PdfViewerSettings.TOPIC, this)
    messageBusConnection.subscribe(EditorColorsManager.TOPIC, this)
  }

  private fun viewStateChanged(viewState: ViewState, reason: ViewStateChangeReason) {
    this.viewState = viewState
    project.messageBus.syncPublisher(PdfViewStateChangedListener.TOPIC).viewStateChanged(
      this,
      this.viewState,
      reason
    )
  }

  val component get() = browser.component

  private fun doActualReload(tryToPreserveState: Boolean = false) {
    viewLoaded = false
    try {
      val base = PdfStaticServer.instance.getPreviewUrl(virtualFile, withReloadSalt = true)
      if (firstLoad) {
        viewState = viewState.copy(sidebarViewMode = PdfViewerSettings.instance.defaultSidebarViewMode)
      }
      val url = when {
        tryToPreserveState -> buildUrlWithState(base, viewState)
        else -> "$base#${createPageModeParameter(PdfViewerSettings.instance.defaultSidebarViewMode)}"
      }
      browser.invokeAndWaitForLoadEnd {
        logger.debug("Loading url $url")
        browser.loadURL(url)
      }
    } catch(exception: Throwable) {
      logger.error(exception)
    } finally {
      isReloading.set(false)
    }
  }

  fun reload(tryToPreserveState: Boolean = false) {
    if (isReloading.compareAndSet(false, true)) {
      // Reloading while preserving the state only makes sense if the pdf was loaded and we wanted to preserve state.
      // Reloading without state should always be available, e.g., a corrupt pdf that was still open could have been fixed and can now be reloaded.
      when (viewLoaded && tryToPreserveState) {
        true -> pipe.send(IdeMessages.BeforeReload())
        else -> doActualReload(false)
      }
    }
  }

  private fun collectThemeColors(
    background: Color = UIUtil.getPanelBackground(),
    foreground: Color = UIUtil.getLabelForeground()
  ): ViewTheme {
    val colorInvertIntensity = PdfViewerSettings.instance.run {
      when {
        invertDocumentColors -> documentColorsInvertIntensity
        else -> 0
      }
    }
    return when {
      PdfViewerSettings.instance.useCustomColors -> PdfViewerSettings.instance.run {
        ViewTheme.create(
          JBColor(customBackgroundColor, customBackgroundColor),
          JBColor(customForegroundColor, customForegroundColor),
          JBColor(customIconColor, customIconColor),
          colorInvertIntensity
        )
      }
      else -> ViewTheme.create(
        background,
        foreground,
        PdfViewerSettings.defaultIconColor,
        colorInvertIntensity
      )
    }
  }

  override fun globalSchemeChange(scheme: EditorColorsScheme?) {
    logger.info("Global color scheme changed")
    updateViewTheme(collectThemeColors())
  }

  override fun settingsChanged(settings: PdfViewerSettings) {
    logger.info("Settings changed")
    updateViewTheme(collectThemeColors())
  }

  fun find(query: SearchQuery, direction: SearchDirection) {
    pipe.send(IdeMessages.Search(query, direction))
  }

  fun releaseSearchHighlighting() {
    pipe.send(IdeMessages.ReleaseSearchHighlighting())
  }

  fun setSidebarViewMode(mode: SidebarViewMode) {
    pipe.send(IdeMessages.SidebarViewModeSetRequest(mode))
  }

  fun goToPage(direction: PageGotoDirection) {
    pipe.send(IdeMessages.GotoPage(direction))
  }

  fun setPageSpreadState(state: PageSpreadState) {
    pipe.send(IdeMessages.PageSpreadStateSetRequest(state))
  }

  fun requestDocumentInfo() {
    pipe.send(IdeMessages.DocumentInfoRequest())
  }

  fun steppedChangeScale(increase: Boolean = true) {
    pipe.send(IdeMessages.ChangeScaleStepped(increase))
  }

  fun rotate(clockwise: Boolean = true) {
    pipe.send(IdeMessages.RotatePages(clockwise))
  }

  fun setScrollDirection(direction: ScrollDirection) {
    pipe.send(IdeMessages.SetScrollDirection(direction))
  }

  @Suppress("MemberVisibilityCanBePrivate")
  fun updateViewTheme(viewTheme: ViewTheme) {
    pipe.send(IdeMessages.UpdateThemeColors(viewTheme))
  }

  fun setForwardSearchData(data: SynctexPreciseLocation) {
    currentForwardSearchData = data
    if (viewLoaded) {
      pipe.send(IdeMessages.SynctexForwardSearch(data))
    }
  }

  fun canNavigate(): Boolean = outline != null

  fun navigate(destinationReference: JsonElement) {
    pipe.send(IdeMessages.NavigateTo(destinationReference))
  }

  fun navigateHistory(direction: HistoryNavigationDirection) {
    pipe.send(IdeMessages.NavigateHistory(direction))
  }

  fun setInverseSearchShortcuts(shortcuts: Set<String>) {
    pipe.send(IdeMessages.InverseSearchShortcuts(shortcuts))
  }

  override fun dispose() {
    logger.debug("dispose $virtualFile")
    PdfStaticServer.instance.disposePreviewUrl(virtualFile)
  }

  companion object {
    private val logger = logger<PdfJcefPreviewController>()

    private val useOsr
      get() = Registry.`is`("pdf.viewer.use.jcef.osr.view")

    private fun getPagemodeValue(value: SidebarViewMode): String {
      return when (value) {
        SidebarViewMode.NONE -> "none"
        SidebarViewMode.THUMBNAILS -> "thumbs"
        SidebarViewMode.OUTLINE -> "bookmarks"
        SidebarViewMode.ATTACHMENTS -> "attachments"
      }
    }

    private fun createPageModeParameter(value: SidebarViewMode): String {
      return "pagemode=${getPagemodeValue(value)}"
    }

    private fun buildUrlWithState(base: String, state: ViewState): String {
      val positionBase = with(state) {
        val zoom = when (zoom.mode) {
          ZoomMode.CUSTOM -> "${zoom.value},${zoom.leftOffset},${zoom.topOffset}"
          ZoomMode.PAGE_FIT -> "page-fit"
          ZoomMode.PAGE_WIDTH -> "page-height"
          ZoomMode.PAGE_HEIGHT -> "page-height"
          ZoomMode.AUTO -> "auto"
        }
        "$base#page=$page&zoom=$zoom&${createPageModeParameter(state.sidebarViewMode)}"
      }
      return positionBase
      // return when {
      //   PdfViewerSettings.instance.doNotOpenSidebarAutomatically -> "$positionBase&pagemode=none"
      //   else -> positionBase
      // }
    }
  }
}
