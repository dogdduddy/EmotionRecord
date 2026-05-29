package com.jim.emotionrecord.quest.ui.record

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.quest.domain.model.Emotion
import com.jim.emotionrecord.quest.domain.model.Mission
import com.jim.emotionrecord.quest.domain.model.MissionProvider
import com.jim.emotionrecord.quest.ui.common.EmotionFace
import com.jim.emotionrecord.quest.ui.common.QuestPrimaryButton
import com.jim.emotionrecord.quest.ui.common.QuestSecondaryButton
import com.jim.emotionrecord.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestRecordScreen(
    fromMap: Boolean = false,
    onNavigateToMap: () -> Unit = {},
    onNavigateToMission: (Int, Long) -> Unit = { _, _ -> },
    onClose: () -> Unit = {},
    viewModel: QuestRecordViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is QuestRecordSideEffect.NavigateToMap -> onNavigateToMap()
            is QuestRecordSideEffect.NavigateToMission -> onNavigateToMission(sideEffect.level, sideEffect.recordId)
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (fromMap) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "닫기", tint = QText1)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg)
            )
        },
        containerColor = QBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "오늘 기분은\n어떤가요?",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp,
                    letterSpacing = (-0.48).sp,
                    textAlign = TextAlign.Center,
                    color = QText1
                )
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (state.selectedEmotion != null) "아래에 짧게 적어둘 수도 있어요" else "얼굴을 골라주세요",
                fontSize = 13.sp,
                color = QText2
            )

            Spacer(Modifier.height(32.dp))

            FaceRow(
                selectedLevel = state.selectedEmotion?.level,
                onSelect = { viewModel.selectEmotion(it) }
            )

            Spacer(Modifier.height(20.dp))

            // Memo Input
            MemoInputBox(
                value = state.memo,
                onValueChange = { viewModel.updateMemo(it) },
                isSelected = state.selectedEmotion != null
            )

            Spacer(Modifier.weight(1f))

            QuestPrimaryButton(
                text = "기록하기",
                onClick = { viewModel.saveRecord() },
                enabled = state.selectedEmotion != null && !state.isSaving
            )
        }
    }

    if (state.showMissionSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onDoMissionLater() },
            sheetState = sheetState,
            containerColor = QBg,
            scrimColor = Color.Black.copy(alpha = 0.42f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            contentWindowInsets = { WindowInsets(0) }
        ) {
            state.selectedEmotion?.let { emotion ->
                MissionProposalContent(
                    level = emotion.level,
                    onDoLater = { viewModel.onDoMissionLater() },
                    onGoToMission = { viewModel.onGoToMission() }
                )
            }
        }
    }
}

@Composable
fun FaceRow(
    selectedLevel: Int?,
    onSelect: (Emotion) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Emotion.entries.forEach { emotion ->
            val isSelected = selectedLevel == emotion.level
            val size by animateDpAsState(if (isSelected) 60.dp else 48.dp, label = "faceSize")
            val opacity by animateFloatAsState(if (selectedLevel == null || isSelected) 1f else 0.55f, label = "faceOpacity")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(emotion) }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(qEmoColor(emotion.level).copy(alpha = opacity))
                        .then(
                            if (isSelected) Modifier.shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                spotColor = qEmoColor(emotion.level).copy(alpha = 0.35f)
                            ) else Modifier
                        )
                ) {
                    EmotionFace(
                        level = emotion.level,
                        size = size * 0.78f,
                        flat = true
                    )
                }

                Text(
                    text = emotion.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) qEmoInk(emotion.level) else QText3.copy(alpha = opacity)
                )
            }
        }
    }
}

@Composable
fun MemoInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    isSelected: Boolean
) {
    val cornerRadius = 16.dp
    val strokeWidth = 3.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) QSurface else Color.Transparent)
            .then(
                if (isSelected) {
                    // true일 경우 일반적인 실선 테두리
                    Modifier.border(
                        width = strokeWidth,
                        color = QLine,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                } else {
                    // false일 경우 점선 테두리
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = QLineStrong,
                            style = Stroke(
                                width = strokeWidth.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(5.dp.toPx(), 4.dp.toPx()), // 점선 길이, 공백 길이
                                    phase = 0f
                                )
                            ),
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                    }
                }
            )
            .padding(14.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = "상황이나 기분을 적어보세요 (선택)",
                fontSize = 14.sp,
                color = QText3
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            enabled = isSelected,
            textStyle = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = QText1
            )
        )
    }
}

@Composable
fun MissionProposalContent(
    level: Int,
    onDoLater: () -> Unit,
    onGoToMission: () -> Unit
) {
    val mission = MissionProvider.getMissionForEmotion(level)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Celebration badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
                    .background(qEmoColor(level), CircleShape)
            ) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }
            Text(
                text = "기록 완료",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = qEmoInk(level)
            )
        }

        Text(
            text = "오늘의 미션이 있어요",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            color = QText1
        )

        MissionPreviewCard(mission, level)

        QuestPrimaryButton(
            text = "미션하러 가기",
            onClick = onGoToMission
        )
        
        QuestSecondaryButton(
            text = "다음에 하기",
            onClick = onDoLater
        )
    }
}

@Composable
fun MissionPreviewCard(mission: Mission, level: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(QSurface, RoundedCornerShape(18.dp))
            .border(1.dp, QLine, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .background(qEmoTint(level), RoundedCornerShape(16.dp))
        ) {
            // Icon placeholder
            Text(
                text = when(mission.icon) {
                    "breath" -> "🫁"
                    "gratitude" -> "💝"
                    "warm" -> "✉️"
                    else -> "👣"
                },
                fontSize = 28.sp
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "오늘의 미션",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = qEmoInk(level).copy(alpha = 0.75f)
            )
            Text(
                text = mission.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = QText1,
                letterSpacing = (-0.16).sp
            )
            Text(
                text = mission.description,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = QText2
            )
        }
    }
}
