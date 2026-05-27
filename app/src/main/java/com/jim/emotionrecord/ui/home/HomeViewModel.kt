package com.jim.emotionrecord.ui.home

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import com.jim.emotionrecord.data.repository.EmotionRepository
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.domain.model.EmotionCheck
import com.jim.emotionrecord.domain.usecase.UpdateMemoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.viewmodel.container
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeState(
    val records: List<EmotionCheck> = emptyList(),
    val isLoading: Boolean = true,
    val editingRecord: EmotionCheck? = null,
    val editMemoText: String = ""
)

sealed interface HomeSideEffect {
    data object NavigateToRecord : HomeSideEffect
    data object NavigateToGraph : HomeSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val emotionRepository: EmotionRepository,
    private val updateMemoUseCase: UpdateMemoUseCase
) : ViewModel(), ContainerHost<HomeState, HomeSideEffect> {

    override val container = container<HomeState, HomeSideEffect>(HomeState())

    init {
        observeTodayRecords()
    }

    @OptIn(OrbitExperimental::class)
    private fun observeTodayRecords() = intent {
        repeatOnSubscription {
            val zone = ZoneId.systemDefault()
            val todayStart = LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli()
            val tomorrowStart = LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

            emotionRepository.observeAll()
                .map { entities ->
                    // 오늘 날짜만 필터링 (최신순)
                    val todayEntities = entities.filter {
                        it.recordedAt in todayStart until tomorrowStart
                    }
                    todayEntities.toEmotionChecks()
                }
                .collect { checks ->
                    reduce { state.copy(records = checks, isLoading = false) }
                }
        }
    }

    fun onRecordClick() = intent {
        postSideEffect(HomeSideEffect.NavigateToRecord)
    }

    fun onGraphClick() = intent {
        postSideEffect(HomeSideEffect.NavigateToGraph)
    }

    fun onEditMemoClick(record: EmotionCheck) = intent {
        reduce { state.copy(editingRecord = record, editMemoText = record.memo) }
    }

    fun onEditMemoTextChange(text: String) = intent {
        reduce { state.copy(editMemoText = text) }
    }

    fun onSaveMemo() = intent {
        val record = state.editingRecord ?: return@intent
        updateMemoUseCase(record.id, state.editMemoText)
        reduce { state.copy(editingRecord = null, editMemoText = "") }
    }

    fun onDismissMemoEdit() = intent {
        reduce { state.copy(editingRecord = null, editMemoText = "") }
    }
}

private fun List<EmotionCheckEntity>.toEmotionChecks(): List<EmotionCheck> {
    val latestId = firstOrNull()?.id
    return map { entity ->
        EmotionCheck(
            id         = entity.id,
            emotion    = Emotion.fromScore(entity.emotionScore),
            recordedAt = Instant.ofEpochMilli(entity.recordedAt),
            memo       = entity.memo,
            isMemoEditable = entity.id == latestId
        )
    }
}
