@file:OptIn(ExperimentalSerializationApi::class)

package com.firsttimeinforever.intellij.pdf.viewer.mpi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MessagePipeSupport {
  /**
   * Use this function instead of raw [MessagePipe.send], as it will automagically
   * serialize message for you.
   */
  inline fun <reified T : Any> MessagePipe.send(message: T) {
    send(message::class.simpleName!!, Json.encodeToString(message))
  }

  /**
   * Use this function instead of raw [MessagePipe.subscribe], as it will automagically
   * deserialize message for you.
   */
  inline fun <reified T> MessagePipe.subscribe(crossinline handler: (T) -> Unit) {
    subscribe(T::class.simpleName!!) {
      handler(Json.decodeFromString(it))
    }
  }

  object MessagePacker {
    fun pack(type: String, data: String): String {
      val string = Json.encodeToString(PackedMessage(type, data))
      return MessageEncoder.encode(string)
    }

    fun unpack(raw: String): Pair<String, String> {
      val (type, data) = Json.decodeFromString<PackedMessage>(MessageEncoder.decode(raw))
      return type to data
    }
  }
}
