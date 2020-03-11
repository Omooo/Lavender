package com.ehi.plugin

import android.app.Application
import android.graphics.Color
import android.view.Gravity
import com.ehi.plugin.fps_detector.FPSDetector

/**
 * @author Omooo
 * @version v1.0
 * @Date 2020/03/11 16:46
 * desc :
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FPSDetector.prepare(this)
            .alpha(0.5f)
            .color(Color.WHITE)
            .gravity(Gravity.TOP or Gravity.END)
            .interval(250)
            .size(12f)
    }
}