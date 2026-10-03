package com.example.chess.engine

import com.example.chess.model.Board
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Position

object ChessEngine {

    fun isKingInCheck(board: Board, color: PieceColor): Boolean {
        val kingPos = board.findKing(color) ?: return false
        return MoveValidator.isSquareAttacked(board, kingPos, color.opposite())
    }

    fun getLegalMoves(board: Board, from: Position, turn: PieceColor): List<Position> {
        val piece = board[from] ?: return emptyList()
        if (piece.color != turn) return emptyList()

        val pseudoMoves = MoveValidator.getPseudoLegalMoves(board, from).toMutableList()

        // Castling logic for King
        if (piece.type == PieceType.KING && !piece.hasMoved && !isKingInCheck(board, turn)) {
            val row = if (turn == PieceColor.WHITE) 7 else 0
            if (from.row == row && from.col == 4) {
                // King-side castling
                val canCastleKingSide = if (turn == PieceColor.WHITE) board.whiteCanCastleKingSide else board.blackCanCastleKingSide
                if (canCastleKingSide) {
                    val rook = board[row, 7]
                    if (rook != null && rook.type == PieceType.ROOK && !rook.hasMoved &&
                        board[row, 5] == null && board[row, 6] == null &&
                        !MoveValidator.isSquareAttacked(board, Position(row, 5), turn.opposite()) &&
                        !MoveValidator.isSquareAttacked(board, Position(row, 6), turn.opposite())
                    ) {
                        pseudoMoves.add(Position(row, 6))
                    }
                }

                // Queen-side castling
                val canCastleQueenSide = if (turn == PieceColor.WHITE) board.whiteCanCastleQueenSide else board.blackCanCastleQueenSide
                if (canCastleQueenSide) {
                    val rook = board[row, 0]
                    if (rook != null && rook.type == PieceType.ROOK && !rook.hasMoved &&
                        board[row, 1] == null && board[row, 2] == null && board[row, 3] == null &&
                        !MoveValidator.isSquareAttacked(board, Position(row, 2), turn.opposite()) &&
                        !MoveValidator.isSquareAttacked(board, Position(row, 3), turn.opposite())
                    ) {
                        pseudoMoves.add(Position(row, 2))
                    }
                }
            }
        }

        // Filter out moves that leave or put king in check
        val legalMoves = mutableListOf<Position>()
        for (to in pseudoMoves) {
            val newBoard = applySimpleMove(board, from, to)
            if (!isKingInCheck(newBoard, turn)) {
                legalMoves.add(to)
            }
        }

        return legalMoves
    }

    fun getAllLegalMoves(board: Board, turn: PieceColor): List<Move> {
        val allMoves = mutableListOf<Move>()
        for (r in 0..7) {
            for (c in 0..7) {
                val pos = Position(r, c)
                val piece = board[pos]
                if (piece != null && piece.color == turn) {
                    val dests = getLegalMoves(board, pos, turn)
                    for (dest in dests) {
                        allMoves.add(createMoveObject(board, pos, dest))
                    }
                }
            }
        }
        return allMoves
    }

    fun createMoveObject(board: Board, from: Position, to: Position, promotionPieceType: PieceType? = null): Move {
        val piece = board[from]!!
        val destPiece = board[to]

        val isCastling = piece.type == PieceType.KING && kotlin.math.abs(from.col - to.col) == 2
        val isEnPassant = piece.type == PieceType.PAWN && to == board.enPassantTarget

        val captured = if (isEnPassant) {
            val pawnRow = if (piece.color == PieceColor.WHITE) to.row + 1 else to.row - 1
            board[pawnRow, to.col]
        } else destPiece

        val finalPromotionType = promotionPieceType ?: if (piece.type == PieceType.PAWN && (to.row == 0 || to.row == 7)) PieceType.QUEEN else null

        val san = generateSanNotation(board, from, to, piece, captured, isCastling, isEnPassant, finalPromotionType)

        return Move(
            from = from,
            to = to,
            piece = piece,
            capturedPiece = captured,
            isCastling = isCastling,
            isEnPassant = isEnPassant,
            promotionPieceType = finalPromotionType,
            sanNotation = san
        )
    }

