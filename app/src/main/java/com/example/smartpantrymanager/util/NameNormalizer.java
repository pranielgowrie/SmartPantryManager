package com.example.smartpantrymanager.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class NameNormalizer {

    private static final Map<String, String> ALIASES =
            new HashMap<>();

    static {
        ALIASES.put("eggs", "egg");
        ALIASES.put("tomatoes", "tomato");
        ALIASES.put("potatoes", "potato");
        ALIASES.put("onions", "onion");
        ALIASES.put("carrots", "carrot");
        ALIASES.put("mushrooms", "mushroom");
        ALIASES.put("peppers", "pepper");
        ALIASES.put("bananas", "banana");
        ALIASES.put("apples", "apple");

        ALIASES.put("bell pepper", "pepper");
        ALIASES.put("bell peppers", "pepper");

        ALIASES.put("spring onion", "green onion");
        ALIASES.put("spring onions", "green onion");
        ALIASES.put("scallion", "green onion");
        ALIASES.put("scallions", "green onion");
    }

    private NameNormalizer() {
        // Utility class
    }

    public static String normalize(String name) {
        String cleanedName = clean(name);

        if (cleanedName.isEmpty()) {
            return "";
        }

        return ALIASES.getOrDefault(
                cleanedName,
                cleanedName
        );
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