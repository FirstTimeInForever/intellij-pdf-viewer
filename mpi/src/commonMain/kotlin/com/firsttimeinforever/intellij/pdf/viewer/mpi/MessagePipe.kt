package com.firsttimeinforever.intellij.pdf.viewer.mpi

interface MessagePipe {
    fun send(type: String, data: String)

    fun subscribe(type: String, handler: MessageReceivedHandler)

    companion object {
        const val browserSendFunctionName = "__messagePipe_sendMessageToBrowser"
        const val ideSendFunctionName = "__messagePipe_sendMessageToIde"
    }
}
