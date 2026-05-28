package com.jim.emotionrecord.quest.domain.model

data class DayStamp(
    val dayIndex: Int,    // 섹션 내 0~6 (0=월, 6=일)
    val label: String,    // "월" ~ "일"
    val state: StampState,
    val emotion: Emotion? = null,  // COMPLETE / MISSION 일 때만 non-null
)
