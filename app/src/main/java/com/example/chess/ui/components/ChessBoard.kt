package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Board
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import com.example.chess.ui.theme.CheckSquare
import com.example.chess.ui.theme.DarkSquare
import com.example.chess.ui.theme.LastMoveSquare
import com.example.chess.ui.theme.LegalMoveCapture
import com.example.chess.ui.theme.LegalMoveDot
import com.example.chess.ui.theme.LightSquare
import com.example.chess.ui.theme.SelectedSquare

@Composable
fun ChessBoard(
    board: Board,
    selectedPosition: Position?,
    legalMoves: List<Position>,
    lastMove: Move?,
    isCheck: Boolean,
    currentTurn: PieceColor,
    isFlipped: Boolean = false,
    onSquareClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val tileSize = maxWidth / 8

        val kingInCheckPos = if (isCheck) board.findKing(currentTurn) else null

        Column(modifier = Modifier.fillMaxSize()) {
            val rows = if (isFlipped) (7 downTo 0) else (0..7)
            for (r in rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val cols = if (isFlipped) (7 downTo 0) else (0..7)
                    for (c in cols) {
                        val pos = Position(r, c)
                        val piece = board[pos]
                        val isLightSquare = (r + c) % 2 == 0
                        val baseColor = if (isLightSquare) LightSquare else DarkSquare

                        val isSelected = pos == selectedPosition
                        val isLegalMove = legalMoves.contains(pos)
                        val isLastMoveSquare = lastMove != null && (pos == lastMove.from || pos == lastMove.to)
                        val isCheckSquare = pos == kingInCheckPos

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    when {
                                        isCheckSquare -> CheckSquare
                                        isSelected -> SelectedSquare
                                        isLastMoveSquare -> LastMoveSquare
                                        else -> baseColor
                                    }
                                )
                                .clickable { onSquareClick(pos) },
                            contentAlignment = Alignment.Center
                        ) {
                            // Coordinate Labels
                            val showRankLabel = if (isFlipped) c == 7 else c == 0
                            val showFileLabel = if (isFlipped) r == 0 else r == 7

                            if (showRankLabel) {
                                Text(
                                    text = (8 - r).toString(),
                                    color = if (isLightSquare) DarkSquare else LightSquare,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(start = 2.dp, top = 1.dp)
                                )
                            }

                            if (showFileLabel) {
                                Text(
                                    text = ('a' + c).toString(),
                                    color = if (isLightSquare) DarkSquare else LightSquare,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 2.dp, bottom = 1.dp)
                                )
                            }

                            // Piece Rendering
                            if (piece != null) {
                                PieceView(piece = piece, size = tileSize)
                            }

                            // Legal Move Highlights
                            if (isLegalMove) {
                                if (piece != null) {
                                    // Ring highlight for capture
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                            .border(width = 3.dp, color = LegalMoveCapture, shape = CircleShape)
                                    )
                                } else {
                                    // Dot highlight for empty destination
                                    Box(
                                        modifier = Modifier
                                            .size(tileSize * 0.3f)
                                            .background(color = LegalMoveDot, shape = CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
