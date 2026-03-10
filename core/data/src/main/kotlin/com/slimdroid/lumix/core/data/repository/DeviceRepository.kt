package com.slimdroid.lumix.core.data.repository

import com.slimdroid.lumix.core.database.entity.toExternalModel
import com.slimdroid.lumix.core.database.source.DeviceLocalDataSource
import com.slimdroid.lumix.core.model.LumixDevice
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DeviceRepository {

    fun getDevices(): Flow<List<LumixDevice>>
}

@Module
@InstallIn(SingletonComponent::class)
class DeviceRepositoryImpl @Inject constructor(
    private val localDataSource: DeviceLocalDataSource
) : DeviceRepository {

    override fun getDevices(): Flow<List<LumixDevice>> = localDataSource.getDevices()
        .map { deviceList ->
            deviceList.map { device -> device.toExternalModel() }
        }
}