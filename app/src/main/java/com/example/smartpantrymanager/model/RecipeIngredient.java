package com.example.smartpantrymanager.model;

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
            String key,
            double quantity,
            String unit) {

        setRecipeId(recipeId);
        setName(name);
        setKey(key);
        setQuantity(quantity);
        setUnit(unit);
    }

    public RecipeIngredient(
            long id,
            long recipeId,
            String name,
            String key,
            double quantity,
            String unit) {

        this(
                recipeId,
                name,
                key,
                quantity,
                unit
        );

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
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        String cleanedKey = clean(key).toLowerCase();

        if (cleanedKey.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingredient key is required."
            );
        }

        this.key = cleanedKey;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (!Double.isFinite(quantity) || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be a valid number greater than zero."
            );
        }

        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        String cleanedUnit = clean(unit).toLowerCase();

        if (!isSupportedUnit(cleanedUnit)) {
            throw new IllegalArgumentException(
                    "Unsupported ingredient unit."
            );
        }

        this.unit = cleanedUnit;
    }

    private boolean isSupportedUnit(String unit) {
        return unit.equals("item")
                || unit.equals("g")
                || unit.equals("kg")
                || unit.equals("ml")
                || unit.equals("l")
                || unit.equals("tsp")
                || unit.equals("tbsp");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}