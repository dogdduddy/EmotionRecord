package com.jim.emotionrecord.quest.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.emotionrecord.ui.theme.QLineStrong
import com.jim.emotionrecord.ui.theme.QSurfaceSub
import com.jim.emotionrecord.ui.theme.QText2
import com.jim.emotionrecord.ui.theme.QText3
import com.jim.emotionrecord.ui.theme.QuestTheme

private val ButtonShape = RoundedCornerShape(18.dp)

private val PrimaryGradient = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFFE66B40),
        0.60f to Color(0xFFD85A30),
        1.00f to Color(0xFFB84A24),
    )
)
private val DisabledGradient = Brush.verticalGradient(
    colors = listOf(QSurfaceSub, QSurfaceSub)
)

/**
 * 코랄 그라데이션 주요 CTA — height 56dp, radius 18dp
 */
@Composable
fun QuestPrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(ButtonShape)
            .background(if (enabled) PrimaryGradient else DisabledGradient)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                enabled = enabled,
                onClick = onClick,
            ),
    ) {
        Text(
            text = text,
            color = if (enabled) Color.White else QText3,
            fontSize = 16.sp,
            fontWeight = FontWeight(700),
            letterSpacing = (-0.16).sp,
        )
    }
}

/**
 * 외곽선 보조 버튼 — height 56dp, radius 18dp
 */
@Composable
fun QuestSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(ButtonShape)
            .border(1.5.dp, QLineStrong, ButtonShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = QText2.copy(alpha = 0.12f)),
                onClick = onClick,
            ),
    ) {
        Text(
            text = text,
            color = QText2,
            fontSize = 15.sp,
            fontWeight = FontWeight(600),
            letterSpacing = (-0.15).sp,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF7F2)
@Composable
private fun QuestButtonPreview() {
    QuestTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(20.dp),
        ) {
            QuestPrimaryButton(text = "기록하기", onClick = {})
            QuestPrimaryButton(text = "비활성 버튼", onClick = {}, enabled = false)
            QuestSecondaryButton(text = "다음에 하기", onClick = {})
        }
    }
}
