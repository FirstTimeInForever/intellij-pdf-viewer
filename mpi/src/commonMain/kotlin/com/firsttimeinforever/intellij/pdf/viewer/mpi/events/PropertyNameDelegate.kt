package com.firsttimeinforever.intellij.pdf.viewer.mpi.events

import kotlin.reflect.KProperty

internal object PropertyNameDelegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>) = property.name
}
