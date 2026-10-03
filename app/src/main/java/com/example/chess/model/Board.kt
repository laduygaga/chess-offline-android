package com.example.chess.model

data class Board(
    val grid: Array<Array<Piece?>> = Array(8) { Array(8) { null } },
    val enPassantTarget: Position? = null,
    val whiteCanCastleKingSide: Boolean = true,
    val whiteCanCastleQueenSide: Boolean = true,
    val blackCanCastleKingSide: Boolean = true,
    val blackCanCastleQueenSide: Boolean = true
) {
    operator fun get(position: Position): Piece? =
        if (position.isValid()) grid[position.row][position.col] else null

    operator fun get(row: Int, col: Int): Piece? =
        if (row in 0..7 && col in 0..7) grid[row][col] else null

    fun copyBoard(): Board {
        val newGrid = Array(8) { r ->
            Array(8) { c ->
                grid[r][c]?.copy()
            }
        }
        return Board(
            grid = newGrid,
            enPassantTarget = enPassantTarget,
            whiteCanCastleKingSide = whiteCanCastleKingSide,
            whiteCanCastleQueenSide = whiteCanCastleQueenSide,
            blackCanCastleKingSide = blackCanCastleKingSide,
            blackCanCastleQueenSide = blackCanCastleQueenSide
        )
    }

    fun findKing(color: PieceColor): Position? {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = grid[r][c]
                if (p != null && p.type == PieceType.KING && p.color == color) {
                    return Position(r, c)
                }
            }
        }
        return null
    }

    companion object {
        fun createStandardBoard(): Board {
            val grid = Array<Array<Piece?>>(8) { Array(8) { null } }

            // Black major pieces (row 0)
            grid[0][0] = Piece(PieceType.ROOK, PieceColor.BLACK)
            grid[0][1] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
            grid[0][2] = Piece(PieceType.BISHOP, PieceColor.BLACK)
            grid[0][3] = Piece(PieceType.QUEEN, PieceColor.BLACK)
            grid[0][4] = Piece(PieceType.KING, PieceColor.BLACK)
            grid[0][5] = Piece(PieceType.BISHOP, PieceColor.BLACK)
            grid[0][6] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
            grid[0][7] = Piece(PieceType.ROOK, PieceColor.BLACK)

            // Black pawns (row 1)
            for (c in 0..7) {
                grid[1][c] = Piece(PieceType.PAWN, PieceColor.BLACK)
            }

            // White pawns (row 6)
            for (c in 0..7) {
                grid[6][c] = Piece(PieceType.PAWN, PieceColor.WHITE)
            }

            // White major pieces (row 7)
            grid[7][0] = Piece(PieceType.ROOK, PieceColor.WHITE)
            grid[7][1] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
            grid[7][2] = Piece(PieceType.BISHOP, PieceColor.WHITE)
            grid[7][3] = Piece(PieceType.QUEEN, PieceColor.WHITE)
            grid[7][4] = Piece(PieceType.KING, PieceColor.WHITE)
            grid[7][5] = Piece(PieceType.BISHOP, PieceColor.WHITE)
            grid[7][6] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
            grid[7][7] = Piece(PieceType.ROOK, PieceColor.WHITE)

            return Board(grid)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (!grid.contentDeepEquals(other.grid)) return false
        if (enPassantTarget != other.enPassantTarget) return false
        if (whiteCanCastleKingSide != other.whiteCanCastleKingSide) return false
        if (whiteCanCastleQueenSide != other.whiteCanCastleQueenSide) return false
        if (blackCanCastleKingSide != other.blackCanCastleKingSide) return false
        if (blackCanCastleKingSide != other.blackCanCastleKingSide) return false

        return true
    }

    override fun hashCode(): Int {
        var result = grid.contentDeepHashCode()
        result = 31 * result + (enPassantTarget?.hashCode() ?: 0)
        result = 31 * result + whiteCanCastleKingSide.hashCode()
        result = 31 * result + whiteCanCastleQueenSide.hashCode()
        result = 31 * result + blackCanCastleKingSide.hashCode()
        result = 31 * result + blackCanCastleQueenSide.hashCode()
        return result
    }
}
