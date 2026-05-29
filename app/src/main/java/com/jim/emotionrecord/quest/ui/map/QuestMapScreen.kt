package com.jim.emotionrecord.quest.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.quest.domain.model.SectionData
import com.jim.emotionrecord.quest.ui.common.QuestPrimaryButton
import com.jim.emotionrecord.ui.theme.QBg
import com.jim.emotionrecord.ui.theme.QBgPaper
import com.jim.emotionrecord.ui.theme.QLineStrong
import com.jim.emotionrecord.ui.theme.QPrimaryDeep
import com.jim.emotionrecord.ui.theme.QText1
import com.jim.emotionrecord.ui.theme.QText2
import com.jim.emotionrecord.ui.theme.QText3
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

// 지그재그 컬럼 패턴 (5컬럼 기준, 인덱스 0~6: 월~일)
private val COL_PATTERN = intArrayOf(2, 3, 4, 3, 2, 1, 2)
private val STAMP_SIZE  = 72.dp
private val CELL_H      = 110.dp
private val PAD_TOP     = 100.dp   // 상단 잠금 카드 영역 여백
private val PAD_BOT     = 84.dp    // 하단 시작 리본 여백

@Composable
fun QuestMapScreen(
    justStamped: Boolean = false,
    onNavigateToRecord: () -> Unit = {},
    viewModel: QuestMapViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    // 진입마다 새로 고침
    LaunchedEffect(Unit) { viewModel.refresh() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            QuestMapEffect.NavigateToRecord -> onNavigateToRecord()
        }
    }

    Scaffold(
        topBar = {
            val currentSection = state.sections.lastOrNull()
            MapTopBar(currentSection, onSeedData = { viewModel.seedData() })
        },
        containerColor = QBg,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            // 지도 영역
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(QBgPaper),
            ) {
                if (state.sections.isNotEmpty()) {
                    MapContent(sections = state.sections, justStamped = justStamped)
                }
            }

            // 하단 CTA
            BottomCta(
                todayRecorded = state.sections.lastOrNull()?.todayRecorded ?: false,
                isLoading     = state.isLoading,
                onClick       = { viewModel.onGoToRecord() },
            )
        }
    }
}

// ── TopBar ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapTopBar(section: SectionData?, onSeedData: () -> Unit) {
    TopAppBar(
        title = {
            if (section != null) {
                Column {
                    Text(
                        text  = "${section.weekNumber}주차",
                        style = MaterialTheme.typography.labelSmall,
                        color = QText3,
                        letterSpacing = 1.2.sp,
                    )
                    val s = section.sectionStart
                    val e = section.sectionEnd
                    Text(
                        text  = "${s.monthValue}/${s.dayOfMonth} – ${e.monthValue}/${e.dayOfMonth}",
                        style = MaterialTheme.typography.titleLarge,
                        color = QText1,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSeedData) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    drawCircle(QText3, style = Stroke(2.dp.toPx()))
                }
            }
            // 그래프 버튼 (TIER 2)
            IconButton(onClick = {}, enabled = false) {
                Canvas(modifier = Modifier.size(22.dp)) {
                    val w = size.width; val h = size.height
                    val col = Color(0xFFB5A89B)
                    drawRect(col, Offset(0f, h * 0.54f), androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.46f))
                    drawRect(col, Offset(w * 0.35f, h * 0.21f), androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.79f))
                    drawRect(col, Offset(w * 0.70f, h * 0.38f), androidx.compose.ui.geometry.Size(w * 0.3f, h * 0.62f))
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg),
    )
}

// ── 지도 콘텐츠 ───────────────────────────────────────────────────────────────

