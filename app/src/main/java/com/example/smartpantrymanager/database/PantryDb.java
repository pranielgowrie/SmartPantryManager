package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class PantryDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Pantry table
    private static final String PANTRY_TABLE = "pantry_items";
    private static final String PANTRY_ID = "id";
    private static final String PANTRY_NAME = "ingredient_name";
    private static final String PANTRY_KEY = "normalized_name";
    private static final String PANTRY_QTY = "quantity";
    private static final String PANTRY_UNIT = "unit";
    private static final String PANTRY_EXPIRY = "expiry_date";

    // Recipe table
    private static final String RECIPE_TABLE = "recipes";
    private static final String RECIPE_ID = "id";
    private static final String RECIPE_NAME = "recipe_name";
    private static final String RECIPE_STEPS = "preparation_steps";

    // Recipe ingredient table
    private static final String INGREDIENT_TABLE = "recipe_ingredients";
    private static final String INGREDIENT_ID = "id";
    private static final String INGREDIENT_RECIPE_ID = "recipe_id";
    private static final String INGREDIENT_NAME = "ingredient_name";
    private static final String INGREDIENT_KEY = "normalized_name";
    private static final String INGREDIENT_QTY = "required_quantity";
    private static final String INGREDIENT_UNIT = "unit";

    // Settings table
    private static final String SETTINGS_TABLE = "app_settings";
    private static final String SETTINGS_KEY = "setting_key";
    private static final String SETTINGS_VALUE = "setting_value";

    public PantryDb(Context context) {
        super(
                context.getApplicationContext(),
                DB_NAME,
                null,
                DB_VERSION
        );
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        // Enforces valid recipe relationships.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        createIndexes(db);
        insertDefaultSettings(db);
        seedRecipes(db);
    }

    private void createTables(SQLiteDatabase db) {
        createPantryTable(db);
        createRecipeTable(db);
        createIngredientTable(db);
        createSettingsTable(db);
    }

    private void createPantryTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + PANTRY_TABLE + " (" +
                        PANTRY_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        PANTRY_NAME +
                        " TEXT NOT NULL " +
                        "CHECK(LENGTH(TRIM(" + PANTRY_NAME + ")) > 0), " +

                        PANTRY_KEY +
                        " TEXT NOT NULL COLLATE NOCASE " +
                        "CHECK(LENGTH(TRIM(" + PANTRY_KEY + ")) > 0), " +

                        PANTRY_QTY +
                        " REAL NOT NULL " +
                        "CHECK(" + PANTRY_QTY + " > 0), " +

                        PANTRY_UNIT +
                        " TEXT NOT NULL " +
                        "CHECK(" + PANTRY_UNIT + " IN " +
                        "('item', 'g', 'kg', 'ml', 'l', 'tsp', 'tbsp')), " +

                        PANTRY_EXPIRY +
                        " TEXT NOT NULL DEFAULT '', " +

                        "UNIQUE(" +
                        PANTRY_KEY + ", " +
                        PANTRY_UNIT + ", " +
                        PANTRY_EXPIRY +
                        ")" +
                        ")";

        db.execSQL(sql);
    }

    private void createRecipeTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + RECIPE_TABLE + " (" +
                        RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_NAME +
                        " TEXT NOT NULL COLLATE NOCASE " +
                        "CHECK(LENGTH(TRIM(" + RECIPE_NAME + ")) > 0), " +

                        RECIPE_STEPS +
                        " TEXT NOT NULL " +
                        "CHECK(LENGTH(TRIM(" + RECIPE_STEPS + ")) > 0), " +

                        "UNIQUE(" + RECIPE_NAME + ")" +
                        ")";

        db.execSQL(sql);
    }

    private void createIngredientTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + INGREDIENT_TABLE + " (" +
                        INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        INGREDIENT_RECIPE_ID +
                        " INTEGER NOT NULL, " +

                        INGREDIENT_NAME +
                        " TEXT NOT NULL " +
                        "CHECK(LENGTH(TRIM(" + INGREDIENT_NAME + ")) > 0), " +

                        INGREDIENT_KEY +
                        " TEXT NOT NULL COLLATE NOCASE " +
                        "CHECK(LENGTH(TRIM(" + INGREDIENT_KEY + ")) > 0), " +

                        INGREDIENT_QTY +
                        " REAL NOT NULL " +
                        "CHECK(" + INGREDIENT_QTY + " > 0), " +

                        INGREDIENT_UNIT +
                        " TEXT NOT NULL " +
                        "CHECK(" + INGREDIENT_UNIT + " IN " +
                        "('item', 'g', 'kg', 'ml', 'l', 'tsp', 'tbsp')), " +

                        "FOREIGN KEY(" + INGREDIENT_RECIPE_ID + ") " +
                        "REFERENCES " + RECIPE_TABLE +
                        "(" + RECIPE_ID + ") ON DELETE CASCADE, " +

                        "UNIQUE(" +
                        INGREDIENT_RECIPE_ID + ", " +
                        INGREDIENT_KEY + ", " +
                        INGREDIENT_UNIT +
                        ")" +
                        ")";

        db.execSQL(sql);
    }

    private void createSettingsTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE " + SETTINGS_TABLE + " (" +
                        SETTINGS_KEY +
                        " TEXT PRIMARY KEY, " +

                        SETTINGS_VALUE +
                        " TEXT NOT NULL" +
                        ")";

        db.execSQL(sql);
    }

    private void createIndexes(SQLiteDatabase db) {
        db.execSQL(
                "CREATE INDEX idx_pantry_key ON " +
                        PANTRY_TABLE + "(" + PANTRY_KEY + ")"
        );

        db.execSQL(
                "CREATE INDEX idx_pantry_expiry ON " +
                        PANTRY_TABLE + "(" + PANTRY_EXPIRY + ")"
        );

        db.execSQL(
                "CREATE INDEX idx_ingredient_recipe ON " +
                        INGREDIENT_TABLE +
                        "(" + INGREDIENT_RECIPE_ID + ")"
        );

        db.execSQL(
                "CREATE INDEX idx_ingredient_key ON " +
                        INGREDIENT_TABLE +
                        "(" + INGREDIENT_KEY + ")"
        );
    }

    // Pantry CRUD
    public long addItem(PantryItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Pantry item is required."
            );
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            long existingId = findMatchingItemId(
                    db,
                    item.getKey(),
                    item.getUnit(),
                    item.getExpiry(),
                    -1
            );

            if (existingId != -1) {
                double currentQuantity =
                        getItemQuantity(db, existingId);

                ContentValues values = new ContentValues();
                values.put(
                        PANTRY_QTY,
                        currentQuantity + item.getQuantity()
                );

                db.update(
                        PANTRY_TABLE,
                        values,
                        PANTRY_ID + " = ?",
                        new String[]{
                                String.valueOf(existingId)
                        }
                );

                db.setTransactionSuccessful();
                return existingId;
            }

            long newId = db.insertOrThrow(
                    PANTRY_TABLE,
                    null,
                    createItemValues(item)
            );

            db.setTransactionSuccessful();
            return newId;
        } finally {
            db.endTransaction();
        }
    }

    public List<PantryItem> getItems() {
        List<PantryItem> items = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                PANTRY_TABLE,
                null,
                null,
                null,
                null,
                null,
                PANTRY_NAME + " COLLATE NOCASE ASC"
        )) {
            while (cursor.moveToNext()) {
                items.add(readItem(cursor));
            }
        }

        return items;
    }

    public PantryItem getItem(long itemId) {
        if (itemId <= 0) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                PANTRY_TABLE,
                null,
                PANTRY_ID + " = ?",
                new String[]{String.valueOf(itemId)},
                null,
                null,
                null,
                "1"
        )) {
            if (cursor.moveToFirst()) {
                return readItem(cursor);
            }
        }

        return null;
    }

    public boolean updateItem(PantryItem item) {
        if (item == null || item.getId() <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            long matchingId = findMatchingItemId(
                    db,
                    item.getKey(),
                    item.getUnit(),
                    item.getExpiry(),
                    item.getId()
            );

            if (matchingId != -1) {
                double matchingQuantity =
                        getItemQuantity(db, matchingId);

                ContentValues mergeValues =
                        new ContentValues();

                mergeValues.put(
                        PANTRY_QTY,
                        matchingQuantity + item.getQuantity()
                );

                db.update(
                        PANTRY_TABLE,
                        mergeValues,
                        PANTRY_ID + " = ?",
                        new String[]{
                                String.valueOf(matchingId)
                        }
                );

                db.delete(
                        PANTRY_TABLE,
                        PANTRY_ID + " = ?",
                        new String[]{
                                String.valueOf(item.getId())
                        }
                );

                db.setTransactionSuccessful();
                return true;
            }

            int rows = db.update(
                    PANTRY_TABLE,
                    createItemValues(item),
                    PANTRY_ID + " = ?",
                    new String[]{
                            String.valueOf(item.getId())
                    }
            );

            db.setTransactionSuccessful();
            return rows > 0;
        } finally {
            db.endTransaction();
        }
    }

    public boolean deleteItem(long itemId) {
        if (itemId <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                PANTRY_TABLE,
                PANTRY_ID + " = ?",
                new String[]{String.valueOf(itemId)}
        );

        return rows > 0;
    }

    private ContentValues createItemValues(
            PantryItem item) {

        ContentValues values = new ContentValues();

        values.put(PANTRY_NAME, item.getName());
        values.put(PANTRY_KEY, item.getKey());
        values.put(PANTRY_QTY, item.getQuantity());
        values.put(PANTRY_UNIT, item.getUnit());
        values.put(PANTRY_EXPIRY, item.getExpiry());

        return values;
    }

    private PantryItem readItem(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(PANTRY_ID)
        );

        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(PANTRY_NAME)
        );

        double quantity = cursor.getDouble(
                cursor.getColumnIndexOrThrow(PANTRY_QTY)
        );

        String unit = cursor.getString(
                cursor.getColumnIndexOrThrow(PANTRY_UNIT)
        );

        String expiry = cursor.getString(
                cursor.getColumnIndexOrThrow(PANTRY_EXPIRY)
        );

        return new PantryItem(
                id,
                name,
                quantity,
                unit,
                expiry
        );
    }

    private long findMatchingItemId(
            SQLiteDatabase db,
            String key,
            String unit,
            String expiry,
            long excludedId) {

        String selection =
                PANTRY_KEY + " = ? COLLATE NOCASE AND " +
                        PANTRY_UNIT + " = ? AND " +
                        PANTRY_EXPIRY + " = ?";

        List<String> arguments = new ArrayList<>();
        arguments.add(key);
        arguments.add(unit);
        arguments.add(expiry);

        if (excludedId > 0) {
            selection += " AND " + PANTRY_ID + " != ?";
            arguments.add(String.valueOf(excludedId));
        }

        try (Cursor cursor = db.query(
                PANTRY_TABLE,
                new String[]{PANTRY_ID},
                selection,
                arguments.toArray(new String[0]),
                null,
                null,
                null,
                "1"
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getLong(
                        cursor.getColumnIndexOrThrow(PANTRY_ID)
                );
            }
        }

        return -1;
    }

    private double getItemQuantity(
            SQLiteDatabase db,
            long itemId) {

        try (Cursor cursor = db.query(
                PANTRY_TABLE,
                new String[]{PANTRY_QTY},
                PANTRY_ID + " = ?",
                new String[]{String.valueOf(itemId)},
                null,
                null,
                null,
                "1"
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getDouble(
                        cursor.getColumnIndexOrThrow(PANTRY_QTY)
                );
            }
        }

        return 0;
    }

    // Recipe queries
    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                RECIPE_TABLE,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " COLLATE NOCASE ASC"
        )) {
            while (cursor.moveToNext()) {
                Recipe recipe = readRecipe(cursor);

                recipe.setIngredients(
                        getRecipeIngredients(
                                db,
                                recipe.getId()
                        )
                );

                recipes.add(recipe);
            }
        }

        return recipes;
    }

    public Recipe getRecipe(long recipeId) {
        if (recipeId <= 0) {
            return null;
        }

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                RECIPE_TABLE,
                null,
                RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                null,
                "1"
        )) {
            if (cursor.moveToFirst()) {
                Recipe recipe = readRecipe(cursor);

                recipe.setIngredients(
                        getRecipeIngredients(
                                db,
                                recipeId
                        )
                );

                return recipe;
            }
        }

        return null;
    }

    private Recipe readRecipe(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndexOrThrow(RECIPE_ID)
        );

        String name = cursor.getString(
                cursor.getColumnIndexOrThrow(RECIPE_NAME)
        );

        String steps = cursor.getString(
                cursor.getColumnIndexOrThrow(RECIPE_STEPS)
        );

        return new Recipe(id, name, steps);
    }

    private List<RecipeIngredient> getRecipeIngredients(
            SQLiteDatabase db,
            long recipeId) {

        List<RecipeIngredient> ingredients =
                new ArrayList<>();

        try (Cursor cursor = db.query(
                INGREDIENT_TABLE,
                null,
                INGREDIENT_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                INGREDIENT_ID + " ASC"
        )) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                                INGREDIENT_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                INGREDIENT_NAME
                        )
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                INGREDIENT_QTY
                        )
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                INGREDIENT_UNIT
                        )
                );

                ingredients.add(
                        new RecipeIngredient(
                                id,
                                recipeId,
                                name,
                                quantity,
                                unit
                        )
                );
            }
        }

        return ingredients;
    }

    // Settings
    public String getSetting(
            String key,
            String defaultValue) {

        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                SETTINGS_TABLE,
                new String[]{SETTINGS_VALUE},
                SETTINGS_KEY + " = ?",
                new String[]{key},
                null,
                null,
                null,
                "1"
        )) {
            if (cursor.moveToFirst()) {
                return cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                SETTINGS_VALUE
                        )
                );
            }
        }

        return defaultValue;
    }

    public void saveSetting(
            String key,
            String value) {

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Setting key is required."
            );
        }

        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY, key.trim());
        values.put(
                SETTINGS_VALUE,
                value == null ? "" : value.trim()
        );

        getWritableDatabase().insertWithOnConflict(
                SETTINGS_TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    private void insertDefaultSettings(SQLiteDatabase db) {
        insertSetting(db, "expiry_alerts", "true");
        insertSetting(db, "expiry_days", "3");
        insertSetting(db, "preferred_units", "metric");
    }

    private void insertSetting(
            SQLiteDatabase db,
            String key,
            String value) {

        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY, key);
        values.put(SETTINGS_VALUE, value);

        db.insertWithOnConflict(
                SETTINGS_TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    // Recipe seed data
    private void seedRecipes(SQLiteDatabase db) {
        addRecipe(
                db,
                "Scrambled Eggs",
                "Beat the eggs. Melt the butter in a pan. " +
                        "Cook while stirring until set.",
                new Object[][]{
                        {"Eggs", 2.0, "item"},
                        {"Butter", 10.0, "g"}
                }
        );

        addRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs. Melt the butter in a pan. " +
                        "Add the eggs and cheese, then fold.",
                new Object[][]{
                        {"Eggs", 2.0, "item"},
                        {"Cheese", 50.0, "g"},
                        {"Butter", 10.0, "g"}
                }
        );

        addRecipe(
                db,
                "Boiled Eggs",
                "Bring the water to a boil. Add the eggs and " +
                        "cook until the desired firmness.",
                new Object[][]{
                        {"Eggs", 2.0, "item"},
                        {"Water", 500.0, "ml"}
                }
        );

        addRecipe(
                db,
                "French Toast",
                "Beat the egg with milk. Dip the bread into " +
                        "the mixture and fry in butter.",
                new Object[][]{
                        {"Bread", 2.0, "item"},
                        {"Egg", 1.0, "item"},
                        {"Milk", 100.0, "ml"},
                        {"Butter", 10.0, "g"}
                }
        );

        addRecipe(
                db,
                "Pancakes",
                "Mix the flour, milk and egg. Cook portions " +
                        "of batter in a buttered pan.",
                new Object[][]{
                        {"Flour", 150.0, "g"},
                        {"Milk", 200.0, "ml"},
                        {"Egg", 1.0, "item"},
                        {"Butter", 10.0, "g"}
                }
        );

        addRecipe(
                db,
                "Grilled Cheese",
                "Butter the bread, add cheese and cook in a " +
                        "pan until golden.",
                new Object[][]{
                        {"Bread", 2.0, "item"},
                        {"Cheese", 60.0, "g"},
                        {"Butter", 10.0, "g"}
                }
        );

        addRecipe(
                db,
                "Tomato Sandwich",
                "Slice the tomato. Butter the bread and add " +
                        "the tomato slices.",
                new Object[][]{
                        {"Bread", 2.0, "item"},
                        {"Tomato", 100.0, "g"},
                        {"Butter", 10.0, "g"}
                }
        );

        addRecipe(
                db,
                "Tuna Sandwich",
                "Mix tuna with mayonnaise and place the " +
                        "mixture between the bread slices.",
                new Object[][]{
                        {"Bread", 2.0, "item"},
                        {"Tuna", 100.0, "g"},
                        {"Mayonnaise", 30.0, "g"}
                }
        );

        addRecipe(
                db,
                "Egg Fried Rice",
                "Heat oil in a pan. Add rice and beaten eggs, " +
                        "then cook until the eggs are set.",
                new Object[][]{
                        {"Rice", 200.0, "g"},
                        {"Eggs", 2.0, "item"},
                        {"Oil", 15.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Vegetable Fried Rice",
                "Heat oil. Cook the onion and carrot, then " +
                        "add rice and stir until hot.",
                new Object[][]{
                        {"Rice", 200.0, "g"},
                        {"Onion", 50.0, "g"},
                        {"Carrot", 50.0, "g"},
                        {"Oil", 15.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Chicken Fried Rice",
                "Cook the chicken in oil. Add rice and egg, " +
                        "then stir until fully cooked.",
                new Object[][]{
                        {"Rice", 200.0, "g"},
                        {"Chicken", 150.0, "g"},
                        {"Egg", 1.0, "item"},
                        {"Oil", 15.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta. Cook the tomato in oil and " +
                        "combine with the drained pasta.",
                new Object[][]{
                        {"Pasta", 200.0, "g"},
                        {"Tomato", 200.0, "g"},
                        {"Oil", 15.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Garlic Pasta",
                "Cook the pasta. Gently fry garlic in oil, " +
                        "then combine with the pasta.",
                new Object[][]{
                        {"Pasta", 200.0, "g"},
                        {"Garlic", 20.0, "g"},
                        {"Oil", 20.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Mashed Potatoes",
                "Boil the potatoes until soft. Drain and mash " +
                        "with milk and butter.",
                new Object[][]{
                        {"Potatoes", 500.0, "g"},
                        {"Milk", 100.0, "ml"},
                        {"Butter", 20.0, "g"}
                }
        );

        addRecipe(
                db,
                "Roast Potatoes",
                "Cut the potatoes, coat with oil and roast " +
                        "until golden and tender.",
                new Object[][]{
                        {"Potatoes", 500.0, "g"},
                        {"Oil", 30.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Banana Oatmeal",
                "Cook oats with milk until thick. Slice the " +
                        "banana and add it before serving.",
                new Object[][]{
                        {"Oats", 100.0, "g"},
                        {"Milk", 250.0, "ml"},
                        {"Banana", 1.0, "item"}
                }
        );

        addRecipe(
                db,
                "Apple Oatmeal",
                "Cook oats with milk until thick. Chop the " +
                        "apple and add it before serving.",
                new Object[][]{
                        {"Oats", 100.0, "g"},
                        {"Milk", 250.0, "ml"},
                        {"Apple", 1.0, "item"}
                }
        );

        addRecipe(
                db,
                "Tomato Soup",
                "Cook tomato and onion until soft. Add water, " +
                        "simmer and blend until smooth.",
                new Object[][]{
                        {"Tomatoes", 400.0, "g"},
                        {"Onion", 100.0, "g"},
                        {"Water", 500.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Potato Soup",
                "Cook potato and onion until soft. Add milk " +
                        "and blend to the desired texture.",
                new Object[][]{
                        {"Potatoes", 400.0, "g"},
                        {"Onion", 100.0, "g"},
                        {"Milk", 250.0, "ml"}
                }
        );

        addRecipe(
                db,
                "Cheese Quesadilla",
                "Place cheese between the tortillas and cook " +
                        "in a pan until melted and golden.",
                new Object[][]{
                        {"Tortillas", 2.0, "item"},
                        {"Cheese", 100.0, "g"}
                }
        );
    }

    private void addRecipe(
            SQLiteDatabase db,
            String name,
            String steps,
            Object[][] ingredients) {

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RECIPE_NAME, name);
        recipeValues.put(RECIPE_STEPS, steps);

        long recipeId = db.insertOrThrow(
                RECIPE_TABLE,
                null,
                recipeValues
        );

        for (Object[] ingredientData : ingredients) {
            String ingredientName =
                    (String) ingredientData[0];

            double quantity =
                    (double) ingredientData[1];

            String unit =
                    (String) ingredientData[2];

            RecipeIngredient ingredient =
                    new RecipeIngredient(
                            recipeId,
                            ingredientName,
                            quantity,
                            unit
                    );

            ContentValues ingredientValues =
                    new ContentValues();

            ingredientValues.put(
                    INGREDIENT_RECIPE_ID,
                    recipeId
            );

            ingredientValues.put(
                    INGREDIENT_NAME,
                    ingredient.getName()
            );

            ingredientValues.put(
                    INGREDIENT_KEY,
                    ingredient.getKey()
            );

            ingredientValues.put(
                    INGREDIENT_QTY,
                    ingredient.getQuantity()
            );

            ingredientValues.put(
                    INGREDIENT_UNIT,
                    ingredient.getUnit()
            );

            db.insertOrThrow(
                    INGREDIENT_TABLE,
                    null,
                    ingredientValues
            );
        }
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // version-specific migrations.
    }
}