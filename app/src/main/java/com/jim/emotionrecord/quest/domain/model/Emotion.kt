package com.jim.emotionrecord.quest.domain.model

enum class Emotion(val level: Int, val label: String) {
    VERY_BAD(1, "매우 나쁨"),
    BAD(2, "나쁨"),
    NEUTRAL(3, "보통"),
    GOOD(4, "좋음"),
    VERY_GOOD(5, "매우 좋음");

    companion object {
        fun fromLevel(level: Int): Emotion = entries.first { it.level == level }
    }
}
