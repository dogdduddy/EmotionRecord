package com.jim.emotionrecord.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary          = Primary,
    onPrimary        = androidx.compose.ui.graphics.Color.White,
    primaryContainer = PrimarySoft,
    onPrimaryContainer = PrimaryDeep,
    secondary        = Emo4,
    onSecondary      = androidx.compose.ui.graphics.Color.White,
    background       = Bg,
    surface          = EmoSurface,
    surfaceVariant   = SurfaceSub,
    onBackground     = Text1,
    onSurface        = Text1,
    onSurfaceVariant = Text2,
    outline          = Line,
    outlineVariant   = LineStrong,
    scrim            = Text1.copy(alpha = 0.32f)
)

@Composable
fun EmotionRecordTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography,
        content     = content
    )
}
