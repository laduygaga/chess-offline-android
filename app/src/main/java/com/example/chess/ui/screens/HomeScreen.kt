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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.AIDifficulty
import com.example.chess.model.GameMode
import com.example.chess.model.PieceColor
import com.example.chess.ui.theme.AppBackground
import com.example.chess.ui.theme.CardBackground
import com.example.chess.ui.theme.LichessBlue
import com.example.chess.ui.theme.LichessGreen

@Composable
fun HomeScreen(
    onStartGame: (GameMode, AIDifficulty, PieceColor, Int) -> Unit
) {
    var selectedMode by remember { mutableStateOf(GameMode.VS_AI) }
    var selectedDifficulty by remember { mutableStateOf(AIDifficulty.MEDIUM) }
    var selectedPlayerColor by remember { mutableStateOf(PieceColor.WHITE) }
    var selectedTimeMinutes by remember { mutableStateOf(10) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "♔ LICHESS STYLE CHESS ♚",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Play Offline with Live Move Evaluation",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "GAME MODE",
                    color = LichessBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedMode == GameMode.VS_AI,
                        onClick = { selectedMode = GameMode.VS_AI },
                        label = { Text("🤖 Play vs AI") },
                        colors = chipColors()
                    )
                    FilterChip(
                        selected = selectedMode == GameMode.PASS_AND_PLAY,
                        onClick = { selectedMode = GameMode.PASS_AND_PLAY },
                        label = { Text("👥 Pass & Play") },
                        colors = chipColors()
                    )
                }

                if (selectedMode == GameMode.VS_AI) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AI DIFFICULTY",
                        color = LichessBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AIDifficulty.entries.forEach { difficulty ->
                            FilterChip(
                                selected = selectedDifficulty == difficulty,
                                onClick = { selectedDifficulty = difficulty },
                                label = { Text(difficulty.name) },
                                colors = chipColors()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PLAY AS",
                        color = LichessBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedPlayerColor == PieceColor.WHITE,
                            onClick = { selectedPlayerColor = PieceColor.WHITE },
                            label = { Text("♔ White") },
                            colors = chipColors()
                        )
                        FilterChip(
                            selected = selectedPlayerColor == PieceColor.BLACK,
                            onClick = { selectedPlayerColor = PieceColor.BLACK },
                            label = { Text("♚ Black") },
                            colors = chipColors()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TIMER LIMIT",
                    color = LichessBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 0).forEach { mins ->
                        FilterChip(
                            selected = selectedTimeMinutes == mins,
                            onClick = { selectedTimeMinutes = mins },
                            label = { Text(if (mins == 0) "Unlimited" else "${mins}m") },
                            colors = chipColors()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                onStartGame(selectedMode, selectedDifficulty, selectedPlayerColor, selectedTimeMinutes)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LichessGreen)
        ) {
            Text(
                text = "PLAY GAME",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun chipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = LichessBlue,
    selectedLabelColor = Color.White,
    containerColor = Color(0xFF2A2825),
    labelColor = Color.White
)
