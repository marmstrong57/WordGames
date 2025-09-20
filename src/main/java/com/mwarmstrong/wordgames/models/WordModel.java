package com.mwarmstrong.wordgames.models;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class WordModel {
    private int wordId;
    private String wordValue;
    private Date lastPlayed;
    private boolean selected;
}
