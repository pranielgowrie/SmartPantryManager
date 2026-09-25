package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies strict recipe matching against the current pantry.
 */
public final class RecipeMatcher {

    private static final double TOLERANCE = 0.000001;

    private RecipeMatcher() {
        // Prevents utility class instances.
    }

    /**
     * Returns recipes with every ingredient requirement satisfied.
     */
    public static List<Recipe> findMatches(
            List<Recipe> recipes,
            List<PantryItem> pantryItems) {

        List<Recipe> matches = new ArrayList<>();

        if (recipes == null || pantryItems == null) {
            return matches;
        }

        if (recipes.isEmpty() || pantryItems.isEmpty()) {
            return matches;
        }

        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantryItems)) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    /**
     * Checks whether every required ingredient is available.
     */
    public static boolean canMake(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        if (recipe == null || pantryItems == null) {
            return false;
        }

        if (!recipe.hasIngredients() || pantryItems.isEmpty()) {
            return false;
        }

        // One failed requirement rejects the entire recipe.
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!hasEnough(required, pantryItems)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether enough of one ingredient is available.
     */
    private static boolean hasEnough(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        double available = getAvailableQuantity(
                required,
                pantryItems
        );

        int comparison = Double.compare(
                available + TOLERANCE,
                required.getQuantity()
        );

        return comparison == 0 || comparison == 1;
    }

    /**
     * Combines matching pantry batches after unit conversion.
     */
    private static double getAvailableQuantity(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        double total = 0;

        for (PantryItem item : pantryItems) {
            if (!sameIngredient(required, item)) {
                continue;
            }

            if (!UnitConverter.canConvert(
                    item.getUnit(),
                    required.getUnit())) {

                continue;
            }

            // Converts pantry stock into the recipe's required unit.
            double converted = UnitConverter.convert(
                    item.getQuantity(),
                    item.getUnit(),
                    required.getUnit()
            );

            total += converted;
        }

        return total;
    }

    /**
     * Compares normalized ingredient keys.
     */
    private static boolean sameIngredient(
            RecipeIngredient required,
            PantryItem item) {

        if (required == null || item == null) {
            return false;
        }

        String requiredKey = required.getKey();
        String pantryKey = item.getKey();

        if (requiredKey == null || pantryKey == null) {
            return false;
        }

        return requiredKey.equalsIgnoreCase(pantryKey);
    }
}