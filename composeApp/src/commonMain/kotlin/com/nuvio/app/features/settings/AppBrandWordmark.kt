package com.nuvio.app.features.settings

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import nuvio.composeapp.generated.resources.Res
import nuvio.composeapp.generated.resources.telumia_wordmark
import org.jetbrains.compose.resources.painterResource

/** Telumia brand lockup (symbol + name). The icon/theme parameters are kept for call-site compatibility. */
@Composable
internal fun AppBrandWordmark(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    icon: AppIconOption? = null,
) {
    Image(
        painter = painterResource(Res.drawable.telumia_wordmark),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
