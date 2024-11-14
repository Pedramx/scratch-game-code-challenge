package com.scratchgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.List;

@Getter
@Setter
public class GameResult {
    private String[][] matrix;
    private double reward;
    private Map<String, List<String>> applied_winning_combinations;
    private String applied_bonus_symbol;

    public GameResult(String[][] matrix, double reward, Map<String, List<String>> appliedWinningCombinations, String appliedBonusSymbol) {
        this.matrix = matrix;
        this.reward = reward;
        this.applied_winning_combinations = appliedWinningCombinations;
        this.applied_bonus_symbol = appliedBonusSymbol;
    }

    public String toJson() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(this);
    }
}
