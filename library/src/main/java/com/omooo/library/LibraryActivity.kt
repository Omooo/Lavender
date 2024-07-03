package com.omooo.library

import android.app.Activity
import android.os.Bundle
import com.omooo.plugin.library.R

/**
 * Author: Omooo
 * Date: 2024/4/28
 * Desc:
 */
internal class LibraryActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }
}