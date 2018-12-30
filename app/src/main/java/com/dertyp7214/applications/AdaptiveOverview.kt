/*
 * Copyright (c) 2018.
 * Created by Josua Lengwenath
 */

package com.dertyp7214.applications

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_adaptive_overview.*

class AdaptiveOverview : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adaptive_overview)

        fg.setImageBitmap(AppScreen.adaptiveFg)
        bg.setImageBitmap(AppScreen.adaptiveBg)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val animator = ValueAnimator.ofFloat(0F, 1F)
        animator.duration = 500
        animator.addUpdateListener {
            fg.background.alpha = (255 * it.animatedValue as Float).toInt()
            bg.background.alpha = (255 * it.animatedValue as Float).toInt()
        }
        animator.start()
    }

    override fun onBackPressed() {
        val anim = ValueAnimator.ofFloat(1F, 0F)
        anim.duration = 200
        anim.addUpdateListener {
            fg.background.alpha = (255 * it.animatedValue as Float).toInt()
            bg.background.alpha = (255 * it.animatedValue as Float).toInt()
        }
        anim.start()
        back()
    }

    private fun back() {
        super.onBackPressed()
        val animator = ValueAnimator.ofFloat(0F, 1F)
        animator.duration = 500
        animator.addUpdateListener {
            if ((it.animatedValue as Float) == 1F)
                AppScreen.iconView?.visibility = View.VISIBLE
        }
        animator.start()
    }
}
