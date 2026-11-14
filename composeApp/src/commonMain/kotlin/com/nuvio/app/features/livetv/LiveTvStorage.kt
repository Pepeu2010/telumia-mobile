package com.nuvio.app.features.livetv

internal expect object LiveTvStorage {
    fun loadPayload(): String?
    fun savePayload(payload: String)
}
