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
import com.jim.emotionrecord.ui.graph.GraphScreen
import com.jim.emotionrecord.ui.home.HomeScreen
import com.jim.emotionrecord.ui.navigation.Screen
import com.jim.emotionrecord.ui.record.RecordScreen
import com.jim.emotionrecord.ui.theme.EmotionRecordTheme
import dagger.hilt.android.AndroidEntryPoint
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
                    var startDestination by remember { mutableStateOf<String?>(null) }

                    LaunchedEffect(Unit) {
                        val dest = decideStartDestinationUseCase()
                        startDestination = when (dest) {
                            StartDestination.RECORD -> Screen.Record.route
                            StartDestination.HOME   -> Screen.Home.route
                        }
                    }

                    if (startDestination != null) {
                        val navController = rememberNavController()

                        NavHost(
                            navController    = navController,
                            startDestination = startDestination!!
                        ) {
                            composable(Screen.Record.route) {
                                RecordScreen(
                                    onNavigateToHome = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Record.route) { inclusive = true }
                                        }
                                    },
                                    // 첫 진입(스플래시→기록) 시에는 닫기 버튼 없음
                                    showCloseButton = startDestination != Screen.Record.route
                                )
                            }
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
                            composable(Screen.Graph.route) {
                                GraphScreen(
                                    onNavigateBack = {
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
