package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDb;
import com.example.smartpantrymanager.model.PantryItem;

// Adds and edits pantry items.
public class ItemFormActivity extends AppCompatActivity {

    // Intent key
    public static final String EXTRA_ITEM_ID = "item_id";

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private EditText nameInput;
    private EditText quantityInput;
    private EditText expiryInput;
    private Spinner unitSpinner;
    private Button saveButton;

    // Item being edited
    private PantryItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);

        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupUnitSpinner();
        setListeners();
        loadItem();
    }

    // Binds layout controls.
    private void bindViews() {
        nameInput = findViewById(R.id.inputName);
        quantityInput = findViewById(R.id.inputQuantity);
        expiryInput = findViewById(R.id.inputExpiry);
        unitSpinner = findViewById(R.id.spinnerUnit);
        saveButton = findViewById(R.id.btnSave);
    }

    // Loads the available units.
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

    // Connects form actions.
    private void setListeners() {
        saveButton.setOnClickListener(
                view -> saveItem()
        );
    }

    // Loads an existing item when editing.
    private void loadItem() {
        long itemId = getIntent().getLongExtra(
                EXTRA_ITEM_ID,
                0
        );

        if (itemId <= 0) {
            setTitle(R.string.add_ingredient);
            return;
        }

        currentItem = pantryDb.getItem(itemId);

        if (currentItem == null) {
            showItemNotFound();
            return;
        }

        setTitle(R.string.edit_ingredient);
        displayItem(currentItem);
    }

    // Displays the selected item.
    private void displayItem(PantryItem item) {
        nameInput.setText(item.getName());
        quantityInput.setText(
                String.valueOf(item.getQuantity())
        );
        expiryInput.setText(item.getExpiry());

        selectUnit(item.getUnit());
    }

    // Selects the stored unit.
    private void selectUnit(String unit) {
        if (unit == null) {
            return;
        }

        for (int index = 0;
             index < unitSpinner.getCount();
             index++) {

            String spinnerUnit =
                    unitSpinner
                            .getItemAtPosition(index)
                            .toString();

            if (spinnerUnit.equalsIgnoreCase(unit)) {
                unitSpinner.setSelection(index);
                return;
            }
        }
    }

    // Reads and validates the form.
    private void saveItem() {
        String name =
                nameInput.getText()
                        .toString()
                        .trim();

        String quantityText =
                quantityInput.getText()
                        .toString()
                        .trim();

        String expiry =
                expiryInput.getText()
                        .toString()
                        .trim();

        Object selectedUnit =
                unitSpinner.getSelectedItem();

        String unit = selectedUnit == null
                ? ""
                : selectedUnit.toString().trim();

        if (!validateRequiredFields(
                name,
                quantityText,
                unit)) {

            return;
        }

        Double quantity = parseQuantity(quantityText);

        if (quantity == null) {
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
            String message = exception.getMessage();

            Toast.makeText(
                    this,
                    message == null
                            ? getString(R.string.save_failed)
                            : message,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Validates required fields.
    private boolean validateRequiredFields(
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

        boolean unitMissing =
                unit.isEmpty()
                        || unit.equalsIgnoreCase(
                        getString(R.string.select_unit)
                );

        if (unitMissing) {
            Toast.makeText(
                    this,
                    R.string.unit_required,
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    // Parses and validates the quantity.
    private Double parseQuantity(String quantityText) {
        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException exception) {
            showQuantityError();
            return null;
        }

        boolean invalid =
                quantity <= 0
                        || Double.isNaN(quantity)
                        || Double.isInfinite(quantity);

        if (invalid) {
            showQuantityError();
            return null;
        }

        return quantity;
    }

    // Shows quantity validation feedback.
    private void showQuantityError() {
        quantityInput.setError(
                getString(R.string.invalid_quantity)
        );

        quantityInput.requestFocus();
    }

    // Inserts or updates the item.
    private void saveToDatabase(PantryItem item) {
        boolean saved;

        if (currentItem == null) {
            saved = pantryDb.addItem(item) != -1;
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

    // Shows missing item feedback.
    private void showItemNotFound() {
        Toast.makeText(
                this,
                R.string.item_not_found,
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