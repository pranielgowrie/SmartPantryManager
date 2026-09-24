package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.List;
import java.util.Locale;

// Displays the complete details of a selected recipe.
public class RecipeActivity extends AppCompatActivity {

    // Intent key used to identify the selected recipe.
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    // Database connection used to load recipe details.
    private PantryDb pantryDb;

    // Recipe detail controls.
    private TextView nameText;
    private TextView ingredientsText;
    private TextView stepsText;

    // Database identifier of the selected recipe.
    private long recipeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Prepares the database and screen controls.
        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        loadRecipe();
    }

    // Connects Java controls to the recipe layout.
    private void bindViews() {
        nameText = findViewById(
                R.id.txtRecipeTitle
        );

        ingredientsText = findViewById(
                R.id.txtRecipeIngredients
        );

        stepsText = findViewById(
                R.id.txtRecipeSteps
        );
    }

    // Loads the selected recipe from the database.
    private void loadRecipe() {
        recipeId = getIntent().getLongExtra(
                EXTRA_RECIPE_ID,
                0
        );

        if (recipeId == 0) {
            showRecipeNotFound();
            return;
        }

        Recipe recipe = pantryDb.getRecipe(
                recipeId
        );

        if (recipe == null) {
            showRecipeNotFound();
            return;
        }

        displayRecipe(recipe);
    }

    // Displays the selected recipe information.
    private void displayRecipe(Recipe recipe) {
        nameText.setText(recipe.getName());

        ingredientsText.setText(
                formatIngredients(
                        recipe.getIngredients()
                )
        );

        stepsText.setText(recipe.getSteps());
    }

    // Formats the ingredient list for the details screen.
    private String formatIngredients(
            List<RecipeIngredient> ingredients) {

        if (ingredients == null || ingredients.isEmpty()) {
            return getString(
                    R.string.no_ingredients
            );
        }

        StringBuilder builder =
                new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {
            builder.append(
                    formatIngredient(ingredient)
            );

            builder.append("\n");
        }

        return builder.toString().trim();
    }

    // Formats one ingredient with its quantity and unit.
    private String formatIngredient(
            RecipeIngredient ingredient) {

        double quantity =
                ingredient.getQuantity();

        boolean wholeNumber =
                Double.compare(
                        quantity,
                        Math.rint(quantity)
                ) == 0;

        if (wholeNumber) {
            return String.format(
                    Locale.getDefault(),
                    "%s %.0f %s",
                    ingredient.getName(),
                    quantity,
                    ingredient.getUnit()
            );
        }

        return String.format(
                Locale.getDefault(),
                "%s %.2f %s",
                ingredient.getName(),
                quantity,
                ingredient.getUnit()
        );
    }

    // Shows feedback when the selected recipe is unavailable.
    private void showRecipeNotFound() {
        Toast.makeText(
                this,
                R.string.recipe_not_found,
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    @Override
    protected void onDestroy() {
        // Releases the database when the Activity closes.
        if (pantryDb != null) {
            pantryDb.close();
        }

        super.onDestroy();
    }
}