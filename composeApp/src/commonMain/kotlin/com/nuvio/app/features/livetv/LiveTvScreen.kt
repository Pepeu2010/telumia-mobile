package com.nuvio.app.features.livetv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.nuvio.app.core.ui.LocalNuvioBottomNavigationOverlayPadding
import com.nuvio.app.core.ui.NuvioInputField
import com.nuvio.app.core.ui.NuvioScreenHeader
import com.nuvio.app.core.ui.nuvio
import com.nuvio.app.features.addons.AddonRepository
import com.nuvio.app.features.catalog.fetchCatalogPage
import com.nuvio.app.features.home.MetaPreview
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.live_tv_all_channels
import nuvio.composeapp.generated.resources.live_tv_empty_message
import nuvio.composeapp.generated.resources.live_tv_empty_title
import nuvio.composeapp.generated.resources.live_tv_error
import nuvio.composeapp.generated.resources.live_tv_loading
import nuvio.composeapp.generated.resources.live_tv_no_results
import nuvio.composeapp.generated.resources.live_tv_search_placeholder
import nuvio.composeapp.generated.resources.live_tv_title
import org.jetbrains.compose.resources.stringResource

private const val LiveTvType = "tv"

private data class LiveTvCatalogSource(
    val manifestUrl: String,
    val catalogId: String,
    val title: String,
)

private data class LiveTvChannel(
    val key: String,
    val source: String,
    val meta: MetaPreview,
)

private sealed interface LiveTvState {
    data object Loading : LiveTvState
    data object Empty : LiveTvState
    data class Error(val partial: List<LiveTvChannel>) : LiveTvState
    data class Ready(val channels: List<LiveTvChannel>) : LiveTvState
}

/**
 * Live TV: lists the channels exposed by the installed addons (catalogs of type "tv").
 * Opening a channel uses the same details/stream flow as any other title.
 */
