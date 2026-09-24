package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.activities.ItemFormActivity;
import com.example.smartpantrymanager.database.PantryDb;

// Displays the pantry list and handles pantry navigation.
public class MainActivity extends AppCompatActivity {

    private PantryDb pantryDb;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepares the database and screen controls.
        openDatabase();
        bindViews();
        setListeners();
    }

    private void openDatabase() {
        // Opens the existing database or creates it on first launch.
        pantryDb = new PantryDb(getApplicationContext());
        pantryDb.getWritableDatabase();
    }

    private void bindViews() {
        // Connects Java controls to the layout.
        addButton = findViewById(R.id.btnAdd);
    }

    private void setListeners() {
        // Opens the ingredient form when Add is selected.
        addButton.setOnClickListener(this::handleAddClick);
    }

    private void handleAddClick(View view) {
        openItemForm();
    }

    private void openItemForm() {
        // Opens the form in add-item mode.
        Intent intent = new Intent(
                MainActivity.this,
                ItemFormActivity.class
        );

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // Releases the database when the Activity is destroyed.
        if (pantryDb != null) {
            pantryDb.close();
        }

        super.onDestroy();
    }
}