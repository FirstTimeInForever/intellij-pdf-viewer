package com.firsttimeinforever.intellij.pdf.viewer.ui.editor.panel.jcef.events.objects

import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

/**
 *
 * @property page The page of the pdf file.
 * @property x The x coordinate in the pdf file.
 * @property y The y coordinate in the pdf file.
 */
@Serializable
data class SynctexInverseDataObject(
    val page: Int,
    val x: Double,
    val y: Double
)

@Serializable
data class SynctexFowardDataObject(
    val page: Int,
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)

/**
 *
 */
object SynctexCoordinateTransformation {

    private val TRANSFORMATION = LinearTransformation(0.78, -13.4)
    private val TRANS = LinearTransformation(0.825, 0.0);

    /**
     * Linear map that transforms a synctex y coordinate to a y coordinate in PDFjs.
     */
    fun toPdf(y: Double) = TRANS.inverse(y)
//    fun toPdf(y: Int) = TRANSFORMATION.apply(y)

    /**
     * Linear map that transforms a PDFjs y coordinate to a y coordinate in synctex.
     */
    fun fromPdf(y: Double) = TRANS.apply(y)
//    fun fromPdf(y: Int) = TRANSFORMATION.inverse(y)

    private class LinearTransformation(private val a: Double, private val b: Double) {
        fun apply(y: Double): Double = (a * y + b)

        fun inverse(y: Double): Double = (y/a - b/a)
    }
}