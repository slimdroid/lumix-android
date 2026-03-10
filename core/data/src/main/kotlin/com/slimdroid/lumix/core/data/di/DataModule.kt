package com.slimdroid.lumix.core.data.di

import com.slimdroid.lumix.core.data.repository.DeviceRepository
import com.slimdroid.lumix.core.data.repository.DeviceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindsDeviceRepository(deviceRepositoryImpl: DeviceRepositoryImpl): DeviceRepository

}