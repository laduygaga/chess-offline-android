package com.example.chess.model

enum class MoveQuality(val label: String, val symbol: String) {
    BEST("Best move", "★"),
    GOOD("Good move", "✓"),
    INACCURACY("Inaccuracy", "?!"),
    MISTAKE("Mistake", "?"),
    BLUNDER("Blunder", "??")
}

data class Move(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val isCastling: Boolean = false,
    val isEnPassant: Boolean = false,
    val promotionPieceType: PieceType? = null,
    val sanNotation: String = "",
    val evalCentipawns: Int? = null,
    val moveQuality: MoveQuality? = null
)
