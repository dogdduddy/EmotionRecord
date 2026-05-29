package com.jim.emotionrecord.ui.navigation

sealed class Screen(val route: String) {
    // 공통 랜딩
    data object Landing : Screen("landing")

    // 재밌는 감정 기록 (fun prototype)
    data object Home   : Screen("home")
    data object Record : Screen("record")
    data object Graph  : Screen("graph")

    // 퀘스트 감정 기록 (quest prototype)
    data object QuestRouter           : Screen("quest_router")
    data object QuestRecord           : Screen("quest_record")
    data object QuestMap              : Screen("quest_map")
    data object QuestMissionBreath    : Screen("quest_mission_breath/{recordId}")
    data object QuestMissionGratitude : Screen("quest_mission_gratitude/{recordId}/{question}")
    data object QuestMissionWarm      : Screen("quest_mission_warm/{recordId}")
}
