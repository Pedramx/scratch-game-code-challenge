package com.scratchgame;

import com.scratchgame.model.Configuration;
import com.scratchgame.service.GameEvaluator;
import com.scratchgame.service.GameResult;
import com.scratchgame.service.MatrixGenerator;
import com.scratchgame.util.CommandLineParser;
import com.scratchgame.util.ConfigurationLoader;

public class Main {
    public static void main(String[] args) {
        // Parse command line arguments
        CommandLineParser parser = new CommandLineParser(args);
        String configFilePath = parser.getConfigFilePath();
        double bettingAmount = parser.getBettingAmount();

        try {
            // Load configuration
            Configuration config = ConfigurationLoader.loadConfig(configFilePath);

            // Generate matrix
            MatrixGenerator generator = new MatrixGenerator(config);
            String[][] matrix = generator.generate();

            // Evaluate game
            GameEvaluator evaluator = new GameEvaluator(config);
            GameResult result = evaluator.evaluate(matrix, bettingAmount);

            // Output result
            System.out.println(result.toJson());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
