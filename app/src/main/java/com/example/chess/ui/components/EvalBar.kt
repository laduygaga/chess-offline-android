package com.example.chess.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.pow

@Composable
fun EvalBar(
    evalCentipawns: Int,
    isFlipped: Boolean = false,
    modifier: Modifier = Modifier
) {
    val winningProb = 1f / (1f + 10f.pow(-evalCentipawns / 400f))
    val whiteRatio = winningProb.coerceIn(0.05f, 0.95f)

    val animatedWhiteRatio by animateFloatAsState(
        targetValue = whiteRatio,
        animationSpec = tween(durationMillis = 350),
        label = "EvalBarAnimation"
    )

    val topRatio = if (isFlipped) animatedWhiteRatio else (1f - animatedWhiteRatio)
    val bottomRatio = if (isFlipped) (1f - animatedWhiteRatio) else animatedWhiteRatio

    val topColor = if (isFlipped) Color.White else Color(0xFF211F1C)
    val bottomColor = if (isFlipped) Color(0xFF211F1C) else Color.White

    val topTextColor = if (isFlipped) Color.Black else Color.White
    val bottomTextColor = if (isFlipped) Color.White else Color.Black

    val evalScoreText = formatEvalScore(evalCentipawns)

    Box(
        modifier = modifier
            .width(22.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF363431), RoundedCornerShape(4.dp))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(topRatio.coerceAtLeast(0.01f))
                    .background(topColor)
            ) {
                if (evalCentipawns < 0 && !isFlipped || evalCentipawns > 0 && isFlipped) {
                    Text(
                        text = evalScoreText,
                        color = topTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp)
                    )
                }
            }

            // Bottom Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(bottomRatio.coerceAtLeast(0.01f))
                    .background(bottomColor)
            ) {
                if (evalCentipawns >= 0 && !isFlipped || evalCentipawns <= 0 && isFlipped) {
                    Text(
                        text = evalScoreText,
                        color = bottomTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

private fun formatEvalScore(centipawns: Int): String {
    return when {
        abs(centipawns) >= 8000 -> {
            val mateIn = maxOf(1, (10000 - abs(centipawns)))
            if (centipawns > 0) "#$mateIn" else "-#$mateIn"
        }
        else -> {
            val score = centipawns / 100f
            if (score > 0) "+%.1f".format(score) else "%.1f".format(score)
        }
    }
}