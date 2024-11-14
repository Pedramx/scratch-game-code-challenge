package com.scratchgame.service;

import com.scratchgame.model.Configuration;
import com.scratchgame.model.Symbol;
import com.scratchgame.model.WinningCombination;

import java.util.*;

public class GameEvaluator {
    private Configuration config;

    public GameEvaluator(Configuration config) {
        this.config = config;
    }

    public GameResult evaluate(String[][] matrix, double betAmount) {
        // Check for empty matrix
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            // Return a GameResult with zero reward and empty data structures
            return new GameResult(matrix, 0.0, new HashMap<>(), null);
        }

        Map<String, Integer> symbolCounts = new HashMap<>();
        Map<String, List<String>> symbolPositions = new HashMap<>();
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        Map<String, Double> symbolRewards = new HashMap<>();

        int rows = matrix.length;
        int columns = matrix[0].length;

        // Collect counts and positions of standard symbols
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                String symbol = matrix[row][col];
                Symbol symbolConfig = config.getSymbols().get(symbol);
                if (symbolConfig != null && symbolConfig.getType().equals("standard")) {
                    symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
                    String position = row + ":" + col;
                    symbolPositions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(position);
                }
            }
        }

        // For each symbol, find applicable winning combinations
        for (String symbol : symbolCounts.keySet()) {
            List<String> symbolAppliedWins = new ArrayList<>();
            Map<String, WinningCombination> highestRewardCombosPerGroup = new HashMap<>();

            for (WinningCombination winCombo : config.getWin_combinations().values()) {
                String group = winCombo.getGroup();
                WinningCombination existingCombo = highestRewardCombosPerGroup.get(group);

                boolean matches = false;

                if (winCombo.getWhen().equals("same_symbols")) {
                    int count = symbolCounts.get(symbol);
                    if (count >= winCombo.getCount()) {
                        matches = true;
                    }
                } else if (winCombo.getWhen().equals("linear_symbols")) {
                    for (List<String> area : winCombo.getCovered_areas()) {
                        boolean match = true;
                        for (String pos : area) {
                            if (!symbolPositions.getOrDefault(symbol, Collections.emptyList()).contains(pos)) {
                                match = false;
                                break;
                            }
                        }
                        if (match) {
                            matches = true;
                            break;
                        }
                    }
                }

                if (matches) {
                    if (existingCombo == null || winCombo.getReward_multiplier() > existingCombo.getReward_multiplier()) {
                        highestRewardCombosPerGroup.put(group, winCombo);
                    }
                }
            }

            // Collect the selected winning combinations
            double rewardMultiplier = 1.0;
            for (WinningCombination combo : highestRewardCombosPerGroup.values()) {
                symbolAppliedWins.add(combo.getName());
                rewardMultiplier *= combo.getReward_multiplier();
            }
            if (!symbolAppliedWins.isEmpty()) {
                appliedWinningCombinations.put(symbol, symbolAppliedWins);
                // Calculate symbol reward
                double symbolReward = betAmount * config.getSymbols().get(symbol).getReward_multiplier() * rewardMultiplier;
                symbolRewards.put(symbol, symbolReward);
            }
        }

        // Sum up rewards
        double totalReward = symbolRewards.values().stream().mapToDouble(Double::doubleValue).sum();

        // Apply bonus symbol if any
        String appliedBonusSymbol = null;
        String bonusSymbol = findBonusSymbol(matrix);
        if (bonusSymbol != null && totalReward > 0) {
            Symbol bonusSymbolConfig = config.getSymbols().get(bonusSymbol);
            if (bonusSymbolConfig != null && bonusSymbolConfig.getType().equals("bonus")) {
                appliedBonusSymbol = bonusSymbol;
                String impact = bonusSymbolConfig.getImpact();
                if (impact.equals("multiply_reward")) {
                    totalReward *= bonusSymbolConfig.getReward_multiplier();
                } else if (impact.equals("extra_bonus")) {
                    totalReward += bonusSymbolConfig.getExtra();
                } else if (impact.equals("miss")) {
                    // Do nothing
                }
            }
        }

        return new GameResult(matrix, totalReward, appliedWinningCombinations, appliedBonusSymbol);
    }

    private String findBonusSymbol(String[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                String symbol = matrix[row][col];
                Symbol symbolConfig = config.getSymbols().get(symbol);
                if (symbolConfig != null && symbolConfig.getType().equals("bonus")) {
                    return symbol;
                }
            }
        }
        return null;
    }
}
