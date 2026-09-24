package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;

// Adds new pantry items and edits existing pantry items.
public class ItemFormActivity extends AppCompatActivity {

    // Intent key used when editing an existing item.
    public static final String EXTRA_ITEM_ID = "item_id";

    // Database connection used by the form.
    private PantryDb pantryDb;

    // Form controls.
    private EditText nameInput;
    private EditText quantityInput;
    private EditText expiryInput;
    private Spinner unitSpinner;
    private Button saveButton;

    // Item being edited. Zero means a new item.
    private long itemId;
    private PantryItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);

        // Prepares the database and form controls.
        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupUnitSpinner();
        setListeners();
        loadItem();
    }

    // Connects Java controls to the form layout.
    private void bindViews() {
        nameInput = findViewById(
                R.id.inputName
        );

        quantityInput = findViewById(
                R.id.inputQuantity
        );

        expiryInput = findViewById(
                R.id.inputExpiry
        );

        unitSpinner = findViewById(
                R.id.spinnerUnit
        );

        saveButton = findViewById(
                R.id.btnSave
        );
    }

    // Loads the supported units into the Spinner.
    private void setupUnitSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.pantry_units,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitSpinner.setAdapter(adapter);
    }

    // Connects the Save button to the save action.
    private void setListeners() {
        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveItem();
                    }
                }
        );
    }

    // Loads an existing item when an item ID was provided.
    private void loadItem() {
        itemId = getIntent().getLongExtra(
                EXTRA_ITEM_ID,
                0
        );

        if (itemId == 0) {
            setTitle(R.string.add_ingredient);
            return;
        }

        currentItem = pantryDb.getItem(itemId);

        if (currentItem == null) {
            Toast.makeText(
                    this,
                    R.string.item_not_found,
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        setTitle(R.string.edit_ingredient);
        displayItem(currentItem);
    }

    // Displays the selected item's current values.
    private void displayItem(PantryItem item) {
        nameInput.setText(item.getName());

        quantityInput.setText(
                String.valueOf(item.getQuantity())
        );

        expiryInput.setText(item.getExpiry());
        selectUnit(item.getUnit());
    }

    // Selects the item's current unit in the Spinner.
    private void selectUnit(String unit) {
        int index = 0;

        while (index != unitSpinner.getCount()) {
            String spinnerUnit = unitSpinner
                    .getItemAtPosition(index)
                    .toString();

            if (spinnerUnit.equalsIgnoreCase(unit)) {
                unitSpinner.setSelection(index);
                return;
            }

            index++;
        }
    }

    // Reads, validates, and saves the form values.
    private void saveItem() {
        String name = nameInput
                .getText()
                .toString()
                .trim();

        String quantityText = quantityInput
                .getText()
                .toString()
                .trim();

        String expiry = expiryInput
                .getText()
                .toString()
                .trim();

        Object selectedUnit =
                unitSpinner.getSelectedItem();

        String unit = selectedUnit == null
                ? ""
                : selectedUnit.toString();

        if (!validateForm(
                name,
                quantityText,
                unit)) {

            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(
                    quantityText
            );
        } catch (NumberFormatException exception) {
            quantityInput.setError(
                    getString(R.string.invalid_quantity)
            );

            quantityInput.requestFocus();
            return;
        }

        // Rejects zero, negative, and invalid quantities.
        if (Double.compare(quantity, 0.0) != 1
                || Double.isNaN(quantity)
                || Double.isInfinite(quantity)) {

            quantityInput.setError(
                    getString(R.string.invalid_quantity)
            );

            quantityInput.requestFocus();
            return;
        }

        try {
            PantryItem item = new PantryItem(
                    name,
                    quantity,
                    unit,
                    expiry
            );

            saveToDatabase(item);
        } catch (IllegalArgumentException exception) {
            Toast.makeText(
                    this,
                    exception.getMessage(),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Checks that all required form values were entered.
    private boolean validateForm(
            String name,
            String quantity,
            String unit) {

        if (name.isEmpty()) {
            nameInput.setError(
                    getString(R.string.name_required)
            );

            nameInput.requestFocus();
            return false;
        }

        if (quantity.isEmpty()) {
            quantityInput.setError(
                    getString(R.string.quantity_required)
            );

            quantityInput.requestFocus();
            return false;
        }

        if (unit.isEmpty()
                || unit.equalsIgnoreCase(
                getString(R.string.select_unit))) {

            Toast.makeText(
                    this,
                    R.string.unit_required,
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    // Inserts a new item or updates the selected item.
    private void saveToDatabase(PantryItem item) {
        boolean saved;

        if (currentItem == null) {
            long newId = pantryDb.addItem(item);
            saved = newId != -1;
        } else {
            item.setId(currentItem.getId());
            saved = pantryDb.updateItem(item);
        }

        if (!saved) {
            Toast.makeText(
                    this,
                    R.string.save_failed,
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Toast.makeText(
                this,
                R.string.item_saved,
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