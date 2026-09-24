package com.example.smartpantrymanager.model;

import com.example.smartpantrymanager.util.NameNormalizer;
import com.example.smartpantrymanager.util.UnitConverter;

public class RecipeIngredient {

    private long id;
    private long recipeId;
    private String name;
    private String key;
    private double quantity;
    private String unit;

    public RecipeIngredient() {
    }

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
        if (id < 0) {
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
        if (recipeId < 0) {
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

        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingredient name is required."
            );
        }

        this.name = cleanedName;
        this.key = NameNormalizer.normalize(cleanedName);
    }

    public String getKey() {
        return key;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (!Double.isFinite(quantity)
                || quantity <= 0) {

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
        String normalizedUnit =
                UnitConverter.normalize(unit);

        if (!UnitConverter.isSupported(normalizedUnit)) {
            throw new IllegalArgumentException(
                    "Unsupported unit."
            );
        }

        this.unit = normalizedUnit;
    }

    private String clean(String value) {
        return value == null
                ? ""
                : value.trim();
    }
}