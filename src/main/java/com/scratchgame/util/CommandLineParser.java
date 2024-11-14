package com.scratchgame.util;

public class CommandLineParser {
    private String configFilePath;
    private double bettingAmount;

    public CommandLineParser(String[] args) {
        if (args.length != 4) {
            throw new IllegalArgumentException("Usage: java -jar <your-jar-file> --config <config-file> --betting-amount <amount>");
        }

        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "--config":
                    configFilePath = args[i + 1];
                    break;
                case "--betting-amount":
                    bettingAmount = Double.parseDouble(args[i + 1]);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public double getBettingAmount() {
        return bettingAmount;
    }
}
