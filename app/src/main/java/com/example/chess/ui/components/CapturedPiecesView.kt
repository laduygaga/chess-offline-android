package com.example.chess.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.ui.theme.GoldAccent

@Composable
fun CapturedPiecesView(
    capturedByWhite: List<Piece>,
    capturedByBlack: List<Piece>,
    modifier: Modifier = Modifier
) {
    val whiteMaterial = capturedByWhite.sumOf { it.type.value }
    val blackMaterial = capturedByBlack.sumOf { it.type.value }
    val advantage = whiteMaterial - blackMaterial

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Black pieces captured by White
        Row(verticalAlignment = Alignment.CenterVertically) {
            val sortedBlackCaptured = capturedByWhite.sortedBy { it.type.value }
            for (p in sortedBlackCaptured) {
                Text(text = p.unicodeSymbol(), fontSize = 16.sp, color = Color.White)
            }
            if (advantage > 0) {
                Text(
                    text = " +$advantage",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // White pieces captured by Black
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (advantage < 0) {
                Text(
                    text = "+${-advantage} ",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            val sortedWhiteCaptured = capturedByBlack.sortedBy { it.type.value }
            for (p in sortedWhiteCaptured) {
                Text(text = p.unicodeSymbol(), fontSize = 16.sp, color = Color(0xFF1E1E1E))
            }
        }
    }
}
