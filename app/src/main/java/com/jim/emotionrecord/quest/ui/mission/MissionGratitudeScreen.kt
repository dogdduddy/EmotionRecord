package com.jim.emotionrecord.quest.ui.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.quest.ui.common.QuestPrimaryButton
import com.jim.emotionrecord.ui.theme.*
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionGratitudeScreen(
    recordId: Long,
    question: String,
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

    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(question, style = MaterialTheme.typography.titleMedium) },
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "오늘 $question\n하나를 적어보세요",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                color = QText1
            )
            Text(
                text = "작은 것이어도 좋아요. 한 줄이면 충분합니다.",
                fontSize = 13.sp,
                color = QText2,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(Modifier.height(22.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(QSurface)
                    .border(
                        width = 1.5.dp,
                        color = QPrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = "여기에 작성해보세요.",
                        fontSize = 15.sp,
                        color = QText3
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = QText1
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${text.length} / 200",
                    fontSize = 11.sp,
                    color = QText3
                )
            }

            Spacer(Modifier.weight(1f))

            QuestPrimaryButton(
                text = "완료",
                onClick = { viewModel.completeMission() },
                enabled = text.isNotBlank()
            )
        }
    }
}
