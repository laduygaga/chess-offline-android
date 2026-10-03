package com.example.chess.model

data class Position(
    val row: Int,
    val col: Int
) {
    fun isValid(): Boolean = row in 0..7 && col in 0..7

    fun toAlgebraic(): String {
        val file = ('a' + col)
        val rank = 8 - row
        return "$file$rank"
    }

    companion object {
        fun fromAlgebraic(algebraic: String): Position? {
            if (algebraic.length != 2) return null
            val col = algebraic[0] - 'a'
            val rank = algebraic[1].digitToIntOrNull() ?: return null
            val row = 8 - rank
            val pos = Position(row, col)
            return if (pos.isValid()) pos else null
        }
    }
}
