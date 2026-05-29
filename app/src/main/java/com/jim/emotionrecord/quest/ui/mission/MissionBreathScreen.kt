package com.jim.emotionrecord.quest.ui.mission

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.quest.ui.common.QuestPrimaryButton
import com.jim.emotionrecord.ui.theme.*
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionBreathScreen(
    recordId: Long,
    onNavigateToMap: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: MissionViewModel = hiltViewModel()
) {
    LaunchedEffect(recordId) {
        viewModel.setRecordId(recordId)
    }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is MissionSideEffect.NavigateToMap -> onNavigateToMap()
            is MissionSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("3분 가이드 호흡", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.goBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = QBg)
            )
        },
        containerColor = QBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "원의 호흡에 맞춰 천천히",
                fontSize = 14.sp,
                color = QText2,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(Modifier.weight(0.5f))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(230.dp)
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(QEmo2.copy(alpha = 0.22f), Color.Transparent),
                                radius = 400f
                            ),
                            CircleShape
                        )
                )

                // Inner circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(174.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFFE2D2), Color(0xFFFFC0A0))
                            )
                        )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (scale > 1.09f) "EXHALE" else "INHALE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = QPrimaryDeep,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = if (scale > 1.09f) "내쉬세요" else "들이쉬세요",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = QText1
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            QuestPrimaryButton(
                text = "완료",
                onClick = { viewModel.completeMission() }
            )
        }
    }
}
