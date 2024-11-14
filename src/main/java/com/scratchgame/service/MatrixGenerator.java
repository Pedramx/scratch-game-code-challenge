package com.scratchgame.service;

import com.scratchgame.model.Configuration;
import com.scratchgame.model.probability.StandardSymbolProbability;

import java.util.*;

public class MatrixGenerator {
    private final Configuration config;
    private final Random random = new Random();

    public MatrixGenerator(Configuration config) {
        this.config = config;
    }

    public String[][] generateMatrix() {
        int rows = config.getRows();
        int columns = config.getColumns();
        String[][] matrix = new String[rows][columns];

        // Generate standard symbols
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                String symbol = generateStandardSymbol(row, col);
                matrix[row][col] = symbol;
            }
        }

        // Place bonus symbols
        placeBonusSymbols(matrix);

        return matrix;
    }

    private String generateStandardSymbol(int row, int col) {
        Map<String, Integer> symbolProbabilities = getStandardSymbolProbabilities(row, col);
        int totalProbability = symbolProbabilities.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(totalProbability) + 1;
        int cumulativeProbability = 0;
        for (Map.Entry<String, Integer> entry : symbolProbabilities.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (rand <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Failed to generate standard symbol");
    }

    private Map<String, Integer> getStandardSymbolProbabilities(int row, int col) {
        List<StandardSymbolProbability> standardSymbols = config.getProbabilities().getStandard_symbols();
        for (StandardSymbolProbability prob : standardSymbols) { // TODO: use stream instead
            if (prob.getRow() == row && prob.getColumn() == col) {
                return prob.getSymbols();
            }
        }
        // Use the first one if not found
        if (!standardSymbols.isEmpty()) {
            return standardSymbols.getFirst().getSymbols();
        }
        throw new RuntimeException("No standard symbol probabilities defined");
    }

    private void placeBonusSymbols(String[][] matrix) {
        int rows = matrix.length;
        int columns = matrix[0].length;
        Map<String, Integer> bonusProbabilities = config.getProbabilities().getBonus_symbols().getSymbols();
        int totalBonusProbability = bonusProbabilities.values().stream().mapToInt(Integer::intValue).sum();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int rand = random.nextInt(100);
                if (rand < 10) { // 10% chance to place a bonus symbol
                    String bonusSymbol = generateBonusSymbol(bonusProbabilities, totalBonusProbability);
                    if (bonusSymbol != null) {
                        matrix[row][col] = bonusSymbol;
                    }
                }
            }
        }
    }

    private String generateBonusSymbol(Map<String, Integer> bonusProbabilities, int totalProbability) {
        int rand = random.nextInt(totalProbability) + 1;
        int cumulativeProbability = 0;
        for (Map.Entry<String, Integer> entry : bonusProbabilities.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (rand <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        return null;
    }
}
