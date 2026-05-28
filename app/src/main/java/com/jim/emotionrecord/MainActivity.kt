package com.jim.emotionrecord

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jim.emotionrecord.domain.usecase.DecideStartDestinationUseCase
import com.jim.emotionrecord.domain.usecase.StartDestination
import com.jim.emotionrecord.quest.ui.QuestRouterScreen
import com.jim.emotionrecord.quest.ui.map.QuestMapScreen
import com.jim.emotionrecord.quest.ui.record.QuestRecordScreen
import com.jim.emotionrecord.ui.graph.GraphScreen
import com.jim.emotionrecord.ui.home.HomeScreen
import com.jim.emotionrecord.ui.landing.LandingScreen
import com.jim.emotionrecord.ui.navigation.Screen
import com.jim.emotionrecord.ui.record.RecordScreen
import com.jim.emotionrecord.ui.theme.EmotionRecordTheme
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
                        composable(Screen.QuestRecord.route) {
                            QuestRecordScreen(
                                fromMap = false,
                                onNavigateToMap = {
                                    navController.navigate(Screen.QuestMap.route) {
                                        popUpTo(Screen.QuestRecord.route) { inclusive = true }
                                    }
                                },
                                onClose = { navController.popBackStack() }
                            )
                        }
                        composable(Screen.QuestMap.route) {
                            QuestMapScreen(
                                onNavigateToRecord = {
                                    navController.navigate(Screen.QuestRecord.route)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
