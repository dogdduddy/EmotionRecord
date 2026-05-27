package com.jim.emotionrecord.domain.usecase

import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import com.jim.emotionrecord.data.repository.EmotionRepository
import com.jim.emotionrecord.data.repository.UserMetaRepository
import javax.inject.Inject
import kotlin.random.Random

class SeedDemoDataUseCase @Inject constructor(
    private val emotionRepository: EmotionRepository,
    private val userMetaRepository: UserMetaRepository
) {
    suspend operator fun invoke(weeks: Int = 4) {
        // 기존 데이터 삭제
        emotionRepository.deleteAll()
        userMetaRepository.deleteAll()

        val now = System.currentTimeMillis()
        val firstRecordedAt = now - weeks.toLong() * 7 * 24 * 60 * 60 * 1000
        val records = mutableListOf<EmotionCheckEntity>()

        for (day in 0 until weeks * 7) {
            val dayBase = firstRecordedAt + day * 24 * 60 * 60 * 1000L
            val countForDay = Random.nextInt(1, 4) // 하루 1~3건
            repeat(countForDay) {
                records += EmotionCheckEntity(
                    emotionScore = Random.nextInt(1, 6),
                    recordedAt = dayBase + Random.nextLong(0, 24 * 60 * 60 * 1000),
                    memo = if (Random.nextBoolean()) demoMemos.random() else ""
                )
            }
        }

        emotionRepository.insertAll(records)
        userMetaRepository.setFirstRecordedAt(firstRecordedAt)
    }

    private val demoMemos = listOf(
        "오늘 날씨가 좋았다",
        "동료랑 점심이 즐거웠다",
        "업무가 잘 풀렸다",
        "피곤한 하루였다",
        "산책을 오래 했다",
        "좋은 책을 읽었다",
        "운동을 못했다",
        "친구를 만났다",
        "혼자 있고 싶은 날",
        "커피가 맛있었다"
    )
}
