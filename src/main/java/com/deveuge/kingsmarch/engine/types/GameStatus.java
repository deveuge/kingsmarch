package com.deveuge.kingsmarch.engine.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GameStatus {
	ACTIVE(false), 
    BLACK_WIN(true), 
    WHITE_WIN(true), 
    FORFEIT(true), 
    STALEMATE(true), 
    RESIGNATION(true);
    
    boolean endOfGame;
}
