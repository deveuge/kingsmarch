package com.deveuge.kingsmarch.engine;

import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.types.CastlingDirection;
import com.deveuge.kingsmarch.engine.types.Colour;

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

	/**
	 * Checks if the current move is a castling move.
	 * 
	 * @return true if is a castling move, false otherwise
	 */
	private boolean checkIfCastlingMove() {
		Piece sourcePiece = this.getStart().getPiece();
		return sourcePiece instanceof King 
				&& ((King) sourcePiece).isCastlingMove(this.getStart(), this.getEnd());
	}
	
	/**
	 * Checks if the current move is a en passant capture move.
	 * 
	 * @param board {@link Board} Current board situation
	 * @return true if is a en passant capture move, false otherwise
	 */
	public boolean checkIfIsEnPassant(Board board) {
		Piece sourcePiece = this.getStart().getPiece();
		return sourcePiece instanceof Pawn 
				&& ((Pawn) sourcePiece).isEnPassantCapture(board, this.getStart(), this.getEnd());
	}
	
	/**
	 * Checks if the current move puts the pawn in a capturable en passant situation.
	 * 
	 * @return true if is an allowed situation en passant move, false otherwise
	 */
	public boolean checkIfIsCapturableEnPassant() {
		Piece sourcePiece = this.getStart().getPiece();
		return sourcePiece instanceof Pawn && sourcePiece.isFirstMove() 
				&& Math.abs(this.getEnd().getRow() - this.getStart().getRow()) == 2;
	}

	/**
	 * Checks if the current move promotes a pawn.
	 * 
	 * @return true if it is a pawn promotion, false otherwise
	 */
	private boolean checkIfPawnPromotion() {
		Piece sourcePiece = this.getStart().getPiece();
		boolean isLastRank = Colour.WHITE.equals(sourcePiece.getColour())
				? this.end.getRow() == 7
				: this.end.getRow() == 0;
		return sourcePiece instanceof Pawn && isLastRank;
	}
	
}
