package com.jim.emotionrecord.quest.ui.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
fun MissionWarmScreen(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("따뜻한 한마디", style = MaterialTheme.typography.titleMedium) },
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
            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background decorative cards
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.8f)
                        .rotate(-2f)
                        .offset(x = (-8).dp, y = 6.dp)
                        .background(Color(0xFFFFF4E5), RoundedCornerShape(18.dp))
                        .border(1.dp, QLine, RoundedCornerShape(18.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.8f)
                        .rotate(1.4f)
                        .offset(x = 8.dp, y = 6.dp)
                        .background(Color(0xFFFCEFE3), RoundedCornerShape(18.dp))
                        .border(1.dp, QLine, RoundedCornerShape(18.dp))
                )

                // Main card
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.8f)
                        .shadow(8.dp, RoundedCornerShape(18.dp))
                        .background(QSurface, RoundedCornerShape(18.dp))
                        .border(1.dp, QLine, RoundedCornerShape(18.dp))
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "“",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = QPrimary,
                        lineHeight = 48.sp
                    )
                    Text(
                        text = "오늘 충분히 버텨낸 것만으로\n이미 잘하고 있어요.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center,
                        color = QText1
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "— 잠시 머물러 읽어보세요",
                        fontSize = 12.sp,
                        color = QText3
                    )
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
