package com.jim.emotionrecord.quest.ui.mission

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class MissionState(
    val recordId: Long = -1,
    val isCompleted: Boolean = false
)

sealed class MissionSideEffect {
    data object NavigateToMap : MissionSideEffect()
    data object NavigateBack : MissionSideEffect()
}

@HiltViewModel
class MissionViewModel @Inject constructor(
    private val repository: EmotionRecordRepository
) : ContainerHost<MissionState, MissionSideEffect>, ViewModel() {

    override val container: Container<MissionState, MissionSideEffect> = container(MissionState())

    fun setRecordId(id: Long) = intent {
        reduce { state.copy(recordId = id) }
    }

    fun completeMission() = intent {
        if (state.recordId != -1L) {
            repository.markMissionCompleted(state.recordId)
            reduce { state.copy(isCompleted = true) }
            postSideEffect(MissionSideEffect.NavigateToMap)
        }
    }
    
    fun goBack() = intent {
        postSideEffect(MissionSideEffect.NavigateBack)
    }
}
