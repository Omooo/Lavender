package com.omooo.plugin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.omooo.library.LibraryMain

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LibraryMain().show(this)

        assets.open("unused.json").close()
        show()
    }

    private fun show() {
        Toast.makeText(this, "2333", Toast.LENGTH_SHORT).show()
    }
}
