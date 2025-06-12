package com.deveuge.kingsmarch.api.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.deveuge.kingsmarch.domain.model.GameId;

@Component
public class StringToGameIdConverter implements Converter<String, GameId> {
    @Override
    public GameId convert(String source) {
        return GameId.of(source);
    }
}