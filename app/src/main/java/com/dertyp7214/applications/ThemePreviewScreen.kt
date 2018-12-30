/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.applications

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_theme_preview_screen.*

class ThemePreviewScreen : AppCompatActivity() {

    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_preview_screen)

        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ThemePreview("Theme1", ThemePreview.Theme(Color.LTGRAY, Color.DKGRAY, Color.GRAY, Color.RED)))
        adapter.addFragment(ThemePreview("Theme2", ThemePreview.Theme(Color.LTGRAY, Color.DKGRAY, Color.GRAY, Color.GREEN)))
        adapter.addFragment(ThemePreview("Theme3", ThemePreview.Theme(Color.LTGRAY, Color.DKGRAY, Color.GRAY, Color.YELLOW)))

        viewPager.adapter = adapter
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add("")
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}
