package com.jim.emotionrecord.quest.ui.map

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.emotionrecord.quest.domain.model.DayStamp
import com.jim.emotionrecord.quest.domain.model.Emotion
import com.jim.emotionrecord.quest.domain.model.StampState
import com.jim.emotionrecord.quest.ui.common.EmotionFace
import com.jim.emotionrecord.ui.theme.QPrimary
import com.jim.emotionrecord.ui.theme.QPrimaryDeep
import com.jim.emotionrecord.ui.theme.QuestTheme
import com.jim.emotionrecord.ui.theme.qEmoColor
import com.jim.emotionrecord.ui.theme.qEmoInk
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 스탬프 4상태: COMPLETE / MISSION / SKIPPED / TODAY
 *
 * [animateLanding] true이면 미션/기록 완료 후 복귀 착지 애니메이션 1회 재생.
 * keyframe: scale 2.2→spring bounce→1.0 / alpha 0→1 (ref: stampLand 480ms)
 */
@Composable
fun EmotionStamp(
    stamp: DayStamp,
    size: Dp = 72.dp,
    tiltDegrees: Float = 0f,
    animateLanding: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val scaleAnim = remember { Animatable(1f) }
    val alphaAnim = remember { Animatable(1f) }
    val played    = remember { mutableStateOf(false) }

    LaunchedEffect(animateLanding) {
        if (animateLanding && !played.value) {
            played.value = true
            scaleAnim.snapTo(2.2f)
            alphaAnim.snapTo(0f)
            // 스케일: 2.2 → spring bounce → 1.0 (overshoot ~0.92)
            launch {
                scaleAnim.animateTo(
                    targetValue   = 1f,
                    animationSpec = spring(
                        dampingRatio = 0.42f,
                        stiffness    = Spring.StiffnessMediumLow,
                    )
                )
            }
            // 알파: 0 → 1 (빠르게)
            alphaAnim.animateTo(
                targetValue   = 1f,
                animationSpec = tween(durationMillis = 260, easing = FastOutLinearInEasing),
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = tiltDegrees
                scaleX    = scaleAnim.value
                scaleY    = scaleAnim.value
                alpha     = alphaAnim.value
            },
    ) {
        when (stamp.state) {
            StampState.COMPLETE -> CompleteStamp(stamp.emotion, size)
            StampState.MISSION  -> MissionStamp(stamp.emotion, size)
            StampState.SKIPPED  -> SkippedMark(size)
            StampState.TODAY    -> TodayMark(size)
            StampState.FUTURE   -> FutureMark(size)
            StampState.LOCKED   -> Unit
        }
    }
}

// ── COMPLETE ────────────────────────────────────────────────────────────────

@Composable
private fun CompleteStamp(emotion: Emotion?, size: Dp) {
    val level = emotion?.level ?: 3
    val discSize = size * 0.78f
    val emoColor = qEmoColor(level)
    val inkColor = qEmoInk(level)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(discSize)) {
        Canvas(modifier = Modifier.size(discSize)) {
            val r = this.size.width / 2f
            val c = Offset(r, r)
            // 감정색 원판
            drawCircle(emoColor, r, c)
            // 잉크 내측 보더 (도장 느낌)
            drawCircle(inkColor, r - 1.7.dp.toPx(), c, style = Stroke(width = 3.4.dp.toPx()))
            // 흰 내측 하이라이트
            drawCircle(Color.White.copy(alpha = 0.50f), r - 1.1.dp.toPx(), c, style = Stroke(width = 2.2.dp.toPx()))
        }
        EmotionFace(level = level, size = discSize * 0.74f, flat = true)
    }
}

// ── MISSION ─────────────────────────────────────────────────────────────────

@Composable
private fun MissionStamp(emotion: Emotion?, size: Dp) {
    val level = emotion?.level ?: 3
    val emoColor = qEmoColor(level)
    val inkColor = qEmoInk(level)
    val discSize = size * 0.78f

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        // 페탈 링 + 글로우 (Canvas)
        Canvas(modifier = Modifier.size(size)) {
            val cx = this.size.width / 2f
            val cy = this.size.height / 2f
            val discR = discSize.toPx() / 2f
            val ringR = discR + 5.dp.toPx()
            val petalCount = 10

            // soft glow halo
            drawCircle(emoColor.copy(alpha = 0.18f), ringR + 4.dp.toPx(), Offset(cx, cy))

            // radiating petals
            repeat(petalCount) { i ->
                val angle = (i.toFloat() / petalCount) * 2 * PI.toFloat() - (PI / 2).toFloat()
                val px = cx + cos(angle) * (ringR + 2.dp.toPx())
                val py = cy + sin(angle) * (ringR + 2.dp.toPx())
                val rotateDeg = angle * 180f / PI.toFloat() + 90f
                withTransform({ rotate(rotateDeg, Offset(px, py)) }) {
                    val pw = 4.4.dp.toPx()
                    val ph = 8.8.dp.toPx()
                    drawOval(
                        color = emoColor.copy(alpha = 0.82f),
                        topLeft = Offset(px - pw / 2f, py - ph / 2f),
                        size = Size(pw, ph),
                    )
                }
            }

            // 2 tiny sparkles
            sparkle(cx * 0.42f, cy * 0.50f, 4.dp.toPx(), emoColor)
            sparkle(cx * 1.58f, cy * 1.54f, 3.dp.toPx(), inkColor)
        }

        // Disc (reuse complete stamp drawing)
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(discSize)) {
            Canvas(modifier = Modifier.size(discSize)) {
                val r = this.size.width / 2f
                val c = Offset(r, r)
                drawCircle(emoColor, r, c)
                drawCircle(inkColor, r - 1.7.dp.toPx(), c, style = Stroke(width = 3.4.dp.toPx()))
                drawCircle(Color.White.copy(0.50f), r - 1.1.dp.toPx(), c, style = Stroke(width = 2.2.dp.toPx()))
            }
            EmotionFace(level = level, size = discSize * 0.74f, flat = true)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.sparkle(
    x: Float, y: Float, r: Float, color: Color
) {
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(x, y - r)
        lineTo(x + r * 0.3f, y - r * 0.3f)
        lineTo(x + r, y)
        lineTo(x + r * 0.3f, y + r * 0.3f)
        lineTo(x, y + r)
        lineTo(x - r * 0.3f, y + r * 0.3f)
        lineTo(x - r, y)
        lineTo(x - r * 0.3f, y - r * 0.3f)
        close()
    }
    drawPath(path, color)
}

