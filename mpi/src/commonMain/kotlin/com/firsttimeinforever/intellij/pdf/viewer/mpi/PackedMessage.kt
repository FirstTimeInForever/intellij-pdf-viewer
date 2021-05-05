package com.firsttimeinforever.intellij.pdf.viewer.mpi

import kotlinx.serialization.Serializable

/**
 * This is an internal message wrapper used by message passing protocol.
 */
@Serializable
data class PackedMessage(
  val type: String,
  val data: String
)
