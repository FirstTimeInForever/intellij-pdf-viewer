package com.firsttimeinforever.intellij.pdf.viewer.mpi

/**
 * Basically an additional step of packing messages that serves as a workaround
 * for JCEFs problems with encoding in executeJavaScript(). Checkout it's implementations
 * for JVM and JS in jvmMain and jsMain.
 */
expect object MessageEncoder {
  /**
   * Called before sending message.
   */
  fun encode(data: String): String

  /**
   * Called on receiving message.
   */
  fun decode(data: String): String
}
