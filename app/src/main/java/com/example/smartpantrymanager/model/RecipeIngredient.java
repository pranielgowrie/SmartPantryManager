package com.example.smartpantrymanager.model;

import com.example.smartpantrymanager.util.NameNormalizer;
import com.example.smartpantrymanager.util.UnitConverter;

// Represents one ingredient which is required by a recipe.
public class RecipeIngredient {

    // Database identifiers
    private long id;
    private long recipeId;

    // Ingredient details
    private String name;
    private String key;
    private double quantity;
    private String unit;

    // Createing an empty object for database loading.
    public RecipeIngredient() {
    }

    // Creates a new ingredient before it is saved.
    public RecipeIngredient(
            long recipeId,
            String name,
            double quantity,
            String unit) {

        setRecipeId(recipeId);
        setName(name);
        setQuantity(quantity);
        setUnit(unit);
    }

    // Creating an ingredient loaded from the database.
    public RecipeIngredient(
            long id,
            long recipeId,
            String name,
            double quantity,
            String unit) {

        this(recipeId, name, quantity, unit);
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        // Database IDs cannot be negative.
        if (Long.signum(id) == -1) {
            throw new IllegalArgumentException(
                    "Ingredient ID cannot be negative."
            );
        }

        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        // Allows zero before the recipe is saved.
        if (Long.signum(recipeId) == -1) {
            throw new IllegalArgumentException(
                    "Recipe ID cannot be negative."
            );
        }

        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String cleanedName = clean(name);

        // A recipe ingredient must have a name.
        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingredient name is required."
            );
        }

        this.name = cleanedName;

        // Generates the key used for strict pantry matching.
        this.key = NameNormalizer.normalize(cleanedName);
    }

    public String getKey() {
        return key;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        boolean invalidQuantity =
                Double.isNaN(quantity)
                        || Double.isInfinite(quantity)
                        || Double.compare(quantity, 0.0) != 1;

        // Required quantities must be valid and positive.
        if (invalidQuantity) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        // Converts unit names such as grams into g.
        String normalizedUnit =
                UnitConverter.normalize(unit);

        // Rejects units unsupported by the database.
        if (!UnitConverter.isSupported(normalizedUnit)) {
            throw new IllegalArgumentException(
                    "Unsupported unit."
            );
        }

        this.unit = normalizedUnit;
    }

    // Removes surrounding spaces and handles null values.
    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}