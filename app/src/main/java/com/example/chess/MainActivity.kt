package com.example.chess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chess.ui.screens.GameScreen
import com.example.chess.ui.screens.HomeScreen
import com.example.chess.ui.theme.AppBackground
import com.example.chess.ui.theme.ChessTheme
import com.example.chess.viewmodel.ChessViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackground
                ) {
                    val navController = rememberNavController()
                    val chessViewModel: ChessViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                onStartGame = { mode, difficulty, playerColor, timeLimit ->
                                    chessViewModel.startNewGame(
                                        gameMode = mode,
                                        aiDifficulty = difficulty,
                                        playerColor = playerColor,
                                        timeLimitMinutes = timeLimit
                                    )
                                    navController.navigate("game")
                                }
                            )
                        }
                        composable("game") {
                            GameScreen(
                                viewModel = chessViewModel,
                                onHomeClick = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
