package com.firsttimeinforever.intellij.pdf.viewer.mpi

/**
 * Defines base interface for message channel implementation.
 * Both IDE and browser implementations must be in sync with each other.
 */
interface MessagePipe {
  /**
   * Sends raw message with type [type] and content [data] to the other end.
   *
   * Use [MessagePipeSupport.send] instead!
   */
  fun send(type: String, data: String)

  /**
   * Subscribes to message with type [type] and calls [handler], passing message content
   * to [MessageReceivedHandler.messageReceived].
   *
   * Use [MessagePipeSupport.subscribe] instead!
   */
  fun subscribe(type: String, handler: MessageReceivedHandler)

  companion object {
    const val browserSendFunctionName = "__messagePipe_sendMessageToBrowser"
    const val ideSendFunctionName = "__messagePipe_sendMessageToIde"
  }
}
