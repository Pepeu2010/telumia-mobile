package com.nuvio.app.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.compose_auth_tagline
import nuvio.composeapp.generated.resources.telumia_symbol
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val OpeningNavy = Color(0xFF0B0F1A)
private val OpeningAmber = Color(0xFFF2A93B)
private val OpeningCoral = Color(0xFFEE6A55)
private val OpeningCream = Color(0xFFF6EBD6)
private val OpeningEase = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
private const val OpeningName = "TELUMIA"

/**
 * Telumia opening: a beam of amber light sweeps across the screen "projecting"
 * the symbol, the letters of the name rise one by one, then the tagline fades in.
 * After the sequence the light keeps breathing until the app is ready.
 */
@Composable
fun TelumiaOpening(modifier: Modifier = Modifier) {
    val reveal = remember { Animatable(0f) }
    val letters = remember { List(OpeningName.length) { Animatable(0f) } }
    val tagline = remember { Animatable(0f) }
    val breathing = rememberInfiniteTransition(label = "telumia_opening_light")
    val glow by breathing.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "telumia_opening_glow",
    )

    LaunchedEffect(Unit) {
        coroutineScope {
            launch { reveal.animateTo(1f, tween(durationMillis = 1050, easing = OpeningEase)) }
            letters.forEachIndexed { index, letter ->
                launch {
                    delay(700L + index * 65L)
                    letter.animateTo(1f, tween(durationMillis = 520, easing = OpeningEase))
                }
            }
            launch {
                delay(1500L)
                tagline.animateTo(1f, tween(durationMillis = 650))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OpeningNavy),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(360.dp)
                .graphicsLayer { alpha = 0.5f * glow * reveal.value }
                .background(
                    Brush.radialGradient(
                        colors = listOf(OpeningAmber.copy(alpha = 0.34f), Color.Transparent),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(width = 360.dp, height = 220.dp)
                .graphicsLayer {
                    alpha = 0.4f * glow * reveal.value
                    translationY = 150.dp.toPx()
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(OpeningCoral.copy(alpha = 0.22f), Color.Transparent),
                    ),
                ),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(Res.drawable.telumia_symbol),
                contentDescription = null,
                modifier = Modifier
                    .size(width = 120.dp, height = 95.dp)
                    .graphicsLayer {
                        val scale = 0.9f + 0.1f * reveal.value
                        scaleX = scale
                        scaleY = scale
                    }
                    .drawWithContent {
                        val edge = size.width * reveal.value
                        clipRect(right = edge) { this@drawWithContent.drawContent() }
                        val beamAlpha = (1f - reveal.value).coerceIn(0f, 1f)
                        if (beamAlpha > 0f) {
                            val beamWidth = 36.dp.toPx()
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, OpeningAmber.copy(alpha = 0.9f * beamAlpha)),
                                    startX = edge - beamWidth,
                                    endX = edge,
                                ),
                                topLeft = Offset(edge - beamWidth, -12.dp.toPx()),
                                size = Size(beamWidth, size.height + 24.dp.toPx()),
                            )
                        }
                    },
                contentScale = ContentScale.Fit,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                OpeningName.forEachIndexed { index, char ->
                    val progress = letters[index].value
                    Text(
                        text = char.toString(),
                        modifier = Modifier.graphicsLayer {
                            alpha = progress
                            translationY = (1f - progress) * 18.dp.toPx()
                        },
                        color = OpeningCream,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 7.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(Res.string.compose_auth_tagline),
                modifier = Modifier
                    .width(300.dp)
                    .graphicsLayer {
                        alpha = tagline.value
                        translationY = (1f - tagline.value) * 8.dp.toPx()
                    },
                color = OpeningCream.copy(alpha = 0.72f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
