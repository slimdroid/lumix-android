package com.slimdroid.lumix.utils

import android.content.Context
import androidx.annotation.StringRes

/**
 * in Activity/Fragment/View:
 *```
 * val string = text.resolve(context)
 * ```
 */
sealed class Text {

    abstract fun resolve(context: Context): CharSequence

    data class PlainText(val value: String) : Text() {
        override fun resolve(context: Context): CharSequence = value
    }

    data class ResText(
        @StringRes val resId: Int,
        private val formatArgs: List<Any>? = null
    ) : Text() {
        override fun resolve(context: Context): CharSequence {
            return formatArgs?.let {
                context.getString(resId, it)
            } ?: context.getString(resId)
        }
    }

}