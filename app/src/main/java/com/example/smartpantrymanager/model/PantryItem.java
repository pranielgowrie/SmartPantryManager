package com.example.smartpantrymanager.model;

import com.example.smartpantrymanager.util.NameNormalizer;
import com.example.smartpantrymanager.util.UnitConverter;

// Represents one ingredient batch stored in the pantry.
public class PantryItem {
    private long id;
    private String name;
    private String key;
    private double quantity;
    private String unit;
    private String expiry;
    public PantryItem() {
    }

    public PantryItem(String name, double quantity, String unit, String expiry) {
        setName(name);
        setQuantity(quantity);
        setUnit(unit);
        setExpiry(expiry);
    }

    public PantryItem(
            long id,
            String name,
            double quantity,
            String unit,
            String expiry) {

        this(name, quantity, unit, expiry);
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException(
                    "Item ID cannot be negative."
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
                    "Ingredient name is required."
            );
        }

        this.name = cleanedName;

        // Generates the key used for recipe matching.
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
                        || quantity <= 0;

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
        String normalizedUnit = UnitConverter.normalize(unit);

        if (!UnitConverter.isSupported(normalizedUnit)) {
            throw new IllegalArgumentException(
                    "Unsupported unit."
            );
        }

        this.unit = normalizedUnit;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        if (expiry == null) {
            this.expiry = "";
        } else {
            this.expiry = expiry.trim();
        }
    }

    public boolean hasExpiry() {
        return expiry != null && !expiry.isEmpty();
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}