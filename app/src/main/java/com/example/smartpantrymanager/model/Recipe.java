package com.example.smartpantrymanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

// Storing a recipe, its preparation steps, and required ingredients.
public class Recipe {

    // Database identifier
    private long id;

    // Recipe information
    private String name;
    private String steps;

    // Ingredients required to make the recipe
    private final List<RecipeIngredient> ingredients;

    // Creating an empty recipe for database loading.
    public Recipe() {
        ingredients = new ArrayList<>();
    }

    // Creating a new recipe before database insertion.
    public Recipe(String name, String steps) {
        this();
        setName(name);
        setSteps(steps);
    }

    // Creating a recipe loaded from the database
    public Recipe(long id, String name, String steps) {
        this(name, steps);
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        // Prevents invalid database identifiers.
        if (Long.signum(id) == -1) {
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

        // Every recipe requires a name.
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

        // Every recipe requires preparation instructions.
        if (cleanedSteps.isEmpty()) {
            throw new IllegalArgumentException(
                    "Preparation steps are required."
            );
        }

        this.steps = cleanedSteps;
    }

    public List<RecipeIngredient> getIngredients() {
        // Prevents external changes to the internal list.
        return Collections.unmodifiableList(ingredients);
    }

    public void setIngredients(
            List<RecipeIngredient> newIngredients) {

        ingredients.clear();

        if (newIngredients == null) {
            return;
        }

        // Applies validation to every ingredient.
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

        // Prevents duplicate ingredient and unit combinations.
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
        Iterator<RecipeIngredient> iterator =
                ingredients.iterator();

        while (iterator.hasNext()) {
            RecipeIngredient ingredient =
                    iterator.next();

            if (ingredient.getId() == ingredientId) {
                iterator.remove();
                return true;
            }
        }

        return false;
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

    // Remove the surrounding spaces and safely handle null values.
    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}