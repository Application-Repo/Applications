/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.dertyp7214.applications

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.themeablecomponents.utils.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_app_screen.*
import saschpe.android.customtabs.CustomTabsHelper
import saschpe.android.customtabs.WebViewFallback
import java.util.*

class AppScreen : AppCompatActivity() {

    private lateinit var application: ApplicationInfo
    private lateinit var res: Resources

    private fun Intent.getString(key: String): String {
        if (extras != null)
            if (extras!!.containsKey(key))
                if (extras!!.getString(key) != null)
                    return extras!![key] as String
                else finish()
            else finish()
        else finish()
        return ""
    }

    private fun View.setMargins(l: Int, t: Int, r: Int, b: Int) {
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val p = layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(l, t, r, b)
            requestLayout()
        }
    }

    override fun onBackPressed() {
        adaptive.visibility = INVISIBLE
        super.onBackPressed()
        Adapter.locked = false
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_screen)

        val packageName = intent.getString("packageName")

        application = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        res = packageManager.getResourcesForApplication(application)

        iconView = findViewById(R.id.icon)
        iconView?.setImageDrawable(application.loadIcon(packageManager))
        findViewById<TextView>(R.id.title).text = application.loadLabel(packageManager)
        findViewById<TextView>(R.id.packageName).text = application.packageName
        findViewById<TextView>(R.id.txt_apk_location).text = application.sourceDir
        findViewById<TextView>(R.id.txt_install_date).text =
                DateFormat.format(
                    "dd MMM yyyy hh:mm:ss",
                    Date(packageManager.getPackageInfo(application.packageName, 0).firstInstallTime)
                )
        findViewById<TextView>(R.id.txt_last_update).text =
                DateFormat.format(
                    "dd MMM yyyy hh:mm:ss",
                    Date(packageManager.getPackageInfo(application.packageName, 0).lastUpdateTime)
                )
        findViewById<TextView>(R.id.txt_version_name).text =
                packageManager.getPackageInfo(application.packageName, 0).versionName
        findViewById<TextView>(R.id.txt_version_code).text =
                packageManager.getPackageInfo(application.packageName, 0).versionCode.toString()
        findViewById<TextView>(R.id.txt_target_sdk).text = application.targetSdkVersion.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            findViewById<ViewGroup>(R.id.layout_min_sdk).visibility = VISIBLE
            findViewById<TextView>(R.id.txt_min_sdk).text = application.minSdkVersion.toString()
        }

        ThemeManager.attach(this, object : ThemeManager.Callback {
            override fun run(themeManager: ThemeManager) {
                btn_open.rippleColor = ColorStateList.valueOf(themeManager.colorAccent)
                btn_uninstall.rippleColor = ColorStateList.valueOf(themeManager.colorAccent)
                if (themeManager.darkMode) {
                    btn_open.setTextColor(themeManager.colorAccent)
                    btn_uninstall.setTextColor(themeManager.colorAccent)
                    btn_open.strokeColor = ColorStateList.valueOf(Helper.adjustAlpha(Color.WHITE, 0.2F))
                    btn_uninstall.strokeColor = ColorStateList.valueOf(Helper.adjustAlpha(Color.WHITE, 0.2F))
                } else {
                    btn_open.setTextColor(themeManager.colorAccent)
                    btn_uninstall.setTextColor(themeManager.colorAccent)
                    btn_open.strokeColor = ColorStateList.valueOf(Helper.adjustAlpha(Color.BLACK, 0.2F))
                    btn_uninstall.strokeColor = ColorStateList.valueOf(Helper.adjustAlpha(Color.BLACK, 0.2F))
                }
                val launchIntent = packageManager.getLaunchIntentForPackage(application.packageName)
                if (launchIntent != null && application.packageName != BuildConfig.APPLICATION_ID) {
                    btn_open.setOnClickListener { startActivity(launchIntent) }
                } else {
                    btn_open.isEnabled = false
                    btn_open.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    btn_open.setTextColor(Color.DKGRAY)
                    btn_open.strokeColor = ColorStateList.valueOf(Color.GRAY)
                }
                if (((application.flags and ApplicationInfo.FLAG_SYSTEM) !== 0)) {
                    btn_uninstall.isEnabled = false
                    btn_uninstall.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    btn_uninstall.setTextColor(Color.DKGRAY)
                    btn_uninstall.strokeColor = ColorStateList.valueOf(Color.GRAY)
                } else {
                    btn_uninstall.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DELETE)
                        intent.data = Uri.parse("package:${application.packageName}")
                        startActivity(intent)
                    }
                }

                btn_permissions.text = "$permissionCount Permissions"
                if (permissionCount > 0) {
                    btn_permissions.setOnClickListener {
                        openPermissionDialog()
                    }
                } else {
                    btn_permissions.isEnabled = false
                    btn_permissions.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
                    btn_permissions.setTextColor(Color.DKGRAY)
                }
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val animator = ValueAnimator.ofFloat(0F, 1F)
        animator.duration = 500
        animator.addUpdateListener {
            if ((it.animatedValue as Float) == 1F && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                loadIcon()
        }
        animator.start()
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, roundPixelSize: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawRoundRect(rectF, roundPixelSize.toFloat(), roundPixelSize.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadIcon() {
        val icon = application.loadUnbadgedIcon(packageManager)
        if (icon is AdaptiveIconDrawable) {
            val background = zoomBitmap(
                if (icon.background is ColorDrawable) {
                    val bitmap = Bitmap.createBitmap(
                        icon.foreground.intrinsicWidth,
                        icon.foreground.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    canvas.drawColor((icon.background as ColorDrawable).color)
                    bitmap
                } else drawableToBitmap(icon.background), 0.67F
            )
            val foreground = zoomBitmap(drawableToBitmap(icon.foreground), 0.67F)
            adaptiveBg = getRoundedCornerBitmap(background, background.width / 2)
            adaptiveFg = getRoundedCornerBitmap(foreground, foreground.width / 2)
            adaptive.visibility = VISIBLE
            adaptive_background.setImageBitmap(adaptiveBg)
            adaptive_foreground.setImageBitmap(adaptiveFg)
            adaptive.setOnLongClickListener {
                findViewById<View>(R.id.icon).visibility = INVISIBLE
                val bg = Pair.create<View, String>(adaptive_background, "bg")
                val fg = Pair.create<View, String>(adaptive_foreground, "fg")
                val options = ActivityOptions.makeSceneTransitionAnimation(this, fg, bg)
                startActivity(Intent(this, AdaptiveOverview::class.java), options.toBundle())
                true
            }
        }
    }

    private fun zoomBitmap(bitmap: Bitmap, zoom: Float): Bitmap {
        val start = bitmap.width - (bitmap.width * zoom).toInt()
        val end = (bitmap.width * zoom).toInt()
        return Bitmap.createBitmap(bitmap, start / 2, start / 2, end, end)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    private fun openPermissionDialog() {
        val builder = BottomSheet.Builder(this)
        builder.title = "Permissions"
        builder.items = getPermissions()
        builder.build().showBottomSheet(supportFragmentManager)
    }

    private var permissionCount = 0
        get() {
            return try {
                packageManager.getPackageInfo(application.packageName, PackageManager.GET_PERMISSIONS)
                    .requestedPermissions.size
            } catch (e: Exception) {
                0
            }
        }

    private fun getPermissions(): List<BottomSheet.Item> {
        val packageInfo = packageManager.getPackageInfo(application.packageName, PackageManager.GET_PERMISSIONS)
        return try {
            packageInfo.requestedPermissions.map {
                val label = try {
                    val info = packageManager.getPermissionInfo(it, PackageManager.GET_META_DATA)
                    val label = info.loadLabel(packageManager)
                    if (label.isBlank() || label.isEmpty()) info.nonLocalizedLabel
                    else label
                } catch (e: Exception) {
                    it
                }
                val description = try {
                    val info = packageManager.getPermissionInfo(it, PackageManager.GET_META_DATA)
                    val description = info.loadDescription(packageManager)
                    if (description.isBlank() || description.isEmpty()) label
                    else description
                } catch (e: Exception) {
                    it
                }
                BottomSheet.Item(label.toString(), description.toString()) {
                    if (it.startsWith("android.permission")) {
                        val customTabsIntent = CustomTabsIntent.Builder()
                            .setInstantAppsEnabled(true)
                            .addDefaultShareMenuItem()
                            .setToolbarColor((getApplication() as Application).primaryColor)
                            .setShowTitle(true)
                            .setStartAnimations(this, R.anim.swipe_in, R.anim.swipe_out)
                            .setExitAnimations(this, R.anim.swipe_in, R.anim.swipe_out)
                            .build()
                        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent)
                        CustomTabsHelper.openCustomTab(
                            this, customTabsIntent,
                            Uri.parse(
                                "https://developer.android.com/reference/android/Manifest.permission#${it.replace(
                                    "android.permission.",
                                    ""
                                )}"
                            ),
                            WebViewFallback()
                        )
                    } else {
                        Toast.makeText(
                            this,
                            "This permission has not the 'android.permission' format!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } catch (e: Exception) {
            ArrayList()
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var iconView: ImageView? = null
        var adaptiveBg: Bitmap? = null
        var adaptiveFg: Bitmap? = null

        class BottomSheet private constructor(
            private val activity: Activity,
            private val title: String,
            private val items: List<Item>
        ) :
            BottomSheetDialogFragment() {
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                val v = inflater.inflate(R.layout.bottom_sheet, container, false)

                val adapter = Adapter(activity, items)
                val recyclerView: RecyclerView = v.findViewById(R.id.rv)
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = adapter

                v.findViewById<View>(R.id.layout).setBackgroundColor((activity.application as Application).primaryColor)

                return v
            }

            private class Adapter(private val activity: Activity, private val items: List<Item>) :
                RecyclerView.Adapter<Adapter.ViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    val v = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_item, parent, false)

                    return ViewHolder(v)
                }

                override fun getItemCount(): Int = items.size

                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    val item = items[position]

                    holder.title.text = item.text
                    holder.description.text = item.description
                    holder.layout.setOnClickListener {
                        item.onClick()
                    }
                }

                class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
                    val layout: ViewGroup = v.findViewById(R.id.layout)
                    val title: TextView = v.findViewById(R.id.title)
                    val description: TextView = v.findViewById(R.id.description)
                }
            }

            class Builder(private val activity: Activity) {
                var title = ""
                var items = ArrayList<Item>() as List<Item>

                fun build(): BottomSheet {
                    return BottomSheet(activity, title, items)
                }
            }

            class Item(val text: String, val description: String, val onClick: () -> Unit)

            fun showBottomSheet(manager: FragmentManager) {
                show(manager, "TAG")
            }
        }
    }
}
