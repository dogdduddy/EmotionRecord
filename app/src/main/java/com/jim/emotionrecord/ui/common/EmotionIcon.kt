package com.jim.emotionrecord.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.ui.theme.emotionColor

@Composable
fun EmotionIcon(
    emotion: Emotion,
    size: Dp = 48.dp,
    fontSize: TextUnit = 28.sp,
    showBackground: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bgColor = if (showBackground) emotionColor(emotion.score).copy(alpha = 0.15f) else Color.Transparent

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor)
    ) {
        Text(
            text = emotion.emoji,
            fontSize = fontSize,
            textAlign = TextAlign.Center
        )
    }
}
