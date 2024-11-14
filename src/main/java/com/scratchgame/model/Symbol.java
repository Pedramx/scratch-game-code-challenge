package com.scratchgame.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Symbol {
    private String type; // "standard" or "bonus"
    private Double reward_multiplier;
    private String impact; // TODO: make it enum
    private Integer extra;
}
