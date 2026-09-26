package com.example.smartpantrymanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.activities.ItemFormActivity;
import com.example.smartpantrymanager.activities.SettingsActivity;
import com.example.smartpantrymanager.activities.SuggestionsActivity;
import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

// Displays and manages pantry items.
public class MainActivity extends AppCompatActivity
        implements PantryAdapter.ItemListener {

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private RecyclerView pantryList;
    private TextView emptyText;
    private Button addButton;

    // Pantry list adapter
    private PantryAdapter pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Pantry");

        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupPantryList();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refreshes the pantry after changes.
        loadPantryItems();
    }

    // Binds layout controls.
    private void bindViews() {
        pantryList = findViewById(R.id.pantryList);
        emptyText = findViewById(R.id.txtPantryEmpty);
        addButton = findViewById(R.id.btnAdd);
    }

    // Configures the pantry list.
    private void setupPantryList() {
        pantryAdapter = new PantryAdapter(this);

        pantryList.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryList.setAdapter(pantryAdapter);
    }

    // Connects screen actions.
    private void setListeners() {
        addButton.setOnClickListener(
                view -> openItemForm(0)
        );
    }

    // Loads current pantry items.
    private void loadPantryItems() {
        List<PantryItem> items = pantryDb.getItems();

        pantryAdapter.setItems(items);
        updateEmptyState(items);
    }

    // Updates the empty pantry message.
    private void updateEmptyState(
            List<PantryItem> items) {

        boolean hasItems =
                items != null && !items.isEmpty();

        pantryList.setVisibility(
                hasItems ? View.VISIBLE : View.GONE
        );

        emptyText.setVisibility(
                hasItems ? View.GONE : View.VISIBLE
        );
    }

    @Override
    public void onEdit(PantryItem item) {
        if (item == null || item.getId() <= 0) {
            return;
        }

        openItemForm(item.getId());
    }

    @Override
    public void onDelete(PantryItem item) {
        if (item == null || item.getId() <= 0) {
            return;
        }

        showDeleteConfirmation(item);
    }

    // Opens the add or edit form.
    private void openItemForm(long itemId) {
        Intent intent = new Intent(
                this,
                ItemFormActivity.class
        );

        if (itemId > 0) {
            intent.putExtra(
                    ItemFormActivity.EXTRA_ITEM_ID,
                    itemId
            );
        }

        startActivity(intent);
    }

    // Confirms item deletion.
    private void showDeleteConfirmation(
            PantryItem item) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Delete " + item.getName()
                                + " from your pantry?"
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteItem(item)
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // Deletes the selected item.
    private void deleteItem(PantryItem item) {
        boolean deleted =
                pantryDb.deleteItem(item.getId());

        if (!deleted) {
            Toast.makeText(
                    this,
                    "Unable to delete ingredient.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Ingredient deleted.",
                Toast.LENGTH_SHORT
        ).show();

        loadPantryItems();
    }

    // Creates the navigation menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.main_menu,
                menu
        );

        return true;
    }

    // Handles navigation selections.
    @Override
    public boolean onOptionsItemSelected(
            @NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menu_pantry) {
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

        return super.onOptionsItemSelected(item);
    }

    // Opens a selected screen.
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