package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefBrowserMessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addConsoleMessageListener
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.createDefaultConsoleMessageListener
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.invokeAndWaitForLoadEnd
import com.firsttimeinforever.intellij.pdf.viewer.jcef.PdfStaticServer
import com.firsttimeinforever.intellij.pdf.viewer.model.*
import com.firsttimeinforever.intellij.pdf.viewer.model.ViewThemeUtils.create
import com.firsttimeinforever.intellij.pdf.viewer.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.tex.SynctexPreciseLocation
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettingsListener
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
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.util.concurrent.locks.ReentrantLock

class PdfJcefPreviewController(val project: Project, val virtualFile: VirtualFile) :
  PdfViewerSettingsListener,
  EditorColorsListener,
  Disposable,
  DumbAware
{
  // TODO: Migrate to OSR when it's ready
  val browser = JCEFHtmlPanel("about:blank")
  val pipe = JcefBrowserMessagePipe(browser)
  val presentationController = PdfPresentationController(this)
  private val messageBusConnection = project.messageBus.connect(this)

  private val loadLock = ReentrantLock()

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

    if (Registry.`is`("pdf.viewer.debug", false)) {
      browser.addConsoleMessageListener(createDefaultConsoleMessageListener(logger))
    }

    pipe.subscribe<BrowserMessages.InitialViewProperties> {
      logger.debug(it.toString())
      viewProperties = it.properties
      // TODO: Move to dedicated in-memory stylesheet and serve it as a resource
      updateViewTheme(collectThemeColors())
      pipe.send(IdeMessages.SynctexAvailability(virtualFile.isSynctexFileAvailable() && isSynctexInstalled()))
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

    reload(tryToPreserveState = true)
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

  fun reload(tryToPreserveState: Boolean = false) {
    // FIXME: Replace with BrowserCommunicationChannel on it's availability in the platform
    try {
      loadLock.lock()
      val base = PdfStaticServer.instance.getPreviewUrl(virtualFile.path)
      val url = when {
        tryToPreserveState -> buildUrlWithState(base, viewState)
        else -> base
      }
      browser.invokeAndWaitForLoadEnd {
        logger.debug("Loading url $url")
        browser.loadURL(url)
      }
    } catch(exception: Throwable) {
      logger.error(exception)
    } finally {
      loadLock.unlock()
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
          Color(customBackgroundColor),
          Color(customForegroundColor),
          Color(customIconColor),
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

  fun updateViewTheme(viewTheme: ViewTheme) {
    pipe.send(IdeMessages.UpdateThemeColors(viewTheme))
  }

  fun setForwardSearchData(data: SynctexPreciseLocation) {
    currentForwardSearchData = data
    pipe.send(IdeMessages.SynctexForwardSearch(data))
  }

  fun canNavigate(): Boolean = outline != null

  fun navigate(destinationReference: String) {
    pipe.send(IdeMessages.NavigateTo(destinationReference))
  }

  fun navigateHistory(direction: HistoryNavigationDirection) {
    pipe.send(IdeMessages.NavigateHistory(direction))
  }

  override fun dispose() = Unit

  companion object {
    private val logger = logger<PdfJcefPreviewController>()

    private fun buildUrlWithState(base: String, state: ViewState): String {
      val positionBase = with(state) {
        val zoom = when (zoom.mode) {
          ZoomMode.CUSTOM -> "${zoom.value},${zoom.leftOffset},${zoom.topOffset}"
          ZoomMode.PAGE_FIT -> "page-fit"
          ZoomMode.PAGE_WIDTH -> "page-height"
          ZoomMode.PAGE_HEIGHT -> "page-height"
          ZoomMode.AUTO -> "auto"
        }
        "$base#page=$page&zoom=$zoom"
      }
      return when {
        PdfViewerSettings.instance.doNotOpenSidebarAutomatically -> "$positionBase&pagemode=none"
        else -> positionBase
      }
    }
  }
}
