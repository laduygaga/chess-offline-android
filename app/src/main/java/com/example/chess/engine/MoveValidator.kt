package com.example.chess.engine

import com.example.chess.model.Board
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position

object MoveValidator {

    fun getPseudoLegalMoves(board: Board, from: Position): List<Position> {
        val piece = board[from] ?: return emptyList()
        val moves = mutableListOf<Position>()

        when (piece.type) {
            PieceType.PAWN -> getPawnMoves(board, from, piece.color, moves)
            PieceType.KNIGHT -> getKnightMoves(board, from, piece.color, moves)
            PieceType.BISHOP -> getSlidingMoves(board, from, piece.color, BISHOP_DIRECTIONS, moves)
            PieceType.ROOK -> getSlidingMoves(board, from, piece.color, ROOK_DIRECTIONS, moves)
            PieceType.QUEEN -> getSlidingMoves(board, from, piece.color, QUEEN_DIRECTIONS, moves)
            PieceType.KING -> getKingMoves(board, from, piece.color, moves)
        }

        return moves
    }

    private val KNIGHT_OFFSETS = arrayOf(
        Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
        Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
    )

    private val BISHOP_DIRECTIONS = arrayOf(Pair(-1, -1), Pair(-1, 1), Pair(1, -1), Pair(1, 1))
    private val ROOK_DIRECTIONS = arrayOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))
    private val QUEEN_DIRECTIONS = BISHOP_DIRECTIONS + ROOK_DIRECTIONS
    private val KING_DIRECTIONS = QUEEN_DIRECTIONS

    private fun getPawnMoves(board: Board, from: Position, color: PieceColor, moves: MutableList<Position>) {
        val direction = if (color == PieceColor.WHITE) -1 else 1
        val startRow = if (color == PieceColor.WHITE) 6 else 1

        // 1 square forward
        val forward1 = Position(from.row + direction, from.col)
        if (forward1.isValid() && board[forward1] == null) {
            moves.add(forward1)

            // 2 squares forward from starting rank
            if (from.row == startRow) {
                val forward2 = Position(from.row + 2 * direction, from.col)
                if (forward2.isValid() && board[forward2] == null) {
                    moves.add(forward2)
                }
            }
        }

        // Standard captures
        for (colOffset in arrayOf(-1, 1)) {
            val capPos = Position(from.row + direction, from.col + colOffset)
            if (capPos.isValid()) {
                val targetPiece = board[capPos]
                if (targetPiece != null && targetPiece.color != color) {
                    moves.add(capPos)
                } else if (capPos == board.enPassantTarget) {
                    // En passant capture
                    moves.add(capPos)
                }
            }
        }
    }

    private fun getKnightMoves(board: Board, from: Position, color: PieceColor, moves: MutableList<Position>) {
        for ((dr, dc) in KNIGHT_OFFSETS) {
            val target = Position(from.row + dr, from.col + dc)
            if (target.isValid()) {
                val destPiece = board[target]
                if (destPiece == null || destPiece.color != color) {
                    moves.add(target)
                }
            }
        }
    }

    private fun getSlidingMoves(
        board: Board,
        from: Position,
        color: PieceColor,
        directions: Array<Pair<Int, Int>>,
        moves: MutableList<Position>
    ) {
        for ((dr, dc) in directions) {
            var r = from.row + dr
            var c = from.col + dc
            while (r in 0..7 && c in 0..7) {
                val target = Position(r, c)
                val destPiece = board[target]
                if (destPiece == null) {
                    moves.add(target)
                } else {
                    if (destPiece.color != color) {
                        moves.add(target)
                    }
                    break
                }
                r += dr
                c += dc
            }
        }
    }

    private fun getKingMoves(board: Board, from: Position, color: PieceColor, moves: MutableList<Position>) {
        for ((dr, dc) in KING_DIRECTIONS) {
            val target = Position(from.row + dr, from.col + dc)
            if (target.isValid()) {
                val destPiece = board[target]
                if (destPiece == null || destPiece.color != color) {
                    moves.add(target)
                }
            }
        }
    }

    fun isSquareAttacked(board: Board, square: Position, attackerColor: PieceColor): Boolean {
        // Check pawn attacks
        val pawnDir = if (attackerColor == PieceColor.WHITE) 1 else -1
        for (dc in arrayOf(-1, 1)) {
            val pawnSquare = Position(square.row + pawnDir, square.col + dc)
            if (pawnSquare.isValid()) {
                val p = board[pawnSquare]
                if (p != null && p.color == attackerColor && p.type == PieceType.PAWN) {
                    return true
                }
            }
        }

        // Check knight attacks
        for ((dr, dc) in KNIGHT_OFFSETS) {
            val knightSquare = Position(square.row + dr, square.col + dc)
            if (knightSquare.isValid()) {
                val p = board[knightSquare]
                if (p != null && p.color == attackerColor && p.type == PieceType.KNIGHT) {
                    return true
                }
            }
        }

        // Check bishop / queen attacks (diagonals)
        for ((dr, dc) in BISHOP_DIRECTIONS) {
            var r = square.row + dr
            var c = square.col + dc
            while (r in 0..7 && c in 0..7) {
                val p = board[r, c]
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                r += dr
                c += dc
            }
        }

        // Check rook / queen attacks (straights)
        for ((dr, dc) in ROOK_DIRECTIONS) {
            var r = square.row + dr
            var c = square.col + dc
            while (r in 0..7 && c in 0..7) {
                val p = board[r, c]
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                r += dr
                c += dc
            }
        }

        // Check king attacks
        for ((dr, dc) in KING_DIRECTIONS) {
            val kingSquare = Position(square.row + dr, square.col + dc)
            if (kingSquare.isValid()) {
                val p = board[kingSquare]
                if (p != null && p.color == attackerColor && p.type == PieceType.KING) {
                    return true
                }
            }
        }

        return false
    }
}
