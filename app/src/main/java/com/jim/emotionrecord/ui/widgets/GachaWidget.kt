package com.jim.emotionrecord.ui.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.ui.common.EmotionFace
import com.jim.emotionrecord.ui.theme.Text2
import com.jim.emotionrecord.ui.theme.Text3
import com.jim.emotionrecord.ui.theme.emotionColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

private enum class GachaState { IDLE, PULLING, REVEALED }

@Composable
fun GachaWidget(
    selectedEmotion: Emotion?,
    onEmotionSelected: (Emotion) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope  = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var gState by remember { mutableStateOf(GachaState.IDLE) }
    var result by remember { mutableStateOf<Emotion?>(null) }

    val leverRot    = remember { Animatable(0f) }
    val capsuleDrop = remember { Animatable(0f) }

    fun pullLever() {
        if (gState != GachaState.IDLE) return
        scope.launch {
            gState = GachaState.PULLING
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            leverRot.animateTo(-65f, tween(600))
            delay(200)
            leverRot.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
            val picked = Emotion.entries[Random.nextInt(Emotion.entries.size)]
            result = picked
            onEmotionSelected(picked)
            capsuleDrop.snapTo(0f)
            capsuleDrop.animateTo(80f, tween(350))
            delay(150)
            gState = GachaState.REVEALED
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // 뽑기 기계 + 레버 + 결과 캡슐
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(width = 280.dp, height = 320.dp)
        ) {
            // 기계 본체
            GachaMachine(
                capsules       = Emotion.entries,
                capsuleVisible = gState == GachaState.IDLE
            )

            // 레버 (우측)
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 4.dp, y = 30.dp)
                    .size(width = 30.dp, height = 80.dp)
                    .rotate(leverRot.value)
            ) {
                // 레버 막대
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF8C5E1F))
                )
                // 레버 손잡이 (드래그 타깃)
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD97D))
                        .border(2.dp, Color(0xFF8C5E1F), CircleShape)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = { pullLever() }
                            ) { _, _ -> }
                        }
                )
            }

            // 낙하 캡슐 (PULLING or REVEALED)
            if (gState != GachaState.IDLE && result != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset { IntOffset(0, capsuleDrop.value.roundToInt()) }
                ) {
                    FallenCapsule(
                        emotion = result!!,
                        opened  = gState == GachaState.REVEALED
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // 안내 텍스트
        if (gState == GachaState.IDLE) {
            Text(
                text  = "레버를 당겨주세요",
                style = MaterialTheme.typography.bodyMedium,
                color = Text3,
                textAlign = TextAlign.Center
            )
        }

        // 다시 뽑기 버튼
        if (gState == GachaState.REVEALED && result != null) {
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn() + scaleIn(initialScale = 0.7f)
            ) {
                val emoColor = emotionColor(result!!.score)
                TextButton(onClick = {
                    gState = GachaState.IDLE
                    result = null
                    scope.launch { capsuleDrop.snapTo(0f) }
                }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = emoColor
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text      = "다시 뽑기",
                        style     = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight(600),
                        color     = emoColor
                    )
                }
            }
        }
    }
}