// ── SKIPPED ─────────────────────────────────────────────────────────────────

@Composable
private fun SkippedMark(size: Dp) {
    val discSize = size * 0.78f
    val textMeasurer = rememberTextMeasurer()
    val grey = Color(0xFFB5A89B)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(discSize)) {
        Canvas(modifier = Modifier.size(discSize)) {
            val r = this.size.width / 2f
            val c = Offset(r, r)
            // 점선 테두리 (회색)
            drawCircle(
                color = grey.copy(alpha = 0.6f),
                radius = r - 1.dp.toPx(),
                center = c,
                style = Stroke(
                    width = 1.8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f
                    )
                ),
            )
            // "?" 텍스트 (회색)
            val layout = textMeasurer.measure(
                text  = "?",
                style = TextStyle(
                    fontSize   = (discSize.toPx() * 0.40f / density).sp,
                    fontWeight = FontWeight(700),
                    color      = grey.copy(alpha = 0.7f),
                )
            )
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(
                    c.x - layout.size.width / 2f,
                    c.y - layout.size.height / 2f,
                ),
            )
        }
    }
}

// ── FUTURE ──────────────────────────────────────────────────────────────────

@Composable
private fun FutureMark(size: Dp) {
    val discSize = size * 0.78f
    val grey = Color(0xFFB5A89B)

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(discSize)) {
        Canvas(modifier = Modifier.size(discSize)) {
            val r = this.size.width / 2f
            val c = Offset(r, r)
            // 점선 테두리만 (더 연한 회색)
            drawCircle(
                color = grey.copy(alpha = 0.35f),
                radius = r - 1.dp.toPx(),
                center = c,
                style = Stroke(
                    width = 1.6.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(4.dp.toPx(), 6.dp.toPx()), 0f
                    )
                ),
            )
        }
    }
}

// ── TODAY ───────────────────────────────────────────────────────────────────

@Composable
fun TodayMark(size: Dp) {
    val discSize = size * 0.78f
    val textMeasurer = rememberTextMeasurer()

    val infiniteTransition = rememberInfiniteTransition(label = "todayPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.12f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size),
    ) {
        // 펄스 글로우 halo
        Canvas(modifier = Modifier.size(size).scale(pulseScale)) {
            val haloR = discSize.toPx() / 2f + 7.dp.toPx()
            drawCircle(
                color = Color(0x2BD85A30),
                radius = haloR,
                center = Offset(this.size.width / 2f, this.size.height / 2f),
            )
        }

        // 점선 원 + "?"
        Canvas(modifier = Modifier.size(discSize)) {
            val r = this.size.width / 2f
            val c = Offset(r, r)
            // 반투명 흰 배경
            drawCircle(Color.White.copy(alpha = 0.70f), r, c)
            // 점선 테두리
            drawCircle(
                color = QPrimary,
                radius = r - 1.dp.toPx(),
                center = c,
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(4.dp.toPx(), 5.dp.toPx()), 0f
                    )
                ),
            )
            // "?" 텍스트
            val layout = textMeasurer.measure(
                text  = "?",
                style = TextStyle(
                    fontSize   = (discSize.toPx() * 0.44f / density).sp,
                    fontWeight = FontWeight(800),
                    color      = QPrimaryDeep,
                )
            )
            drawText(
                textLayoutResult = layout,
                topLeft = Offset(
                    c.x - layout.size.width / 2f,
                    c.y - layout.size.height / 2f,
                ),
            )
        }
    }
}

// ── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF7F1E6)
@Composable
private fun StampPreview() {
    QuestTheme {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            EmotionStamp(DayStamp(0, "월", StampState.COMPLETE, Emotion.GOOD))
            EmotionStamp(DayStamp(1, "화", StampState.MISSION, Emotion.VERY_GOOD))
            EmotionStamp(DayStamp(2, "수", StampState.SKIPPED))
            EmotionStamp(DayStamp(3, "목", StampState.TODAY, isToday = true))
        }
    }
}
