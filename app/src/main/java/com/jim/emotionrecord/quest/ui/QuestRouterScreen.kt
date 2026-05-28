package com.jim.emotionrecord.quest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jim.emotionrecord.ui.theme.QBg
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun QuestRouterScreen(
    onNavigateToRecord: () -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: QuestRouterViewModel = hiltViewModel(),
) {
    viewModel.collectSideEffect { effect ->
        when (effect) {
            QuestRouterEffect.NavigateToRecord -> onNavigateToRecord()
            QuestRouterEffect.NavigateToMap    -> onNavigateToMap()
        }
    }

    // DB 조회 완료 전까지 잠깐 표시되는 배경만 — 사실상 깜박임 없음
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(QBg),
    )
}
