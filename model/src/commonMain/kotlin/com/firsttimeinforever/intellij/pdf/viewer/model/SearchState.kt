package com.firsttimeinforever.intellij.pdf.viewer.model

// object PdfFindState {
//   const val FOUND = 0
//   const val NOT_FOUND = 1
//   const val WRAPPED = 2
//   const val PENDING = 3
// }
enum class SearchState {
  FOUND,
  NOT_FOUND,
  WRAPPED,
  PENDING
}
