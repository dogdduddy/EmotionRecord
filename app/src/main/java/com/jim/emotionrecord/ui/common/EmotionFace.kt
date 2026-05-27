package com.jim.emotionrecord.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jim.emotionrecord.ui.theme.emotionColor
import com.jim.emotionrecord.ui.theme.emotionInk
import com.jim.emotionrecord.ui.theme.emotionTint

/**
 * Canvas로 직접 그리는 감정 얼굴 (5단계).
 * [score] 1~5, [size] DP 크기, [accent] L5에서 볼터치 표시
 */
@Composable
fun EmotionFace(
    score: Int,
    size: Dp = 48.dp,
    accent: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val canvasW = this.size.width
        val canvasH = this.size.height
        // 100x100 viewBox 기준으로 그리기 위한 스케일
        val sx = canvasW / 100f
        val sy = canvasH / 100f

        val fillColor = emotionColor(score)
        val inkColor  = emotionInk(score)
        val borderColor = Color.Black.copy(alpha = 0.06f)

        // face circle
        drawCircle(color = fillColor, radius = 44f * sx, center = Offset(50f * sx, 50f * sy))
        drawCircle(
            color = borderColor,
            radius = 44f * sx,
            center = Offset(50f * sx, 50f * sy),
            style = Stroke(width = 1f * sx)
        )

        drawExpression(score, sx, sy, inkColor, accent)
    }
}

private fun DrawScope.drawExpression(
    score: Int,
    sx: Float,
    sy: Float,
    ink: Color,
    accent: Boolean
) {
    val sw = Stroke(width = 3.4f * sx, cap = StrokeCap.Round)

    when (score) {
        1 -> {
            // sad slanted eyes: M30 41 L41 47 and M70 41 L59 47
            drawLine(ink, Offset(30f * sx, 41f * sy), Offset(41f * sx, 47f * sy), strokeWidth = 3.4f * sx, cap = StrokeCap.Round)
            drawLine(ink, Offset(70f * sx, 41f * sy), Offset(59f * sx, 47f * sy), strokeWidth = 3.4f * sx, cap = StrokeCap.Round)
            // tear: M37 53 q1 6 4 9  + circle at 40.5 64
            val tearPath = Path().apply {
                moveTo(37f * sx, 53f * sy)
                quadraticTo(38f * sx, 59f * sy, 41f * sx, 62f * sy)
            }
            drawPath(tearPath, ink, style = Stroke(width = 2.6f * sx, cap = StrokeCap.Round))
            drawCircle(ink, radius = 2.6f * sx, center = Offset(40.5f * sx, 64f * sy))
            // frown: M36 74 q14 -10 28 0
            val frownPath = Path().apply {
                moveTo(36f * sx, 74f * sy)
                quadraticTo(50f * sx, 64f * sy, 64f * sx, 74f * sy)
            }
            drawPath(frownPath, ink, style = sw)
        }
        2 -> {
            // half-closed sad eyes: M30 47 q5 -5 12 0 and M58 47 q5 -5 12 0
            val eye1 = Path().apply {
                moveTo(30f * sx, 47f * sy)
                quadraticTo(35f * sx, 42f * sy, 42f * sx, 47f * sy)
            }
            drawPath(eye1, ink, style = sw)
            val eye2 = Path().apply {
                moveTo(58f * sx, 47f * sy)
                quadraticTo(63f * sx, 42f * sy, 70f * sx, 47f * sy)
            }
            drawPath(eye2, ink, style = sw)
            // small frown: M40 71 q10 -5 20 0
            val frown = Path().apply {
                moveTo(40f * sx, 71f * sy)
                quadraticTo(50f * sx, 66f * sy, 60f * sx, 71f * sy)
            }
            drawPath(frown, ink, style = sw)
        }
        3 -> {
            // dot eyes
            drawCircle(ink, radius = 3.4f * sx, center = Offset(36f * sx, 46f * sy))
            drawCircle(ink, radius = 3.4f * sx, center = Offset(64.5f * sx, 46f * sy))
            // flat mouth: M38 68 L62 68
            drawLine(ink, Offset(38f * sx, 68f * sy), Offset(62f * sx, 68f * sy), strokeWidth = 3.4f * sx, cap = StrokeCap.Round)
        }
        4 -> {
            // round dot eyes
            drawCircle(ink, radius = 3.6f * sx, center = Offset(36f * sx, 44f * sy))
            drawCircle(ink, radius = 3.6f * sx, center = Offset(64.5f * sx, 44f * sy))
            // gentle smile: M34 62 q16 14 32 0
            val smile = Path().apply {
                moveTo(34f * sx, 62f * sy)
                quadraticTo(50f * sx, 76f * sy, 66f * sx, 62f * sy)
            }
            drawPath(smile, ink, style = Stroke(width = 3.6f * sx, cap = StrokeCap.Round))
        }
        5 -> {
            // squinted happy eyes (crescents): M28 48 q8 -10 16 0 / M56 48 q8 -10 16 0
            val eye1 = Path().apply {
                moveTo(28f * sx, 48f * sy)
                quadraticTo(36f * sx, 38f * sy, 44f * sx, 48f * sy)
            }
            drawPath(eye1, ink, style = Stroke(width = 3.6f * sx, cap = StrokeCap.Round))
            val eye2 = Path().apply {
                moveTo(56f * sx, 48f * sy)
                quadraticTo(64f * sx, 38f * sy, 72f * sx, 48f * sy)
            }
            drawPath(eye2, ink, style = Stroke(width = 3.6f * sx, cap = StrokeCap.Round))
            // big smile: M30 62 q20 22 40 0
            val smile = Path().apply {
                moveTo(30f * sx, 62f * sy)
                quadraticTo(50f * sx, 84f * sy, 70f * sx, 62f * sy)
            }
            drawPath(smile, ink, style = Stroke(width = 3.6f * sx, cap = StrokeCap.Round))
            // optional blush
            if (accent) {
                drawOval(
                    color = Color(0x59FF5A50),  // rgba(255,90,80,0.35)
                    topLeft = Offset(20f * sx, 61f * sy),
                    size = Size(10f * sx, 6f * sy)
                )
                drawOval(
                    color = Color(0x59FF5A50),
                    topLeft = Offset(70f * sx, 61f * sy),
                    size = Size(10f * sx, 6f * sy)
                )
            }
        }
    }
}

/**
 * 감정 tint 배경 원형 컨테이너 + 그 안에 78% 크기의 EmotionFace
 */
@Composable
fun FaceChip(
    score: Int,
    size: Dp = 44.dp,
    accent: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(emotionTint(score))
            .border(1.dp, Color.Black.copy(alpha = 0.04f), CircleShape)
    ) {
        EmotionFace(
            score = score,
            size  = size * 0.78f,
            accent = accent
        )
    }
}