    fun applySimpleMove(board: Board, from: Position, to: Position, promotionPieceType: PieceType? = null): Board {
        val piece = board[from] ?: return board
        val isCastling = piece.type == PieceType.KING && kotlin.math.abs(from.col - to.col) == 2
        val isEnPassant = piece.type == PieceType.PAWN && to == board.enPassantTarget

        val newGrid = Array(8) { r -> Array(8) { c -> board.grid[r][c] } }
        val movedPiece = piece.copy(hasMoved = true)

        newGrid[from.row][from.col] = null

        if (isEnPassant) {
            val capturedRow = if (piece.color == PieceColor.WHITE) to.row + 1 else to.row - 1
            newGrid[capturedRow][to.col] = null
        }

        val finalPiece = if (promotionPieceType != null && piece.type == PieceType.PAWN) {
            Piece(type = promotionPieceType, color = piece.color, hasMoved = true)
        } else {
            movedPiece
        }
        newGrid[to.row][to.col] = finalPiece

        if (isCastling) {
            val row = from.row
            if (to.col == 6) {
                val rook = newGrid[row][7]
                newGrid[row][7] = null
                newGrid[row][5] = rook?.copy(hasMoved = true)
            } else if (to.col == 2) {
                val rook = newGrid[row][0]
                newGrid[row][0] = null
                newGrid[row][3] = rook?.copy(hasMoved = true)
            }
        }

        var newEnPassantTarget: Position? = null
        if (piece.type == PieceType.PAWN && kotlin.math.abs(from.row - to.row) == 2) {
            val enPassantRow = (from.row + to.row) / 2
            newEnPassantTarget = Position(enPassantRow, from.col)
        }

        var wCK = board.whiteCanCastleKingSide
        var wCQ = board.whiteCanCastleQueenSide
        var bCK = board.blackCanCastleKingSide
        var bCQ = board.blackCanCastleQueenSide

        if (piece.type == PieceType.KING) {
            if (piece.color == PieceColor.WHITE) {
                wCK = false
                wCQ = false
            } else {
                bCK = false
                bCQ = false
            }
        }
        if (piece.type == PieceType.ROOK) {
            if (from == Position(7, 7)) wCK = false
            if (from == Position(7, 0)) wCQ = false
            if (from == Position(0, 7)) bCK = false
            if (from == Position(0, 0)) bCQ = false
        }
        if (to == Position(7, 7)) wCK = false
        if (to == Position(7, 0)) wCQ = false
        if (to == Position(0, 7)) bCK = false
        if (to == Position(0, 0)) bCQ = false

        return Board(
            grid = newGrid,
            enPassantTarget = newEnPassantTarget,
            whiteCanCastleKingSide = wCK,
            whiteCanCastleQueenSide = wCQ,
            blackCanCastleKingSide = bCK,
            blackCanCastleQueenSide = bCQ
        )
    }

    fun applyMoveToBoard(board: Board, move: Move): Board {
        return applySimpleMove(board, move.from, move.to, move.promotionPieceType)
    }

    fun isCheckmate(board: Board, turn: PieceColor): Boolean {
        return isKingInCheck(board, turn) && getHasAnyLegalMoves(board, turn).not()
    }

    fun isStalemate(board: Board, turn: PieceColor): Boolean {
        return !isKingInCheck(board, turn) && getHasAnyLegalMoves(board, turn).not()
    }

