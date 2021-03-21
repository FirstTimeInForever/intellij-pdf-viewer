package com.firsttimeinforever.intellij.pdf.viewer.mpi.model

import kotlinx.serialization.Serializable

@Serializable
data class ZoomState(
    val mode: ZoomMode = ZoomMode.AUTO,
    val value: Double = 100.0,
    val leftOffset: Int = 0,
    val topOffset: Int = 0
)
