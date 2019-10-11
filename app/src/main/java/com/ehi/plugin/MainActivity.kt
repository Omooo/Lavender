package com.ehi.plugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ehi.annotation.MethodTrace

class MainActivity : AppCompatActivity() {

    @MethodTrace
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
