package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.MainActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.RecipeMatcher;

import java.util.List;

// Displays recipes that fully match the pantry.
public class SuggestionsActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeListener {

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private RecyclerView recipeList;
    private TextView emptyText;

    // Recipe list adapter
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        setTitle("Suggested Recipes");

        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupRecipeList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refreshes matches after pantry changes.
        loadSuggestions();
    }

    // Binds layout controls.
    private void bindViews() {
        recipeList = findViewById(R.id.recipeList);
        emptyText = findViewById(R.id.txtSuggestionsEmpty);
    }

    // Configures the recipe list.
    private void setupRecipeList() {
        recipeAdapter = new RecipeAdapter(this);

        recipeList.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeList.setAdapter(recipeAdapter);
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

    // Updates the empty list message.
    private void updateEmptyState(
            List<Recipe> matches) {

        boolean hasMatches =
                matches != null
                        && !matches.isEmpty();

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

        // Opens the selected recipe.
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.main_menu,
                menu
        );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            @NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menu_pantry) {
            openScreen(MainActivity.class);
            return true;
        }

        if (itemId == R.id.menu_suggestions) {
            return true;
        }

        if (itemId == R.id.menu_settings) {
            openScreen(SettingsActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Opens a navigation destination.
    private void openScreen(Class<?> destination) {
        Intent intent = new Intent(
                this,
                destination
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
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