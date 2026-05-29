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
import kotlin.random.Random

enum class SeedScenario(val label: String, val totalWeeks: Int) {
    WEEK_1("1주차 진행 중", 1),
    WEEK_2("2주차 진행 중", 2),
    WEEK_5("5주차 진행 중", 5),
    WEEK_9("9주차 진행 중", 9)
}

@Singleton
class SeedQuestDataUseCase @Inject constructor(
    private val emotionRecordRepository: EmotionRecordRepository,
    private val questUserMetaRepository: QuestUserMetaRepository,
) {
    suspend operator fun invoke(scenario: SeedScenario = SeedScenario.WEEK_1) {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now()
        val thisMonday = today.with(DayOfWeek.MONDAY)

        // 기존 데이터 삭제
        emotionRecordRepository.deleteAll()
        
        // 시작일 계산 (N주차 진행 중이면 (N-1)주 전 월요일이 시작일)
        val firstMonday = thisMonday.minusWeeks((scenario.totalWeeks - 1).toLong())
        val firstMondayMs = firstMonday.atStartOfDay(zone).toInstant().toEpochMilli()

        questUserMetaRepository.upsert(QuestUserMetaEntity(firstRecordedAt = firstMondayMs, onboardingSeen = true))

        val random = Random(42) // 고정 시드

        // 과거 주차들 (0 ~ totalWeeks-2) 채우기
        for (w in 0 until (scenario.totalWeeks - 1)) {
            val weekStart = firstMonday.plusWeeks(w.toLong())
            for (d in 0..6) {
                val date = weekStart.plusDays(d.toLong())
                
                // 80% 확률로 기록 (가끔 빠진 날 생성)
                if (random.nextFloat() < 0.85f) {
                    val level = random.nextInt(1, 6)
                    val mission = random.nextFloat() < 0.4f // 40% 확률로 미션 완료
                    
                    emotionRecordRepository.insert(
                        EmotionRecordEntity(
                            emotionLevel     = level,
                            memo             = "과거 기록 - ${w + 1}주차 ${d + 1}일째",
                            missionCompleted = mission,
                            recordedAt       = date.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()
                        )
                    )
                }
            }
        }

        // 현재 주차 (이번 주) 월요일부터 어제까지 채우기
        for (d in 0..6) {
            val date = thisMonday.plusDays(d.toLong())
            if (date.isAfter(today.minusDays(1))) break
            
            if (random.nextFloat() < 0.9f) {
                val level = random.nextInt(2, 6)
                val mission = random.nextFloat() < 0.5f
                emotionRecordRepository.insert(
                    EmotionRecordEntity(
                        emotionLevel     = level,
                        memo             = "이번 주 기록 - $date",
                        missionCompleted = mission,
                        recordedAt       = date.atTime(15, 0).atZone(zone).toInstant().toEpochMilli()
                    )
                )
            }
        }
        
        // 오늘은 기록 안 함 (QuestMapScreen에서 TODAY/미완료 상태를 확인하기 위함)
    }
}
