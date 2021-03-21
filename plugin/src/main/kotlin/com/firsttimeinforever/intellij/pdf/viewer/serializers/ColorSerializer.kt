package com.firsttimeinforever.intellij.pdf.viewer.serializers

import com.intellij.ui.ColorUtil
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.awt.Color

internal class ColorSerializer: KSerializer<Color> {
    override val descriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color {
        return ColorUtil.fromHex(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeString("#${ColorUtil.toHex(value, true)}")
    }
}
