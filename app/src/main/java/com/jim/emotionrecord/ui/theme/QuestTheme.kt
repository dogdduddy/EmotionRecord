package com.jim.emotionrecord.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuestColorScheme = lightColorScheme(
    primary             = QPrimary,
    onPrimary           = Color.White,
    primaryContainer    = QPrimarySoft,
    onPrimaryContainer  = QPrimaryDeep,
    background          = QBg,
    onBackground        = QText1,
    surface             = QSurface,
    onSurface           = QText1,
    surfaceVariant      = QSurfaceSub,
    onSurfaceVariant    = QText2,
    outline             = QLine,
    outlineVariant      = QLineStrong,
    scrim               = QText1.copy(alpha = 0.42f),
)

@Composable
fun QuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = QuestColorScheme,
        typography  = QuestTypography,
        content     = content
    )
}
