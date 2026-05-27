package com.jim.emotionrecord.domain.model

import java.time.LocalDate

data class WeekSummary(
    val weekIndex: Int,           // 1부터 시작
    val startDate: LocalDate,
    val endDate: LocalDate,
    val averageScore: Float?,     // null = 기록 없음
    val recordCount: Int
)
