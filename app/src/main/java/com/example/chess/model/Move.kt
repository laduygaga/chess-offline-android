package com.example.chess.model

data class Move(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false,
    val promotionPieceType: PieceType? = null,
    val sanNotation: String = ""
)
