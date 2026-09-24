package com.example.smartpantrymanager.util;

import java.util.Locale;

/**
 * Normalizes and converts supported measurement units.
 */
public final class UnitConverter {

    private static final String MASS = "mass";
    private static final String VOLUME = "volume";
    private static final String COUNT = "count";

    private UnitConverter() {
        // Prevents utility class instances.
    }

    public static String normalize(String unit) {
        if (unit == null) {
            return "";
        }

        String cleanedUnit = unit
                .trim()
                .toLowerCase(Locale.ROOT);

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

    public static boolean canConvert(
            String firstUnit,
            String secondUnit) {

        String firstType = getType(firstUnit);
        String secondType = getType(secondUnit);

        return !firstType.isEmpty()
                && firstType.equals(secondType);
    }

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

        double baseQuantity = toBase(
                quantity,
                sourceUnit
        );

        return fromBase(
                baseQuantity,
                targetUnit
        );
    }

    private static double toBase(
            double quantity,
            String unit) {

        switch (unit) {
            case "kg":
            case "l":
                return quantity * 1000;

            case "tbsp":
                return quantity * 15;

            case "tsp":
                return quantity * 5;

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

    private static double fromBase(
            double quantity,
            String unit) {

        switch (unit) {
            case "kg":
            case "l":
                return quantity / 1000;

            case "tbsp":
                return quantity / 15;

            case "tsp":
                return quantity / 5;

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

    private static void validateQuantity(double quantity) {
        if (Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || quantity < 0) {

            throw new IllegalArgumentException(
                    "Quantity must be a valid non-negative number."
            );
        }
    }
}