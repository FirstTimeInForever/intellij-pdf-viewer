package com.firsttimeinforever.intellij.pdf.viewer.application

import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe.Companion.browserSendFunctionName
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipe.Companion.ideSendFunctionName
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessagePipeSupport
import com.firsttimeinforever.intellij.pdf.viewer.mpi.MessageReceivedHandler
import kotlinx.browser.window
import org.w3c.dom.get

class BrowserMessagePipe : MessagePipe {
  private val receiveSubscribers = hashMapOf<String, MutableList<MessageReceivedHandler>>()

  init {
    val dynamicWindow = window.asDynamic()
    dynamicWindow[browserSendFunctionName] = { raw: String ->
      val (type, data) = MessagePipeSupport.MessagePacker.unpack(raw)
      console.log()
      callSubscribers(type, data)
    }
  }

  private fun callSubscribers(type: String, data: String) {
    when (val subscribers = receiveSubscribers[type]) {
      null -> console.warn("No subscribers for message type: $type!")
      else -> subscribers.forEach { it.messageReceived(data) }
    }
  }

  override fun send(type: String, data: String) {
    try {
      val raw = MessagePipeSupport.MessagePacker.pack(type, data)
      window[ideSendFunctionName](raw)
    } catch (exception: dynamic) {
      println(exception)
    }
  }

  override fun subscribe(type: String, handler: MessageReceivedHandler) {
    when {
      receiveSubscribers.contains(type) -> receiveSubscribers[type]!!.add(handler)
      else -> receiveSubscribers[type] = mutableListOf(handler)
    }
  }
}
