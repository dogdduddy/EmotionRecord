package com.jim.emotionrecord.quest.ui.graph

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.domain.model.DaySummary
import com.jim.emotionrecord.domain.model.WeekSummary
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import com.jim.emotionrecord.quest.domain.usecase.GetQuestDaySummariesInWeekUseCase
import com.jim.emotionrecord.quest.domain.usecase.GetQuestWeekSummariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

data class QuestGraphState(
    val weekSummaries: List<WeekSummary> = emptyList(),
    val selectedWeekIndex: Int? = null,
    val daySummariesInWeek: List<DaySummary> = emptyList(),
    val selectedDaySummary: DaySummary? = null,
    val isLoading: Boolean = true
)

sealed interface QuestGraphSideEffect {
    data object NavigateBack : QuestGraphSideEffect
}

@HiltViewModel
class QuestGraphViewModel @Inject constructor(
    private val getQuestWeekSummariesUseCase: GetQuestWeekSummariesUseCase,
    private val getQuestDaySummariesInWeekUseCase: GetQuestDaySummariesInWeekUseCase,
    private val questUserMetaRepository: QuestUserMetaRepository
) : ViewModel(), ContainerHost<QuestGraphState, QuestGraphSideEffect> {

    override val container = container<QuestGraphState, QuestGraphSideEffect>(QuestGraphState())

    init {
        loadData()
    }

    fun loadData() = intent {
        reduce { state.copy(isLoading = true) }
        val weeks = getQuestWeekSummariesUseCase()
        val currentWeekIdx = weeks.lastOrNull()?.weekIndex
        reduce {
            state.copy(
                weekSummaries = weeks,
                selectedWeekIndex = currentWeekIdx,
                isLoading = false
            )
        }
        currentWeekIdx?.let { loadWeekDetails(it) }
    }

    fun onWeekSelected(weekIndex: Int) = intent {
        reduce { state.copy(selectedWeekIndex = weekIndex, selectedDaySummary = null) }
        loadWeekDetails(weekIndex)
    }

    private fun loadWeekDetails(weekIndex: Int) = intent {
        val meta = questUserMetaRepository.get() ?: return@intent
        val firstRecordedAt = meta.firstRecordedAt ?: return@intent
        val zone = ZoneId.systemDefault()
        val firstDate = Instant.ofEpochMilli(firstRecordedAt).atZone(zone).toLocalDate()
        val startOfFirstWeek = firstDate.with(java.time.DayOfWeek.MONDAY)
        
        val targetWeekStart = startOfFirstWeek.plusWeeks((weekIndex - 1).toLong())
        val targetWeekStartMs = targetWeekStart.atStartOfDay(zone).toInstant().toEpochMilli()
        
        val daySummaries = getQuestDaySummariesInWeekUseCase(targetWeekStartMs)

        // 기본 선택: 마지막 기록이 있는 날
        val defaultDay = daySummaries.lastOrNull { it.records.isNotEmpty() }
            ?: daySummaries.firstOrNull()

        reduce {
            state.copy(
                daySummariesInWeek = daySummaries,
                selectedDaySummary = defaultDay
            )
        }
    }

    fun onDaySelected(daySummary: DaySummary) = intent {
        reduce { state.copy(selectedDaySummary = daySummary) }
    }

    fun onBack() = intent {
        postSideEffect(QuestGraphSideEffect.NavigateBack)
    }
}
