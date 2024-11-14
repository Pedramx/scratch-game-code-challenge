package com.scratchgame.model.probability;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandardSymbolProbability extends Probability {
    private int column;
    private int row;
}
