/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.applications

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ThemePreview(private val title: String, private val theme: Theme) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.theme_preview, container, false)

        val windowBackground = theme.windowBackground
        val primaryDark = theme.primaryDark
        val primary = theme.primary
        val accent = theme.accent
        v.findViewById<ViewGroup>(R.id.statusbar_theme).setBackgroundColor(primaryDark)
        v.findViewById<ViewGroup>(R.id.toolbar_theme).setBackgroundColor(primary)
        v.findViewById<ViewGroup>(R.id.layout_theme).setBackgroundColor(windowBackground)
        v.findViewById<TextView>(R.id.fab_theme).backgroundTintList = ColorStateList.valueOf(accent)
        v.findViewById<TextView>(R.id.fab_theme).setTextColor(if (isColorDark(accent)) Color.WHITE else Color.BLACK)
        v.findViewById<TextView>(R.id.title_theme).text = title
        v.findViewById<TextView>(R.id.title_theme).setTextColor(if (isColorDark(primary)) Color.WHITE else Color.BLACK)

        return v
    }

    private fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    class Theme(val windowBackground: Int, val primaryDark: Int, val primary: Int, val accent: Int)
}
