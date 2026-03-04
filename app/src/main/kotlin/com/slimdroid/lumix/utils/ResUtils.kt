package com.slimdroid.lumix.utils

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.Dimension
import androidx.annotation.Dimension.Companion.DP
import androidx.annotation.Dimension.Companion.PX

object ResUtils {

    @Dimension(unit = Dimension.SP)
    fun pixelToSp(dimen: Float): Int {
        return (dimen / Resources.getSystem().displayMetrics.scaledDensity).toInt()
    }

    @Dimension(unit = PX)
    fun dpToPixel(@Dimension(unit = DP) dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        )
    }

}