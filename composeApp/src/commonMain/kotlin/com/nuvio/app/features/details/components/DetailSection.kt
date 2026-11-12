package com.nuvio.app.features.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import com.nuvio.app.core.ui.nuvio
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailSection(
    title: String,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        if (showHeader) {
            DetailSectionTitle(title = title)
        }
        content()
    }
}

@Composable
fun DetailSectionTitle(
    title: String,
    fullWidth: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val titleModifier = if (fullWidth) modifier.fillMaxWidth() else modifier
    BoxWithConstraints(modifier = titleModifier) {
        val titleSize = if (maxWidth >= 720.dp) 22.sp else 20.sp
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 20.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.nuvio.colors.accent,
                                MaterialTheme.nuvio.colors.accent.copy(alpha = 0.45f),
                            ),
                        ),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = titleSize,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}