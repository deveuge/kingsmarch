package com.deveuge.kingsmarch.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.deveuge.kingsmarch.engine.pieces.Bishop;
import com.deveuge.kingsmarch.engine.pieces.King;
import com.deveuge.kingsmarch.engine.pieces.Knight;
import com.deveuge.kingsmarch.engine.pieces.Pawn;
import com.deveuge.kingsmarch.engine.pieces.Piece;
import com.deveuge.kingsmarch.engine.pieces.Queen;
import com.deveuge.kingsmarch.engine.pieces.Rook;
import com.deveuge.kingsmarch.engine.types.Colour;

public class Board {

	Square[][] squares;

	public Board() {
		this.init();
	}

	public Square getSquare(int row, int col) {
		return squares[row][col];
	}
	
	public List<Square> getOccupiedSquares(Colour colour) {
		List<Square> occupiedSquares = new ArrayList<>();
		for (Square[] rows : squares) {
		    for (Square square : rows) {
		        if(square.isOccupied() && colour.equals(square.getPiece().getColour())) {
		        	occupiedSquares.add(square);
		        }
 		    }
		}
		return occupiedSquares;
	}

	public void init() {
		squares = new Square[8][8];
		List<Piece> whitePieces = Arrays.asList(
				new Rook(), new Knight(), new Bishop(),
				new Queen(), new King(),
				new Bishop(), new Knight(), new Rook());
		List<Piece> blackPieces = Arrays.asList(
				new Rook(), new Knight(), new Bishop(),
				new Queen(), new King(),
				new Bishop(), new Knight(), new Rook());
		blackPieces.forEach(blackPiece -> blackPiece.setColour(Colour.BLACK));
		
		for(int col = 0; col < whitePieces.size(); col++) {
			squares[0][col] = new Square(0, col, whitePieces.get(col));
			squares[1][col] = new Square(1, col, new Pawn());
			squares[6][col] = new Square(6, col, new Pawn(Colour.BLACK));
			squares[7][col] = new Square(7, col, blackPieces.get(col));
		}

		// Initialize remaining squares without any piece
		for (int row = 2; row < 6; row++) {
			for (int col = 0; col < 8; col++) {
				squares[row][col] = new Square(row, col, null);
			}
		}
	}
	
	public void empty() {
		squares = new Square[8][8];
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				squares[row][col] = new Square(row, col, null);
			}
		}
	}

	public String getFEN() {
		StringBuilder sb = new StringBuilder();
		for (int row = 7; row >= 0; row--) {
			int emptySquares = 0;
			for (int col = 0; col < 8; col++) {
				Square square = squares[row][col];
				if(square.isOccupied()) {
					if(emptySquares != 0) {
						sb.append(emptySquares);
						emptySquares = 0;
					}
					String pieceNotation = square.getPiece().getAlgebraicNotation();
					sb.append(square.getPiece().isWhite() ? pieceNotation : pieceNotation.toLowerCase());
				} else {
					emptySquares++;
				}
			}
			if(emptySquares != 0) {
				sb.append(emptySquares);
				emptySquares = 0;
			}
			sb.append("/");
		}
		
		String fen = sb.toString();
		return fen.substring(0, fen.length() - 1);
	}
}
