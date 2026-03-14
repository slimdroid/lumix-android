package com.slimdroid.lumix.core.bluetooth.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.slimdroid.lumix.core.bluetooth.connector.BleControlManager
import com.slimdroid.lumix.core.bluetooth.connector.BleControlManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BluetoothModule {

    @Binds
    @Singleton
    abstract fun bindsBleControlManager(bleControlManagerImpl: BleControlManagerImpl): BleControlManager

    companion object {
        @Provides
        @Singleton
        fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter? =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

}