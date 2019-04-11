package com.chewy.android.dynamicfeaturessample.themeg

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.wrappers.InstantApps
import com.google.android.play.core.splitcompat.SplitCompat

class DynamicFeatureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_feature)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        /* Apparently using SplitCompat.install on InstantApps could cause problems -> This is not documented
        anywhere. */
        if (!InstantApps.isInstantApp(this)) {
            SplitCompat.install(this)
        }
    }
}