// ─── 뽑기 기계 본체 Canvas ─────────────────────────────────────────────────────
@Composable
private fun GachaMachine(
    capsules: List<Emotion>,
    capsuleVisible: Boolean
) {
    Canvas(modifier = Modifier.size(width = 240.dp, height = 300.dp)) {
        val w = size.width
        val h = size.height

        // 베이스
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFFE07856), Color(0xFFB95536)),
                startY = h * 0.55f, endY = h * 0.85f
            ),
            topLeft = Offset(w * 0.13f, h * 0.55f),
            size    = Size(w * 0.74f, h * 0.30f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
        )

        // 라벨 플레이트
        drawRoundRect(
            color   = Color(0xFFFBF5ED),
            topLeft = Offset(w * 0.28f, h * 0.615f),
            size    = Size(w * 0.44f, h * 0.10f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // 동전 슬롯
        drawRoundRect(
            color   = Color(0xFF3E2C23).copy(alpha = 0.4f),
            topLeft = Offset(w * 0.43f, h * 0.56f),
            size    = Size(w * 0.14f, h * 0.028f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
        )

        // 캡슐 슈트
        drawRoundRect(
            color   = Color(0x403E2C23),
            topLeft = Offset(w * 0.375f, h * 0.838f),
            size    = Size(w * 0.25f, h * 0.075f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // 돔 (glass)
        val domePath = Path().apply {
            moveTo(w * 0.16f, h * 0.56f)
            quadraticTo(w * 0.16f, h * 0.05f, w * 0.50f, h * 0.05f)
            quadraticTo(w * 0.84f, h * 0.05f, w * 0.84f, h * 0.56f)
            close()
        }
        drawPath(
            domePath,
            Brush.verticalGradient(
                listOf(Color(0xFFFFF5EB), Color(0xFFFFE3D4)),
                startY = 0f, endY = h * 0.56f
            )
        )
        drawPath(domePath, Color(0xFFE07856), style = Stroke(width = 1.5.dp.toPx()))

        // 돔 하이라이트
        drawCircle(
            brush  = Brush.radialGradient(
                listOf(Color.White.copy(alpha = 0.6f), Color.Transparent),
                center = Offset(w * 0.30f, h * 0.15f),
                radius = w * 0.15f
            ),
            radius = w * 0.15f,
            center = Offset(w * 0.30f, h * 0.15f)
        )

        // 스탠드 다리 좌
        drawRoundRect(
            color   = Color(0xFFB95536),
            topLeft = Offset(w * 0.18f, h * 0.845f),
            size    = Size(w * 0.16f, h * 0.12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )
        // 스탠드 다리 우
        drawRoundRect(
            color   = Color(0xFFB95536),
            topLeft = Offset(w * 0.66f, h * 0.845f),
            size    = Size(w * 0.16f, h * 0.12f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )
    }

    // 돔 안 미니 캡슐들
    if (capsuleVisible) {
        Box(modifier = Modifier.size(width = 240.dp, height = 300.dp)) {
            val positions = listOf(
                Pair(0.25f, 0.12f), Pair(0.45f, 0.08f), Pair(0.65f, 0.14f),
                Pair(0.30f, 0.25f), Pair(0.55f, 0.24f), Pair(0.70f, 0.33f),
                Pair(0.20f, 0.35f), Pair(0.50f, 0.40f)
            )
            capsules.forEachIndexed { i, emotion ->
                val pos = positions[i % positions.size]
                val rot = (i * 23 - 30).toFloat()
                MiniCapsule(
                    emotion  = emotion,
                    rotation = rot,
                    modifier = Modifier
                        .size(28.dp, 20.dp)
                        .offset(
                            x = (240.dp * pos.first - 14.dp),
                            y = (300.dp * pos.second - 10.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun MiniCapsule(
    emotion: Emotion,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.rotate(rotation)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color      = emotionColor(emotion.score),
                startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft    = Offset(0f, 0f),
                size       = Size(size.width, size.height)
            )
            drawArc(
                color      = Color.White,
                startAngle = 0f, sweepAngle = 180f, useCenter = false,
                topLeft    = Offset(0f, 0f),
                size       = Size(size.width, size.height)
            )
            drawOval(
                color   = Color.Black.copy(alpha = 0.1f),
                topLeft = Offset(0f, 0f),
                size    = Size(size.width, size.height),
                style   = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

// ─── 낙하된(열린) 캡슐 ──────────────────────────────────────────────────────
@Composable
private fun FallenCapsule(emotion: Emotion, opened: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(130.dp, 90.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (!opened) {
                // 닫힌 캡슐
                drawArc(
                    color      = emotionColor(emotion.score),
                    startAngle = 180f, sweepAngle = 180f, useCenter = false,
                    topLeft    = Offset(w * 0.15f, h * 0.1f),
                    size       = Size(w * 0.70f, h * 0.80f)
                )
                drawArc(
                    color      = Color.White,
                    startAngle = 0f, sweepAngle = 180f, useCenter = false,
                    topLeft    = Offset(w * 0.15f, h * 0.1f),
                    size       = Size(w * 0.70f, h * 0.80f)
                )
            } else {
                // 열린 캡슐 - 상단 반쪽 살짝 비틀림
                drawArc(
                    color      = emotionColor(emotion.score),
                    startAngle = 195f, sweepAngle = 180f, useCenter = false,
                    topLeft    = Offset(w * 0.10f, h * 0.0f),
                    size       = Size(w * 0.65f, h * 0.55f)
                )
                // 하단 반쪽
                drawArc(
                    color      = Color.White,
                    startAngle = 0f, sweepAngle = 180f, useCenter = false,
                    topLeft    = Offset(w * 0.15f, h * 0.40f),
                    size       = Size(w * 0.70f, h * 0.50f)
                )
            }
        }

        // 열린 후 얼굴 pop-up
        AnimatedVisibility(
            visible = opened,
            enter   = fadeIn(tween(200)) + scaleIn(initialScale = 0f, animationSpec = tween(300))
        ) {
            EmotionFace(
                score    = emotion.score,
                size     = 60.dp,
                accent   = emotion.score == 5,
                modifier = Modifier.offset(y = (-14).dp)
            )
        }
    }
}
