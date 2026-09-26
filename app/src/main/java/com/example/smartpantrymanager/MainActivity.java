package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.activities.BaseDrawerActivity;
import com.example.smartpantrymanager.activities.ItemFormActivity;
import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

// Displays and manages pantry items.
public class MainActivity extends BaseDrawerActivity
        implements PantryAdapter.ItemListener {

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private RecyclerView pantryList;
    private TextView emptyText;
    private Button addButton;
    private Button backButton;

    // Pantry adapter
    private PantryAdapter pantryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.pantry_title);

        pantryDb = new PantryDb(getApplicationContext());

        setupNavigationDrawer();
        bindViews();
        setupPantryList();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refreshes the pantry list.
        loadPantryItems();
    }

    // Binds layout controls.
    private void bindViews() {
        pantryList = findViewById(R.id.pantryList);
        emptyText = findViewById(R.id.txtPantryEmpty);
        addButton = findViewById(R.id.btnAdd);
        backButton = findViewById(R.id.btnBack);
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

        backButton.setOnClickListener(
                view -> getOnBackPressedDispatcher()
                        .onBackPressed()
        );
    }

    // Loads pantry items.
    private void loadPantryItems() {
        List<PantryItem> items =
                pantryDb.getItems();

        pantryAdapter.setItems(items);
        updateEmptyState(items);
    }

    // Updates the empty state.
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

    // Shows the delete confirmation.
    private void showDeleteConfirmation(
            PantryItem item) {

        String message = getString(
                R.string.delete_confirmation,
                item.getName()
        );

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_ingredient)
                .setMessage(message)
                .setPositiveButton(
                        R.string.delete,
                        (dialog, which) -> deleteItem(item)
                )
                .setNegativeButton(
                        R.string.cancel,
                        null
                )
                .show();
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

    // Deletes the selected item.
    private void deleteItem(PantryItem item) {
        boolean deleted =
                pantryDb.deleteItem(item.getId());

        int messageId = deleted
                ? R.string.item_deleted
                : R.string.delete_failed;

        Toast.makeText(
                this,
                messageId,
                Toast.LENGTH_SHORT
        ).show();

        if (deleted) {
            loadPantryItems();
        }
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