package com.example.chess

import com.example.chess.engine.ChessEngine
import com.example.chess.engine.MinimaxAI
import com.example.chess.model.AIDifficulty
import com.example.chess.model.Board
import com.example.chess.model.PieceColor
import com.example.chess.model.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessEngineTest {

    @Test
    fun testInitialBoardSetup() {
        val board = Board.createStandardBoard()
        assertEquals("P", board[Position(6, 4)]?.type?.symbol)
        assertEquals(PieceColor.WHITE, board[Position(6, 4)]?.color)

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
        assertEquals(20, allMoves.size)
    }

    @Test
    fun testMoveExecutionAndSanGenerationNoRecursion() {
        var board = Board.createStandardBoard()
        val e2 = Position(6, 4)
        val e4 = Position(4, 4)

        // Player moves e2 -> e4
        val move1 = ChessEngine.createMoveObject(board, e2, e4)
        assertEquals("e4", move1.sanNotation)

        board = ChessEngine.applyMoveToBoard(board, move1)

        // Black moves e7 -> e5
        val e7 = Position(1, 4)
        val e5 = Position(3, 4)
        val move2 = ChessEngine.createMoveObject(board, e7, e5)
        assertEquals("e5", move2.sanNotation)

        board = ChessEngine.applyMoveToBoard(board, move2)

        // White knight g1 -> f3
        val g1 = Position(7, 6)
        val f3 = Position(5, 5)
        val move3 = ChessEngine.createMoveObject(board, g1, f3)
        assertEquals("Nf3", move3.sanNotation)
    }

    @Test
    fun testMinimaxAiGeneratesValidMove() {
        val board = Board.createStandardBoard()
        val move = MinimaxAI.findBestMove(board, PieceColor.BLACK, AIDifficulty.EASY)
        assertNotNull(move)
        assertEquals(PieceColor.BLACK, move?.piece?.color)
    }
}
