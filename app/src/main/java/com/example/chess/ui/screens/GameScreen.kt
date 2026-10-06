package com.example.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.chess.model.MoveQuality
import com.example.chess.model.PieceColor
import com.example.chess.ui.components.CapturedPiecesView
import com.example.chess.ui.components.ChessBoard
import com.example.chess.ui.components.ChessClock
import com.example.chess.ui.components.EvalBar
import com.example.chess.ui.components.GameOverDialog
import com.example.chess.ui.components.MoveHistoryView
import com.example.chess.ui.components.PromotionDialog
import com.example.chess.ui.theme.AppBackground
import com.example.chess.ui.theme.CardBackground
import com.example.chess.ui.theme.LichessBlue
import com.example.chess.ui.theme.QualityBest
import com.example.chess.ui.theme.QualityBlunder
import com.example.chess.ui.theme.QualityGood
import com.example.chess.ui.theme.QualityInaccuracy
import com.example.chess.ui.theme.QualityMistake
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
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onHomeClick,
                colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "← Home", color = Color.White, fontSize = 13.sp)
            }

            Text(
                text = if (state.gameMode == GameMode.VS_AI) "vs AI (${state.aiDifficulty.name})" else "Pass & Play",
                color = LichessBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { viewModel.toggleEvalBar() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (state.showEvalBar) LichessBlue else CardBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "📊 Bar", color = Color.White, fontSize = 13.sp)
                }

                Button(
                    onClick = { isBoardFlipped = !isBoardFlipped },
                    colors = ButtonDefaults.buttonColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "🔄", color = Color.White, fontSize = 13.sp)
                }
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

        val lastQuality = state.lastMoveQuality
        if (lastQuality != null) {
            val (badgeColor, badgeText) = when (lastQuality) {
                MoveQuality.BEST -> Pair(QualityBest, "★ Best Move")
                MoveQuality.GOOD -> Pair(QualityGood, "✓ Good Move")
                MoveQuality.INACCURACY -> Pair(QualityInaccuracy, "?! Inaccuracy")
                MoveQuality.MISTAKE -> Pair(QualityMistake, "? Mistake")
                MoveQuality.BLUNDER -> Pair(QualityBlunder, "?? Blunder")
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.showEvalBar) {
                EvalBar(
                    evalCentipawns = state.evalCentipawns,
                    isFlipped = isBoardFlipped,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }

            ChessBoard(
                board = state.board,
                selectedPosition = state.selectedPosition,
                legalMoves = state.legalMovesForSelected,
                lastMove = state.moveHistory.lastOrNull(),
                isCheck = state.isCheck,
                currentTurn = state.currentTurn,
                isFlipped = isBoardFlipped,
                bestSuggestedMove = state.bestSuggestedMove,
                onSquareClick = { pos -> viewModel.onSquareSelected(pos) },
                modifier = Modifier.weight(1f)
            )
        }

        // Bottom Clock
        ChessClock(
            timeSeconds = bottomTime,
            isActive = state.currentTurn == bottomPlayerColor && state.gameStatus == GameStatus.IN_PROGRESS,
            playerTitle = bottomTitle,
            playerColor = bottomPlayerColor
        )

        // Move History Log
        MoveHistoryView(moves = state.moveHistory)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.evaluateCurrentPosition() },
                modifier = Modifier
                    .weight(1.2f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LichessBlue)
            ) {
                Text(
                    text = if (state.isEvaluating) "Evaluating..." else "🔍 Evaluate Move",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { viewModel.undoMove() },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardBackground)
            ) {
                Text(text = "↩ Undo", color = Color.White, fontSize = 13.sp)
            }

            Button(
                onClick = { viewModel.resign(state.currentTurn) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B2222))
            ) {
                Text(text = "🏳 Resign", color = Color.White, fontSize = 13.sp)
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
