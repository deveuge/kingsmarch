package com.deveuge.kingsmarch.domain.model.opening;

import java.util.List;

import com.deveuge.kingsmarch.domain.engine.Move;

public interface OpeningBook {
    List<Move> getNext(List<Move> historic, List<Move> calculatedMoves);
}