package com.jim.emotionrecord.quest.ui.map

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.quest.domain.model.SectionData
import com.jim.emotionrecord.quest.domain.usecase.GetQuestMapSectionsUseCase
import com.jim.emotionrecord.quest.domain.usecase.SeedQuestDataUseCase
import com.jim.emotionrecord.quest.domain.usecase.SeedScenario
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

data class QuestMapState(
    val isLoading: Boolean = true,
    val sections: List<SectionData> = emptyList(),
)

sealed interface QuestMapEffect {
    data object NavigateToRecord : QuestMapEffect
}

@HiltViewModel
class QuestMapViewModel @Inject constructor(
    private val getQuestMapSectionsUseCase: GetQuestMapSectionsUseCase,
    private val seedQuestDataUseCase: SeedQuestDataUseCase,
) : ViewModel(), ContainerHost<QuestMapState, QuestMapEffect> {

    override val container = container<QuestMapState, QuestMapEffect>(QuestMapState())

    init {
        refresh()
    }

    fun refresh() = intent {
        reduce { state.copy(isLoading = true) }
        val sections = getQuestMapSectionsUseCase()
        reduce { state.copy(isLoading = false, sections = sections) }
    }

    fun seedData(scenario: SeedScenario) = intent {
        reduce { state.copy(isLoading = true) }
        seedQuestDataUseCase(scenario)
        val sections = getQuestMapSectionsUseCase()
        reduce { state.copy(isLoading = false, sections = sections) }
    }

    fun onGoToRecord() = intent {
        postSideEffect(QuestMapEffect.NavigateToRecord)
    }
}
