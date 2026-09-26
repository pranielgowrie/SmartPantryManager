package com.example.smartpantrymanager.activities;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smartpantrymanager.MainActivity;
import com.example.smartpantrymanager.R;
import com.google.android.material.navigation.NavigationView;

// Provides the navigation drawer for top-level screens.
public abstract class BaseDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    // Configures the navigation drawer.
    protected void setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);

        NavigationView navigationView =
                findViewById(R.id.navigationView);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_navigation,
                R.string.close_navigation
        );

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(
                item -> handleNavigation(item.getItemId())
        );
    }

    // Opens the selected screen.
    private boolean handleNavigation(int itemId) {
        drawerLayout.closeDrawers();

        if (itemId == R.id.menu_pantry) {
            openScreen(MainActivity.class);
            return true;
        }

        if (itemId == R.id.menu_suggestions) {
            openScreen(SuggestionsActivity.class);
            return true;
        }

        if (itemId == R.id.menu_settings) {
            openScreen(SettingsActivity.class);
            return true;
        }

        return false;
    }

    // Prevents duplicate Activity instances.
    private void openScreen(Class<?> destination) {
        if (destination.isInstance(this)) {
            return;
        }

        Intent intent = new Intent(this, destination);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(
            @NonNull MenuItem item) {

        if (drawerToggle != null
                && drawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}