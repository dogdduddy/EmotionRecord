package com.jim.emotionrecord.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.ui.common.EmotionFace
import com.jim.emotionrecord.ui.theme.Text3

private val ITEM_HEIGHT_DP = 160.dp

@Composable
fun ReelWidget(
    selectedEmotion: Emotion?,
    onEmotionSelected: (Emotion) -> Unit,
    modifier: Modifier = Modifier
) {
    val emotions   = Emotion.entries
    val count      = emotions.size * 200
    val startIdx   = emotions.size * 100

    val listState  = rememberLazyListState(initialFirstVisibleItemIndex = startIdx)
    val haptic     = LocalHapticFeedback.current
    val density    = LocalDensity.current

    // density는 Composable 컨텍스트에서 읽어야 함
    val itemPx = with(density) { ITEM_HEIGHT_DP.toPx() }.toInt()

    val centeredIdx by remember(listState) {
        derivedStateOf {
            val first  = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            if (offset > itemPx / 2) first + 1 else first
        }
    }

    LaunchedEffect(centeredIdx) {
        val emotion = emotions[centeredIdx % emotions.size]
        if (emotion != selectedEmotion) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onEmotionSelected(emotion)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(width = 240.dp, height = 320.dp)
    ) {
        // 중앙 강조 띠
        Box(
            modifier = Modifier
                .size(width = 244.dp, height = 160.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x0FFF8A65), Color(0x1FFF8A65))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(1.5.dp, Color(0x59FF8A65), RoundedCornerShape(28.dp))
        )

        // 릴 목록 (snap)
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(220.dp)
                .height(320.dp),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(listState)
        ) {
            // 위 여백
            item { Box(modifier = Modifier.height(ITEM_HEIGHT_DP)) }
            item { Box(modifier = Modifier.height(ITEM_HEIGHT_DP)) }

            items(count) { absIdx ->
                val emotion    = emotions[absIdx % emotions.size]
                val isCentered = absIdx == centeredIdx

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT_DP)
                ) {
                    if (isCentered) {
                        EmotionFace(
                            score  = emotion.score,
                            size   = 140.dp,
                            accent = emotion.score == 5
                        )
                    } else {
                        EmotionFace(
                            score    = emotion.score,
                            size     = 70.dp,
                            modifier = Modifier
                                .alpha(0.35f)
                                .blur(1.2.dp)
                        )
                    }
                }
            }

            // 아래 여백
            item { Box(modifier = Modifier.height(ITEM_HEIGHT_DP)) }
            item { Box(modifier = Modifier.height(ITEM_HEIGHT_DP)) }
        }

        // 위/아래 페이드 오버레이
        Box(
            modifier = Modifier
                .size(width = 240.dp, height = 320.dp)
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xCCFBF7F2),
                            0.22f to Color.Transparent,
                            0.78f to Color.Transparent,
                            1.00f to Color(0xCCFBF7F2)
                        )
                    )
                )
        )

        // ▲▼ chevron
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(width = 240.dp, height = 320.dp)
                .padding(vertical = 12.dp)
        ) {
            ChevronStack(up = true)
            ChevronStack(up = false)
        }
    }
}

@Composable
private fun ChevronStack(up: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-6).dp)
    ) {
        val alphas = listOf(0.18f, 0.36f, 0.54f)
        val list   = if (up) alphas.reversed() else alphas
        list.forEach { a ->
            Text(
                text  = if (up) "▲" else "▼",
                color = Text3.copy(alpha = a),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
