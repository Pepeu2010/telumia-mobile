package com.nuvio.app.features.livetv

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nuvio.app.core.ui.LocalNuvioBottomNavigationOverlayPadding
import com.nuvio.app.core.ui.NuvioScreenHeader
import com.nuvio.app.core.ui.nuvio
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.live_tv_category_all
import nuvio.composeapp.generated.resources.live_tv_category_movies
import nuvio.composeapp.generated.resources.live_tv_category_news
import nuvio.composeapp.generated.resources.live_tv_category_sports
import nuvio.composeapp.generated.resources.live_tv_live_badge
import nuvio.composeapp.generated.resources.live_tv_next_format
import nuvio.composeapp.generated.resources.live_tv_preview_notice
import nuvio.composeapp.generated.resources.live_tv_title
import org.jetbrains.compose.resources.stringResource

private enum class LiveTvCategory { All, News, Sports, Movies }

private data class LiveTvChannel(
    val number: String,
    val name: String,
    val category: LiveTvCategory,
    val now: String,
    val next: String,
    val progress: Float,
)

/** Sample lineup for the interface preview; real channels need a connected source. */
private val SampleChannels = listOf(
    LiveTvChannel("01", "Canal Um", LiveTvCategory.News, "Jornal da manhã", "Previsão do tempo", 0.62f),
    LiveTvChannel("02", "Canal Dois", LiveTvCategory.Movies, "Cine Clássicos: Rota 9", "Mar Aberto", 0.25f),
    LiveTvChannel("03", "Canal Três", LiveTvCategory.Sports, "Futebol ao vivo", "Resumo da rodada", 0.80f),
    LiveTvChannel("04", "Canal Quatro", LiveTvCategory.Movies, "Documentário: Terra Rubra", "Noite Longa", 0.10f),
    LiveTvChannel("05", "Canal Cinco", LiveTvCategory.All, "Animação em família", "Lost", 0.45f),
)

@Composable
fun LiveTvScreen(modifier: Modifier = Modifier) {
    val tokens = MaterialTheme.nuvio
    var category by rememberSaveable { mutableStateOf(LiveTvCategory.All) }
    val channels = if (category == LiveTvCategory.All) {
        SampleChannels
    } else {
        SampleChannels.filter { it.category == category }
    }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 40.dp + bottomOverlayPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CategoryChip(stringResource(Res.string.live_tv_category_all), category == LiveTvCategory.All) { category = LiveTvCategory.All }
                        CategoryChip(stringResource(Res.string.live_tv_category_news), category == LiveTvCategory.News) { category = LiveTvCategory.News }
                        CategoryChip(stringResource(Res.string.live_tv_category_sports), category == LiveTvCategory.Sports) { category = LiveTvCategory.Sports }
                        CategoryChip(stringResource(Res.string.live_tv_category_movies), category == LiveTvCategory.Movies) { category = LiveTvCategory.Movies }
                    }
                    Text(
                        text = stringResource(Res.string.live_tv_preview_notice),
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.colors.textMuted,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            items(channels, key = { it.number }) { channel ->
                ChannelRow(channel)
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
        )
    }
}

@Composable
private fun ChannelRow(channel: LiveTvChannel) {
    val tokens = MaterialTheme.nuvio
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = tokens.colors.surface,
        border = BorderStroke(1.dp, tokens.colors.borderSubtle),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(androidx.compose.ui.graphics.Color(0xFF1C2438)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = channel.number,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = tokens.colors.textSecondary,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = channel.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.colors.textMuted,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CoralDot),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(Res.string.live_tv_live_badge),
                        color = CoralDot,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp,
                    )
                }
                Text(
                    text = channel.now,
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = tokens.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(tokens.colors.borderSubtle),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(channel.progress)
                            .fillMaxHeight()
                            .background(tokens.colors.accent),
                    )
                }
                Text(
                    text = stringResource(Res.string.live_tv_next_format, channel.next),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val CoralDot = androidx.compose.ui.graphics.Color(0xFFEE6A55)
