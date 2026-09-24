package com.example.smartpantrymanager.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Creates consistent ingredient names for recipe matching.
 */
public final class NameNormalizer {

    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        // Handles common plural ingredient names.
        ALIASES.put("eggs", "egg");
        ALIASES.put("tomatoes", "tomato");
        ALIASES.put("potatoes", "potato");
        ALIASES.put("onions", "onion");
        ALIASES.put("carrots", "carrot");
        ALIASES.put("mushrooms", "mushroom");
        ALIASES.put("peppers", "pepper");
        ALIASES.put("bananas", "banana");
        ALIASES.put("apples", "apple");
        ALIASES.put("tortillas", "tortilla");

        // Handles common alternative ingredient names.
        ALIASES.put("bell pepper", "pepper");
        ALIASES.put("bell peppers", "pepper");
        ALIASES.put("spring onion", "green onion");
        ALIASES.put("spring onions", "green onion");
        ALIASES.put("scallion", "green onion");
        ALIASES.put("scallions", "green onion");
    }

    private NameNormalizer() {
        // Prevents utility class instances.
    }

    public static String normalize(String name) {
        String cleanedName = clean(name);

        if (cleanedName.isEmpty()) {
            return "";
        }

        if (ALIASES.containsKey(cleanedName)) {
            return ALIASES.get(cleanedName);
        }

        return cleanedName;
    }

    public static boolean matches(
            String firstName,
            String secondName) {

        String firstKey = normalize(firstName);
        String secondKey = normalize(secondName);

        return !firstKey.isEmpty()
                && firstKey.equals(secondKey);
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}