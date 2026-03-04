package com.slimdroid.lumix

import android.app.Application
import com.google.android.material.color.DynamicColors
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Napier.base(DebugAntilog())
        instance = this
    }

}