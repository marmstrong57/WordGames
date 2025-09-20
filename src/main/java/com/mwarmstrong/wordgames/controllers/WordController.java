package com.mwarmstrong.wordgames.controllers;

import com.mwarmstrong.wordgames.models.Words;
import com.mwarmstrong.wordgames.services.WordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class WordController {
    final WordService wordService;

    @GetMapping("/words")
    ResponseEntity<Words> words() {
        long startTimestamp = System.currentTimeMillis();
        String word = null;
        try {
            var words = wordService.getNextWord();
            word = words.getSelectedValue();
            return ResponseEntity.ok(words);
        } catch (Exception e) {
            log.error("Error in WordController.index()", e);
            return ResponseEntity.internalServerError().build();
        } finally {
            log.info("WordController.index() took {} ms, returned {}",
                    System.currentTimeMillis() - startTimestamp, word);
        }
    }
}
