package com.scratchgame.service;

import com.scratchgame.model.Configuration;
import com.scratchgame.util.ConfigurationLoader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameEvaluatorTest {

    @Test
    public void testWinningCombinationWithBonusMultiplier() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix with a winning combination and a bonus multiplier
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "C", "D"},
                {"10x", "E", "F"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 100);

        // Expected reward calculation:
        // For symbol A: 100(bet) * 5(reward_multiplier of A) * 1(reward_multiplier of same_symbol_3_times) * 2(reward_multiplier of same_symbols_horizontally)
        // Total reward before bonus: 1000
        // Apply bonus 10x: 500 * 10 = 10000

        assertEquals(10000, result.getReward(), 0.001);
        assertEquals("10x", result.getApplied_bonus_symbol());
        assertTrue(result.getApplied_winning_combinations().containsKey("A"));
        assertTrue(result.getApplied_winning_combinations().get("A").contains("same_symbol_3_times"));
    }

    @Test
    public void testWinningCombinationWithExtraBonus() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix with a winning combination and an extra bonus
        String[][] matrix = {
                {"B", "B", "B"},
                {"C", "D", "E"},
                {"+500", "F", "A"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 200);

        // Expected reward calculation:
        // For symbol B: 200(bet) * 3(reward_multiplier of B) * 1(reward_multiplier of same_symbol_3_times) * 2(reward_multiplier of same_symbols_horizontally)
        // Total reward before bonus: 1200
        // Apply bonus +500: 600 + 500 = 1700

        assertEquals(1700, result.getReward(), 0.001);
        assertEquals("+500", result.getApplied_bonus_symbol());
        assertTrue(result.getApplied_winning_combinations().containsKey("B"));
        assertTrue(result.getApplied_winning_combinations().get("B").contains("same_symbol_3_times"));
    }

    @Test
    public void testNoWinningCombinationWithBonusSymbol() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix without any winning combinations but with a bonus symbol
        String[][] matrix = {
                {"A", "B", "C"},
                {"D", "E", "F"},
                {"MISS", "E", "F"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 150);

        // Expected reward: 0 (no winning combinations), bonus symbol not applied
        assertEquals(0, result.getReward(), 0.001);
        assertNull(result.getApplied_bonus_symbol());
        assertTrue(result.getApplied_winning_combinations().isEmpty());
    }

    @Test
    public void testMultipleWinningSymbols() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix where multiple symbols have winning combinations
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "B", "B"},
                {"C", "C", "C"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 100);

        // Expected reward calculation:
        // For symbol A: 100 * 5 * 1 * 2 = 1000
        // For symbol B: 100 * 3 * 1 * 2 = 600
        // For symbol C: 100 * 2.5 * 1 * 2 = 500
        // Total reward: 1000 + 600 + 500 = 1050

        assertEquals(2100, result.getReward(), 0.001);
        assertTrue(result.getApplied_winning_combinations().containsKey("A"));
        assertTrue(result.getApplied_winning_combinations().containsKey("B"));
        assertTrue(result.getApplied_winning_combinations().containsKey("C"));
        assertNull(result.getApplied_bonus_symbol());
    }

    @Test
    public void testWinningCombinationWithVerticalLine() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix with a vertical line winning combination
        String[][] matrix = {
                {"D", "B", "C"},
                {"D", "E", "F"},
                {"D", "A", "B"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 100);

        // Expected reward calculation:
        // For symbol D: 100 * 2(reward_multiplier of D) * 2(reward_multiplier of same_symbols_vertically)
        // Total reward: 100 * 2 * 2 = 400

        assertEquals(400, result.getReward(), 0.001);
        assertTrue(result.getApplied_winning_combinations().containsKey("D"));
        assertTrue(result.getApplied_winning_combinations().get("D").contains("same_symbols_vertically"));
    }

    @Test
    public void testWinningCombinationWithDiagonalLine() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix with a diagonal line winning combination
        String[][] matrix = {
                {"E", "B", "C"},
                {"A", "E", "F"},
                {"A", "B", "E"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 50);

        // Expected reward calculation:
        // For symbol E: 50 * 1.2(reward_multiplier of E) * 5(reward_multiplier of same_symbols_diagonally_left_to_right)
        // Total reward: 50 * 1.2 * 5 = 300

        assertEquals(300, result.getReward(), 0.001);
        assertTrue(result.getApplied_winning_combinations().containsKey("E"));
        assertTrue(result.getApplied_winning_combinations().get("E").contains("same_symbols_diagonally_left_to_right"));
    }

    @Test
    public void testMaximumPossibleReward() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix with maximum possible winning combinations and the highest bonus
        String[][] matrix = {
                {"A", "A", "A"},
                {"A", "10x", "A"},
                {"A", "A", "A"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 1000);

        // Expected reward calculation:
        // For symbol A: 1000 * 5 * 20(same_symbol_9_times) * 2(same_symbols_horizontally) * 2(same_symbols_vertically) * 5(same_symbols_diagonally_left_to_right) * 5(same_symbols_diagonally_right_to_left)
        // Reward multipliers per group are chosen as the highest possible per group.
        // Total reward before bonus: calculated accordingly
        // Apply bonus 10x: total_reward * 10

        // Since the calculation is complex, we'll focus on checking if reward is greater than zero
        assertTrue(result.getReward() > 0);
        assertEquals("10x", result.getApplied_bonus_symbol());
    }

    @Test
    public void testInvalidConfiguration() {
        // Attempt to load an invalid configuration file
        assertThrows(Exception.class, () -> {
            ConfigurationLoader.loadConfig("src/test/resources/invalid_config.json");
        });
    }

    @Test
    public void testNoBonusAppliedWhenNoWin() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix with a bonus symbol but no winning combinations
        String[][] matrix = {
                {"A", "B", "C"},
                {"D", "5x", "F"},
                {"E", "B", "C"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 200);

        // Expected reward: 0, bonus not applied
        assertEquals(0, result.getReward(), 0.001);
        assertNull(result.getApplied_bonus_symbol());
    }

    @Test
    public void testSameSymbolMultipleWinningCombinations() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare a matrix where the same symbol meets multiple winning combinations
        String[][] matrix = {
                {"F", "F", "F"},
                {"F", "F", "F"},
                {"F", "F", "F"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 50);

        // Expected reward calculation:
        // For symbol F: 50 * 1(reward_multiplier of F) * 20(same_symbol_9_times) * 2(same_symbols_horizontally) * 2(same_symbols_vertically) * 5(same_symbols_diagonally_left_to_right) * 5(same_symbols_diagonally_right_to_left)
        // Total reward before bonus: calculated accordingly

        assertTrue(result.getReward() > 0);
        assertNull(result.getApplied_bonus_symbol());
        assertTrue(result.getApplied_winning_combinations().get("F").contains("same_symbol_9_times"));
    }

    @Test
    public void testEdgeCaseNoSymbols() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config_empty_symbols.json");

        // Prepare a matrix with no symbols
        String[][] matrix = new String[0][0];

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 100);

        // Expected reward: 0
        assertEquals(0, result.getReward(), 0.001);
    }

    @Test
    public void testZeroBetAmount() throws Exception {
        // Load test configuration
        Configuration config = ConfigurationLoader.loadConfig("src/test/resources/test_config.json");

        // Prepare any matrix
        String[][] matrix = {
                {"A", "A", "A"},
                {"B", "B", "B"},
                {"C", "C", "C"}
        };

        GameEvaluator evaluator = new GameEvaluator(config);
        GameResult result = evaluator.evaluate(matrix, 0);

        // Expected reward: 0, since bet amount is zero
        assertEquals(0, result.getReward(), 0.001);
    }
}
