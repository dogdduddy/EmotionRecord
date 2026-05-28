package com.jim.emotionrecord.ui.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.emotionrecord.ui.theme.Bg
import com.jim.emotionrecord.ui.theme.BgWarm
import com.jim.emotionrecord.ui.theme.Line
import com.jim.emotionrecord.ui.theme.LineStrong
import com.jim.emotionrecord.ui.theme.Primary
import com.jim.emotionrecord.ui.theme.PrimaryDeep
import com.jim.emotionrecord.ui.theme.PrimarySoft
import com.jim.emotionrecord.ui.theme.SurfaceSub
import com.jim.emotionrecord.ui.theme.Text1
import com.jim.emotionrecord.ui.theme.Text2
import com.jim.emotionrecord.ui.theme.Text3

@Composable
fun LandingScreen(
    onSelectFun: () -> Unit,
    onSelectQuest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 80.dp, bottom = 40.dp)
        ) {
            // ── 헤더 ──────────────────────────────────────────────────────────
            Text(
                text = "감정 기록",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight(800),
                color = Text1
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "어떤 방식으로 오늘의 감정을\n기록해볼까요?",
                style = MaterialTheme.typography.bodyLarge,
                color = Text2,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(Modifier.weight(1f))

            // ── 카드 1: 재밌는 감정 기록 ────────────────────────────────────
            AppCard(
                emoji = "🎡",
                badgeText = "기존 프로토타입",
                badgeColor = Primary,
                title = "재밌는 감정 기록",
                description = "다이얼, 릴, 뽑기 위젯으로\n감각적으로 감정을 기록해요",
                backgroundColor = PrimarySoft,
                borderColor = Primary.copy(alpha = 0.3f),
                onClick = onSelectFun
            )

            Spacer(Modifier.height(16.dp))

            // ── 카드 2: 퀘스트 감정 기록 ────────────────────────────────────
            AppCard(
                emoji = "🏆",
                badgeText = "신규 프로토타입",
                badgeColor = Color(0xFF63A16B),
                title = "퀘스트 감정 기록",
                description = "듀오링고처럼 미션을 완료하며\n꾸준히 감정을 기록해요",
                backgroundColor = Color(0xFFEBF5EC),
                borderColor = Color(0xFF63A16B).copy(alpha = 0.3f),
                onClick = onSelectQuest
            )

            Spacer(Modifier.weight(1f))

            // ── 하단 캡션 ─────────────────────────────────────────────────
            Text(
                text = "두 가지 프로토타입을 모두 경험해보세요",
                style = MaterialTheme.typography.bodySmall,
                color = Text3,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppCard(
    emoji: String,
    badgeText: String,
    badgeColor: Color,
    title: String,
    description: String,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 이모지 아이콘
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.7f))
                ) {
                    Text(text = emoji, fontSize = 26.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    // 배지
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(badgeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight(600),
                            color = badgeColor
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight(800),
                        color = Text1
                    )
                }

                // 화살표
                Text(
                    text = "→",
                    fontSize = 20.sp,
                    color = Text2
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Text2,
                lineHeight = 22.sp
            )
        }
    }
}
