package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.PieceColor
import com.example.chess.ui.theme.GoldAccent

@Composable
fun ChessClock(
    timeSeconds: Long,
    isActive: Boolean,
    playerTitle: String,
    playerColor: PieceColor,
    modifier: Modifier = Modifier
) {
    val minutes = timeSeconds / 60
    val seconds = timeSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val cardBg = if (isActive) GoldAccent.copy(alpha = 0.25f) else Color(0xFF383531)
    val textColor = if (isActive) GoldAccent else Color.White

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (playerColor == PieceColor.WHITE) "♔ " else "♚ ",
                    color = textColor,
                    fontSize = 18.sp
                )
                Text(
                    text = playerTitle,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = timeFormatted,
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
