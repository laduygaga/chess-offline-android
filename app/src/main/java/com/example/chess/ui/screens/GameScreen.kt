package com.example.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.GameMode
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.chess.ui.components.CapturedPiecesView
import com.example.chess.ui.components.ChessBoard
import com.example.chess.ui.components.ChessClock
import com.example.chess.ui.components.GameOverDialog
import com.example.chess.ui.components.MoveHistoryView
import com.example.chess.ui.components.PromotionDialog
import com.example.chess.ui.theme.AppBackground
import com.example.chess.ui.theme.CardBackground
import com.example.chess.ui.theme.GoldAccent
import com.example.chess.viewmodel.ChessViewModel

@Composable
fun GameScreen(
    viewModel: ChessViewModel,
    onHomeClick: () -> Unit
) {
    val state by viewModel.gameState.collectAsState()
    var isBoardFlipped by remember { mutableStateOf(state.gameMode == GameMode.VS_AI && state.aiColor == PieceColor.WHITE) }

    val topPlayerColor = if (isBoardFlipped) PieceColor.WHITE else PieceColor.BLACK
    val bottomPlayerColor = if (isBoardFlipped) PieceColor.BLACK else PieceColor.WHITE

    val topTime = if (topPlayerColor == PieceColor.WHITE) state.whiteTimeSeconds else state.blackTimeSeconds
    val bottomTime = if (bottomPlayerColor == PieceColor.WHITE) state.whiteTimeSeconds else state.blackTimeSeconds

    val topTitle = if (state.gameMode == GameMode.VS_AI && topPlayerColor == state.aiColor) "Bot (${state.aiDifficulty.name})" else "Player 2"
    val bottomTitle = if (state.gameMode == GameMode.VS_AI && bottomPlayerColor == state.aiColor) "Bot (${state.aiDifficulty.name})" else "Player 1"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onHomeClick,
                colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "← Home", color = Color.White)
            }

            Text(
                text = if (state.gameMode == GameMode.VS_AI) "vs AI (${state.aiDifficulty.name})" else "Pass & Play",
                color = GoldAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { isBoardFlipped = !isBoardFlipped },
                colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "🔄 Flip", color = Color.White)
            }
        }

        // Top Clock
        ChessClock(
            timeSeconds = topTime,
            isActive = state.currentTurn == topPlayerColor && state.gameStatus == GameStatus.IN_PROGRESS,
            playerTitle = topTitle,
            playerColor = topPlayerColor
        )

        // Captured Pieces View
        CapturedPiecesView(
            capturedByWhite = state.capturedPieces.filter { it.color == PieceColor.BLACK },
            capturedByBlack = state.capturedPieces.filter { it.color == PieceColor.WHITE }
        )

        // Interactive Chess Board
        ChessBoard(
            board = state.board,
            selectedPosition = state.selectedPosition,
            legalMoves = state.legalMovesForSelected,
            lastMove = state.moveHistory.lastOrNull(),
            isCheck = state.isCheck,
            currentTurn = state.currentTurn,
            isFlipped = isBoardFlipped,
            onSquareClick = { pos -> viewModel.onSquareSelected(pos) }
        )

        // Bottom Clock
        ChessClock(
            timeSeconds = bottomTime,
            isActive = state.currentTurn == bottomPlayerColor && state.gameStatus == GameStatus.IN_PROGRESS,
            playerTitle = bottomTitle,
            playerColor = bottomPlayerColor
        )

        // Move History Log
        MoveHistoryView(moves = state.moveHistory)

        // Action Buttons Row (Undo, Resign, Restart)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.undoMove() },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardBackground)
            ) {
                Text(text = "↩ Undo", color = Color.White)
            }

            Button(
                onClick = { viewModel.resign(state.currentTurn) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B2222))
            ) {
                Text(text = "🏳 Resign", color = Color.White)
            }
        }
    }

    // Pawn Promotion Selection Dialog
    if (state.pendingPromotionMove != null) {
        PromotionDialog(
            color = state.currentTurn,
            onSelect = { choice -> viewModel.onPromotionChoice(choice) }
        )
    }

    // Game Over Alert Dialog
    if (state.gameStatus != GameStatus.IN_PROGRESS) {
        GameOverDialog(
            gameStatus = state.gameStatus,
            winner = state.winner,
            onRestart = {
                viewModel.startNewGame(
                    gameMode = state.gameMode,
                    aiDifficulty = state.aiDifficulty,
                    playerColor = state.aiColor.opposite(),
                    timeLimitMinutes = (state.initialTimeSeconds / 60).toInt()
                )
            },
            onHome = onHomeClick
        )
    }
}
