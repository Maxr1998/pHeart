@file:Suppress("NOTHING_TO_INLINE")

package edu.uaux.pheart.util.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

inline fun Context.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

inline fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, resId, duration).show()