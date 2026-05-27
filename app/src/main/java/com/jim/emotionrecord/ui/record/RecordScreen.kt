package com.jim.emotionrecord.ui.record

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.ui.home.GradientButton
import com.jim.emotionrecord.ui.theme.Bg
import com.jim.emotionrecord.ui.theme.Line
import com.jim.emotionrecord.ui.theme.Primary
import com.jim.emotionrecord.ui.theme.SurfaceSub
import com.jim.emotionrecord.ui.theme.Text1
import com.jim.emotionrecord.ui.theme.Text2
import com.jim.emotionrecord.ui.theme.Text3
import com.jim.emotionrecord.ui.theme.emotionColor
import com.jim.emotionrecord.ui.widgets.DialWidget
import com.jim.emotionrecord.ui.widgets.GachaWidget
import com.jim.emotionrecord.ui.widgets.ReelWidget
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    onNavigateToHome: () -> Unit,
    showCloseButton: Boolean = false,
    viewModel: RecordViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            RecordSideEffect.NavigateToHome -> onNavigateToHome()
            is RecordSideEffect.ShowError   -> scope.launch {
                snackbarHostState.showSnackbar(sideEffect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (showCloseButton) {
                        IconButton(onClick = { viewModel.onClose() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "닫기",
                                tint = Text1
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        },
        containerColor = Bg
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // 타이틀 & 부제목 (상태별 변화)
            AnimatedContent(
                targetState = state.selectedEmotion,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "title_anim"
            ) { selectedEmotion ->
                TitleSection(
                    widgetType = state.widgetType,
                    selectedEmotion = selectedEmotion
                )
            }

            Spacer(Modifier.height(24.dp))

            // 위젯 영역
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                when (WidgetType.DIAL) {
//                when (state.widgetType) {
                    WidgetType.DIAL  -> DialWidget(
                        selectedEmotion = state.selectedEmotion,
                        onEmotionSelected = viewModel::onEmotionSelected
                    )
                    WidgetType.REEL  -> ReelWidget(
                        selectedEmotion = state.selectedEmotion,
                        onEmotionSelected = viewModel::onEmotionSelected
                    )
                    WidgetType.GACHA -> GachaWidget(
                        selectedEmotion = state.selectedEmotion,
                        onEmotionSelected = viewModel::onEmotionSelected
                    )
                }
            }

            // 다이얼 전용: 5단계 progress dot
            if (state.widgetType == WidgetType.DIAL) {
                Spacer(Modifier.height(20.dp))
                ProgressDots(selectedScore = state.selectedEmotion?.score)
            }

            Spacer(Modifier.height(32.dp))

            // 기록하기 버튼
            if (state.isSaving) {
                CircularProgressIndicator(color = Primary)
            } else {
                GradientButton(
                    label   = "기록하기",
                    enabled = state.selectedEmotion != null,
                    onClick = { viewModel.onRecord() },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ─── 타이틀 섹션 ───────────────────────────────────────────────────────────────
@Composable
private fun TitleSection(
    widgetType: WidgetType,
    selectedEmotion: Emotion?
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        when (widgetType) {
            WidgetType.DIAL -> {
                if (selectedEmotion == null) {
                    Text(
                        text = "지금 기분은\n어떠세요?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight(800),
                        color = Text1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "다이얼을 돌려 감정을 골라보세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Text3,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = emotionColor(selectedEmotion.score), fontWeight = FontWeight(800))) {
                                append(selectedEmotion.label)
                            }
                            withStyle(SpanStyle(color = Text1, fontWeight = FontWeight(800))) {
                                append("으로\n맞추셨어요")
                            }
                        },
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "확실하면 아래 버튼을 눌러주세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Text3,
                        textAlign = TextAlign.Center
                    )
                }
            }
            WidgetType.REEL -> {
                Text(
                    text = "위아래로 굴려서\n골라보세요",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight(800),
                    color = Text1,
                    textAlign = TextAlign.Center
                )
                if (selectedEmotion != null) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(emotionColor(selectedEmotion.score).copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = selectedEmotion.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight(600),
                            color = emotionColor(selectedEmotion.score)
                        )
                    }
                }
            }
            WidgetType.GACHA -> {
                if (selectedEmotion == null) {
                    Text(
                        text = "오늘의 감정을\n뽑아볼까요?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight(800),
                        color = Text1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "레버를 당겨주세요",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Text3,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = emotionColor(selectedEmotion.score), fontWeight = FontWeight(800))) {
                                append(selectedEmotion.label)
                            }
                            withStyle(SpanStyle(color = Text1, fontWeight = FontWeight(800))) {
                                append("이\n나왔어요")
                            }
                        },
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ─── 다이얼 진행 도트 ──────────────────────────────────────────────────────────
@Composable
private fun ProgressDots(selectedScore: Int?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { score ->
            val isActive = score == selectedScore
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .then(
                        if (isActive) Modifier.size(width = 22.dp, height = 8.dp)
                        else Modifier.size(8.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isActive) Primary else Line.copy(alpha = 0.18f)
                    )
            )
        }
    }
}
