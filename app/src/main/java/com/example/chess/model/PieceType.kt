package com.example.chess.model

enum class PieceType(val value: Int, val symbol: String) {
    PAWN(1, "P"),
    KNIGHT(3, "N"),
    BISHOP(3, "B"),
    ROOK(5, "R"),
    QUEEN(9, "Q"),
    KING(1000, "K")
}