@Composable
fun LiveTvScreen(
    modifier: Modifier = Modifier,
    onChannelClick: ((MetaPreview) -> Unit)? = null,
) {
    val tokens = MaterialTheme.nuvio
    val addonsState by AddonRepository.uiState.collectAsState()
    val sources = remember(addonsState) {
        addonsState.addons
            .filter { it.isActive }
            .flatMap { addon ->
                val manifest = addon.manifest ?: return@flatMap emptyList()
                manifest.catalogs
                    .filter { it.type == LiveTvType && it.extra.none { extra -> extra.isRequired } }
                    .map { catalog ->
                        LiveTvCatalogSource(
                            manifestUrl = addon.manifestUrl,
                            catalogId = catalog.id,
                            title = catalog.name.ifBlank { addon.displayTitle },
                        )
                    }
            }
            .distinctBy { it.manifestUrl to it.catalogId }
    }

    var state by remember { mutableStateOf<LiveTvState>(LiveTvState.Loading) }
    LaunchedEffect(sources) {
        if (sources.isEmpty()) {
            state = LiveTvState.Empty
            return@LaunchedEffect
        }
        state = LiveTvState.Loading
        var failed = false
        val channels = mutableListOf<LiveTvChannel>()
        sources.forEach { source ->
            runCatching {
                fetchCatalogPage(manifestUrl = source.manifestUrl, type = LiveTvType, catalogId = source.catalogId)
            }.onSuccess { page ->
                page.items.forEach { meta ->
                    channels += LiveTvChannel("${source.manifestUrl}|${source.catalogId}|${meta.id}", source.title, meta)
                }
                state = LiveTvState.Ready(channels.toList())
            }.onFailure { failed = true }
        }
        state = when {
            channels.isEmpty() && failed -> LiveTvState.Error(emptyList())
            channels.isEmpty() -> LiveTvState.Empty
            failed -> LiveTvState.Error(channels.toList())
            else -> LiveTvState.Ready(channels.toList())
        }
    }

    var query by rememberSaveable { mutableStateOf("") }
    var selectedSource by rememberSaveable { mutableStateOf<String?>(null) }
    val bottomOverlayPadding = LocalNuvioBottomNavigationOverlayPadding.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.colors.background),
    ) {
        NuvioScreenHeader(
            title = stringResource(Res.string.live_tv_title),
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        val channels = when (val current = state) {
            is LiveTvState.Ready -> current.channels
            is LiveTvState.Error -> current.partial
            else -> emptyList()
        }

        when {
            state is LiveTvState.Loading && channels.isEmpty() -> MessageCard(
                title = stringResource(Res.string.live_tv_loading),
                message = null,
            )
            state is LiveTvState.Empty -> MessageCard(
                title = stringResource(Res.string.live_tv_empty_title),
                message = stringResource(Res.string.live_tv_empty_message),
            )
            state is LiveTvState.Error && channels.isEmpty() -> MessageCard(
                title = stringResource(Res.string.live_tv_error),
                message = null,
            )
            else -> {
                val sourceTitles = channels.map { it.source }.distinct()
                val filtered = channels.filter { channel ->
                    (selectedSource == null || channel.source == selectedSource) &&
                        (query.isBlank() || channel.meta.name.contains(query.trim(), ignoreCase = true))
                }
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    // Adapt the grid to the phone: ~160dp per channel card, between 2 and 6 columns.
                    val columns = (maxWidth.value / 160f).toInt().coerceIn(2, 6)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 40.dp + bottomOverlayPadding),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                NuvioInputField(
                                    value = query,
                                    onValueChange = { query = it },
                                    placeholder = stringResource(Res.string.live_tv_search_placeholder),
                                    shape = RoundedCornerShape(24.dp),
                                )
                                if (sourceTitles.size > 1) {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        item {
                                            CategoryChip(stringResource(Res.string.live_tv_all_channels), selectedSource == null) { selectedSource = null }
                                        }
                                        items(sourceTitles.size) { index ->
                                            val title = sourceTitles[index]
                                            CategoryChip(title, selectedSource == title) { selectedSource = title }
                                        }
                                    }
                                }
                                if (state is LiveTvState.Error) {
                                    Text(
                                        text = stringResource(Res.string.live_tv_error),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.colors.textMuted,
                                    )
                                }
                                if (filtered.isEmpty()) {
                                    Text(
                                        text = stringResource(Res.string.live_tv_no_results),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.colors.textMuted,
                                    )
                                }
                            }
                        }
                        items(filtered, key = { it.key }) { channel ->
                            ChannelCard(channel, onClick = onChannelClick?.let { click -> { click(channel.meta) } })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageCard(title: String, message: String?) {
    val tokens = MaterialTheme.nuvio
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = tokens.colors.surface,
        border = BorderStroke(1.dp, tokens.colors.borderSubtle),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = tokens.colors.textPrimary, fontWeight = FontWeight.Bold)
            if (message != null) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium, color = tokens.colors.textMuted)
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val tokens = MaterialTheme.nuvio
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) tokens.colors.textPrimary else tokens.colors.surface,
        contentColor = if (selected) tokens.colors.background else tokens.colors.textSecondary,
        border = BorderStroke(1.dp, if (selected) tokens.colors.textPrimary else tokens.colors.borderSubtle),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChannelCard(channel: LiveTvChannel, onClick: (() -> Unit)?) {
    val tokens = MaterialTheme.nuvio
    val logo = channel.meta.logo ?: channel.meta.poster ?: channel.meta.landscapePoster
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        color = tokens.colors.surface,
        border = BorderStroke(1.dp, tokens.colors.borderSubtle),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tokens.colors.surfaceElevated),
                contentAlignment = Alignment.Center,
            ) {
                if (logo != null) {
                    AsyncImage(
                        model = logo,
                        contentDescription = channel.meta.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        text = channel.meta.name.take(2).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.colors.textSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                text = channel.meta.name,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.colors.textPrimary,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            val subtitle = channel.meta.genres.firstOrNull() ?: channel.source
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(tokens.colors.accent),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
