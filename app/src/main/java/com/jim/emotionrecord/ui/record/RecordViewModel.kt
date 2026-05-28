package com.jim.emotionrecord.ui.record

import androidx.lifecycle.ViewModel
import com.jim.emotionrecord.domain.model.Emotion
import com.jim.emotionrecord.domain.usecase.RecordEmotionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

enum class WidgetType { DIAL, REEL, GACHA }

data class RecordState(
    val selectedEmotion: Emotion? = null,
    val widgetType: WidgetType = WidgetType.entries.random(),
    val isSaving: Boolean = false
)

sealed interface RecordSideEffect {
    data object NavigateToHome : RecordSideEffect
    data class ShowError(val message: String) : RecordSideEffect
}

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val recordEmotionUseCase: RecordEmotionUseCase
) : ViewModel(), ContainerHost<RecordState, RecordSideEffect> {

    // 위젯 타입은 ViewModel 생성 시 1회 랜덤 결정 (config change 에도 유지됨)
    override val container = container<RecordState, RecordSideEffect>(
        RecordState(widgetType = WidgetType.DIAL)
//        RecordState(widgetType = WidgetType.entries.random())
    )

    fun onEmotionSelected(emotion: Emotion) = intent {
        reduce { state.copy(selectedEmotion = emotion) }
    }

    fun onRecord() = intent {
        val emotion = state.selectedEmotion ?: return@intent
        reduce { state.copy(isSaving = true) }
        try {
            recordEmotionUseCase(emotion.score, "")  // 감정만 기록, 메모는 홈에서
            postSideEffect(RecordSideEffect.NavigateToHome)
        } catch (e: Exception) {
            reduce { state.copy(isSaving = false) }
            postSideEffect(RecordSideEffect.ShowError("저장에 실패했습니다"))
        }
    }

    fun onClose() = intent {
        postSideEffect(RecordSideEffect.NavigateToHome)
    }
}
