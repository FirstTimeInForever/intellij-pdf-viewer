package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.view

import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefBrowserMessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addLoadEndHandler
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addLoadHandler
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.invokeAndWaitForLoadEnd
import com.firsttimeinforever.intellij.pdf.viewer.jcef.PdfStaticServer
import com.firsttimeinforever.intellij.pdf.viewer.mpi.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.IdeMessages
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.send
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport.subscribe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.model.*
import com.firsttimeinforever.intellij.pdf.viewer.ui.editor.presentation.PdfPresentationController
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeAndWaitIfNeeded
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColorUtil
import com.intellij.ui.jcef.JCEFHtmlPanel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.locks.ReentrantLock

class PdfJcefPreviewController(val project: Project, val virtualFile: VirtualFile) : Disposable, DumbAware {
  // TODO: Migrate to OSR when it's ready
  val browser = JCEFHtmlPanel("about:blank")
  val pipe = JcefBrowserMessagePipe(browser)
  val presentationController = PdfPresentationController()
  private val busConnection = project.messageBus.connect(this)

  private val loadLock = ReentrantLock()

  var viewState = ViewState()
    private set

  var viewProperties = ViewProperties()
    private set

  init {
    Disposer.register(this, browser)
    Disposer.register(this, busConnection)
    pipe.subscribe<BrowserMessages.InitialViewProperties> {
      logger.debug(it.toString())
      viewProperties = it.properties
    }
    pipe.subscribe<BrowserMessages.ViewStateChanged> {
      logger.debug(it.toString())
      viewStateChanged(it.state, it.reason)
    }
    busConnection.subscribe(EditorColorsManager.TOPIC, EditorColorsListener { scheme ->
      scheme?.let {
        pipe.send(
          IdeMessages.LafChanged(
            "#${ColorUtil.toHex(it.defaultBackground, true)}",
            "#${ColorUtil.toHex(it.defaultForeground, true)}"
          )
        )
      }
    })
    reload(tryToPreserveState = true)
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
