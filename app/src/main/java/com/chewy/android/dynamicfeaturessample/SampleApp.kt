package com.chewy.android.dynamicfeaturessample

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.common.wrappers.InstantApps
import com.google.android.play.core.splitcompat.SplitCompat

class SampleApp : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.i("ChewyApp", "attachBaseContext")
        /* Apparently using SplitCompat.install on InstantApps could cause problems -> This is not documented
       anywhere. */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("ChewyApp", "Build.VERSION.SDK_INT >= Build.VERSION_CODES.O")
            if (!packageManager.isInstantApp) {
                Log.i("ChewyApp", "!packageManager.isInstantApp")
                SplitCompat.install(this)
            }
        } else if (!InstantApps.isInstantApp(this)) {
            Log.i("ChewyApp", "!InstantApps.isInstantApp(this)")
            SplitCompat.install(this)
        } else {
            Log.i("ChewyApp", "Nothing happens")
        }
    }
}