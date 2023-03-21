package com.example.roombindingstatexml

import android.app.Application
import android.os.StrictMode

class AppContext : Application() {

    override fun onCreate() {
        super.onCreate()

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .setClassInstanceLimit(ContactDatabase::class.java, 1)
                .build()
        )
    }
}