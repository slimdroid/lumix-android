package com.slimdroid.lumix.ui.shape

import androidx.compose.ui.graphics.Path
import androidx.graphics.shapes.RoundedPolygon

fun RoundedPolygon.toPath() = Path().apply {
    var first = true
    rewind()
    for (cubic in cubics) {
        if (first) {
            moveTo(cubic.anchor0X, cubic.anchor0Y)
            first = false
        }
        cubicTo(
            cubic.control0X,
            cubic.control0Y,
            cubic.control1X,
            cubic.control1Y,
            cubic.anchor1X,
            cubic.anchor1Y
        )
    }
    close()
}