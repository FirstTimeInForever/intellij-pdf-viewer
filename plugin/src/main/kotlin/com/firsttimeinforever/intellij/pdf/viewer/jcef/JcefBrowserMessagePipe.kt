package com.firsttimeinforever.intellij.pdf.viewer.jcef

import com.firsttimeinforever.intellij.pdf.viewer.jcef.JcefUtils.addLoadEndHandler
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

class JcefBrowserMessagePipe(private val browser: JBCefBrowser) : MessagePipe {
  private val query = checkNotNull(JBCefJSQuery.create(browser as JBCefBrowserBase))
  private val receiveSubscribers = hashMapOf<String, MutableList<MessageReceivedHandler>>()

  init {
    query.addUnitHandler(::receiveHandler)
    browser.addLoadEndHandler {
      // FIXME: Implement generic way of packing/unpacking messages into strings
      val code = query.inject("raw")
      browser.executeJavaScript("window['$ideSendFunctionName'] = raw => $code;")
      browser.executeJavaScript("window.dispatchEvent(new Event('IdeReady'));")
    }
  }

  private fun receiveHandler(raw: String) {
    logger.debug(raw)
    val (type, data) = MessagePipeSupport.MessagePacker.unpack(raw)
    callSubscribers(type, data)
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
    browser.executeJavaScript("$browserSendFunctionName('$raw')")
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
