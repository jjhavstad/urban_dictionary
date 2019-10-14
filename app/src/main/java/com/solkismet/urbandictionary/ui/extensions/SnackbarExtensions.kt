package com.solkismet.urbandictionary.ui.extensions

import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar

fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar{
    view.setBackgroundColor(colorInt)
    return this
}
