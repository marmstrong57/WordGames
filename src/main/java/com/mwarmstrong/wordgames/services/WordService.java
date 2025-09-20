package com.mwarmstrong.wordgames.services;

import com.mwarmstrong.wordgames.models.Words;

import java.sql.SQLException;

public interface WordService {
    Words getNextWord() throws SQLException;
}
