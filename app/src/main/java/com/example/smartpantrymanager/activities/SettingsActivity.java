package com.example.smartpantrymanager.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDb;
import com.google.android.material.switchmaterial.SwitchMaterial;

// Manages pantry and application settings.
public class SettingsActivity extends AppCompatActivity {

    // Database setting keys.
    private static final String KEY_ALERTS = "expiry_alerts";
    private static final String KEY_DAYS = "expiry_days";
    private static final String KEY_UNITS = "preferred_units";

    // Theme preference keys.
    private static final String PREFS_NAME = "smart_pantry_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";

    // Database connection.
    private PantryDb pantryDb;

    // Settings controls.
    private SwitchCompat alertsSwitch;
    private SwitchMaterial darkModeSwitch;
    private EditText daysInput;
    private Spinner unitsSpinner;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize database.
        pantryDb = new PantryDb(getApplicationContext());

        bindViews();
        setupUnitsSpinner();
        loadSettings();
        loadThemePreference();
        setListeners();
    }

    // Connects views to XML controls.
    private void bindViews() {

        alertsSwitch =
                findViewById(R.id.switchExpiryAlerts);

        darkModeSwitch =
                findViewById(R.id.switchDarkMode);

        daysInput =
                findViewById(R.id.inputExpiryDays);

        unitsSpinner =
                findViewById(R.id.spinnerPreferredUnits);

        saveButton =
                findViewById(R.id.btnSaveSettings);
    }

    // Loads available unit preferences.
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

    // Loads pantry settings from database.
    private void loadSettings() {

        String alertsValue =
                pantryDb.getSetting(
                        KEY_ALERTS,
                        "true"
                );

        String daysValue =
                pantryDb.getSetting(
                        KEY_DAYS,
                        "3"
                );

        String unitsValue =
                pantryDb.getSetting(
                        KEY_UNITS,
                        "metric"
                );

        alertsSwitch.setChecked(
                Boolean.parseBoolean(alertsValue)
        );

        daysInput.setText(daysValue);

        selectUnitPreference(unitsValue);
    }

    // Loads saved light/dark mode preference.
    private void loadThemePreference() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        boolean isDarkMode =
                preferences.getBoolean(
                        KEY_DARK_MODE,
                        false
                );

        darkModeSwitch.setChecked(isDarkMode);
    }

    // Restores previously selected units.
    private void selectUnitPreference(String preference) {

        int index = 0;

        while (index < unitsSpinner.getCount()) {

            String currentValue =
                    unitsSpinner
                            .getItemAtPosition(index)
                            .toString();

            if (currentValue.equalsIgnoreCase(preference)) {

                unitsSpinner.setSelection(index);
                return;
            }

            index++;
        }
    }

    // Connects button and switch actions.
    private void setListeners() {

        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveSettings();
                    }
                }
        );

        darkModeSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    SharedPreferences preferences =
                            getSharedPreferences(
                                    PREFS_NAME,
                                    MODE_PRIVATE
                            );

                    preferences.edit()
                            .putBoolean(
                                    KEY_DARK_MODE,
                                    isChecked
                            )
                            .apply();

                    if (isChecked) {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_YES
                        );

                    } else {

                        AppCompatDelegate.setDefaultNightMode(
                                AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                }
        );
    }

    // Validates and saves the selected preferences.
    private void saveSettings() {

        String daysText =
                daysInput.getText()
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

        String unitPreference =
                selectedUnits == null
                        ? "metric"
                        : selectedUnits.toString().toLowerCase();

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

        if (pantryDb != null) {
            pantryDb.close();
        }

        super.onDestroy();
    }
}