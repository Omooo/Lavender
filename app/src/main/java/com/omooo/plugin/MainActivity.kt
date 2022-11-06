package com.omooo.plugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.omooo.library.LibraryMain

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LibraryMain().show(this)
    }
}
