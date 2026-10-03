package com.example.chess.engine

import com.example.chess.model.AIDifficulty
import com.example.chess.model.Board
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlin.random.Random

object MinimaxAI {

    fun findBestMove(board: Board, aiColor: PieceColor, difficulty: AIDifficulty): Move? {
        val legalMoves = ChessEngine.getAllLegalMoves(board, aiColor)
        if (legalMoves.isEmpty()) return null

        if (difficulty == AIDifficulty.EASY) {
            // Easy AI: 70% random move, 30% best depth-1 move
            return if (Random.nextFloat() < 0.7f) {
                legalMoves.random()
            } else {
                getBestMoveAtDepth(board, aiColor, depth = 1)
            }
        }

        val searchDepth = when (difficulty) {
            AIDifficulty.EASY -> 1
            AIDifficulty.MEDIUM -> 3
            AIDifficulty.HARD -> 4
        }

        return getBestMoveAtDepth(board, aiColor, searchDepth)
    }

    private fun getBestMoveAtDepth(board: Board, aiColor: PieceColor, depth: Int): Move? {
        var moves = ChessEngine.getAllLegalMoves(board, aiColor)
        if (moves.isEmpty()) return null

        moves = orderMoves(moves)

        var bestMove: Move? = null
        var bestEval = if (aiColor == PieceColor.WHITE) Int.MIN_VALUE else Int.MAX_VALUE
        var alpha = Int.MIN_VALUE
        var beta = Int.MAX_VALUE

        for (move in moves) {
            val newBoard = ChessEngine.applyMoveToBoard(board, move)
            val eval = minimax(newBoard, depth - 1, alpha, beta, aiColor.opposite(), aiColor)

            if (aiColor == PieceColor.WHITE) {
                if (eval > bestEval) {
                    bestEval = eval
                    bestMove = move
                }
                alpha = maxOf(alpha, bestEval)
            } else {
                if (eval < bestEval) {
                    bestEval = eval
                    bestMove = move
                }
                beta = minOf(beta, bestEval)
            }

            if (beta <= alpha) break
        }

        return bestMove ?: moves.random()
    }

    private fun minimax(
        board: Board,
        depth: Int,
        alpha: Int,
        beta: Int,
        turn: PieceColor,
        aiColor: PieceColor
    ): Int {
        var a = alpha
        var b = beta

        if (depth == 0) {
            return evaluateBoard(board)
        }

        if (ChessEngine.isCheckmate(board, turn)) {
            return if (turn == PieceColor.WHITE) -100000 + (5 - depth) else 100000 - (5 - depth)
        }
        if (ChessEngine.isStalemate(board, turn) || ChessEngine.isInsufficientMaterial(board)) {
            return 0
        }

        var moves = ChessEngine.getAllLegalMoves(board, turn)
        if (moves.isEmpty()) return evaluateBoard(board)
        moves = orderMoves(moves)

        if (turn == PieceColor.WHITE) {
            var maxEval = Int.MIN_VALUE
            for (move in moves) {
                val newBoard = ChessEngine.applyMoveToBoard(board, move)
                val eval = minimax(newBoard, depth - 1, a, b, PieceColor.BLACK, aiColor)
                maxEval = maxOf(maxEval, eval)
                a = maxOf(a, eval)
                if (b <= a) break
            }
            return maxEval
        } else {
            var minEval = Int.MAX_VALUE
            for (move in moves) {
                val newBoard = ChessEngine.applyMoveToBoard(board, move)
                val eval = minimax(newBoard, depth - 1, a, b, PieceColor.WHITE, aiColor)
                minEval = minOf(minEval, eval)
                b = minOf(b, eval)
                if (b <= a) break
            }
            return minEval
        }
    }

    private fun orderMoves(moves: List<Move>): List<Move> {
        return moves.sortedByDescending { move ->
            var score = 0
            if (move.capturedPiece != null) {
                score += 10 * move.capturedPiece.type.value - move.piece.type.value
            }
            if (move.promotionPieceType != null) {
                score += 900
            }
            if (move.isCastling) {
                score += 50
            }
            score
        }
    }

