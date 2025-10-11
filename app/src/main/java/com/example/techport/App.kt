package com.example.techport

import android.app.Application
import com.google.android.libraries.places.api.Places

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key))
        }
    }
}
