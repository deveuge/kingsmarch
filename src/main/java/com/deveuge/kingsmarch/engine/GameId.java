package com.deveuge.kingsmarch.engine;

import java.security.SecureRandom;
import java.util.Base64;

public class GameId {

	private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generates a new SecureRandom to be used as a game identifier.
     * 
     * @return {@link String} A new unique game ID
     */
    public static String generate() {
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }
}
