package com.slimdroid.lumix.core.scanner

import kotlinx.coroutines.flow.Flow

interface Scanner {

    fun startScan(): Flow<Device>

    fun stopScan()

}