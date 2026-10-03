package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
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
import com.example.chess.ui.theme.CardBackground
import com.example.chess.ui.theme.GoldAccent

@Composable
fun PromotionDialog(
    color: PieceColor,
    onSelect: (PieceType) -> Unit
) {
    val choices = listOf(
        PieceType.QUEEN,
        PieceType.ROOK,
        PieceType.BISHOP,
        PieceType.KNIGHT
    )

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "Promote Pawn",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (type in choices) {
                    val piece = Piece(type = type, color = color)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(GoldAccent.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp))
                            .clickable { onSelect(type) },
                        contentAlignment = Alignment.Center
                    ) {
                        PieceView(piece = piece, size = 44.dp)
                    }
                }
            }
        },
        confirmButton = { },
        containerColor = CardBackground
    )
}
