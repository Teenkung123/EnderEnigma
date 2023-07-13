package com.teenkung.enderenigma.utils;

import com.teenkung.enderenigma.config.SubPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeightedRandomSelector {

    /**
     * Select a random item from a map of items and their corresponding weights.
     * @param items a map of items and their corresponding SubPoolConfig from which weights can be obtained.
     * @return the selected item, or null if the map is empty
     */
    public static String select(Map<String, SubPoolConfig> items) {
        // Create a new HashMap to store the items and their weights.
        // We do this to ensure that the original map is not modified by this method.
        Map<String, Integer> weights = new HashMap<>();
        for (Map.Entry<String, SubPoolConfig> entry : items.entrySet()) {
            weights.put(entry.getKey(), entry.getValue().getWeight());
        }

        // Calculate the total weight of all the items in the map.
        int totalWeight = 0;
        for (int weight : weights.values()) {
            totalWeight += weight;
        }

        // Generate a random number between 0 and the total weight.
        Random random = new Random();
        int selectedWeight = random.nextInt(totalWeight);

        // Loop through the items in the map and subtract their weights
        // from the selected weight until we find the selected item.
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            String key = entry.getKey();
            int weight = entry.getValue();

            if (selectedWeight < weight) {
                return key;
            }

            selectedWeight -= weight;
        }

        // If no item was selected, return null.
        return null;
    }
}
