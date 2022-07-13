package com.fs.customviews.simple_button

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat

/**
 * Created by cindyfeliciasantoso on 13/07/22
 * Copyright (c) Cindy Felicia Santoso
 */

fun Int.getColor(context: Context) : Int {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        context.resources.getColor(this, context.theme)
    } else {
        ContextCompat.getColor(context, this)
    }
}

fun Int.toPx(contexts: Context): Int {
    val context = contexts
    return (this * context.resources.displayMetrics.density).toInt()
}

fun Int.toDp(contexts: Context): Int {
    val context = contexts
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        )
    )
}
