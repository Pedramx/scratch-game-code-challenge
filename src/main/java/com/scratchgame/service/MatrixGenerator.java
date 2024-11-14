package com.scratchgame.service;

import com.scratchgame.model.Configuration;
import com.scratchgame.model.probability.StandardSymbolProbability;

import java.util.*;

public class MatrixGenerator {
    private final Configuration config;
    private final Random random;

    public MatrixGenerator(Configuration config) {
        this.config = config;
        this.random = new Random();
    }

    public MatrixGenerator(Configuration config, Random random) {
        this.config = config;
        this.random = random;
    }

    public String[][] generate() {
        int rows = config.getRows();
        int columns = config.getColumns();
        String[][] matrix = new String[rows][columns];

        List<StandardSymbolProbability> standardProbabilities = config.getProbabilities().getStandard_symbols();
        Map<String, Integer> bonusSymbolWeights = config.getProbabilities().getBonus_symbols().getSymbols();
        int totalBonusWeight = bonusSymbolWeights.values().stream().mapToInt(Integer::intValue).sum();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                // Get standard symbol weights for this cell
                Map<String, Integer> symbolWeights = getSymbolWeightsForCell(row, col, standardProbabilities);
                int totalWeight = symbolWeights.values().stream().mapToInt(Integer::intValue).sum();

                // Combine standard and bonus symbols if necessary
                Map<String, Integer> combinedWeights = new HashMap<>(symbolWeights);
                combinedWeights.putAll(bonusSymbolWeights);
                int combinedTotalWeight = totalWeight + totalBonusWeight;

                // Select a symbol based on combined weights
                String symbol = selectSymbol(combinedWeights, combinedTotalWeight);
                matrix[row][col] = symbol;
            }
        }

        return matrix;
    }

    private Map<String, Integer> getSymbolWeightsForCell(int row, int col, List<StandardSymbolProbability> standardProbabilities) {
        // Find the probability configuration for this cell
        for (StandardSymbolProbability prob : standardProbabilities) {
            if (prob.getRow() == row && prob.getColumn() == col) {
                return prob.getSymbols();
            }
        }

        // If no specific configuration, use default (first in the list)
        return standardProbabilities.get(0).getSymbols();
    }

    private String selectSymbol(Map<String, Integer> symbolWeights, int totalWeight) {
        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (Map.Entry<String, Integer> entry : symbolWeights.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue < cumulativeWeight) {
                return entry.getKey();
            }
        }
        // Should not reach here if weights are correct
        throw new IllegalStateException("Failed to select a symbol");
    }
}
