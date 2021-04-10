package com.firsttimeinforever.intellij.pdf.viewer.application.utility

import kotlin.js.Promise

object PromiseBuilders {
  @Suppress("NOTHING_TO_INLINE")
  inline fun promise(noinline block: (resolve: () -> Unit) -> Unit): Promise<dynamic> {
    return Promise { resolve, _ -> block(resolve.asDynamic()) }
  }

  inline fun <reified T> promise(noinline block: (resolve: (T) -> Unit) -> Unit): Promise<T> {
    return Promise { resolve, _ -> block(resolve.asDynamic()) }
  }
}
