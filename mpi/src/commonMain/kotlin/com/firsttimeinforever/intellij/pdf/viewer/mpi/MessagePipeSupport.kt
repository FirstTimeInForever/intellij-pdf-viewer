package com.firsttimeinforever.intellij.pdf.viewer.mpi

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MessagePipeSupport {
    inline fun <reified T: Any> MessagePipe.send(message: T) {
        send(message::class.simpleName!!, Json.encodeToString(message))
    }

    inline fun <reified T> MessagePipe.subscribe(crossinline handler: (T) -> Unit) {
        subscribe(T::class.simpleName!!) {
            handler(Json.decodeFromString(it))
        }
    }

    object MessagePacker {
        fun pack(type: String, data: String): String {
            return Json.encodeToString(PackedMessage(type, data))
        }

        fun unpack(raw: String): Pair<String, String> {
            val (type, data) = Json.decodeFromString<PackedMessage>(raw)
            return type to data
        }
    }
}
