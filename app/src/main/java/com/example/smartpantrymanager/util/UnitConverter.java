package com.example.smartpantrymanager.util;

import java.util.Locale;

// Normalizes and converts supported units.
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public final class UnitConverter {

    // Unit categories
    private static final String MASS = "mass";
    private static final String VOLUME = "volume";
    private static final String COUNT = "count";

    private UnitConverter() {
        // Prevents utility class instances.
    }

    // Normalizes unit names.
    public static String normalize(String unit) {
        if (unit == null) {
            return "";
        }

        String cleanedUnit =
                unit.trim().toLowerCase(Locale.ROOT);

        switch (cleanedUnit) {
            case "gram":
            case "grams":
                return "g";

            case "kilogram":
            case "kilograms":
                return "kg";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "teaspoon":
            case "teaspoons":
                return "tsp";

            case "tablespoon":
            case "tablespoons":
                return "tbsp";

            case "items":
            case "piece":
            case "pieces":
                return "item";

            default:
                return cleanedUnit;
        }
    }

    // Checks whether a unit is supported.
    public static boolean isSupported(String unit) {
        String normalizedUnit = normalize(unit);

        switch (normalizedUnit) {
            case "item":
            case "g":
            case "kg":
            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
                return true;

            default:
                return false;
        }
    }

    // Checks whether two units are compatible.
    public static boolean canConvert(
            String firstUnit,
            String secondUnit) {

        String firstType = getType(firstUnit);
        String secondType = getType(secondUnit);

        return !firstType.isEmpty()
                && firstType.equals(secondType);
    }

    // Converts a quantity between compatible units.
    public static double convert(
            double quantity,
            String fromUnit,
            String toUnit) {

        validateQuantity(quantity);

        String sourceUnit = normalize(fromUnit);
        String targetUnit = normalize(toUnit);

        if (!canConvert(sourceUnit, targetUnit)) {
            throw new IllegalArgumentException(
                    "Units are not compatible."
            );
        }

        double baseQuantity =
                toBase(quantity, sourceUnit);

        return fromBase(
                baseQuantity,
                targetUnit
        );
    }

    // Converts a quantity to its base unit.
    private static double toBase(
            double quantity,
            String unit) {

        switch (unit) {
            case "kg":
            case "l":
                return quantity * 1000.0;

            case "tbsp":
                return quantity * 15.0;

            case "tsp":
                return quantity * 5.0;

            case "g":
            case "ml":
            case "item":
                return quantity;

            default:
                throw new IllegalArgumentException(
                        "Unsupported unit."
                );
        }
    }

    // Converts a base quantity to the target unit.
    private static double fromBase(
            double quantity,
            String unit) {

        switch (unit) {
            case "kg":
            case "l":
                return quantity / 1000.0;

            case "tbsp":
                return quantity / 15.0;

            case "tsp":
                return quantity / 5.0;

            case "g":
            case "ml":
            case "item":
                return quantity;

            default:
                throw new IllegalArgumentException(
                        "Unsupported unit."
                );
        }
    }

    // Returns the unit category.
    private static String getType(String unit) {
        String normalizedUnit = normalize(unit);

        switch (normalizedUnit) {
            case "g":
            case "kg":
                return MASS;

            case "ml":
            case "l":
            case "tsp":
            case "tbsp":
                return VOLUME;

            case "item":
                return COUNT;

            default:
                return "";
        }
    }

    // Validates a conversion quantity.
    private static void validateQuantity(double quantity) {
        boolean invalid =
                Double.isNaN(quantity)
                        || Double.isInfinite(quantity)
                        || quantity < 0;

        if (invalid) {
            throw new IllegalArgumentException(
                    "Quantity must be a valid non-negative number."
            );
        }
    }
}