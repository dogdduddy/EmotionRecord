package com.jim.emotionrecord.quest.domain.model

import java.time.LocalDate

data class SectionData(
    val weekNumber: Int,
    val sectionStart: LocalDate,
    val sectionEnd: LocalDate,
    val stamps: List<DayStamp>,
    val todayRecorded: Boolean,
    val todayStampIndex: Int?,   // stamps 리스트 내 오늘 항목의 인덱스
)
