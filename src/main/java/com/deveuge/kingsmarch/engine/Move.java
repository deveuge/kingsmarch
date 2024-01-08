package com.deveuge.kingsmarch.engine;

import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.CastlingDirection;
import com.deveuge.kingsmarch.engine.util.Position;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Move {
	
	private Player player;
	private Square start;
	@Setter
	private Square end;
	private Piece pieceMoved;
	private Piece pieceKilled;
	
	private boolean castlingMove = false;
	private boolean enPassant = false;
	private boolean capturableEnPassant = false;
	private boolean pawnPromotion = false;
	
	private CastlingDirection castlingDirection;
	private Square rookCastlingSquare;
	private Square enPassantCaptureSquare;

	public Move(Player player, Square start, Square end, Board board) {
		this.player = player;
		this.start = start;
		this.end = end;
		this.pieceMoved = start.getPiece();
		this.pieceKilled = end.getPiece();
		
		this.castlingMove = checkIfCastlingMove();
		this.enPassant = checkIfIsEnPassant(board);
		this.capturableEnPassant = checkIfIsCapturableEnPassant();
		this.pawnPromotion = checkIfPawnPromotion();
		
		if(isEnPassant()) {
			enPassantCaptureSquare = ((Pawn) start.getPiece()).getEnPassantPawnSquare(board, start, end);
			this.pieceKilled = enPassantCaptureSquare.getPiece();
		}
		
		if(isCastlingMove()) {
			castlingDirection = CastlingDirection.get(end.getCol());
			rookCastlingSquare = board.getSquare(start.getRow(), castlingDirection.getCol());
			this.pieceKilled = null;
		}
	}
	
	public Move(Square start, Square end, Piece pieceMoved) {
		super();
		this.start = start;
		this.end = end;
		this.pieceMoved = pieceMoved;
	}

	/**
	 * Checks if the current move is a castling move.
	 * 
	 * @return true if is a castling move, false otherwise
	 */
	private boolean checkIfCastlingMove() {
		return this.pieceMoved instanceof King 
				&& ((King) this.pieceMoved).isCastlingMove(this.getStart(), this.getEnd());
	}
	
	/**
	 * Checks if the current move is a en passant capture move.
	 * 
	 * @param board {@link Board} Current board situation
	 * @return true if is a en passant capture move, false otherwise
	 */
	public boolean checkIfIsEnPassant(Board board) {
		return this.pieceMoved instanceof Pawn 
				&& ((Pawn) this.pieceMoved).isEnPassantCapture(board, this.getStart(), this.getEnd());
	}
	
	/**
	 * Checks if the current move puts the pawn in a capturable en passant situation.
	 * 
	 * @return true if is an allowed situation en passant move, false otherwise
	 */
	public boolean checkIfIsCapturableEnPassant() {
		return this.pieceMoved instanceof Pawn && this.pieceMoved.isFirstMove() 
				&& Math.abs(this.getEnd().getRow() - this.getStart().getRow()) == 2;
	}

	/**
	 * Checks if the current move promotes a pawn.
	 * 
	 * @return true if it is a pawn promotion, false otherwise
	 */
	private boolean checkIfPawnPromotion() {
		boolean isLastRank = this.pieceMoved.getColour().isWhite()
				? this.end.getRow() == 7
				: this.end.getRow() == 0;
		return this.pieceMoved instanceof Pawn && isLastRank;
	}
	
	/**
	 * Obtains the algebraic notation of the move.
	 * 
	 * @return {@link String}
	 */
	public String getAlgebraicNotation() {
		return String.format("%s-%s", new Position(this.getStart()).getAlgebraicNotation(), new Position(this.getEnd()).getAlgebraicNotation());
	}
	
}
