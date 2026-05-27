package com.jim.emotionrecord.ui.graph

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.data.repository.UserMetaRepository
import com.jim.emotionrecord.domain.model.DaySummary
import com.jim.emotionrecord.domain.model.WeekSummary
import com.jim.emotionrecord.domain.usecase.GetDaySummariesInWeekUseCase
import com.jim.emotionrecord.domain.usecase.GetWeekSummariesUseCase
import com.jim.emotionrecord.domain.usecase.SeedDemoDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

data class GraphState(
    val weekSummaries: List<WeekSummary> = emptyList(),
    val selectedWeekIndex: Int? = null,   // 1-based
    val daySummariesInWeek: List<DaySummary> = emptyList(),
    val selectedDaySummary: DaySummary? = null,
    val isLoading: Boolean = true
)

sealed interface GraphSideEffect {
    data object NavigateBack : GraphSideEffect
}

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val getWeekSummariesUseCase: GetWeekSummariesUseCase,
    private val getDaySummariesInWeekUseCase: GetDaySummariesInWeekUseCase,
    private val seedDemoDataUseCase: SeedDemoDataUseCase,
    private val userMetaRepository: UserMetaRepository
) : ViewModel(), ContainerHost<GraphState, GraphSideEffect> {

    override val container = container<GraphState, GraphSideEffect>(GraphState())

    init {
        loadData()
    }

    fun loadData() = intent {
        reduce { state.copy(isLoading = true) }
        val weeks = getWeekSummariesUseCase()
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
        val firstRecordedAt = userMetaRepository.getFirstRecordedAt() ?: return@intent
        val weekMillis = 7L * 24 * 60 * 60 * 1000
        val weekStartMillis = firstRecordedAt + (weekIndex - 1) * weekMillis
        val daySummaries = getDaySummariesInWeekUseCase(weekStartMillis)

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
        postSideEffect(GraphSideEffect.NavigateBack)
    }

    fun seedDemoData() = intent {
        reduce { state.copy(isLoading = true) }
        seedDemoDataUseCase()
        loadData()
    }
}
