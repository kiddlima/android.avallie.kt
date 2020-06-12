package com.avallie

import android.app.Application
import io.paperdb.Paper
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import com.google.firebase.iid.InstanceIdResult
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId



class InitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Paper.init(this)

        CalligraphyConfig.initDefault(
            CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )


    }
}