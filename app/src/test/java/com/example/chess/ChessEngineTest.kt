package com.example.chess

import com.example.chess.engine.ChessEngine
import com.example.chess.engine.MinimaxAI
import com.example.chess.model.AIDifficulty
import com.example.chess.model.Board
import com.example.chess.model.PieceColor
import com.example.chess.model.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessEngineTest {

    @Test
    fun testInitialBoardSetup() {
        val board = Board.createStandardBoard()
        // White pawns on row 6
        assertEquals("P", board[Position(6, 4)]?.type?.symbol)
        assertEquals(PieceColor.WHITE, board[Position(6, 4)]?.color)

        // Black King on row 0, col 4
        assertEquals("K", board[Position(0, 4)]?.type?.symbol)
        assertEquals(PieceColor.BLACK, board[Position(0, 4)]?.color)
    }

    @Test
    fun testInitialWhitePawnMoves() {
        val board = Board.createStandardBoard()
        val e2 = Position(6, 4)
        val legalMoves = ChessEngine.getLegalMoves(board, e2, PieceColor.WHITE)

        assertEquals(2, legalMoves.size)
        assertTrue(legalMoves.contains(Position(5, 4))) // e3
        assertTrue(legalMoves.contains(Position(4, 4))) // e4
    }

    @Test
    fun testInitialTotalMoves() {
        val board = Board.createStandardBoard()
        val allMoves = ChessEngine.getAllLegalMoves(board, PieceColor.WHITE)
        // 16 pawn moves + 4 knight moves = 20 initial legal moves
        assertEquals(20, allMoves.size)
    }

    @Test
    fun testMinimaxAiGeneratesValidMove() {
        val board = Board.createStandardBoard()
        val move = MinimaxAI.findBestMove(board, PieceColor.BLACK, AIDifficulty.EASY)
        assertNotNull(move)
        assertEquals(PieceColor.BLACK, move?.piece?.color)
    }
}
