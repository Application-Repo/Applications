/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.applications

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import com.dertyp7214.themeablecomponents.controller.AppController

class Application : AppController() {

    var textColor: Int = Color.BLACK
        private set(value) {
            field = value
        }
    var primaryColor: Int = Color.GRAY
        private set(value) {
            field = value
        }

    override fun onCreate() {
        super.onCreate(true)
        val accentColor = getAttrColor(this, android.R.attr.colorAccent)
        themeManager.setDefaultAccent(accentColor)
        themeManager.changeAccentColor(accentColor)
        themeManager.darkMode = (getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).nightMode ==
                UiModeManager.MODE_NIGHT_YES

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
                applyActivityTheme(activity)
            }

            override fun onActivityResumed(activity: Activity?) {
                applyActivityTheme(activity)
            }

            override fun onActivityStarted(activity: Activity?) {
                applyActivityTheme(activity)
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                applyActivityTheme(activity)
            }
        })
    }

    private fun applyActivityTheme(activity: Activity?) {
        val primaryColor = if (themeManager.darkMode) getAttrColor(this, android.R.attr.colorPrimary)
        else resources.getColor(R.color.lightPrimary)
        this.primaryColor = primaryColor
        textColor = if (themeManager.darkMode)
            Color.WHITE
        else
            Color.BLACK
        if (activity != null) {
            themeManager.enableStatusAndNavBar(activity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                activity.window.navigationBarDividerColor =
                        if (themeManager.darkMode) Color.TRANSPARENT else resources.getColor(R.color.divider)
            themeManager.setDefaultPrimary(primaryColor)
            themeManager.changePrimaryColor(primaryColor)
        }
    }

    private fun getAttrColor(context: Context, attr: Int): Int {
        return try {
            val ta = context.obtainStyledAttributes(intArrayOf(attr))
            val colorAccent = ta.getColor(0, 0)
            ta.recycle()
            colorAccent
        } catch (e: Exception) {
            resources.getColor(R.color.pixelBlue)
        }
    }
}
