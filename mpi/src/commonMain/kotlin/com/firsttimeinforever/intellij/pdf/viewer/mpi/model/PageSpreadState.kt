package com.firsttimeinforever.intellij.pdf.viewer.mpi.model

import kotlinx.serialization.Serializable

/*
const SpreadMode = {
  UNKNOWN: -1,
  NONE: 0, // Default value.
  ODD: 1,
  EVEN: 2,
};
 */
@Serializable
enum class PageSpreadState {
  NONE,
  EVEN,
  ODD
}