    fun evaluateBoard(board: Board): Int {
        var totalScore = 0

        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r, c] ?: continue
                val pieceVal = piece.type.value * 100
                val pstScore = getPstScore(piece, r, c)
                val score = pieceVal + pstScore

                if (piece.color == PieceColor.WHITE) {
                    totalScore += score
                } else {
                    totalScore -= score
                }
            }
        }

        return totalScore
    }

    private fun getPstScore(piece: Piece, r: Int, c: Int): Int {
        val row = if (piece.color == PieceColor.WHITE) r else 7 - r
        val col = c

        val table = when (piece.type) {
            PieceType.PAWN -> pawnPst
            PieceType.KNIGHT -> knightPst
            PieceType.BISHOP -> bishopPst
            PieceType.ROOK -> rookPst
            PieceType.QUEEN -> queenPst
            PieceType.KING -> kingPst
        }

        return table[row][col]
    }

    // Piece-Square Tables (from White perspective)
    private val pawnPst = arrayOf(
        intArrayOf(0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf(50, 50, 50, 50, 50, 50, 50, 50),
        intArrayOf(10, 10, 20, 30, 30, 20, 10, 10),
        intArrayOf( 5,  5, 10, 27, 27, 10,  5,  5),
        intArrayOf( 0,  0,  0, 20, 20,  0,  0,  0),
        intArrayOf( 5, -5,-10,  0,  0,-10, -5,  5),
        intArrayOf( 5, 10, 10,-20,-20, 10, 10,  5),
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0)
    )

    private val knightPst = arrayOf(
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50),
        intArrayOf(-40,-20,  0,  0,  0,  0,-20,-40),
        intArrayOf(-30,  0, 10, 15, 15, 10,  0,-30),
        intArrayOf(-30,  5, 15, 20, 20, 15,  5,-30),
        intArrayOf(-30,  0, 15, 20, 20, 15,  0,-30),
        intArrayOf(-30,  5, 10, 15, 15, 10,  5,-30),
        intArrayOf(-40,-20,  0,  5,  5,  0,-20,-40),
        intArrayOf(-50,-40,-30,-30,-30,-30,-40,-50)
    )

    private val bishopPst = arrayOf(
        intArrayOf(-20,-10,-10,-10,-10,-10,-10,-20),
        intArrayOf(-10,  0,  0,  0,  0,  0,  0,-10),
        intArrayOf(-10,  0,  5, 10, 10,  5,  0,-10),
        intArrayOf(-10,  5,  5, 10, 10,  5,  5,-10),
        intArrayOf(-10,  0, 10, 10, 10, 10,  0,-10),
        intArrayOf(-10, 10, 10, 10, 10, 10, 10,-10),
        intArrayOf(-10,  5,  0,  0,  0,  0,  5,-10),
        intArrayOf(-20,-10,-10,-10,-10,-10,-10,-20)
    )

    private val rookPst = arrayOf(
        intArrayOf( 0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf( 5, 10, 10, 10, 10, 10, 10,  5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf(-5,  0,  0,  0,  0,  0,  0, -5),
        intArrayOf( 0,  0,  0,  5,  5,  0,  0,  0)
    )

    private val queenPst = arrayOf(
        intArrayOf(-20,-10,-10, -5, -5,-10,-10,-20),
        intArrayOf(-10,  0,  0,  0,  0,  0,  0,-10),
        intArrayOf(-10,  0,  5,  5,  5,  5,  0,-10),
        intArrayOf( -5,  0,  5,  5,  5,  5,  0, -5),
        intArrayOf(  0,  0,  5,  5,  5,  5,  0, -5),
        intArrayOf(-10,  5,  5,  5,  5,  5,  0,-10),
        intArrayOf(-10,  0,  5,  0,  0,  0,  0,-10),
        intArrayOf(-20,-10,-10, -5, -5,-10,-10,-20)
    )

    private val kingPst = arrayOf(
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-30,-40,-40,-50,-50,-40,-40,-30),
        intArrayOf(-20,-30,-30,-40,-40,-30,-30,-20),
        intArrayOf(-10,-20,-20,-20,-20,-20,-20,-10),
        intArrayOf( 20, 20,  0,  0,  0,  0, 20, 20),
        intArrayOf( 20, 30, 10,  0,  0, 10, 30, 20)
    )
}
