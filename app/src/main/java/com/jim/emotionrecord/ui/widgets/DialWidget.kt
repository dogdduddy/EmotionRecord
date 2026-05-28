package com.jim.emotionrecord.ui.widgets

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.ui.common.EmotionFace
import com.jim.emotionrecord.ui.theme.PrimaryDeep
import com.jim.emotionrecord.ui.theme.Text3
import com.jim.emotionrecord.ui.theme.emotionColor
import com.jim.emotionrecord.ui.theme.emotionInk
import com.jim.emotionrecord.ui.theme.emotionTint
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private const val FACE_COUNT = 5
private const val STEP_DEG   = 360f / FACE_COUNT   // 72°

// ── 진동 헬퍼 — 시스템 터치 피드백 설정과 무관하게 독립 진동 ──────────────────
// API 29+: OS가 기기별로 최적화된 predefined 패턴 사용
// API 26~28: createOneShot(ms, amplitude) fallback
@Suppress("DEPRECATION")
private fun Vibrator.tick() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    } else {
        vibrate(VibrationEffect.createOneShot(8L, 80))
    }
}

@Suppress("DEPRECATION")
private fun Vibrator.confirm() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
    } else {
        vibrate(VibrationEffect.createOneShot(25L, 200))
    }
}

@Composable
fun DialWidget(
    selectedEmotion: Emotion?,
    onEmotionSelected: (Emotion) -> Unit,
    modifier: Modifier = Modifier
) {
    val emotions = Emotion.entries
    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()
    val density  = LocalDensity.current

    // VibratorManager (API 31+) 또는 레거시 Vibrator 획득
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    val rotationAnim = remember { Animatable(144f) }
    var lastAngle    by remember { mutableFloatStateOf(0f) }
    var isDragging   by remember { mutableStateOf(false) }
    var prevSnapped  by remember { mutableStateOf<Emotion?>(null) }

    // ── 관성 + 조기 스냅 튜닝 파라미터 ──────────────────────────────────────
    // absVelocityThreshold: 이 각속도(deg/sec) 아래로 떨어지면 decay를 멈추고
    //                        즉시 자석 스냅을 시작합니다.
    //   값을 올릴수록 → 더 빨리 스냅 (휠이 아직 움직이는 도중에 붙음)
    //   값을 내릴수록 → 더 천천히 스냅 (거의 멈출 때까지 자유 회전)
    //   권장 범위: 80f (느슨) ~ 400f (즉각)
    val SNAP_VELOCITY_THRESHOLD = 100f   // ← 여기서 조기 스냅 타이밍 조절

    val velocityTracker = remember { VelocityTracker() }
    val decay = remember {
        exponentialDecay<Float>(
            frictionMultiplier    = 0.4f,
            absVelocityThreshold  = SNAP_VELOCITY_THRESHOLD
        )
    }

    fun computeEmotion(angle: Float): Emotion {
        val norm = (((-angle % 360f) + 360f) % 360f)
        val idx  = ((norm + STEP_DEG / 2f) / STEP_DEG).toInt() % FACE_COUNT
        return emotions[idx]
    }

    val delayedRotation by animateFloatAsState(
        targetValue = rotationAnim.value,
        animationSpec = spring(
            dampingRatio = 0.3f, // 낮을수록 멈출 때 많이 출렁거림
            stiffness = 60f      // 낮을수록 지연이 커져서 돌릴 때 크게 기울어짐
        ),
        label = "delayed_rotation"
    )

    val tiltAngle = rotationAnim.value - delayedRotation

    // face 변화 감지 → 회전 중에는 라쳇 tick, 정지 후 안착 시 묵직한 confirmation
    LaunchedEffect(rotationAnim.value) {
        val current = computeEmotion(rotationAnim.value)
        if (current != prevSnapped) {
            prevSnapped = current
            onEmotionSelected(current)
            if (isDragging || rotationAnim.isRunning) {
                vibrator.tick()     // 회전 중 — 가벼운 tick
            } else {
                vibrator.confirm()  // 최종 스냅 확정 — 묵직한 진동
            }
        }
    }

    val containerSize    = 300.dp                            // was 280.dp
    val orbitRadiusDp    = containerSize / 2f - 40.dp        // = 110dp (was 96dp)
    val faceTokenDp      = 60.dp
    val centerPreviewDp  = 132.dp

    val orbitRadiusPx = with(density) { orbitRadiusDp.toPx() }
    val containerPx   = with(density) { containerSize.toPx() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(containerSize)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { pos ->
                        isDragging = true
                        // 진행 중인 decay/spring 중단 — 손가락이 즉시 휠을 잡도록
                        scope.launch { rotationAnim.stop() }
                        velocityTracker.resetTracking()
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        lastAngle = atan2(
                            (pos.y - cy).toDouble(),
                            (pos.x - cx).toDouble()
                        ).toFloat() * (180f / PI.toFloat())
                        // 시작 시점의 누적각을 추적기에 등록
                        velocityTracker.addPosition(
                            0L,
                            Offset(0f, rotationAnim.value)
                        )
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val cx   = size.width / 2f
                        val cy   = size.height / 2f
                        val curr = atan2(
                            (change.position.y - cy).toDouble(),
                            (change.position.x - cx).toDouble()
                        ).toFloat() * (180f / PI.toFloat())
                        var delta = curr - lastAngle
                        if (delta > 180f) delta -= 360f
                        if (delta < -180f) delta += 360f
                        val newCumulative = rotationAnim.value + delta
                        // 누적각을 y축으로 사용해 각속도 계산
                        velocityTracker.addPosition(
                            change.uptimeMillis,
                            Offset(0f, newCumulative)
                        )
                        scope.launch { rotationAnim.snapTo(newCumulative) }
                        lastAngle = curr
                    },
                    onDragEnd = {
                        isDragging = false
                        val angularVelocity = velocityTracker.calculateVelocity().y  // deg/sec
                        scope.launch {
                            // 1) 충분한 속도면 관성 자유 회전 (마그네틱 휠 coast)
                            if (kotlin.math.abs(angularVelocity) > 30f) {
                                rotationAnim.animateDecay(angularVelocity, decay)
                            }
                            // 2) 감속 종료 지점에서 가장 가까운 72°로 자석 흡착
                            val snapped = (rotationAnim.value / STEP_DEG)
                                .roundToInt() * STEP_DEG
                            rotationAnim.animateTo(
                                targetValue   = snapped,
                                animationSpec = spring(
                                    dampingRatio = 0.7f,
                                    stiffness    = 180f
                                )
                            )
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                )
            }
    ) {
        // 배경 halo + 점선 orbit ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx      = size.width / 2f
            val cy      = size.height / 2f
            val orbitR  = orbitRadiusPx
            val contR   = size.minDimension / 2f

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0x8CFFE1CD),
                        0.45f to Color(0x2DFFE1CD),
                        0.70f to Color.Transparent
                    ),
                    center = Offset(cx, cy * 0.9f),
                    radius = contR
                ),
                radius = contR,
                center = Offset(cx, cy)
            )

            drawCircle(
                color  = Color(0x1A3E2C23),
                radius = orbitR,
                center = Offset(cx, cy),
                style  = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(2.dp.toPx(), 6.dp.toPx())
                    )
                )
            )
        }

        // 5개 face 토큰
        emotions.forEachIndexed { idx, emotion ->
            val baseAngleDeg  = -90f + idx * STEP_DEG
            val totalAngleDeg = baseAngleDeg + rotationAnim.value
            val rad = (totalAngleDeg * (PI / 180.0))

            val fx = (orbitRadiusPx * cos(rad)).toFloat()
            val fy = (orbitRadiusPx * sin(rad)).toFloat()

            val isSelected   = emotion == selectedEmotion
            val tokenSizeDp  = if (isSelected) 72.dp else faceTokenDp

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(tokenSizeDp)
                    .offset {
                        IntOffset(
                            fx.roundToInt(),
                            fy.roundToInt()
                        )
                    }
                    .rotate(tiltAngle)
                    .clip(CircleShape)
                    .background(emotionTint(emotion.score))
                    .then(
                        if (isSelected)
                            Modifier.border(2.5.dp, emotionColor(emotion.score), CircleShape)
                        else
                            Modifier.border(1.dp, Color.Black.copy(alpha = 0.04f), CircleShape)
                    )
            ) {
                EmotionFace(
                    score  = emotion.score,
                    size   = tokenSizeDp * 0.78f,
                    accent = isSelected && emotion.score == 5
                )
            }
        }

        // 중앙 미리보기 132dp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(centerPreviewDp)
                .clip(CircleShape)
                .background(
                    if (selectedEmotion != null) emotionTint(selectedEmotion.score)
                    else Color(0xFFFFFBF6)
                )
                .border(1.dp, Color(0x0F3E2C23), CircleShape)
        ) {
            AnimatedContent(
                targetState = selectedEmotion,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.85f)) togetherWith fadeOut()
                },
                label = "center_preview"
            ) { emotion ->
                if (emotion == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "지금 기분",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight(700),
                            color = Text3
                        )
                        Text(
                            text = "?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight(800),
                            color = Text3.copy(alpha = 0.25f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        EmotionFace(
                            score  = emotion.score,
                            size   = 88.dp,
                            accent = emotion.score == 5
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = emotion.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight(700),
                            color = emotionInk(emotion.score)
                        )
                    }
                }
            }
        }
    }
}
