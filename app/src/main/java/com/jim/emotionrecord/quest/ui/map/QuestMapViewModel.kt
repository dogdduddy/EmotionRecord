package com.jim.emotionrecord.quest.ui.map

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.quest.domain.model.SectionData
import com.jim.emotionrecord.quest.domain.usecase.GetCurrentSectionStampsUseCase
import com.jim.emotionrecord.quest.domain.usecase.SeedQuestDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class QuestMapState(
    val isLoading: Boolean = true,
    val sectionData: SectionData? = null,
)

sealed interface QuestMapEffect {
    data object NavigateToRecord : QuestMapEffect
}

@HiltViewModel
class QuestMapViewModel @Inject constructor(
    private val getCurrentSectionStampsUseCase: GetCurrentSectionStampsUseCase,
    private val seedQuestDataUseCase: SeedQuestDataUseCase,
) : ViewModel(), ContainerHost<QuestMapState, QuestMapEffect> {

    override val container = container<QuestMapState, QuestMapEffect>(QuestMapState())

    init {
        refresh()
    }

    fun refresh() = intent {
        reduce { state.copy(isLoading = true) }
        val section = getCurrentSectionStampsUseCase()
        reduce { state.copy(isLoading = false, sectionData = section) }
    }

    fun seedData() = intent {
        seedQuestDataUseCase()
        refresh()
    }

    fun onGoToRecord() = intent {
        postSideEffect(QuestMapEffect.NavigateToRecord)
    }
}
