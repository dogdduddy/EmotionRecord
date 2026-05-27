package com.jim.emotionrecord.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.R
import com.jim.emotionrecord.domain.model.EmotionCheck
import com.jim.emotionrecord.ui.common.EmotionCard
import com.jim.emotionrecord.ui.common.EmotionFace
import com.jim.emotionrecord.ui.common.FaceChip
import com.jim.emotionrecord.ui.theme.Bg
import com.jim.emotionrecord.ui.theme.Line
import com.jim.emotionrecord.ui.theme.LineStrong
import com.jim.emotionrecord.ui.theme.Primary
import com.jim.emotionrecord.ui.theme.PrimaryDeep
import com.jim.emotionrecord.ui.theme.SurfaceSoft
import com.jim.emotionrecord.ui.theme.Text1
import com.jim.emotionrecord.ui.theme.Text2
import com.jim.emotionrecord.ui.theme.Text3
import com.jim.emotionrecord.ui.theme.emotionInk
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter    = DateTimeFormatter.ofPattern("M월 d일 · EEEE")
private val timeFormatter    = DateTimeFormatter.ofPattern("a h:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRecord: () -> Unit,
    onNavigateToGraph: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            HomeSideEffect.NavigateToRecord -> onNavigateToRecord()
            HomeSideEffect.NavigateToGraph  -> onNavigateToGraph()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "감정 체크",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Text1
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.onGraphClick() }) {
                        BarChartIcon()
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg)
            )
        },
        containerColor = Bg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BigRecordButton(
                onClick = { viewModel.onRecordClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text("오늘의 기록", style = MaterialTheme.typography.titleSmall, color = Text2)
                Text(
                    text = LocalDate.now().format(dateFormatter),
                    style = MaterialTheme.typography.labelMedium,
                    color = Text3
                )
            }

            Spacer(Modifier.height(12.dp))

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("불러오는 중...", color = Text3)
                }
                state.records.isEmpty() -> EmptyTodayState(
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.records, key = { it.id }) { record ->
                        EmotionCard(
                            record = record,
                            onEditMemoClick = { viewModel.onEditMemoClick(it) }
                        )
                    }
                }
            }
        }
    }

    if (state.editingRecord != null) {
        MemoEditBottomSheet(
            record       = state.editingRecord!!,
            memo         = state.editMemoText,
            onMemoChange = viewModel::onEditMemoTextChange,
            onSave       = viewModel::onSaveMemo,
            onDismiss    = viewModel::onDismissMemoEdit
        )
    }
}

// ─── 큰 감정 기록 버튼 ─────────────────────────────────────────────────────────
@Composable
private fun BigRecordButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick  = onClick,
        modifier = modifier.height(104.dp),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xFFFFD0A6),
                            0.45f to Color(0xFFFF9B7A),
                            1.00f to Color(0xFFFF8A65)
                        )
                    )
                )
        ) {
            // 장식 원 — 우상단
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .offset(x = 20.dp, y = (-32).dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            )
            // 장식 원 — 우하단
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = (-12).dp, y = 18.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "감정 기록하기",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight(800),
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        FaceChip(score = 4, size = 18.dp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "지금 30초만",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight(600),
                            color = Color.White
                        )
                    }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        modifier = Modifier.rotate(90f),
                        tint = PrimaryDeep,
                        painter = painterResource(R.drawable.arrow_up),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

// ─── 빈 상태 ──────────────────────────────────────────────────────────────────
@Composable
private fun EmptyTodayState(modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceSoft),
        border    = BorderStroke(1.dp, LineStrong),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 20.dp)
        ) {
            EmotionFace(score = 3, size = 56.dp)
            Spacer(Modifier.height(14.dp))
            Text(
                text = "아직 오늘의 기록이 없어요",
                style = MaterialTheme.typography.bodyMedium,
                color = Text2,
                textAlign = TextAlign.Center
            )
            Text(
                text = "위 버튼을 눌러 시작해보세요",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Text1,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── 메모 편집 바텀시트 ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoEditBottomSheet(
    record: EmotionCheck,
    memo: String,
    onMemoChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor   = Bg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x333E2C23))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FaceChip(score = record.emotion.score, size = 44.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.emotion.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = emotionInk(record.emotion.score)
                    )
                    Text(
                        text = record.recordedAt
                            .atZone(ZoneId.systemDefault())
                            .format(timeFormatter),
                        style = MaterialTheme.typography.labelMedium,
                        color = Text3
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("취소", color = Text2, style = MaterialTheme.typography.bodyMedium)
                }
            }

            OutlinedTextField(
                value       = memo,
                onValueChange = { if (it.length <= 200) onMemoChange(it) },
                placeholder = { Text("지금 기분을 메모해보세요", color = Text3) },
                modifier    = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Primary,
                    unfocusedBorderColor    = Primary.copy(alpha = 0.5f),
                    focusedContainerColor   = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                supportingText = {
                    Text(
                        text  = "${memo.length} / 200",
                        style = MaterialTheme.typography.labelSmall,
                        color = Text3,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
            )

            // CTA 저장 버튼
            GradientButton(
                label    = "저장",
                onClick  = onSave,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─── 그라데이션 버튼 (공용) ───────────────────────────────────────────────────
@Composable
fun GradientButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (enabled) Brush.linearGradient(
                    colorStops = arrayOf(
                        0.00f to Color(0xFFFFB28A),
                        0.55f to Color(0xFFFF8A65),
                        1.00f to Color(0xFFE07856)
                    )
                ) else Brush.linearGradient(listOf(Color(0xFFF5EFE7), Color(0xFFF5EFE7)))
            )
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Color.White else Text3
        )
    }
}

// ─── 아이콘 헬퍼 ──────────────────────────────────────────────────────────────
@Composable
fun BarChartIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(22.dp)) {
        val w  = size.width
        val h  = size.height
        val bw = w * 0.18f
        val r  = bw * 0.3f
        listOf(
            Triple(w * 0.20f, h * 0.50f, h * 0.48f),
            Triple(w * 0.50f, h * 0.18f, h * 0.82f),
            Triple(w * 0.80f, h * 0.35f, h * 0.65f)
        ).forEach { (xc, yTop, barH) ->
            val left = xc - bw / 2
            val right = xc + bw / 2
            val path = Path().apply {
                addRoundRect(RoundRect(left = left, top = yTop, right = right, bottom = yTop + barH, radiusX = r, radiusY = r))
            }
            drawPath(path, Text1)
        }
    }
}

@Composable
fun ArrowRightIcon(tint: Color = PrimaryDeep, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(20.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val ah = size.height * 0.22f
        val aw = size.width * 0.28f
        val path = Path().apply {
            moveTo(cx - aw, cy - ah)
            lineTo(cx + aw * 0.7f, cy)
            lineTo(cx - aw, cy + ah)
        }
        drawPath(path, tint, style = Stroke(width = 2.4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}
