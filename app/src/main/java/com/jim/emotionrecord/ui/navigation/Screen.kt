package com.jim.emotionrecord.ui.navigation

sealed class Screen(val route: String) {
    // 공통 랜딩
    data object Landing : Screen("landing")

    // 재밌는 감정 기록 (fun prototype)
    data object Home   : Screen("home")
    data object Record : Screen("record")
    data object Graph  : Screen("graph")

    // 퀘스트 감정 기록 (quest prototype)
    data object QuestRouter : Screen("quest_router")
    data object QuestRecord : Screen("quest_record") {
        // fromMap=true 일 때 X 버튼 표시 (지도에서 진입)
        fun withFromMap(fromMap: Boolean) = if (fromMap) "$route?fromMap=true" else route
        const val COMPOSABLE_ROUTE = "quest_record?fromMap={fromMap}"
    }
    data object QuestMap : Screen("quest_map") {
        // 기록/미션 완료 직후 착지 애니메이션을 재생할 때 사용
        fun withJustStamped() = "$route?justStamped=true"
        const val COMPOSABLE_ROUTE = "quest_map?justStamped={justStamped}"
    }
    data object QuestGraph : Screen("quest_graph")
    data object QuestMissionBreath    : Screen("quest_mission_breath/{recordId}")
    data object QuestMissionGratitude : Screen("quest_mission_gratitude/{recordId}/{question}")
    data object QuestMissionWarm      : Screen("quest_mission_warm/{recordId}")
}
