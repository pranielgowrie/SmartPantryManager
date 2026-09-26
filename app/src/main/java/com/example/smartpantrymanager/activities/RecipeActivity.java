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

// Displays the details of a selected recipe.
public class RecipeActivity extends AppCompatActivity {

    // Intent key
    public static final String EXTRA_RECIPE_ID = "recipe_id";

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private TextView nameText;
    private TextView ingredientsText;
    private TextView stepsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        setTitle("Recipe Details");

        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        loadRecipe();
    }

    // Binds layout controls.
    private void bindViews() {
        nameText = findViewById(R.id.txtRecipeTitle);
        ingredientsText = findViewById(R.id.txtRecipeIngredients);
        stepsText = findViewById(R.id.txtRecipeSteps);
    }

    // Loads the selected recipe.
    private void loadRecipe() {
        long recipeId = getIntent().getLongExtra(
                EXTRA_RECIPE_ID,
                0
        );

        if (recipeId <= 0) {
            showRecipeNotFound();
            return;
        }

        Recipe recipe = pantryDb.getRecipe(recipeId);

        if (recipe == null) {
            showRecipeNotFound();
            return;
        }

        displayRecipe(recipe);
    }

    // Displays the recipe details.
    private void displayRecipe(Recipe recipe) {
        nameText.setText(recipe.getName());

        ingredientsText.setText(
                formatIngredients(recipe.getIngredients())
        );

        stepsText.setText(recipe.getSteps());
    }

    // Formats the complete ingredient list.
    private String formatIngredients(
            List<RecipeIngredient> ingredients) {

        if (ingredients == null || ingredients.isEmpty()) {
            return getString(R.string.no_ingredients);
        }

        StringBuilder builder = new StringBuilder();

        for (RecipeIngredient ingredient : ingredients) {
            if (builder.length() > 0) {
                builder.append("\n");
            }

            builder.append(formatIngredient(ingredient));
        }

        return builder.toString();
    }

    // Formats one ingredient.
    private String formatIngredient(
            RecipeIngredient ingredient) {

        double quantity = ingredient.getQuantity();

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

    // Shows missing recipe feedback.
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
        // Closes the database connection.
        if (pantryDb != null) {
            pantryDb.close();
        }

        super.onDestroy();
    }
}