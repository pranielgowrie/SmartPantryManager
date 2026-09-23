package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Pantry table
    public static final String PANTRY_TABLE = "pantry_items";
    public static final String PANTRY_ID = "id";
    public static final String PANTRY_NAME = "ingredient_name";
    public static final String PANTRY_KEY = "normalized_name";
    public static final String PANTRY_QTY = "quantity";
    public static final String PANTRY_UNIT = "unit";
    public static final String PANTRY_EXPIRY = "expiry_date";

    // Recipe table
    public static final String RECIPE_TABLE = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_STEPS = "preparation_steps";

    // Recipe ingredient table
    public static final String RECIPE_ITEM_TABLE = "recipe_ingredients";
    public static final String RECIPE_ITEM_ID = "id";
    public static final String RECIPE_ITEM_RECIPE_ID = "recipe_id";
    public static final String RECIPE_ITEM_NAME = "ingredient_name";
    public static final String RECIPE_ITEM_KEY = "normalized_name";
    public static final String RECIPE_ITEM_QTY = "required_quantity";
    public static final String RECIPE_ITEM_UNIT = "unit";

    // Settings table
    public static final String SETTINGS_TABLE = "app_settings";
    public static final String SETTINGS_KEY = "setting_key";
    public static final String SETTINGS_VALUE = "setting_value";

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

        // Enforces recipe and ingredient relationships.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            createPantryTable(db);
            createRecipeTable(db);
            createRecipeItemTable(db);
            createSettingsTable(db);
            createIndexes(db);
            insertDefaultSettings(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createPantryTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE IF NOT EXISTS " + PANTRY_TABLE + " (" +
                        PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        PANTRY_NAME + " TEXT NOT NULL " +
                        "CHECK(LENGTH(TRIM(" + PANTRY_NAME + ")) > 0), " +

                        PANTRY_KEY + " TEXT NOT NULL COLLATE NOCASE " +
                        "CHECK(LENGTH(TRIM(" + PANTRY_KEY + ")) > 0), " +

                        PANTRY_QTY + " REAL NOT NULL " +
                        "CHECK(" + PANTRY_QTY + " > 0), " +

                        PANTRY_UNIT + " TEXT NOT NULL " +
                        "CHECK(" + PANTRY_UNIT + " IN " +
                        "('item', 'g', 'kg', 'ml', 'l', 'tsp', 'tbsp')), " +

                        PANTRY_EXPIRY + " TEXT NOT NULL DEFAULT '', " +

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
                "CREATE TABLE IF NOT EXISTS " + RECIPE_TABLE + " (" +
                        RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_NAME + " TEXT NOT NULL COLLATE NOCASE " +
                        "CHECK(LENGTH(TRIM(" + RECIPE_NAME + ")) > 0), " +

                        RECIPE_STEPS + " TEXT NOT NULL " +
                        "CHECK(LENGTH(TRIM(" + RECIPE_STEPS + ")) > 0), " +

                        "UNIQUE(" + RECIPE_NAME + ")" +
                        ")";

        db.execSQL(sql);
    }

    private void createRecipeItemTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE IF NOT EXISTS " +
                        RECIPE_ITEM_TABLE + " (" +

                        RECIPE_ITEM_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        RECIPE_ITEM_RECIPE_ID +
                        " INTEGER NOT NULL, " +

                        RECIPE_ITEM_NAME + " TEXT NOT NULL " +
                        "CHECK(LENGTH(TRIM(" +
                        RECIPE_ITEM_NAME + ")) > 0), " +

                        RECIPE_ITEM_KEY +
                        " TEXT NOT NULL COLLATE NOCASE " +
                        "CHECK(LENGTH(TRIM(" +
                        RECIPE_ITEM_KEY + ")) > 0), " +

                        RECIPE_ITEM_QTY + " REAL NOT NULL " +
                        "CHECK(" + RECIPE_ITEM_QTY + " > 0), " +

                        RECIPE_ITEM_UNIT + " TEXT NOT NULL " +
                        "CHECK(" + RECIPE_ITEM_UNIT + " IN " +
                        "('item', 'g', 'kg', 'ml', 'l', 'tsp', 'tbsp')), " +

                        "FOREIGN KEY(" + RECIPE_ITEM_RECIPE_ID + ") " +
                        "REFERENCES " + RECIPE_TABLE +
                        "(" + RECIPE_ID + ") ON DELETE CASCADE, " +

                        "UNIQUE(" +
                        RECIPE_ITEM_RECIPE_ID + ", " +
                        RECIPE_ITEM_KEY + ", " +
                        RECIPE_ITEM_UNIT +
                        ")" +
                        ")";

        db.execSQL(sql);
    }

    private void createSettingsTable(SQLiteDatabase db) {
        String sql =
                "CREATE TABLE IF NOT EXISTS " + SETTINGS_TABLE + " (" +

                        SETTINGS_KEY + " TEXT PRIMARY KEY " +
                        "CHECK(LENGTH(TRIM(" +
                        SETTINGS_KEY + ")) > 0), " +

                        SETTINGS_VALUE + " TEXT NOT NULL" +
                        ")";

        db.execSQL(sql);
    }

    private void createIndexes(SQLiteDatabase db) {
        // Speeds up pantry matching.
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_pantry_key " +
                        "ON " + PANTRY_TABLE +
                        "(" + PANTRY_KEY + ")"
        );

        // Speeds up recipe ingredient loading.
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_recipe_item_recipe " +
                        "ON " + RECIPE_ITEM_TABLE +
                        "(" + RECIPE_ITEM_RECIPE_ID + ")"
        );

        // Speeds up ingredient matching.
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_recipe_item_key " +
                        "ON " + RECIPE_ITEM_TABLE +
                        "(" + RECIPE_ITEM_KEY + ")"
        );

        // Speeds up expiry-date queries.
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_pantry_expiry " +
                        "ON " + PANTRY_TABLE +
                        "(" + PANTRY_EXPIRY + ")"
        );
    }

    private void insertDefaultSettings(SQLiteDatabase db) {
        insertDefaultSetting(db, "expiry_alerts", "true");
        insertDefaultSetting(db, "expiry_days", "3");
        insertDefaultSetting(db, "preferred_units", "metric");
    }

    private void insertDefaultSetting(
            SQLiteDatabase db,
            String key,
            String value) {

        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY, key);
        values.put(SETTINGS_VALUE, value);

        // Keeps the existing value if initialization repeats.
        db.insertWithOnConflict(
                SETTINGS_TABLE,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        // Apply every missed migration in sequence.
        if (oldVersion < 2) {
            // Add the version 2 migration here.
        }
    }
}