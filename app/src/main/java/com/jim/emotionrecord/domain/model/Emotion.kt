package com.jim.emotionrecord.domain.model

enum class Emotion(val score: Int, val label: String, val emoji: String) {
    VERY_BAD(1, "끔찍한", "😢"),
    BAD(2, "나쁜", "😟"),
    NEUTRAL(3, "괜찮은", "😐"),
    GOOD(4, "좋은", "🙂"),
    VERY_GOOD(5, "훌륭한", "😄");

    companion object {
        fun fromScore(score: Int): Emotion = entries.first { it.score == score }
    }
}
