package com.jim.emotionrecord.quest.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jim.emotionrecord.ui.theme.QuestTheme
import com.jim.emotionrecord.ui.theme.qEmoColor
import com.jim.emotionrecord.ui.theme.qEmoInk
import com.jim.emotionrecord.ui.theme.qEmoTint

/**
 * Quest 감정 얼굴 — quest-faces.jsx QFace 기준으로 Compose Canvas로 재구현.
 * [level] 1..5
 * [flat] true이면 배경 투명 + inkColor 테두리 (스탬프 disc 위에 올려쓸 때)
 */
@Composable
fun EmotionFace(
    level: Int,
    size: Dp = 80.dp,
    flat: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val fillColor = qEmoColor(level)
    val inkColor  = qEmoInk(level)

    Canvas(modifier = modifier.size(size)) {
        val sx = this.size.width / 100f
        val center = Offset(50f * sx, 50f * sx)
        val radius = 44f * sx

        if (flat) {
            drawCircle(
                color  = inkColor,
                radius = radius,
                center = center,
                style  = Stroke(width = 2.6f * sx),
            )
        } else {
            drawCircle(color = fillColor, radius = radius, center = center)
            drawCircle(
                color  = Color.Black.copy(alpha = 0.05f),
                radius = radius,
                center = center,
                style  = Stroke(width = 1f * sx),
            )
        }

        drawQuestExpression(level, sx, inkColor)
    }
}

private fun DrawScope.drawQuestExpression(level: Int, sx: Float, ink: Color) {
    val sw34 = Stroke(width = 3.4f * sx, cap = StrokeCap.Round)
    val sw36 = Stroke(width = 3.6f * sx, cap = StrokeCap.Round)

    when (level) {
        1 -> {
            // down-slanted eyes (calmer, no tear)
            // M30 44 L42 50  /  M70 44 L58 50
            drawLine(ink, Offset(30f*sx,44f*sx), Offset(42f*sx,50f*sx), strokeWidth=3.4f*sx, cap=StrokeCap.Round)
            drawLine(ink, Offset(70f*sx,44f*sx), Offset(58f*sx,50f*sx), strokeWidth=3.4f*sx, cap=StrokeCap.Round)
            // flat-down mouth: M38 72 q12 -6 24 0 → ctrl(50,66) end(62,72)
            drawPath(Path().apply {
                moveTo(38f*sx, 72f*sx); quadraticTo(50f*sx, 66f*sx, 62f*sx, 72f*sx)
            }, ink, style = sw34)
        }
        2 -> {
            // gentle curved-down eyes: M30 46 q5 -5 12 0 → ctrl(35,41) end(42,46)
            drawPath(Path().apply { moveTo(30f*sx,46f*sx); quadraticTo(35f*sx,41f*sx,42f*sx,46f*sx) }, ink, style=sw34)
            drawPath(Path().apply { moveTo(58f*sx,46f*sx); quadraticTo(63f*sx,41f*sx,70f*sx,46f*sx) }, ink, style=sw34)
            // slight frown: M38 70 q12 -4 24 0 → ctrl(50,66) end(62,70)
            drawPath(Path().apply { moveTo(38f*sx,70f*sx); quadraticTo(50f*sx,66f*sx,62f*sx,70f*sx) }, ink, style=sw34)
        }
        3 -> {
            // dot eyes  cx=36,cy=46  /  cx=64.5,cy=46
            drawCircle(ink, radius=3.4f*sx, center=Offset(36f*sx,46f*sx))
            drawCircle(ink, radius=3.4f*sx, center=Offset(64.5f*sx,46f*sx))
            // flat mouth: M38 68 L62 68
            drawLine(ink, Offset(38f*sx,68f*sx), Offset(62f*sx,68f*sx), strokeWidth=3.4f*sx, cap=StrokeCap.Round)
        }
        4 -> {
            // round dot eyes  cx=36,cy=44  /  cx=64.5,cy=44
            drawCircle(ink, radius=3.6f*sx, center=Offset(36f*sx,44f*sx))
            drawCircle(ink, radius=3.6f*sx, center=Offset(64.5f*sx,44f*sx))
            // gentle smile: M34 62 q16 14 32 0 → ctrl(50,76) end(66,62)
            drawPath(Path().apply { moveTo(34f*sx,62f*sx); quadraticTo(50f*sx,76f*sx,66f*sx,62f*sx) }, ink, style=sw36)
        }
        5 -> {
            // crescent happy eyes: M28 48 q8 -10 16 0 → ctrl(36,38) end(44,48)
            drawPath(Path().apply { moveTo(28f*sx,48f*sx); quadraticTo(36f*sx,38f*sx,44f*sx,48f*sx) }, ink, style=sw36)
            drawPath(Path().apply { moveTo(56f*sx,48f*sx); quadraticTo(64f*sx,38f*sx,72f*sx,48f*sx) }, ink, style=sw36)
            // big smile: M30 62 q20 22 40 0 → ctrl(50,84) end(70,62)
            drawPath(Path().apply { moveTo(30f*sx,62f*sx); quadraticTo(50f*sx,84f*sx,70f*sx,62f*sx) }, ink, style=sw36)
        }
    }
}

/**
 * Tint 원형 배경 + flat 얼굴 — 목록 행 / 스탬프 위 칩에 사용
 */
@Composable
fun EmotionFaceChip(
    level: Int,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(qEmoTint(level)),
    ) {
        EmotionFace(level = level, size = size * 0.78f, flat = true)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF7F2)
@Composable
private fun EmotionFacePreview() {
    QuestTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            for (level in 1..5) {
                EmotionFace(level = level, size = 64.dp)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBF7F2)
@Composable
private fun EmotionFaceFlatPreview() {
    QuestTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            for (level in 1..5) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(qEmoColor(level)),
                ) {
                    EmotionFace(level = level, size = 50.dp, flat = true)
                }
            }
        }
    }
}
