package com.jim.emotionrecord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jim.emotionrecord.domain.usecase.DecideStartDestinationUseCase
import com.jim.emotionrecord.domain.usecase.StartDestination
import com.jim.emotionrecord.quest.ui.QuestRouterScreen
import com.jim.emotionrecord.quest.ui.map.QuestMapScreen
import com.jim.emotionrecord.quest.ui.mission.MissionBreathScreen
import com.jim.emotionrecord.quest.ui.mission.MissionGratitudeScreen
import com.jim.emotionrecord.quest.ui.mission.MissionWarmScreen
import com.jim.emotionrecord.quest.ui.record.QuestRecordScreen
import com.jim.emotionrecord.quest.ui.graph.QuestGraphScreen
import com.jim.emotionrecord.ui.graph.GraphScreen
import com.jim.emotionrecord.ui.home.HomeScreen
import com.jim.emotionrecord.ui.landing.LandingScreen
import com.jim.emotionrecord.ui.navigation.Screen
import com.jim.emotionrecord.ui.record.RecordScreen
import com.jim.emotionrecord.ui.theme.EmotionRecordTheme
import com.jim.emotionrecord.ui.theme.QBg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var decideStartDestinationUseCase: DecideStartDestinationUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmotionRecordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController    = navController,
                        startDestination = Screen.Landing.route
                    ) {
                        // ── 앱 선택 랜딩 ──────────────────────────────────
                        composable(Screen.Landing.route) {
                            LandingScreen(
                                onSelectFun = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val dest = decideStartDestinationUseCase()
                                        val startDestination = when (dest) {
                                            StartDestination.RECORD -> Screen.Record.route
                                            StartDestination.HOME   -> Screen.Home.route
                                        }

                                        navController.navigate(startDestination)
                                    }
                                },
                                onSelectQuest = {
                                    navController.navigate(Screen.QuestRouter.route)
                                }
                            )
                        }

                        // ── 재밌는 감정 기록 앱 ───────────────────────────
                        composable(Screen.Home.route) {
                            HomeScreen(
                                onNavigateToRecord = {
                                    navController.navigate(Screen.Record.route)
                                },
                                onNavigateToGraph = {
                                    navController.navigate(Screen.Graph.route)
                                }
                            )
                        }
                        composable(Screen.Record.route) {
                            RecordScreen(
                                onNavigateToHome = {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Record.route) { inclusive = true }
                                    }
                                },
                                showCloseButton = true
                            )
                        }
                        composable(Screen.Graph.route) {
                            GraphScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // ── 퀘스트 감정 기록 앱 ───────────────────────────
                        composable(Screen.QuestRouter.route) {
                            QuestRouterScreen(
                                onNavigateToRecord = {
                                    navController.navigate(Screen.QuestRecord.route) {
                                        popUpTo(Screen.QuestRouter.route) { inclusive = true }
                                    }
                                },
                                onNavigateToMap = {
                                    navController.navigate(Screen.QuestMap.route) {
                                        popUpTo(Screen.QuestRouter.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        // QuestRecord: fromMap 쿼리 파라미터로 X 버튼 제어
                        composable(
                            route = Screen.QuestRecord.COMPOSABLE_ROUTE,
                            arguments = listOf(
                                navArgument("fromMap") { type = NavType.BoolType; defaultValue = false }
                            )
                        ) { backStackEntry ->
                            val fromMap = backStackEntry.arguments?.getBoolean("fromMap") ?: false
                            QuestRecordScreen(
                                fromMap = fromMap,
                                onNavigateToMap = {
                                    // 기록 완료("다음에 하기") → 착지 애니메이션 재생
                                    navController.navigate(Screen.QuestMap.withJustStamped()) {
                                        popUpTo(Screen.Landing.route) { inclusive = false }
                                    }
                                },
                                onNavigateToMission = { level, recordId ->
                                    val route = when (level) {
                                        1, 2 -> Screen.QuestMissionBreath.route.replace("{recordId}", recordId.toString())
                                        3    -> Screen.QuestMissionGratitude.route
                                                    .replace("{recordId}", recordId.toString())
                                                    .replace("{question}", "작은 성취")
                                        4, 5 -> Screen.QuestMissionGratitude.route
                                                    .replace("{recordId}", recordId.toString())
                                                    .replace("{question}", "감사한 것")
                                        else -> Screen.QuestMissionWarm.route.replace("{recordId}", recordId.toString())
                                    }
                                    navController.navigate(route)
                                },
                                onClose = { navController.popBackStack() }
                                )
                                }

                                // QuestMap: justStamped 쿼리 파라미터로 착지 애니메이션 제어
                        composable(
                            route = Screen.QuestMap.COMPOSABLE_ROUTE,
                            arguments = listOf(
                                navArgument("justStamped") { type = NavType.BoolType; defaultValue = false }
                            )
                        ) { backStackEntry ->
                            val justStamped = backStackEntry.arguments?.getBoolean("justStamped") ?: false
                            QuestMapScreen(
                                justStamped = justStamped,
                                onNavigateToRecord = {
                                    // 지도→기록: fromMap=true → X 버튼 노출
                                    navController.navigate(Screen.QuestRecord.withFromMap(true))
                                },
                                onNavigateToGraph = {
                                    navController.navigate(Screen.QuestGraph.route)
                                }
                            )
                        }

                        composable(Screen.QuestGraph.route) {
                            QuestGraphScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // ── 퀘스트 미션 ──────────────────────────────────
                        composable(
                            route = Screen.QuestMissionBreath.route,
                            arguments = listOf(navArgument("recordId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: -1L
                            MissionBreathScreen(
                                recordId = recordId,
                                onNavigateToMap = {
                                    // 미션 완료 → 착지 애니메이션 재생
                                    navController.navigate(Screen.QuestMap.withJustStamped()) {
                                        popUpTo(Screen.Landing.route) { inclusive = false }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = Screen.QuestMissionGratitude.route,
                            arguments = listOf(
                                navArgument("recordId") { type = NavType.StringType },
                                navArgument("question") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: -1L
                            val question = backStackEntry.arguments?.getString("question") ?: "감사한 것"
                            MissionGratitudeScreen(
                                recordId = recordId,
                                question = question,
                                onNavigateToMap = {
                                    navController.navigate(Screen.QuestMap.withJustStamped()) {
                                        popUpTo(Screen.Landing.route) { inclusive = false }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = Screen.QuestMissionWarm.route,
                            arguments = listOf(navArgument("recordId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val recordId = backStackEntry.arguments?.getString("recordId")?.toLongOrNull() ?: -1L
                            MissionWarmScreen(
                                recordId = recordId,
                                onNavigateToMap = {
                                    navController.navigate(Screen.QuestMap.withJustStamped()) {
                                        popUpTo(Screen.Landing.route) { inclusive = false }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
