package com.jim.emotionrecord.quest.domain.usecase

import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 디버그용 시드 데이터.
 * 이번 주 월요일을 기준으로 4상태가 모두 보이도록 구성:
 *   월=COMPLETE(보통), 화=MISSION(좋음), 수=SKIPPED, 오늘=TODAY, 미래=미표시
 *
 * 목요일 이후에 실행해야 4상태가 완전히 보임.
 */
@Singleton
class SeedQuestDataUseCase @Inject constructor(
    private val emotionRecordRepository: EmotionRecordRepository,
    private val questUserMetaRepository: QuestUserMetaRepository,
) {
    suspend operator fun invoke() {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()

        // 이번 주 월요일 (ISO: 월=1)
        val monday = today.with(DayOfWeek.MONDAY)
        val mondayMs = monday.atStartOfDay(zone).toInstant().toEpochMilli()

        // 기존 데이터 삭제
        emotionRecordRepository.deleteAll()
        questUserMetaRepository.upsert(QuestUserMetaEntity(firstRecordedAt = null, onboardingSeen = false))

        // firstRecordedAt = 이번 주 월요일
        questUserMetaRepository.upsert(QuestUserMetaEntity(firstRecordedAt = mondayMs, onboardingSeen = false))

        // 월요일: level3(보통) → COMPLETE  [우상향 시작]
        emotionRecordRepository.insert(
            EmotionRecordEntity(
                emotionLevel     = 3,
                memo             = "오늘 하루 무난하게 지나갔다.",
                missionCompleted = false,
                recordedAt       = monday.atTime(9, 0).atZone(zone).toInstant().toEpochMilli(),
            )
        )

        // 화요일: level4(좋음) + missionCompleted → MISSION  [우상향]
        val tuesday = monday.plusDays(1)
        emotionRecordRepository.insert(
            EmotionRecordEntity(
                emotionLevel     = 4,
                memo             = "산책 다녀왔더니 기분이 좋아졌다.",
                missionCompleted = true,
                recordedAt       = tuesday.atTime(10, 30).atZone(zone).toInstant().toEpochMilli(),
            )
        )

        // 수요일: 기록 없음 → SKIPPED
        // 오늘(목요일 이후): 기록 없음 → TODAY
        // 미래: 자동으로 미표시
    }
}
