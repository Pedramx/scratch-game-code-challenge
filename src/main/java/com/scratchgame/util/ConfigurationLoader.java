package com.scratchgame.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scratchgame.model.Configuration;
import com.scratchgame.model.WinningCombination;

import java.io.File;
import java.util.List;

public class ConfigurationLoader {
    public static Configuration loadConfig(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Configuration configuration = mapper.readValue(new File(filePath), Configuration.class);
        return enrichConfig(configuration);
    }

    private static Configuration enrichConfig(Configuration configuration) {
        var winningCombination = configuration.getWin_combinations();

        if (winningCombination == null) {
            return configuration;
        }

        for (String combName : winningCombination.keySet()) {
            var combination = winningCombination.get(combName);
            combination.setName(combName);
        }

        return configuration;
    }


}
