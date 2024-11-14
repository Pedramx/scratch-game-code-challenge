package com.scratchgame.model.probability;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Probabilities {
    private List<StandardSymbolProbability> standard_symbols;
    private BonusSymbolProbability bonus_symbols;
}
