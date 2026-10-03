package com.example.chess.model

enum class GameMode {
    PASS_AND_PLAY,
    VS_AI
}

enum class AIDifficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class GameStatus {
    IN_PROGRESS,
    CHECKMATE,
    STALEMATE,
    DRAW_FIFTY_MOVES,
    DRAW_INSUFFICIENT_MATERIAL,
    RESIGNED,
    TIME_OUT
}

data class GameState(
    val board: Board = Board.createStandardBoard(),
    val currentTurn: PieceColor = PieceColor.WHITE,
    val moveHistory: List<Move> = emptyList(),
    val capturedPieces: List<Piece> = emptyList(),
    val isCheck: Boolean = false,
    val gameStatus: GameStatus = GameStatus.IN_PROGRESS,
    val winner: PieceColor? = null,
    val gameMode: GameMode = GameMode.PASS_AND_PLAY,
    val aiColor: PieceColor = PieceColor.BLACK,
    val aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
    val whiteTimeSeconds: Long = 600, // 10 minutes default
    val blackTimeSeconds: Long = 600,
    val initialTimeSeconds: Long = 600,
    val halfMoveClock: Int = 0,
    val fullMoveNumber: Int = 1,
    val selectedPosition: Position? = null,
    val legalMovesForSelected: List<Position> = emptyList(),
    val pendingPromotionMove: Pair<Position, Position>? = null
)
