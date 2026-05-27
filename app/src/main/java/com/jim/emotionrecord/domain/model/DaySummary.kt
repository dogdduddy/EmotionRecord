package com.jim.emotionrecord.domain.model

import java.time.LocalDate

data class DaySummary(
    val date: LocalDate,
    val averageScore: Float?,     // 그 날 기록들의 평균 (점 1개로 표현)
    val records: List<EmotionCheck> // 그 날의 모든 기록
)
