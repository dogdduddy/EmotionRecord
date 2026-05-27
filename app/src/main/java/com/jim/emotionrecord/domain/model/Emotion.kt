package com.jim.emotionrecord.domain.model

enum class Emotion(val score: Int, val label: String, val emoji: String) {
    VERY_BAD(1, "매우 나쁨", "😢"),
    BAD(2, "나쁨", "😟"),
    NEUTRAL(3, "보통", "😐"),
    GOOD(4, "좋음", "🙂"),
    VERY_GOOD(5, "매우 좋음", "😄");

    companion object {
        fun fromScore(score: Int): Emotion = entries.first { it.score == score }
    }
}
