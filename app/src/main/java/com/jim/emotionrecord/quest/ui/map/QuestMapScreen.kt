package com.jim.emotionrecord.quest.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.emotionrecord.ui.theme.QBg
import com.jim.emotionrecord.ui.theme.QText1
import com.jim.emotionrecord.ui.theme.QText2

// STEP 6에서 전체 구현으로 교체됨
@Composable
fun QuestMapScreen(
    justStamped: Boolean = false,
    onNavigateToRecord: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(QBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp),
    ) {
        Text(
            text = "퀘스트 지도",
            fontSize = 24.sp,
            fontWeight = FontWeight(800),
            color = QText1,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "STEP 6에서 구현 예정",
            fontSize = 14.sp,
            color = QText2,
        )
    }
}
