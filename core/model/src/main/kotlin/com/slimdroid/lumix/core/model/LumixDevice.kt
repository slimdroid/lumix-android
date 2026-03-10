package com.slimdroid.lumix.core.model

data class LumixDevice(
    val id: String,
    val ip: String,
    val name: String,
    val type: String,
    val firmwareVersion: String,
    val online: Boolean = false
)