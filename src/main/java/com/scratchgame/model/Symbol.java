package com.scratchgame.model;

import com.scratchgame.enums.SymbolImpactEnum;
import com.scratchgame.enums.SymbolTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Symbol {
    private SymbolTypeEnum type;
    private Double reward_multiplier;
    private SymbolImpactEnum impact;
    private Integer extra;
}
