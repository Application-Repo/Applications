/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.dertyp7214.applications

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val activity: Activity, private val applications: List<ApplicationInfo>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    companion object {
        val imageMap = HashMap<String, Drawable>()
        var locked = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val v = LayoutInflater.from(activity).inflate(R.layout.application, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int = applications.size

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        val applicationInfo = applications[position]

        val image = imageMap[applicationInfo.packageName] ?: applicationInfo.loadIcon(activity.packageManager)
        imageMap[applicationInfo.packageName] = image

        holder.icon.setImageDrawable(image)
        holder.title.text = applicationInfo.loadLabel(activity.packageManager)
        holder.packageName.text = applicationInfo.packageName

        holder.title.setTextColor((activity.application as Application).textColor)
        holder.packageName.setTextColor((activity.application as Application).textColor)

        holder.layout.setOnClickListener {
            if (!locked) {
                locked = true
                val intent = Intent(activity, AppScreen::class.java)
                val icon = Pair.create<View, String>(holder.icon, "icon")
                val options = ActivityOptions.makeSceneTransitionAnimation(activity, icon)
                intent.putExtra("packageName", applicationInfo.packageName)
                activity.startActivity(intent, options.toBundle())
            }
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var layout: ViewGroup = v.findViewById(R.id.layout)
        val icon: ImageView = v.findViewById(R.id.icon)
        val title: TextView = v.findViewById(R.id.title)
        val packageName: TextView = v.findViewById(R.id.packageName)
    }
}