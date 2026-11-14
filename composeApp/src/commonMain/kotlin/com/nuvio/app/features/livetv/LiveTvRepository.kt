package com.nuvio.app.features.livetv

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class LiveTvPersisted(
    val favorites: List<String> = emptyList(),
    val recents: List<String> = emptyList(),
)

/** Favorite and recently watched channels, stored per profile. Channels are identified by their meta id. */
object LiveTvRepository {
    private const val MAX_RECENTS = 12

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val _favorites = MutableStateFlow<List<String>>(emptyList())
    val favorites: StateFlow<List<String>> = _favorites.asStateFlow()

    private val _recents = MutableStateFlow<List<String>>(emptyList())
    val recents: StateFlow<List<String>> = _recents.asStateFlow()

    private var hasLoaded = false

    fun ensureLoaded() {
        if (!hasLoaded) loadFromDisk()
    }

    fun onProfileChanged() = loadFromDisk()

    fun toggleFavorite(channelId: String) {
        ensureLoaded()
        _favorites.value = if (channelId in _favorites.value) {
            _favorites.value - channelId
        } else {
            _favorites.value + channelId
        }
        persist()
    }

    fun recordWatched(channelId: String) {
        ensureLoaded()
        _recents.value = (listOf(channelId) + _recents.value.filterNot { it == channelId }).take(MAX_RECENTS)
        persist()
    }

    private fun loadFromDisk() {
        hasLoaded = true
        val payload = LiveTvStorage.loadPayload().orEmpty().trim()
        val data = if (payload.isEmpty()) {
            LiveTvPersisted()
        } else {
            runCatching { json.decodeFromString<LiveTvPersisted>(payload) }.getOrDefault(LiveTvPersisted())
        }
        _favorites.value = data.favorites
        _recents.value = data.recents
    }

    private fun persist() {
        LiveTvStorage.savePayload(json.encodeToString(LiveTvPersisted.serializer(), LiveTvPersisted(_favorites.value, _recents.value)))
    }
}
