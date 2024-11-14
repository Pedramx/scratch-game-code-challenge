package com.scratchgame.model;

import com.scratchgame.model.probability.Probabilities;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Configuration {
    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;
    private Map<String, WinningCombination> win_combinations;
}
