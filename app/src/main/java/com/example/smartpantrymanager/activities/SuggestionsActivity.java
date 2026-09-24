package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.RecipeMatcher;

import java.util.List;

// Displays recipes that fully match the current pantry.
public class SuggestionsActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeListener {

    // Database connection used to load pantry and recipe data.
    private PantryDb pantryDb;

    // Displays recipes that pass strict matching.
    private RecyclerView recipeList;

    // Displays feedback when no recipes match.
    private TextView emptyText;

    // Connects recipe data to the RecyclerView.
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        // Prepares the database and screen controls.
        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupRecipeList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refreshes suggestions when pantry contents change.
        loadSuggestions();
    }

    // Connects Java controls to the suggestions layout.
    private void bindViews() {
        recipeList = findViewById(
                R.id.recipeList
        );

        emptyText = findViewById(
                R.id.txtSuggestionsEmpty
        );
    }

    // Prepares the RecyclerView and its adapter.
    private void setupRecipeList() {
        recipeAdapter = new RecipeAdapter(this);

        recipeList.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeList.setAdapter(recipeAdapter);
    }

    // Loads recipes that satisfy every ingredient requirement.
    private void loadSuggestions() {
        List<PantryItem> pantryItems =
                pantryDb.getItems();

        List<Recipe> recipes =
                pantryDb.getRecipes();

        List<Recipe> matches =
                RecipeMatcher.findMatches(
                        recipes,
                        pantryItems
                );

        recipeAdapter.setRecipes(matches);
        updateEmptyState(matches);
    }

    // Shows feedback when no recipes match the pantry.
    private void updateEmptyState(
            List<Recipe> matches) {

        boolean hasMatches =
                matches != null
                        && !matches.isEmpty();

        if (hasMatches) {
            recipeList.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
            return;
        }

        recipeList.setVisibility(View.GONE);
        emptyText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        if (recipe == null) {
            return;
        }

        // Opens the selected recipe details.
        Intent intent = new Intent(
                SuggestionsActivity.this,
                RecipeActivity.class
        );

        intent.putExtra(
                RecipeActivity.EXTRA_RECIPE_ID,
                recipe.getId()
        );

        startActivity(intent);
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