package com.scratchgame.service;

import com.scratchgame.model.Configuration;
import com.scratchgame.model.probability.StandardSymbolProbability;
import com.scratchgame.util.ConfigurationLoader;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixGeneratorTest {

    @Test
    public void testMatrixDimensions() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        MatrixGenerator generator = new MatrixGenerator(config);
        String[][] matrix = generator.generate();

        // Expected dimensions from configuration
        int expectedRows = config.getRows();
        int expectedColumns = config.getColumns();

        assertEquals(expectedRows, matrix.length, "Matrix should have correct number of rows");
        for (String[] row : matrix) {
            assertEquals(expectedColumns, row.length, "Each row should have correct number of columns");
        }
    }

    @Test
    public void testSymbolProbabilities() throws Exception {
        // Load test configuration with known probabilities
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config_probabilities.json");

        MatrixGenerator generator = new MatrixGenerator(config);

        // Generate a large number of matrices to get a good distribution
        Map<String, Integer> symbolCounts = new HashMap<>();
        int totalSymbols = config.getRows() * config.getColumns() * 1000;

        for (int i = 0; i < 1000; i++) {
            String[][] matrix = generator.generate();
            for (String[] row : matrix) {
                for (String symbol : row) {
                    symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
                }
            }
        }

        // Calculate expected probabilities
        Map<String, Double> expectedProbabilities = calculateExpectedProbabilities(config);

        // Compare the observed frequencies with expected probabilities
        for (Map.Entry<String, Double> entry : expectedProbabilities.entrySet()) {
            String symbol = entry.getKey();
            double expectedProbability = entry.getValue();
            int observedCount = symbolCounts.getOrDefault(symbol, 0);
            double observedProbability = (double) observedCount / totalSymbols;

            // Allow a margin of error due to randomness
            double margin = 0.05;
            assertTrue(Math.abs(expectedProbability - observedProbability) <= margin,
                    "Symbol '" + symbol + "' should have a probability close to " + expectedProbability);
        }
    }

    private Map<String, Double> calculateExpectedProbabilities(Configuration config) {
        Map<String, Double> probabilities = new HashMap<>();
        Map<String, Integer> totalSymbolWeights = new HashMap<>();

        // Aggregate symbol weights across all cells
        int totalWeight = 0;
        for (int row = 0; row < config.getRows(); row++) {
            for (int col = 0; col < config.getColumns(); col++) {
                Map<String, Integer> symbolWeights = getSymbolWeightsForCell(row, col, config);
                for (Map.Entry<String, Integer> entry : symbolWeights.entrySet()) {
                    String symbol = entry.getKey();
                    int weight = entry.getValue();
                    totalSymbolWeights.put(symbol, totalSymbolWeights.getOrDefault(symbol, 0) + weight);
                    totalWeight += weight;
                }
            }
        }

        // Include bonus symbols
        Map<String, Integer> bonusSymbolWeights = config.getProbabilities().getBonus_symbols().getSymbols();
        for (Map.Entry<String, Integer> entry : bonusSymbolWeights.entrySet()) {
            String symbol = entry.getKey();
            int weight = entry.getValue() * config.getRows() * config.getColumns();
            totalSymbolWeights.put(symbol, totalSymbolWeights.getOrDefault(symbol, 0) + weight);
            totalWeight += weight;
        }

        // Calculate probabilities
        for (Map.Entry<String, Integer> entry : totalSymbolWeights.entrySet()) {
            String symbol = entry.getKey();
            int weight = entry.getValue();
            double probability = (double) weight / totalWeight;
            probabilities.put(symbol, probability);
        }

        return probabilities;
    }


    private Map<String, Integer> getSymbolWeightsForCell(int row, int col, Configuration config) {
        List<StandardSymbolProbability> standardProbabilities = config.getProbabilities().getStandard_symbols();

        // Find the probability configuration for this cell
        for (StandardSymbolProbability prob : standardProbabilities) {
            if (prob.getRow() == row && prob.getColumn() == col) {
                return prob.getSymbols();
            }
        }

        // If no specific configuration, use default (first in the list)
        return standardProbabilities.get(0).getSymbols();
    }

    // Other test methods...

}
