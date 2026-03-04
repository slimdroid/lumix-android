package com.slimdroid.lumix.ui.shape

import androidx.annotation.IntRange
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import kotlin.math.PI
import kotlin.math.cos

/**
 * Shape describing a polygon with rounded corners
 *
 * Note: The shape draws within the minimum of provided width and height so can't be used to create stretched shape.
 *
 * @param sides number of sides. Minimum number of sides - 3.
 * @param cornerRadius radius of rounding of polygon vertices. If the radius of rounding of the
 * vertices of a polygon exceeds the radius of the inscribed circle, then the figure will take the
 * shape of a circle.
 * @param fillMaxSize fills the entire shape if corners are rounded.
 */
class RoundedPolygonShape(
    @IntRange(from = 3) val sides: Int,
    val cornerRadius: Dp = 0.dp,
    val fillMaxSize: Boolean = true
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val centerX = size.center.x
        val centerY = size.center.y
        var radius = size.minDimension / 2f

        if (cornerRadius.value > 0f && fillMaxSize) {
            val scaleFactor: Float =
                cornerRadius.value * density.density * (1 / cos(PI.toFloat() / sides) - 1)
            radius += scaleFactor
        }

        val path = RoundedPolygon(
            numVertices = sides,
            radius = radius,
            centerX = centerX,
            centerY = centerY,
            rounding = CornerRounding(radius = cornerRadius.value * density.density)
        ).toPath()

        if (layoutDirection == LayoutDirection.Rtl) {
            Matrix().apply {
                scale(-1f, 1f)
                translate(-size.width, 0f)
            }.also {
                path.transform(it)
            }
        }

        return Outline.Generic(path = path)
    }
}
