package com.example.smartpantrymanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Recipe {
    private long id;
    private String name;
    private String steps;
    private final List<RecipeIngredient> ingredients;
    public Recipe() {
        ingredients = new ArrayList<>();
    }

    public Recipe(
            String name,
            String steps) {

        this();
        setName(name);
        setSteps(steps);
    }

    public Recipe(
            long id,
            String name,
            String steps) {

        this(name, steps);
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException(
                    "Recipe ID cannot be negative."
            );
        }

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String cleanedName = clean(name);

        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Recipe name is required."
            );
        }

        this.name = cleanedName;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        String cleanedSteps = clean(steps);

        if (cleanedSteps.isEmpty()) {
            throw new IllegalArgumentException(
                    "Preparation steps are required."
            );
        }

        this.steps = cleanedSteps;
    }

    public List<RecipeIngredient> getIngredients() {
        // Prevents direct changes to the internal list.
        return Collections.unmodifiableList(ingredients);
    }

    public void setIngredients(
            List<RecipeIngredient> newIngredients) {

        ingredients.clear();

        if (newIngredients == null) {
            return;
        }

        for (RecipeIngredient ingredient : newIngredients) {
            addIngredient(ingredient);
        }
    }

    public void addIngredient(
            RecipeIngredient ingredient) {

        if (ingredient == null) {
            throw new IllegalArgumentException(
                    "Recipe ingredient is required."
            );
        }

        if (containsIngredient(
                ingredient.getKey(),
                ingredient.getUnit())) {

            throw new IllegalArgumentException(
                    "Recipe ingredient already exists."
            );
        }

        ingredients.add(ingredient);
    }

    public boolean removeIngredient(long ingredientId) {
        return ingredients.removeIf(
                ingredient ->
                        ingredient.getId() == ingredientId
        );
    }

    public boolean hasIngredients() {
        return !ingredients.isEmpty();
    }

    public int getIngredientCount() {
        return ingredients.size();
    }

    private boolean containsIngredient(
            String key,
            String unit) {

        for (RecipeIngredient ingredient : ingredients) {
            boolean sameKey =
                    ingredient.getKey().equalsIgnoreCase(key);

            boolean sameUnit =
                    ingredient.getUnit().equalsIgnoreCase(unit);

            if (sameKey && sameUnit) {
                return true;
            }
        }

        return false;
    }

    private String clean(String value) {
        return value == null
                ? ""
                : value.trim();
    }
}
