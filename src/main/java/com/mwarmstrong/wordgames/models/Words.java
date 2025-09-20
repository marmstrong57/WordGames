package com.mwarmstrong.wordgames.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Words {
    private String selectedValue;
    private List<String> values;
}
