/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.applications

import android.app.UiModeManager
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.dertyp7214.themeablecomponents.utils.ThemeManager

object Helper {
    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun checkUiModeAvailability(context: Context, value: Boolean, themeManager: ThemeManager?): Boolean {
        val uiModeService = (context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager)
        val mode = if (value) UiModeManager.MODE_NIGHT_YES else UiModeManager.MODE_NIGHT_NO
        if (themeManager != null) themeManager.darkMode = value
        uiModeService.nightMode = mode
        return uiModeService.nightMode == mode
    }
}
