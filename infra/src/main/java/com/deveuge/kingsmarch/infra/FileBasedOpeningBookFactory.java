package com.deveuge.kingsmarch.infra;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.deveuge.kingsmarch.domain.model.opening.Opening;
import com.deveuge.kingsmarch.domain.model.opening.OpeningBook;
import com.deveuge.kingsmarch.domain.service.GameAI;

import jakarta.annotation.PostConstruct;

@Component
public class FileBasedOpeningBookFactory {

    private OpeningBook openingBook;

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("Openings.txt").getInputStream(), StandardCharsets.UTF_8))) {

            List<Opening> openings = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                openings.add(new Opening(line, GameAI.AI_COLOUR));
            }

            this.openingBook = new DefaultOpeningBook(openings);

        } catch (Exception e) {
            throw new RuntimeException("Error loading Opening Book", e);
        }
    }

    public OpeningBook getOpeningBook() {
        return openingBook;
    }
}