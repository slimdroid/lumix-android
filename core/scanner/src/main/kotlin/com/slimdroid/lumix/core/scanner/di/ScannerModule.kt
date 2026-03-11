package com.slimdroid.lumix.core.scanner.di

import com.slimdroid.lumix.core.scanner.DeviceScanner
import com.slimdroid.lumix.core.scanner.DeviceScannerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ScannerModule {

    @Binds
    fun bindsDeviceScanner(deviceRepositoryImpl: DeviceScannerImpl): DeviceScanner

}