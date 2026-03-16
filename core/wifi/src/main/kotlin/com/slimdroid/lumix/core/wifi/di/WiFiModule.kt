package com.slimdroid.lumix.core.wifi.di

import android.content.Context
import android.net.wifi.WifiManager
import com.slimdroid.lumix.core.wifi.WiFiScanner
import com.slimdroid.lumix.core.wifi.WiFiScannerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class WiFiModule {

    @Binds
    @Singleton
    abstract fun bindsWiFiScanner(wiFiScannerImpl: WiFiScannerImpl): WiFiScanner

    companion object {
        @Provides
        @Singleton
        fun provideWifiManager(@ApplicationContext context: Context): WifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
}