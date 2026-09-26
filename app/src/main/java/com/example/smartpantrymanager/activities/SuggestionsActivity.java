package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.RecipeMatcher;

import java.util.List;

// Displays recipes that fully match the pantry.
public class SuggestionsActivity extends BaseDrawerActivity
        implements RecipeAdapter.RecipeListener {

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private RecyclerView recipeList;
    private TextView emptyText;
    private Button backButton;

    // Recipe adapter
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        setTitle(R.string.suggestions_title);

        pantryDb = new PantryDb(getApplicationContext());

        setupNavigationDrawer();
        bindViews();
        setupRecipeList();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    // Binds layout controls.
    private void bindViews() {
        recipeList = findViewById(R.id.recipeList);
        emptyText = findViewById(R.id.txtSuggestionsEmpty);
        backButton = findViewById(R.id.btnBack);
    }

    // Configures the recipe list.
    private void setupRecipeList() {
        recipeAdapter = new RecipeAdapter(this);

        recipeList.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeList.setAdapter(recipeAdapter);
    }

    // Connects screen actions.
    private void setListeners() {
        backButton.setOnClickListener(
                view -> getOnBackPressedDispatcher()
                        .onBackPressed()
        );
    }

    // Loads strictly matched recipes.
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

    // Updates the empty state.
    private void updateEmptyState(
            List<Recipe> matches) {

        boolean hasMatches =
                matches != null && !matches.isEmpty();

        recipeList.setVisibility(
                hasMatches ? View.VISIBLE : View.GONE
        );

        emptyText.setVisibility(
                hasMatches ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        if (recipe == null || recipe.getId() <= 0) {
            return;
        }

        Intent intent = new Intent(
                this,
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
        if (pantryDb != null) {
            pantryDb.close();
        }

        super.onDestroy();
    }
}