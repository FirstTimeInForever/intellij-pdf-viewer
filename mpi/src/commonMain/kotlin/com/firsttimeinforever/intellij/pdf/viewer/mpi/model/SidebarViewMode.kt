package com.firsttimeinforever.intellij.pdf.viewer.mpi.model

import kotlinx.serialization.Serializable

/*
const SidebarView = {
  UNKNOWN: -1,
  NONE: 0,
  THUMBS: 1, // Default value.
  OUTLINE: 2,
  ATTACHMENTS: 3,
  LAYERS: 4,
};
 */
/**
 * Order of values matters!
 */
@Serializable
enum class SidebarViewMode {
  NONE,
  THUMBNAILS,
  OUTLINE,
  ATTACHMENTS
}
