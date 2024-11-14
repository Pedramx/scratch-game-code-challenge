package com.scratchgame.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WinningCombination {
    private String name;
    private Double reward_multiplier;
    private String when;
    private Integer count;
    private String group; // TODO: make it an enum
    private List<List<String>> covered_areas;
}
