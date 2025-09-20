package com.mwarmstrong.wordgames.services.impl;

import com.mwarmstrong.wordgames.models.WordModel;
import com.mwarmstrong.wordgames.models.Words;
import com.mwarmstrong.wordgames.services.WordService;
import com.mwarmstrong.wordgames.utilities.AppUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordServiceImpl implements WordService {
    final DataSource dataSource;

    private List<WordModel> words;
    private List<String> allWords;

    @PostConstruct
    public void init() throws SQLException, IOException {
        createTable();
        loadWords();
        if (CollectionUtils.isEmpty(words)) {
            addWords();
        }
        // fileName is relative to src/main/resources
        String fileName = "word-check.txt";
        Resource resource = new ClassPathResource(fileName);
        allWords = Files.readAllLines(resource.getFile().toPath());
        allWords.addAll(words.stream().map(WordModel::getWordValue).toList());
        Set<String> uniqueElements = new HashSet<>(allWords);
        allWords = new ArrayList<>(uniqueElements);
        Collections.sort(allWords);
        log.info("Created {}, Words: {}, Dictionary: {}",
                this.getClass().getSimpleName(),
                words.size(), allWords.size());
    }

    @Override
    public Words getNextWord() throws SQLException {
        Words result = new Words();
        List<WordModel> usable = words.stream().filter(w -> w.getLastPlayed() == null).toList();
        if (CollectionUtils.isEmpty(usable)) {
            clearLastPlayed();
            loadWords();
            usable = words.stream().filter(w -> w.getLastPlayed() == null).toList();
        }
        int random = new Random().nextInt(usable.size());
        WordModel word = usable.get(random);
        updateLastPlayed(word);
        result.setSelectedValue(word.getWordValue());
        result.setValues(allWords);
        return result;
    }

    void loadWords() throws SQLException {
        String sql = "SELECT WORD_ID, WORD_VALUE, LAST_PLAYED FROM WORD_LIST ORDER BY LAST_PLAYED";
        words = new ArrayList<>();
        if (log.isDebugEnabled()) {
            log.debug("loadWords()");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        words.add(mapWord(rs));
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("loadWords() loaded {} words", words.size());
        }
    }

    int clearLastPlayed() throws SQLException {
        String sql = "UPDATE WORD_LIST SET LAST_PLAYED = null";
        try (Connection conn = dataSource.getConnection()) {
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                return stmt.executeUpdate();
            }
        }
    }

    WordModel updateLastPlayed(WordModel wordModel) throws SQLException {
        String sql = "UPDATE WORD_LIST SET LAST_PLAYED = ? WHERE WORD_ID = ?";
        try (Connection conn = dataSource.getConnection()) {
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                wordModel.setLastPlayed(new java.sql.Date(System.currentTimeMillis()));
                stmt.setDate(1, wordModel.getLastPlayed());
                stmt.setInt(2, wordModel.getWordId());
                stmt.executeUpdate();
            }
        }
        return wordModel;
    }

    void addWords() throws SQLException, IOException {
        // fileName is relative to src/main/resources
        String fileName = "word-dictionary.txt";
        Resource resource = new ClassPathResource(fileName);
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        List<List<String>> wordLists = AppUtils.chopList(lines, 250);
        String sql = "INSERT INTO WORD_LIST (WORD_VALUE) VALUES (?)";
        try (Connection conn = dataSource.getConnection()) {
            for (List<String> words : wordLists) {
                log.info("Saving {} words", words.size());
                try (java.sql.PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    for (String line : words) {
                        preparedStatement.setString(1, line);
                        preparedStatement.addBatch();
                    }
                    preparedStatement.executeBatch();
                    conn.commit();
                }
            }
        }
        loadWords();
    }

    void addWord(String word) throws SQLException {
        String sql = "INSERT INTO WORD_LIST (WORD_VALUE) VALUES (?)";
        try (Connection conn = dataSource.getConnection()) {
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, word);
                stmt.executeUpdate();
            }
        }
    }

    void createTable() throws SQLException {
        if (!tableExists()) {
            String sql = "CREATE TABLE WORD_LIST(" +
                    "WORD_ID INT AUTO_INCREMENT, " +
                    "WORD_VALUE VARCHAR(8), " +
                    "LAST_PLAYED DATE)";
            try (Connection conn = dataSource.getConnection()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                }
            }
        } else {
            log.info("WORD_LIST table already exists");
        }
    }

    boolean tableExists() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return conn.getMetaData().getTables(null, null, "WORD_LIST", null).next();
        }
    }

    private WordModel mapWord(ResultSet rs) throws SQLException {
        WordModel wordModel = new WordModel();
        wordModel.setWordId(rs.getInt("WORD_ID"));
        wordModel.setWordValue(rs.getString("WORD_VALUE"));
        wordModel.setLastPlayed(rs.getDate("LAST_PLAYED"));
        return wordModel;
    }
}
