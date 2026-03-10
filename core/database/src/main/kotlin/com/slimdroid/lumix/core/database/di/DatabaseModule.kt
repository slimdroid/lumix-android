package com.slimdroid.lumix.core.database.di

import android.content.Context
import com.slimdroid.lumix.core.database.AppDatabase
import com.slimdroid.lumix.core.database.source.DeviceLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDeviceDataSource(@ApplicationContext context: Context) =
        DeviceLocalDataSource(
            deviceDao = provideAppDatabase(context).deviceDao(),
            ioDispatcher = Dispatchers.IO
        )

    private fun provideAppDatabase(context: Context) = AppDatabase.getInstance(context)

}