package com.jim.emotionrecord.quest.ui.record

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import com.jim.emotionrecord.quest.domain.model.Emotion
import com.jim.emotionrecord.quest.domain.model.Mission
import com.jim.emotionrecord.quest.domain.model.MissionProvider
import com.jim.emotionrecord.quest.domain.model.MissionType
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class QuestRecordState(
    val selectedEmotion: Emotion? = null,
    val memo: String = "",
    val showMissionSheet: Boolean = false,
    val assignedMission: Mission? = null,
    val recordedId: Long? = null,
    val isSaving: Boolean = false
)

sealed class QuestRecordSideEffect {
    data object NavigateToMap : QuestRecordSideEffect()
    data class NavigateToMission(
        val type: MissionType,
        val missionId: String,
        val recordId: Long
    ) : QuestRecordSideEffect()
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

        val mission = MissionProvider.getMissionForEmotion(emotion.level)

        reduce {
            state.copy(
                recordedId = id,
                assignedMission = mission,
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
        val mission = state.assignedMission ?: return@intent
        val recordId = state.recordedId ?: return@intent
        reduce { state.copy(showMissionSheet = false) }
        postSideEffect(
            QuestRecordSideEffect.NavigateToMission(
                type = mission.type,
                missionId = mission.id,
                recordId = recordId
            )
        )
    }
}
