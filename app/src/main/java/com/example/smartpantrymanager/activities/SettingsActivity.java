package com.example.smartpantrymanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDb;

// Manages the user's pantry preferences.
public class SettingsActivity extends AppCompatActivity {

    // Database setting keys.
    private static final String KEY_ALERTS = "expiry_alerts";
    private static final String KEY_DAYS = "expiry_days";
    private static final String KEY_UNITS = "preferred_units";

    // Database connection used by this screen.
    private PantryDb pantryDb;

    // Settings controls.
    private SwitchCompat alertsSwitch;
    private EditText daysInput;
    private Spinner unitsSpinner;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Prepares the database and screen controls.
        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupUnitsSpinner();
        loadSettings();
        setListeners();
    }

    // Connects Java controls to the settings layout.
    private void bindViews() {
        alertsSwitch = findViewById(
                R.id.switchExpiryAlerts
        );

        daysInput = findViewById(
                R.id.inputExpiryDays
        );

        unitsSpinner = findViewById(
                R.id.spinnerPreferredUnits
        );

        saveButton = findViewById(
                R.id.btnSaveSettings
        );
    }

    // Loads the supported unit preferences.
    private void setupUnitsSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.unit_preferences,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        unitsSpinner.setAdapter(adapter);
    }

    // Loads the current settings from the database.
    private void loadSettings() {
        String alertsValue = pantryDb.getSetting(
                KEY_ALERTS,
                "true"
        );

        String daysValue = pantryDb.getSetting(
                KEY_DAYS,
                "3"
        );

        String unitsValue = pantryDb.getSetting(
                KEY_UNITS,
                "metric"
        );

        alertsSwitch.setChecked(
                Boolean.parseBoolean(alertsValue)
        );

        daysInput.setText(daysValue);
        selectUnitPreference(unitsValue);
    }

    // Selects the saved unit preference.
    private void selectUnitPreference(String preference) {
        int index = 0;

        while (index != unitsSpinner.getCount()) {
            String currentValue = unitsSpinner
                    .getItemAtPosition(index)
                    .toString();

            if (currentValue.equalsIgnoreCase(preference)) {
                unitsSpinner.setSelection(index);
                return;
            }

            index++;
        }
    }

    // Connects the Save button to the save action.
    private void setListeners() {
        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveSettings();
                    }
                }
        );
    }

    // Validates and saves the selected preferences.
    private void saveSettings() {
        String daysText = daysInput
                .getText()
                .toString()
                .trim();

        if (daysText.isEmpty()) {
            daysInput.setError(
                    getString(R.string.expiry_days_required)
            );

            daysInput.requestFocus();
            return;
        }

        int warningDays;

        try {
            warningDays = Integer.parseInt(daysText);
        } catch (NumberFormatException exception) {
            daysInput.setError(
                    getString(R.string.invalid_expiry_days)
            );

            daysInput.requestFocus();
            return;
        }

        if (warningDays < 1) {
            daysInput.setError(
                    getString(R.string.invalid_expiry_days)
            );

            daysInput.requestFocus();
            return;
        }

        Object selectedUnits =
                unitsSpinner.getSelectedItem();

        String unitPreference = selectedUnits == null
                ? "metric"
                : selectedUnits.toString().toLowerCase();

        // Stores the current settings in the database.
        pantryDb.saveSetting(
                KEY_ALERTS,
                String.valueOf(alertsSwitch.isChecked())
        );

        pantryDb.saveSetting(
                KEY_DAYS,
                String.valueOf(warningDays)
        );

        pantryDb.saveSetting(
                KEY_UNITS,
                unitPreference
        );

        Toast.makeText(
                this,
                R.string.settings_saved,
                Toast.LENGTH_SHORT
        ).show();
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