package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Move
import com.example.chess.model.MoveQuality
import com.example.chess.ui.theme.CardBackground
import com.example.chess.ui.theme.LichessBlue
import com.example.chess.ui.theme.QualityBest
import com.example.chess.ui.theme.QualityBlunder
import com.example.chess.ui.theme.QualityGood
import com.example.chess.ui.theme.QualityInaccuracy
import com.example.chess.ui.theme.QualityMistake

@Composable
fun MoveHistoryView(
    moves: List<Move>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(moves.size) {
        if (moves.isNotEmpty()) {
            listState.animateScrollToItem(moves.size - 1)
        }
    }

    val movePairs = moves.chunked(2)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(CardBackground, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp)
    ) {
        if (moves.isEmpty()) {
            Text(
                text = "Moves will appear here...",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        } else {
            LazyRow(
                state = listState,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(movePairs) { index, pair ->
                    val moveNum = index + 1
                    val whiteMove = pair.getOrNull(0)
                    val blackMove = pair.getOrNull(1)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$moveNum.",
                            color = LichessBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        if (whiteMove != null) {
                            Text(
                                text = " ${whiteMove.sanNotation}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            if (whiteMove.moveQuality != null) {
                                QualitySymbol(quality = whiteMove.moveQuality)
                            }
                        }
                        if (blackMove != null) {
                            Text(
                                text = " ${blackMove.sanNotation}",
                                color = Color.LightGray,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            if (blackMove.moveQuality != null) {
                                QualitySymbol(quality = blackMove.moveQuality)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QualitySymbol(quality: MoveQuality) {
    val (color, symbol) = when (quality) {
        MoveQuality.BEST -> Pair(QualityBest, "★")
        MoveQuality.GOOD -> Pair(QualityGood, "✓")
        MoveQuality.INACCURACY -> Pair(QualityInaccuracy, "?!")
        MoveQuality.MISTAKE -> Pair(QualityMistake, "?")
        MoveQuality.BLUNDER -> Pair(QualityBlunder, "??")
    }
    Text(
        text = symbol,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 2.dp)
    )
}
