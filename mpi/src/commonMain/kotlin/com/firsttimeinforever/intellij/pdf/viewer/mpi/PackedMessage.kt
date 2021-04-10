package com.firsttimeinforever.intellij.pdf.viewer.mpi

import kotlinx.serialization.Serializable

@Serializable
data class PackedMessage(
  val type: String,
  val data: String
)
