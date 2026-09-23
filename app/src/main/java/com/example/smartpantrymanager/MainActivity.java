package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.activities.ItemFormActivity;
import com.example.smartpantrymanager.database.PantryDb;

public class MainActivity extends AppCompatActivity {

    private PantryDb pantryDb;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDatabase();
        bindViews();
        setListeners();
    }

    private void openDatabase() {
        // Opens or creates the local SQLite database.
        pantryDb = new PantryDb(getApplicationContext());
        pantryDb.getWritableDatabase();
    }

    private void bindViews() {
        addButton = findViewById(R.id.btnAdd);
    }

    private void setListeners() {
        addButton.setOnClickListener(view -> openItemForm());
    }

    private void openItemForm() {
        Intent intent = new Intent(
                MainActivity.this,
                ItemFormActivity.class
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