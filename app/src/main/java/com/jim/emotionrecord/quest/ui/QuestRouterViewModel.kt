package com.jim.emotionrecord.quest.ui

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

sealed interface QuestRouterEffect {
    data object NavigateToRecord : QuestRouterEffect
    data object NavigateToMap    : QuestRouterEffect
}

@HiltViewModel
class QuestRouterViewModel @Inject constructor(
    private val emotionRecordRepository: EmotionRecordRepository,
) : ViewModel(), ContainerHost<Unit, QuestRouterEffect> {

    override val container = container<Unit, QuestRouterEffect>(Unit)

    init {
        route()
    }

    private fun route() = intent {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val now = System.currentTimeMillis()

        val hasTodayRecord = emotionRecordRepository.getInRange(todayStart, now).isNotEmpty()

        postSideEffect(
            if (hasTodayRecord) QuestRouterEffect.NavigateToMap
            else QuestRouterEffect.NavigateToRecord
        )
    }
}
