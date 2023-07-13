package com.teenkung.enderenigma.utils;

import java.util.Map;
import java.util.Random;

public class ChanceRandomSelector {
    public static <K> K selectByChance(Map<K, Double> map) {
        if (map.isEmpty()) {
            return null;
        }

        float total = 0;
        K highestKey = null;
        double highestChance = 0;

        for (Map.Entry<K, Double> entry : map.entrySet()) {
            double chance = entry.getValue();
            total += chance;
            if (chance > highestChance) {
                highestKey = entry.getKey();
                highestChance = chance;
            }
        }

        if (total == 0) {
            return highestKey;
        }

        float random = new Random().nextFloat() * total;
        float cumulative = 0;

        for (Map.Entry<K, Double> entry : map.entrySet()) {
            double chance = entry.getValue();
            cumulative += chance;
            if (cumulative > random) {
                return entry.getKey();
            }
        }

        return highestKey;
    }

}
