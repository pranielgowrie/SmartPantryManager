package com.example.smartpantrymanager.model;

import com.example.smartpantrymanager.util.NameNormalizer;
import com.example.smartpantrymanager.util.UnitConverter;

// Represents one ingredient batch stored in the pantry.
public class PantryItem {

    // Database identifier
    private long id;

    // Pantry item details
    private String name;
    private String key;
    private double quantity;
    private String unit;
    private String expiry;

    // Creates an empty pantry item for database loading.
    public PantryItem() {
    }

    // Creates a new pantry item
    public PantryItem(
            String name,
            double quantity,
            String unit,
            String expiry) {

        setName(name);
        setQuantity(quantity);
        setUnit(unit);
        setExpiry(expiry);
    }

    // Creates a pantry item loaded from the database
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
        // Rejects negative database identifiers.
        if (Long.signum(id) == -1) {
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

        // Every pantry item requires a name.
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
        boolean invalidNumber =
                Double.isNaN(quantity)
                        || Double.isInfinite(quantity);

        boolean notPositive =
                Double.compare(quantity, 0.0) != 1;

        // Quantity must be a valid positive number.
        if (invalidNumber || notPositive) {
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
        // Converts names such as grams into g.
        String normalizedUnit =
                UnitConverter.normalize(unit);

        // Rejects unsupported measurement units.
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
        // Expiry date is optional.
        if (expiry == null) {
            this.expiry = "";
            return;
        }

        this.expiry = expiry.trim();
    }

    public boolean hasExpiry() {
        return expiry != null && !expiry.isEmpty();
    }

    // Removes surrounding spaces and handles null values.
    private String clean(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}