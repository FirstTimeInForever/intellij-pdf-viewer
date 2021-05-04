package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefBrowserMessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.invokeAndWaitForLoadEnd
import com.firsttimeinforever.intellij.pdf.viewer.jcef.PdfStaticServer
import com.firsttimeinforever.intellij.pdf.viewer.mpi.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.*
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.ViewThemeUtils.create
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettings
import com.firsttimeinforever.intellij.pdf.viewer.settings.PdfViewerSettingsListener
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
  val presentationController = PdfPresentationController()
  private val messageBusConnection = project.messageBus.connect(this)

  private val loadLock = ReentrantLock()

  var viewState = ViewState()
    private set

  var viewProperties = ViewProperties()
    private set

  init {
    Disposer.register(this, browser)
    Disposer.register(this, messageBusConnection)
    pipe.subscribe<BrowserMessages.InitialViewProperties> {
      logger.debug(it.toString())
      viewProperties = it.properties
      // TODO: Move to dedicated in-memory stylesheet and serve it as a resource
      updateViewTheme(collectThemeColors())
    }
    pipe.subscribe<BrowserMessages.ViewStateChanged> {
      logger.debug(it.toString())
      viewStateChanged(it.state, it.reason)
    }
    pipe.subscribe<BrowserMessages.DocumentInfoResponse> {
      Dialogs.showDocumentInfoDialog(it.info)
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

  fun find(text: String, direction: SearchDirection) {
    pipe.send(IdeMessages.Search(text, direction))
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

  override fun dispose() = Unit

  companion object {
    private val logger = logger<PdfJcefPreviewController>()

    private fun buildUrlWithState(base: String, state: ViewState): String {
      return with(state) {
        val zoom = when (zoom.mode) {
          ZoomMode.CUSTOM -> "${zoom.value},${zoom.leftOffset},${zoom.topOffset}"
          ZoomMode.PAGE_FIT -> "page-fit"
          ZoomMode.PAGE_WIDTH -> "page-height"
          ZoomMode.PAGE_HEIGHT -> "page-height"
          ZoomMode.AUTO -> "auto"
        }
        "$base#page=$page&zoom=$zoom"
      }
    }
  }
}
