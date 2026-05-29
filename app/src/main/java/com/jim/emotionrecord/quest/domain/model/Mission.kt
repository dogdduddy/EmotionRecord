package com.jim.emotionrecord.quest.domain.model

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // breath, gratitude, warm, walk
    val type: MissionType
)

enum class MissionType {
    BREATH, GRATITUDE, WARM, WALK
}

object MissionProvider {
    fun getMissionForEmotion(level: Int): Mission {
        return when (level) {
            1, 2 -> listOf(
                Mission(
                    id = "warm_1",
                    title = "따뜻한 한마디 받기",
                    description = "오늘은 그저 쉬어가도 괜찮아요. 짧은 위로를 읽어볼래요?",
                    icon = "warm",
                    type = MissionType.WARM
                ),
                Mission(
                    id = "breath_1",
                    title = "3분 가이드 호흡",
                    description = "잠시 숨을 고르며 마음을 가라앉혀보세요.",
                    icon = "breath",
                    type = MissionType.BREATH
                )
            ).random()

            3 -> Mission(
                id = "achievement_1",
                title = "작은 성취 기록",
                description = "오늘 내가 해낸 작은 일을 한 줄 적어볼까요?",
                icon = "gratitude",
                type = MissionType.GRATITUDE
            )
            4, 5 -> Mission(
                id = "gratitude_1",
                title = "감사 기록",
                description = "오늘 감사한 것 하나를 짧게 적어볼까요?",
                icon = "gratitude",
                type = MissionType.GRATITUDE
            )
            else -> Mission(
                id = "warm_1",
                title = "따뜻한 한마디 받기",
                description = "오늘은 그저 쉬어가도 괜찮아요. 짧은 위로를 읽어볼래요?",
                icon = "warm",
                type = MissionType.WARM
            )
        }
    }
}