@Composable
private fun MapContent(sections: List<SectionData>, justStamped: Boolean) {
    val sectionH      = CELL_H * 7
    val totalH        = PAD_TOP + sectionH * sections.size + PAD_BOT

    val scrollState   = rememberScrollState()
    val density       = LocalDensity.current
    val configuration = LocalConfiguration.current

    val allStamps = sections.flatMap { it.stamps }

    // 오늘 스탬프가 화면 중앙에 오도록 초기 스크롤
    LaunchedEffect(sections) {
        val currentSection = sections.lastOrNull() ?: return@LaunchedEffect
        val todayIdxInSection = currentSection.todayStampIndex ?: 0
        val totalTodayIdx = (sections.size - 1) * 7 + todayIdxInSection
        
        val todayCenterY = totalH - PAD_BOT - CELL_H * totalTodayIdx.toFloat()
        val screenH = configuration.screenHeightDp.dp
        val targetDp = (todayCenterY - screenH / 2 + 100.dp).coerceAtLeast(0.dp)
        scrollState.scrollTo(with(density) { targetDp.roundToPx() })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        // 점선 트레일 + 배경
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalH),
        ) {
            val mapW = size.width
            val colGap = mapW / 6f

            // 희미한 점 격자
            val gridPx = 14.dp.toPx()
            val dotR   = 0.9.dp.toPx()
            val dotC   = Color(0x0A3B2A20)
            repeat((size.height / gridPx).toInt() + 2) { r ->
                repeat((mapW / gridPx).toInt() + 2) { c ->
                    drawCircle(dotC, dotR, Offset(7.dp.toPx() + c * gridPx, 7.dp.toPx() + r * gridPx))
                }
            }

            // 전체 트레일
            if (allStamps.size > 1) {
                val pts = allStamps.mapIndexed { i, s ->
                    Offset(
                        x = COL_PATTERN[s.dayIndex % 7] * colGap,
                        y = (totalH - PAD_BOT - CELL_H * i.toFloat()).toPx(),
                    )
                }
                drawPath(
                    path  = smoothTrail(pts),
                    color = Color(0x223B2A20),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap   = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(3.dp.toPx(), 8.dp.toPx()), 0f
                        ),
                    ),
                )
            }
        }

        // 잠금 카드 (다음 섹션)
        NextSectionLock(
            weekNumber = (sections.lastOrNull()?.weekNumber ?: 0) + 1,
            modifier   = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp),
        )

        val approxMapW = (configuration.screenWidthDp - 32).dp
        val colGapDp   = approxMapW / 6

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalH),
        ) {
            sections.forEachIndexed { sIdx, section ->
                // 섹션 시작 리본 (각 섹션 하단)
                val ribbonY = totalH - PAD_BOT - sectionH * sIdx - 10.dp
                SectionStartRibbon(
                    weekNumber = section.weekNumber,
                    modifier   = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = ribbonY),
                )

                section.stamps.forEachIndexed { i, stamp ->
                    val globalIdx = sIdx * 7 + i
                    val xCenter = colGapDp * COL_PATTERN[stamp.dayIndex % 7].toFloat()
                    val yCenter = totalH - PAD_BOT - CELL_H * globalIdx.toFloat()
                    val tilt    = when (globalIdx % 3) { 0 -> -2.5f; 1 -> 1.5f; else -> -1f }

                    EmotionStamp(
                        stamp          = stamp,
                        size           = STAMP_SIZE,
                        tiltDegrees    = tilt,
                        animateLanding = justStamped && stamp.isToday,
                        modifier       = Modifier.offset(
                            x = xCenter - STAMP_SIZE / 2,
                            y = yCenter - STAMP_SIZE / 2,
                        ),
                    )

                    Text(
                        text       = stamp.label,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight(600),
                        color      = if (stamp.isToday) QPrimaryDeep else QText3,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier
                            .offset(x = xCenter - STAMP_SIZE / 2, y = yCenter + STAMP_SIZE / 2 + 4.dp)
                            .width(STAMP_SIZE),
                    )
                }
            }
        }
    }
}

// ── 잠금 카드 ────────────────────────────────────────────────────────────────

@Composable
private fun NextSectionLock(weekNumber: Int, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0x1AB5A89B)),
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                val w = size.width; val h = size.height
                val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                val color = Color(0xFF7A6A5E)
                drawRoundRect(color, Offset(w*0.2f, h*0.46f), androidx.compose.ui.geometry.Size(w*0.6f, h*0.46f),
                    androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()), style = stroke)
                val path = Path().apply {
                    moveTo(w*0.34f, h*0.46f); lineTo(w*0.34f, h*0.34f)
                    quadraticTo(w*0.34f, h*0.10f, w*0.5f, h*0.10f)
                    quadraticTo(w*0.66f, h*0.10f, w*0.66f, h*0.34f); lineTo(w*0.66f, h*0.46f)
                }
                drawPath(path, color, style = stroke)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text      = "${weekNumber}주차 잠김",
            fontSize  = 11.sp,
            fontWeight= FontWeight(600),
            color     = QText3,
        )
    }
}

// ── 섹션 시작 리본 ───────────────────────────────────────────────────────────

@Composable
private fun SectionStartRibbon(weekNumber: Int, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.width(36.dp).height(1.dp).background(QLineStrong))
        Spacer(Modifier.width(10.dp))
        Text(
            text       = "${weekNumber}주차 시작",
            fontSize   = 12.sp,
            fontWeight = FontWeight(700),
            color      = QText2,
            letterSpacing = 0.8.sp,
        )
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.width(36.dp).height(1.dp).background(QLineStrong))
    }
}

// ── CTA 버튼 ─────────────────────────────────────────────────────────────────

@Composable
private fun BottomCta(
    todayRecorded: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(Color.Transparent, QBg)),
                alpha = 0.98f,
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        QuestPrimaryButton(
            text    = if (todayRecorded) "오늘 완료!" else "오늘의 퀘스트 하러 가기",
            onClick = onClick,
            enabled = !todayRecorded && !isLoading,
        )
    }
}

// ── Trail smoothing (catmull-rom) ─────────────────────────────────────────────

private fun smoothTrail(pts: List<Offset>): Path {
    val path = Path()
    if (pts.isEmpty()) return path
    path.moveTo(pts[0].x, pts[0].y)
    if (pts.size == 1) return path
    for (i in 0 until pts.size - 1) {
        val p0 = if (i > 0) pts[i - 1] else pts[i]
        val p1 = pts[i]
        val p2 = pts[i + 1]
        val p3 = if (i < pts.size - 2) pts[i + 2] else p2
        val c1x = p1.x + (p2.x - p0.x) / 6f
        val c1y = p1.y + (p2.y - p0.y) / 6f
        val c2x = p2.x - (p3.x - p1.x) / 6f
        val c2y = p2.y - (p3.y - p1.y) / 6f
        path.cubicTo(c1x, c1y, c2x, c2y, p2.x, p2.y)
    }
    return path
}
