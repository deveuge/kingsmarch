package com.deveuge.kingsmarch.domain.model;

import java.security.SecureRandom;
import java.util.Base64;

public class GameId {
	
	private final String value;
	private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    
    public GameId(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
    
    public static GameId of(String value) {
        return new GameId(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameId)) return false;
        GameId gameId = (GameId) o;
        return value.equals(gameId.value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }

	/**
     * Generates a new SecureRandom to be used as a game identifier.
     * 
     * @return {@link GameId} A new unique game ID
     */
    public static GameId generate() {
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return new GameId(encoder.encodeToString(buffer));
    }

}
