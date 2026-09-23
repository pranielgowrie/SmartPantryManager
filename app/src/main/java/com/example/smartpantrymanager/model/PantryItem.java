package com.example.smartpantrymanager.model;

public class PantryItem {

    private long id;
    private String name;
    private String key;
    private double quantity;
    private String unit;
    private String expiry;

    public PantryItem() {
    }

    public PantryItem(
            String name,
            String key,
            double quantity,
            String unit,
            String expiry) {

        this.name = name;
        this.key = key;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public PantryItem(
            long id,
            String name,
            String key,
            double quantity,
            String unit,
            String expiry) {

        this.id = id;
        this.name = name;
        this.key = key;
        this.quantity = quantity;
        this.unit = unit;
        this.expiry = expiry;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = clean(name);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = clean(key).toLowerCase();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        if (quantity <= 0) {
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
        this.unit = clean(unit).toLowerCase();
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry == null ? "" : expiry.trim();
    }

    public boolean hasExpiry() {
        return expiry != null && !expiry.isEmpty();
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}