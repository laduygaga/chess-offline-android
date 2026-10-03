package com.example.chess.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.engine.ChessEngine
import com.example.chess.engine.MinimaxAI
import com.example.chess.model.AIDifficulty
import com.example.chess.model.Board
import com.example.chess.model.GameMode
import com.example.chess.model.GameState
import com.example.chess.model.GameStatus
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChessViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var timerJob: Job? = null
    private var aiJob: Job? = null

    init {
        startTimer()
    }

    fun startNewGame(
        gameMode: GameMode = GameMode.PASS_AND_PLAY,
        aiDifficulty: AIDifficulty = AIDifficulty.MEDIUM,
        playerColor: PieceColor = PieceColor.WHITE,
        timeLimitMinutes: Int = 10
    ) {
        val aiColor = playerColor.opposite()
        val timeSeconds = timeLimitMinutes * 60L

        timerJob?.cancel()
        aiJob?.cancel()

        _gameState.value = GameState(
            gameMode = gameMode,
            aiDifficulty = aiDifficulty,
            aiColor = aiColor,
            whiteTimeSeconds = timeSeconds,
            blackTimeSeconds = timeSeconds,
            initialTimeSeconds = timeSeconds
        )

        startTimer()

        // If playing vs AI and AI plays White, trigger AI move
        if (gameMode == GameMode.VS_AI && playerColor == PieceColor.BLACK) {
            triggerAiMoveIfNeeded()
        }
    }

    fun onSquareSelected(pos: Position) {
        val state = _gameState.value
        if (state.gameStatus != GameStatus.IN_PROGRESS) return

        // If playing vs AI and it's AI turn, ignore clicks
        if (state.gameMode == GameMode.VS_AI && state.currentTurn == state.aiColor) return

        // If pending promotion choice, ignore board clicks until choice is made
        if (state.pendingPromotionMove != null) return

        val selected = state.selectedPosition

        if (selected == null) {
            // Select piece if owned by current turn
            val piece = state.board[pos]
            if (piece != null && piece.color == state.currentTurn) {
                val legalMoves = ChessEngine.getLegalMoves(state.board, pos, state.currentTurn)
                _gameState.update { it.copy(selectedPosition = pos, legalMovesForSelected = legalMoves) }
            }
        } else {
            if (selected == pos) {
                // Deselect
                _gameState.update { it.copy(selectedPosition = null, legalMovesForSelected = emptyList()) }
            } else if (state.legalMovesForSelected.contains(pos)) {
                // Check if move is a pawn promotion
                val piece = state.board[selected]
                val isPawnPromotion = piece?.type == PieceType.PAWN && (pos.row == 0 || pos.row == 7)

                if (isPawnPromotion) {
                    _gameState.update {
                        it.copy(
                            pendingPromotionMove = Pair(selected, pos),
                            selectedPosition = null,
                            legalMovesForSelected = emptyList()
                        )
                    }
                } else {
                    executeMove(selected, pos, null)
                }
            } else {
                // Select new piece if same color
                val piece = state.board[pos]
                if (piece != null && piece.color == state.currentTurn) {
                    val legalMoves = ChessEngine.getLegalMoves(state.board, pos, state.currentTurn)
                    _gameState.update { it.copy(selectedPosition = pos, legalMovesForSelected = legalMoves) }
                } else {
                    _gameState.update { it.copy(selectedPosition = null, legalMovesForSelected = emptyList()) }
                }
            }
        }
    }

    fun onPromotionChoice(promotionType: PieceType) {
        val state = _gameState.value
        val pending = state.pendingPromotionMove ?: return
        _gameState.update { it.copy(pendingPromotionMove = null) }
        executeMove(pending.first, pending.second, promotionType)
    }

    private fun executeMove(from: Position, to: Position, promotionType: PieceType?) {
        val state = _gameState.value
        val move = ChessEngine.createMoveObject(state.board, from, to, promotionType)
        val newBoard = ChessEngine.applyMoveToBoard(state.board, move)

        val nextTurn = state.currentTurn.opposite()
        val isCheck = ChessEngine.isKingInCheck(newBoard, nextTurn)

        val isMate = ChessEngine.isCheckmate(newBoard, nextTurn)
        val isStalemate = ChessEngine.isStalemate(newBoard, nextTurn)
        val isInsufficient = ChessEngine.isInsufficientMaterial(newBoard)

        val newStatus = when {
            isMate -> GameStatus.CHECKMATE
            isStalemate -> GameStatus.STALEMATE
            isInsufficient -> GameStatus.DRAW_INSUFFICIENT_MATERIAL
            else -> GameStatus.IN_PROGRESS
        }

        val newCaptured = if (move.capturedPiece != null) {
            state.capturedPieces + move.capturedPiece
        } else {
            state.capturedPieces
        }

        _gameState.update {
            it.copy(
                board = newBoard,
                currentTurn = nextTurn,
                moveHistory = it.moveHistory + move,
                capturedPieces = newCaptured,
                isCheck = isCheck,
                gameStatus = newStatus,
                winner = if (isMate) state.currentTurn else null,
                selectedPosition = null,
                legalMovesForSelected = emptyList(),
                fullMoveNumber = if (nextTurn == PieceColor.WHITE) it.fullMoveNumber + 1 else it.fullMoveNumber
            )
        }

        if (newStatus == GameStatus.IN_PROGRESS) {
            triggerAiMoveIfNeeded()
        }
    }

    private fun triggerAiMoveIfNeeded() {
        val state = _gameState.value
        if (state.gameMode != GameMode.VS_AI) return
        if (state.currentTurn != state.aiColor) return
        if (state.gameStatus != GameStatus.IN_PROGRESS) return

        aiJob?.cancel()
        aiJob = viewModelScope.launch(Dispatchers.Default) {
            delay(400) // Realistic move thinking time delay for UI smoothness
            val bestMove = MinimaxAI.findBestMove(
                board = _gameState.value.board,
                aiColor = _gameState.value.aiColor,
                difficulty = _gameState.value.aiDifficulty
            )

            if (bestMove != null) {
                withContext(Dispatchers.Main) {
                    executeMove(bestMove.from, bestMove.to, bestMove.promotionPieceType)
                }
            }
        }
    }

    fun undoMove() {
        val state = _gameState.value
        if (state.moveHistory.isEmpty()) return

        // If playing vs AI, undo 2 moves (AI move + human move)
        val undoCount = if (state.gameMode == GameMode.VS_AI && state.moveHistory.size >= 2) 2 else 1
        var currentBoard = Board.createStandardBoard()
        var currentTurn = PieceColor.WHITE
        val remainingMoves = state.moveHistory.dropLast(undoCount)

        val newCaptured = mutableListOf<com.example.chess.model.Piece>()

        for (m in remainingMoves) {
            if (m.capturedPiece != null) newCaptured.add(m.capturedPiece)
            currentBoard = ChessEngine.applyMoveToBoard(currentBoard, m)
            currentTurn = currentTurn.opposite()
        }

        _gameState.update {
            it.copy(
                board = currentBoard,
                currentTurn = currentTurn,
                moveHistory = remainingMoves,
                capturedPieces = newCaptured,
                isCheck = ChessEngine.isKingInCheck(currentBoard, currentTurn),
                gameStatus = GameStatus.IN_PROGRESS,
                winner = null,
                selectedPosition = null,
                legalMovesForSelected = emptyList()
            )
        }
    }

    fun resign(color: PieceColor) {
        _gameState.update {
            it.copy(
                gameStatus = GameStatus.RESIGNED,
                winner = color.opposite()
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _gameState.value
                if (state.gameStatus == GameStatus.IN_PROGRESS && state.initialTimeSeconds > 0) {
                    if (state.currentTurn == PieceColor.WHITE) {
                        if (state.whiteTimeSeconds > 0) {
                            val newTime = state.whiteTimeSeconds - 1
                            _gameState.update { it.copy(whiteTimeSeconds = newTime) }
                            if (newTime <= 0) {
                                _gameState.update { it.copy(gameStatus = GameStatus.TIME_OUT, winner = PieceColor.BLACK) }
                            }
                        }
                    } else {
                        if (state.blackTimeSeconds > 0) {
                            val newTime = state.blackTimeSeconds - 1
                            _gameState.update { it.copy(blackTimeSeconds = newTime) }
                            if (newTime <= 0) {
                                _gameState.update { it.copy(gameStatus = GameStatus.TIME_OUT, winner = PieceColor.WHITE) }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        aiJob?.cancel()
    }
}