    private fun getHasAnyLegalMoves(board: Board, turn: PieceColor): Boolean {
        for (r in 0..7) {
            for (c in 0..7) {
                val p = Position(r, c)
                val piece = board[p]
                if (piece != null && piece.color == turn) {
                    if (getLegalMoves(board, p, turn).isNotEmpty()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun isInsufficientMaterial(board: Board): Boolean {
        val pieces = mutableListOf<Piece>()
        for (r in 0..7) {
            for (c in 0..7) {
                val p = board[r, c]
                if (p != null) pieces.add(p)
            }
        }

        if (pieces.size == 2) return true

        if (pieces.size == 3) {
            val nonKing = pieces.firstOrNull { it.type != PieceType.KING }
            if (nonKing != null && (nonKing.type == PieceType.BISHOP || nonKing.type == PieceType.KNIGHT)) {
                return true
            }
        }

        return false
    }

    private fun generateSanNotation(
        board: Board,
        from: Position,
        to: Position,
        piece: Piece,
        captured: Piece?,
        isCastling: Boolean,
        isEnPassant: Boolean,
        promotionPieceType: PieceType?
    ): String {
        if (isCastling) {
            return if (to.col == 6) "O-O" else "O-O-O"
        }

        val sb = StringBuilder()
        if (piece.type != PieceType.PAWN) {
            sb.append(piece.type.symbol)
        }

        // Disambiguation if multiple pieces of same type can move to 'to'
        if (piece.type != PieceType.PAWN) {
            val ambiguousPieces = mutableListOf<Position>()
            for (r in 0..7) {
                for (c in 0..7) {
                    val p = Position(r, c)
                    if (p != from) {
                        val otherPiece = board[p]
                        if (otherPiece != null && otherPiece.color == piece.color && otherPiece.type == piece.type) {
                            val pseudoDests = MoveValidator.getPseudoLegalMoves(board, p)
                            if (pseudoDests.contains(to)) {
                                val testBoard = applySimpleMove(board, p, to)
                                if (!isKingInCheck(testBoard, piece.color)) {
                                    ambiguousPieces.add(p)
                                }
                            }
                        }
                    }
                }
            }

            if (ambiguousPieces.isNotEmpty()) {
                val sameFile = ambiguousPieces.any { it.col == from.col }
                val sameRank = ambiguousPieces.any { it.row == from.row }

                if (!sameFile) {
                    sb.append(('a' + from.col))
                } else if (!sameRank) {
                    sb.append((8 - from.row))
                } else {
                    sb.append(from.toAlgebraic())
                }
            }
        }

        // Captures
        if (captured != null || isEnPassant) {
            if (piece.type == PieceType.PAWN) {
                sb.append(('a' + from.col))
            }
            sb.append('x')
        }

        sb.append(to.toAlgebraic())

        if (promotionPieceType != null) {
            sb.append("=").append(promotionPieceType.symbol)
        }

        // Check if move gives check or checkmate
        val nextBoard = applySimpleMove(board, from, to, promotionPieceType)
        val opponent = piece.color.opposite()
        if (isCheckmate(nextBoard, opponent)) {
            sb.append("#")
        } else if (isKingInCheck(nextBoard, opponent)) {
            sb.append("+")
        }

        return sb.toString()
    }

    fun boardToFen(board: Board, turn: PieceColor, halfMoveClock: Int = 0, fullMoveNumber: Int = 1): String {
        val fenBuilder = StringBuilder()

        for (r in 0..7) {
            var emptyCount = 0
            for (c in 0..7) {
                val p = board[r, c]
                if (p == null) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) {
                        fenBuilder.append(emptyCount)
                        emptyCount = 0
                    }
                    val char = p.type.symbol[0]
                    fenBuilder.append(if (p.color == PieceColor.WHITE) char.uppercaseChar() else char.lowercaseChar())
                }
            }
            if (emptyCount > 0) fenBuilder.append(emptyCount)
            if (r < 7) fenBuilder.append('/')
        }

        fenBuilder.append(if (turn == PieceColor.WHITE) " w " else " b ")

        var castleStr = ""
        if (board.whiteCanCastleKingSide) castleStr += "K"
        if (board.whiteCanCastleQueenSide) castleStr += "Q"
        if (board.blackCanCastleKingSide) castleStr += "k"
        if (board.blackCanCastleQueenSide) castleStr += "q"
        if (castleStr.isEmpty()) castleStr = "-"
        fenBuilder.append(castleStr).append(" ")

        fenBuilder.append(board.enPassantTarget?.toAlgebraic() ?: "-").append(" ")
        fenBuilder.append(halfMoveClock).append(" ").append(fullMoveNumber)

        return fenBuilder.toString()
    }
}
