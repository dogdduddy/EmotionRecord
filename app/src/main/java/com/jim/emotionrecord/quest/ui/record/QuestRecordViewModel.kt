package com.jim.emotionrecord.quest.ui.record

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import com.jim.emotionrecord.quest.domain.model.Emotion
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class QuestRecordState(
    val selectedEmotion: Emotion? = null,
    val memo: String = "",
    val showMissionSheet: Boolean = false,
    val recordedId: Long? = null,
    val isSaving: Boolean = false
)

sealed class QuestRecordSideEffect {
    data object NavigateToMap : QuestRecordSideEffect()
    data class NavigateToMission(val missionId: String) : QuestRecordSideEffect()
}

@HiltViewModel
class QuestRecordViewModel @Inject constructor(
    private val emotionRepository: EmotionRecordRepository,
    private val userMetaRepository: QuestUserMetaRepository
) : ContainerHost<QuestRecordState, QuestRecordSideEffect>, ViewModel() {

    override val container: Container<QuestRecordState, QuestRecordSideEffect> = container(QuestRecordState())

    fun selectEmotion(emotion: Emotion) = intent {
        reduce { state.copy(selectedEmotion = emotion) }
    }

    fun updateMemo(memo: String) = intent {
        reduce { state.copy(memo = memo) }
    }

    fun saveRecord() = intent {
        val emotion = state.selectedEmotion ?: return@intent
        reduce { state.copy(isSaving = true) }

        val now = System.currentTimeMillis()
        val entity = EmotionRecordEntity(
            emotionLevel = emotion.level,
            memo = state.memo,
            missionCompleted = false,
            recordedAt = now
        )

        val id = emotionRepository.insert(entity)

        // Update firstRecordedAt if null
        val meta = userMetaRepository.get() ?: QuestUserMetaEntity()
        if (meta.firstRecordedAt == null) {
            userMetaRepository.upsert(meta.copy(firstRecordedAt = now))
        }

        reduce {
            state.copy(
                recordedId = id,
                showMissionSheet = true,
                isSaving = false
            )
        }
    }

    fun onDoMissionLater() = intent {
        reduce { state.copy(showMissionSheet = false) }
        postSideEffect(QuestRecordSideEffect.NavigateToMap)
    }

    fun onGoToMission() = intent {
        val emotion = state.selectedEmotion ?: return@intent
        reduce { state.copy(showMissionSheet = false) }
        // Simple logic for mission navigation - usually you'd pass type or id
        postSideEffect(QuestRecordSideEffect.NavigateToMission(emotion.level.toString()))
    }
}
