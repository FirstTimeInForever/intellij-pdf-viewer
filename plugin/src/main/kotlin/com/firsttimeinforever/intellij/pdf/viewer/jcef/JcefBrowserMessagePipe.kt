package com.firsttimeinforever.intellij.pdf.viewer.jcef

import com.firsttimeinforever.intellij.pdf.viewer.BrowserMessages
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addLoadHandler
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addUnitHandler
import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.executeJavaScript
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe.Companion.browserSendFunctionName
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe.Companion.ideSendFunctionName
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessageReceivedHandler
import com.intellij.openapi.diagnostic.logger
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBase
import com.intellij.ui.jcef.JBCefJSQuery
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandlerAdapter
import org.cef.network.CefRequest

class JcefBrowserMessagePipe(private val browser: JBCefBrowser) : MessagePipe {
  private val query = checkNotNull(JBCefJSQuery.create(browser as JBCefBrowserBase))
  private val receiveSubscribers = hashMapOf<String, MutableList<MessageReceivedHandler>>()
  private val pendingMessages = ArrayDeque<String>()
  private val lock = Any()
  @Volatile
  private var isBrowserReady = false

  init {
    query.addUnitHandler(::receiveHandler)
    browser.addLoadHandler(object : CefLoadHandlerAdapter() {
      override fun onLoadStart(browser: CefBrowser, frame: CefFrame, transitionType: CefRequest.TransitionType) {
        if (!frame.isMain) return
        synchronized(lock) {
          isBrowserReady = false
        }
      }

      override fun onLoadEnd(browser: CefBrowser, frame: CefFrame, httpStatusCode: Int) {
        if (!frame.isMain) return
        val code = query.inject("raw")
        this@JcefBrowserMessagePipe.browser.executeJavaScript("window['$ideSendFunctionName'] = raw => $code;")
        this@JcefBrowserMessagePipe.browser.executeJavaScript("window.dispatchEvent(new Event('IdeReady'));")
      }
    })
  }

  private fun receiveHandler(raw: String) {
    logger.debug(raw)
    val (type, data) = MessagePipeSupport.MessagePacker.unpack(raw)
    if (type == BrowserMessages.BrowserReady::class.simpleName) {
      onBrowserReady()
      return
    }
    callSubscribers(type, data)
  }

  private fun onBrowserReady() {
    val messagesToFlush = mutableListOf<String>()
    synchronized(lock) {
      isBrowserReady = true
      while (pendingMessages.isNotEmpty()) {
        messagesToFlush += pendingMessages.removeFirst()
      }
    }
    messagesToFlush.forEach { browser.executeJavaScript(it) }
  }

  private fun callSubscribers(type: String, data: String) {
    when (val subscribers = receiveSubscribers[type]) {
      null -> logger.warn("No subscribers for $type!\nAttached data: $data")
      else -> subscribers.forEach { it.messageReceived(data) }
    }
  }

  override fun send(type: String, data: String) {
    val raw = MessagePipeSupport.MessagePacker.pack(type, data)
    logger.debug("Sending message: $raw")
    val messageCall = "$browserSendFunctionName('$raw')"
    synchronized(lock) {
      if (!isBrowserReady) {
        pendingMessages.addLast(messageCall)
        return
      }
    }
    browser.executeJavaScript(messageCall)
  }

  override fun subscribe(type: String, handler: MessageReceivedHandler) {
    // TODO: Make target value list lazy somehow
    receiveSubscribers.merge(type, mutableListOf(handler)) { current, _ ->
      current.also { it.add(handler) }
    }
  }

  companion object {
    private val logger = logger<JcefBrowserMessagePipe>()
  }
}
