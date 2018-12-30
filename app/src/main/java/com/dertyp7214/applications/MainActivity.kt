@file:Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")

package com.dertyp7214.applications

import android.app.ProgressDialog
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.dertyp7214.themeablecomponents.components.ThemeableProgressBar
import com.dertyp7214.themeablecomponents.utils.ThemeManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val applicationsWS = ArrayList<ApplicationInfo>()
    private val applications = ArrayList<ApplicationInfo>()
    private lateinit var adapter: Adapter
    private lateinit var preferences: SharedPreferences

    private fun ProgressDialog.dismissSafe() {
        try {
            dismiss()
        } catch (e: Exception) {
        }
    }

    private fun showSafe(context: Context, title: String, message: String): ProgressDialog {
        return try {
            ProgressDialog.show(context, title, message)
        } catch (e: Exception) {
            ProgressDialog(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (ThemeManager.getInstance(this).darkMode)
            toolbar.popupTheme = R.style.CustomPopupThemeDark
        else
            toolbar.popupTheme = R.style.CustomPopupThemeLight

        preferences = getSharedPreferences("apps", Context.MODE_PRIVATE)

        adapter = Adapter(this, applications)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        val progressDialog = showSafe(this, "", "Loading apps")
        progressDialog.setIndeterminateDrawable(ThemeableProgressBar(this).indeterminateDrawable)

        Thread {
            applicationsWS.clear()
            applicationsWS.addAll(
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA).sortedWith(
                    comparator = Comparator { o1, o2 ->
                        val name1 = o1.loadLabel(packageManager).toString()
                        val name2 = o2.loadLabel(packageManager).toString()
                        name1.compareTo(name2, true)
                    })
            )
            applications.clear()
            applications.addAll(applicationsWS.filter {
                if (!preferences.getBoolean("system_apps", true)) {
                    !((it.flags and ApplicationInfo.FLAG_SYSTEM) !== 0)
                } else
                    true
            })
            applicationsWS.forEach {
                Adapter.imageMap[it.packageName] = it.loadIcon(packageManager)
            }
            runOnUiThread {
                progressDialog.dismissSafe()
                adapter.notifyDataSetChanged()
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        menu?.findItem(R.id.app_system_apps)?.isChecked = preferences.getBoolean("system_apps", true)

        /*menu?.add(Menu.NONE, 1, Menu.NONE, "Theme Preview Screen")
        menu?.findItem(1)?.setOnMenuItemClickListener {
            startActivity(Intent(this, ThemePreviewScreen::class.java))
            true
        }*/

        if (BuildConfig.DEBUG) {
            menu?.add(Menu.NONE, 2, Menu.NONE, "Toggle Darkmode")
            menu?.findItem(2)?.setOnMenuItemClickListener {
                val themeManager = ThemeManager.getInstance(this)
                val manager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                manager.nightMode = if (themeManager.darkMode) UiModeManager.MODE_NIGHT_NO
                else UiModeManager.MODE_NIGHT_YES
                themeManager.darkMode = !themeManager.darkMode
                recreate()
                true
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.app_system_apps -> {
                item.isChecked = !item.isChecked
                preferences.edit {
                    putBoolean("system_apps", item.isChecked)
                }
                applications.clear()
                applications.addAll(applicationsWS.filter {
                    if (!preferences.getBoolean("system_apps", true)) {
                        !((it.flags and ApplicationInfo.FLAG_SYSTEM) !== 0)
                    } else
                        true
                })
                adapter.notifyDataSetChanged()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
