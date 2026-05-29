package com.jim.emotionrecord.quest.ui.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.R
import com.jim.emotionrecord.domain.model.DaySummary
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.domain.model.WeekSummary
import com.jim.emotionrecord.ui.common.EmotionCard
import com.jim.emotionrecord.ui.common.EmotionFace
import com.jim.emotionrecord.ui.theme.QBg
import com.jim.emotionrecord.ui.theme.QBgPaper
import com.jim.emotionrecord.ui.theme.QLineStrong
import com.jim.emotionrecord.ui.theme.QPrimary
import com.jim.emotionrecord.ui.theme.QPrimaryDeep
import com.jim.emotionrecord.ui.theme.QSurfaceSub
import com.jim.emotionrecord.ui.theme.QText1
import com.jim.emotionrecord.ui.theme.QText2
import com.jim.emotionrecord.ui.theme.QText3
import com.jim.emotionrecord.ui.theme.QuestTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.format.DateTimeFormatter

private val weekDayFmt     = DateTimeFormatter.ofPattern("E")
private val dateHeaderFmt  = DateTimeFormatter.ofPattern("M월 d일 EEEE")
private val monthDayFmt    = DateTimeFormatter.ofPattern("M/d")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestGraphScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuestGraphViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect { effect ->
        when (effect) { QuestGraphSideEffect.NavigateBack -> onNavigateBack() }
    }

    QuestTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "감정 통계",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = QText1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onBack() }) {
                            Icon(
                                modifier = Modifier.rotate(-90f),
                                tint = QText1,
                                painter = painterResource(R.drawable.arrow_up),
                                contentDescription = "뒤로",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg)
                )
            },
            containerColor = QBg
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = QPrimary) }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(4.dp))

                if (state.weekSummaries.isEmpty()) {
                    GraphEmptyState()
                } else {
                    // ── Section 1: 전체 흐름 ─────────────────────────────────
                    SectionHeader(
                        title   = "전체 흐름",
                        caption = "${state.weekSummaries.size}주"
                    )
                    if (state.weekSummaries.size < 2) {
                        InsufficientDataPlaceholder()
                    } else {
                        QuestOverallLineChart(
                            weeks            = state.weekSummaries,
                            selectedWeekIdx  = state.selectedWeekIndex,
                            onWeekClick      = { viewModel.onWeekSelected(it) },
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                        )
                    }

                    // ── Section 2: 주간 상세 ────────────────────────────────
                    state.selectedWeekIndex?.let { weekIdx ->
                        val week = state.weekSummaries.find { it.weekIndex == weekIdx }
                        if (week != null) {
                            SectionHeader(
                                title   = "${week.weekIndex}주차 상세",
                                caption = "${week.startDate.format(monthDayFmt)} – ${week.endDate.format(monthDayFmt)}"
                            )
                            QuestWeeklyLineChart(
                                days           = state.daySummariesInWeek,
                                selectedDate   = state.selectedDaySummary?.date,
                                onDayClick     = { viewModel.onDaySelected(it) },
                                modifier = Modifier.fillMaxWidth().height(180.dp)
                            )
                        }
                    }

                    // ── Section 3: 일별 기록 ──────────────────────────────────
                    state.selectedDaySummary?.let { day ->
                        SectionHeader(title = day.date.format(dateHeaderFmt))
                        if (day.records.isEmpty()) {
                            Text(
                                "이 날은 기록이 없어요",
                                style = MaterialTheme.typography.bodyMedium,
                                color = QText3
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                day.records.forEach { record ->
                                    EmotionCard(record = record)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 섹션 헤더 ────────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, caption: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = QText2)
        if (caption != null) {
            Text(caption, style = MaterialTheme.typography.labelMedium, color = QText3)
        }
    }
}

// ─── 빈 상태 ──────────────────────────────────────────────────────────────────
@Composable
private fun GraphEmptyState() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(QSurfaceSub)
            .border(1.dp, QLineStrong, RoundedCornerShape(18.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EmotionFace(score = 3, size = 48.dp)
            Text(
                "최소 1주 이상 기록하면\n감정의 추이가 보여요",
                style = MaterialTheme.typography.bodyMedium,
                color = QText2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InsufficientDataPlaceholder() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(QSurfaceSub)
            .border(1.dp, QLineStrong, RoundedCornerShape(14.dp))
    ) {
        Text(
            "더 기록할수록 변화가 보여요",
            style = MaterialTheme.typography.bodyMedium,
            color = QText3,
            textAlign = TextAlign.Center
        )
    }
}

// ─── 전체 기간 라인 차트 ────────────────────────────────────────────────────────
@Composable
private fun QuestOverallLineChart(
    weeks: List<WeekSummary>,
    selectedWeekIdx: Int?,
    onWeekClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().background(QBgPaper, RoundedCornerShape(24.dp))) {
            val pL = 42.dp.toPx()
            val pR = 12.dp.toPx()
            val pT = 14.dp.toPx()
            val pB = 30.dp.toPx()
            val cW = size.width - pL - pR
            val cH = size.height - pT - pB

            // ── y축 그리드 + Emotion 이모지 ─────────────────────────
            for (score in 1..5) {
                val y = pT + cH - (score - 1) / 4f * cH
                drawLine(
                    color = Color(0x0F3B2A20),
                    start = Offset(pL, y), end = Offset(size.width - pR, y),
                    strokeWidth = 1.dp.toPx()
                )
                val emotion = Emotion.fromScore(score)
                val lm = textMeasurer.measure(emotion.emoji, TextStyle(fontSize = 13.sp))
                drawText(lm, topLeft = Offset(12.dp.toPx(), y - lm.size.height / 2f))
            }

            if (weeks.isEmpty()) return@Canvas

            val xStep = if (weeks.size > 1) cW / (weeks.size - 1) else cW / 2f

            // ── 데이터 포인트 계산 ────────────────────────────────────
            val pts = weeks.mapIndexedNotNull { i, w ->
                w.averageScore?.let { s ->
                    Triple(i, Offset(pL + i * xStep, pT + cH - (s - 1) / 4f * cH), w)
                }
            }

            // ── area fill ────────────────────────────────────────────
            if (pts.size > 1) {
                val areaPath = Path().apply {
                    moveTo(pts.first().second.x, pT + cH)
                    pts.forEach { (_, o, _) -> lineTo(o.x, o.y) }
                    lineTo(pts.last().second.x, pT + cH)
                    close()
                }
                drawPath(
                    areaPath,
                    Brush.verticalGradient(
                        listOf(QPrimary.copy(alpha = 0.22f), Color.Transparent),
                        startY = pT, endY = pT + cH
                    )
                )

                // ── 라인 ─────────────────────────────────────────────
                val linePath = Path()
                pts.forEachIndexed { i, (_, o, _) ->
                    if (i == 0) linePath.moveTo(o.x, o.y) else linePath.lineTo(o.x, o.y)
                }
                drawPath(
                    linePath,
                    Brush.linearGradient(
                        listOf(QPrimary.copy(alpha = 0.8f), QPrimaryDeep),
                        start = Offset(pL, 0f), end = Offset(pL + cW, 0f)
                    ),
                    style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // ── 선택 컬럼 강조 띠 ─────────────────────────────────────
            pts.forEach { (_, o, week) ->
                val isSelected = week.weekIndex == selectedWeekIdx
                if (isSelected) {
                    val bandW = xStep * 0.9f
                    val path = Path().apply {
                        addRoundRect(RoundRect(
                            left = o.x - bandW / 2f, top = pT - 6.dp.toPx(),
                            right = o.x + bandW / 2f, bottom = pT + cH + 6.dp.toPx(),
                            radiusX = 10.dp.toPx(), radiusY = 10.dp.toPx()
                        ))
                    }
                    drawPath(path, QPrimary.copy(alpha = 0.08f))
                }
            }

            // ── 포인트 + 라벨 + x축 ──────────────────────────────────
            pts.forEach { (_, o, week) ->
                val isSelected = week.weekIndex == selectedWeekIdx
                val r = if (isSelected) 6.dp.toPx() else 4.dp.toPx()

                // halo
                if (isSelected) {
                    drawCircle(QPrimary.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = o)
                }
                // 흰 채움
                drawCircle(Color.White, radius = r, center = o)
                // 테두리
                drawCircle(
                    if (isSelected) QPrimaryDeep else QPrimary,
                    radius = r,
                    center = o,
                    style = Stroke(width = (if (isSelected) 2.4f else 2f).dp.toPx())
                )

                // tooltip (선택된 주차)
                if (isSelected) {
                    val avgStr = "%.1f".format(week.averageScore)
                    val tm = textMeasurer.measure(avgStr, TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
                    val tW = tm.size.width + 8.dp.toPx()
                    val tH = tm.size.height + 4.dp.toPx()
                    drawRoundRect(
                        Color(0xFF3B2A20),
                        topLeft = Offset(o.x - tW / 2f, o.y - tH - 6.dp.toPx()),
                        size = Size(tW, tH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                    drawText(tm, topLeft = Offset(o.x - tm.size.width / 2f, o.y - tm.size.height - 8.dp.toPx()))
                }

                // x축 라벨
                val wLabel = "${week.weekIndex}주"
                val wm = textMeasurer.measure(
                    wLabel,
                    TextStyle(
                        fontSize   = if (isSelected) 12.sp else 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight(500),
                        color      = if (isSelected) QText1 else QText3
                    )
                )
                drawText(wm, topLeft = Offset(o.x - wm.size.width / 2f, size.height - wm.size.height))
            }
        }

        // 터치 오버레이
        Row(Modifier.fillMaxSize()) {
            weeks.forEach { week ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onWeekClick(week.weekIndex) }
                        )
                )
            }
        }
    }
}

// ─── 주간 라인 차트 ───────────────────────────────────────────────────────────
@Composable
private fun QuestWeeklyLineChart(
    days: List<DaySummary>,
    selectedDate: java.time.LocalDate?,
    onDayClick: (DaySummary) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize().background(QBgPaper, RoundedCornerShape(24.dp))) {
            val pL = 42.dp.toPx(); val pR = 12.dp.toPx()
            val pT = 14.dp.toPx(); val pB = 30.dp.toPx()
            val cW = size.width - pL - pR
            val cH = size.height - pT - pB

            // y축
            for (score in 1..5) {
                val y = pT + cH - (score - 1) / 4f * cH
                drawLine(Color(0x0F3B2A20), Offset(pL, y), Offset(size.width - pR, y), 1.dp.toPx())
                val emotion = Emotion.fromScore(score)
                val lm = textMeasurer.measure(emotion.emoji, TextStyle(fontSize = 12.sp))
                drawText(lm, topLeft = Offset(12.dp.toPx(), y - lm.size.height / 2f))
            }

            if (days.isEmpty()) return@Canvas

            val xStep = cW / (days.size - 1).coerceAtLeast(1).toFloat()

            val pts = days.mapIndexedNotNull { i, d ->
                d.averageScore?.let { s ->
                    Triple(i, Offset(pL + i * xStep, pT + cH - (s - 1) / 4f * cH), d)
                }
            }

            // area fill
            if (pts.size > 1) {
                val areaPath = Path().apply {
                    moveTo(pts.first().second.x, pT + cH)
                    pts.forEach { (_, o, _) -> lineTo(o.x, o.y) }
                    lineTo(pts.last().second.x, pT + cH)
                    close()
                }
                drawPath(areaPath, Brush.verticalGradient(
                    listOf(QPrimary.copy(alpha = 0.22f), Color.Transparent),
                    startY = pT, endY = pT + cH
                ))

                val linePath = Path()
                pts.forEachIndexed { i, (_, o, _) ->
                    if (i == 0) linePath.moveTo(o.x, o.y) else linePath.lineTo(o.x, o.y)
                }
                drawPath(linePath,
                    Brush.linearGradient(
                        listOf(QPrimary.copy(alpha = 0.8f), QPrimaryDeep),
                        start = Offset(pL, 0f), end = Offset(pL + cW, 0f)
                    ),
                    style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 선택 컬럼 띠 + 포인트
            pts.forEach { (_, o, day) ->
                val isSelected = day.date == selectedDate

                if (isSelected) {
                    val bandW = xStep * 0.9f
                    val p = Path().apply {
                        addRoundRect(RoundRect(o.x - bandW/2, pT - 6.dp.toPx(), o.x + bandW/2, pT + cH + 6.dp.toPx(),
                            radiusX = 10.dp.toPx(), radiusY = 10.dp.toPx()))
                    }
                    drawPath(p, QPrimary.copy(alpha = 0.08f))
                    drawCircle(QPrimary.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = o)
                }

                drawCircle(Color.White, radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(), center = o)
                drawCircle(
                    if (isSelected) QPrimaryDeep else QPrimary,
                    radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                    center = o,
                    style = Stroke(width = if (isSelected) 2.4.dp.toPx() else 2.dp.toPx())
                )

                // tooltip
                if (isSelected) {
                    val avgStr = "%.1f".format(day.averageScore)
                    val tm = textMeasurer.measure(avgStr, TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White))
                    val tW = tm.size.width + 8.dp.toPx(); val tH = tm.size.height + 4.dp.toPx()
                    drawRoundRect(Color(0xFF3B2A20), Offset(o.x - tW/2, o.y - tH - 6.dp.toPx()),
                        Size(tW, tH), androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()))
                    drawText(tm, topLeft = Offset(o.x - tm.size.width/2, o.y - tm.size.height - 8.dp.toPx()))
                }

                // x축 요일
                val dLabel = day.date.format(weekDayFmt)
                val dm = textMeasurer.measure(dLabel, TextStyle(
                    fontSize = if (isSelected) 12.sp else 11.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight(500),
                    color = if (isSelected) QText1 else QText3
                ))
                drawText(dm, topLeft = Offset(o.x - dm.size.width/2, size.height - dm.size.height))
            }
        }

        // 터치 오버레이
        Row(Modifier.fillMaxSize()) {
            days.forEach { day ->
                Box(Modifier.weight(1f).fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onDayClick(day) }
                    )
                )
            }
        }
    }
}
