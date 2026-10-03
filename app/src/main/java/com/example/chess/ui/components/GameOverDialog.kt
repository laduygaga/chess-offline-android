package com.example.chess.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.chess.ui.theme.CardBackground
import com.example.chess.ui.theme.GoldAccent

@Composable
fun GameOverDialog(
    gameStatus: GameStatus,
    winner: PieceColor?,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    val titleText = when (gameStatus) {
        GameStatus.CHECKMATE -> "Checkmate!"
        GameStatus.STALEMATE -> "Stalemate!"
        GameStatus.DRAW_FIFTY_MOVES -> "Draw (50-move rule)"
        GameStatus.DRAW_INSUFFICIENT_MATERIAL -> "Draw (Insufficient material)"
        GameStatus.RESIGNED -> "Resignation"
        GameStatus.TIME_OUT -> "Time Out!"
        else -> "Game Over"
    }

    val subtitleText = when {
        winner != null -> "${if (winner == PieceColor.WHITE) "White" else "Black"} Wins!"
        else -> "It's a Draw!"
    }

    AlertDialog(
        onDismissRequest = { },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    color = GoldAccent,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitleText,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
            ) {
                Text(text = "Play Again", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onHome,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text(text = "Main Menu", color = Color.White)
            }
        },
        containerColor = CardBackground
    )
}
