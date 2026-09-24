package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecipeMatcher {

    private static final double TOLERANCE = 0.000001;

    private RecipeMatcher() {
        // Prevents instances of this utility class.
    }

    public static List<Recipe> findMatches(
            List<Recipe> recipes,
            List<PantryItem> pantryItems) {

        if (recipes == null || recipes.isEmpty()) {
            return Collections.emptyList();
        }

        if (pantryItems == null || pantryItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Recipe> matches = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (canMake(recipe, pantryItems)) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    public static boolean canMake(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        if (recipe == null
                || pantryItems == null
                || pantryItems.isEmpty()
                || !recipe.hasIngredients()) {

            return false;
        }

        for (RecipeIngredient required :
                recipe.getIngredients()) {

            if (!hasEnough(required, pantryItems)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasEnough(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        double available = getAvailableQuantity(
                required,
                pantryItems
        );

        return available + TOLERANCE
                >= required.getQuantity();
    }

    private static double getAvailableQuantity(
            RecipeIngredient required,
            List<PantryItem> pantryItems) {

        double total = 0;

        for (PantryItem pantryItem : pantryItems) {
            if (!sameIngredient(required, pantryItem)) {
                continue;
            }

            if (!UnitConverter.canConvert(
                    pantryItem.getUnit(),
                    required.getUnit())) {

                continue;
            }

            total += UnitConverter.convert(
                    pantryItem.getQuantity(),
                    pantryItem.getUnit(),
                    required.getUnit()
            );
        }

        return total;
    }

    private static boolean sameIngredient(
            RecipeIngredient required,
            PantryItem pantryItem) {

        if (required == null || pantryItem == null) {
            return false;
        }

        return NameNormalizer.matches(
                required.getName(),
                pantryItem.getName()
        );
    }
}