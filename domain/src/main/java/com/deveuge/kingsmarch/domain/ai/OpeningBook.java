package com.deveuge.kingsmarch.domain.ai;

import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Move;

public interface OpeningBook {
    List<Move> getNext(List<Move> historic, List<Move> calculatedMoves);
}