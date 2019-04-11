package com.chewy.android.dynamicfeaturessample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            loadFragment()
        }
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, dynamicFeatureLoadingFragment())
            .commitNow()
    }
}
