package com.example.smartpantrymanager.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.database.PantryDb;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

// Manages application settings.
public class SettingsActivity extends BaseDrawerActivity {

    // Database keys
    private static final String KEY_ALERTS = "expiry_alerts";
    private static final String KEY_DAYS = "expiry_days";
    private static final String KEY_UNITS = "preferred_units";

    // Theme keys
    private static final String PREFS_NAME =
            "smart_pantry_preferences";

    private static final String KEY_DARK_MODE =
            "dark_mode";

    // Database connection
    private PantryDb pantryDb;

    // UI controls
    private SwitchCompat alertsSwitch;
    private SwitchMaterial darkModeSwitch;
    private EditText daysInput;
    private Spinner unitsSpinner;
    private Button saveButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(R.string.settings_title);

        pantryDb = new PantryDb(getApplicationContext());

        setupNavigationDrawer();
        bindViews();
        setupUnitsSpinner();
        loadSettings();
        loadThemePreference();
        setListeners();
    }

    // Binds layout controls.
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

        backButton =
                findViewById(R.id.btnBack);
    }

    // Loads available unit options.
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

    // Loads saved settings.
    private void loadSettings() {
        String alertsValue =
                pantryDb.getSetting(KEY_ALERTS, "true");

        String daysValue =
                pantryDb.getSetting(KEY_DAYS, "3");

        String unitsValue =
                pantryDb.getSetting(KEY_UNITS, "metric");

        alertsSwitch.setChecked(
                Boolean.parseBoolean(alertsValue)
        );

        daysInput.setText(daysValue);
        selectUnitPreference(unitsValue);
    }

    // Loads the saved theme.
    private void loadThemePreference() {
        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        boolean darkModeEnabled =
                preferences.getBoolean(
                        KEY_DARK_MODE,
                        false
                );

        darkModeSwitch.setChecked(darkModeEnabled);
    }

    // Selects the saved unit option.
    private void selectUnitPreference(String preference) {
        if (preference == null) {
            return;
        }

        for (int index = 0;
             index < unitsSpinner.getCount();
             index++) {

            String option =
                    unitsSpinner
                            .getItemAtPosition(index)
                            .toString();

            if (option.equalsIgnoreCase(preference)) {
                unitsSpinner.setSelection(index);
                return;
            }
        }
    }

    // Connects screen actions.
    private void setListeners() {
        saveButton.setOnClickListener(
                view -> saveSettings()
        );

        backButton.setOnClickListener(
                view -> getOnBackPressedDispatcher()
                        .onBackPressed()
        );

        darkModeSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    saveThemePreference(isChecked);
                    applyTheme(isChecked);
                }
        );
    }

    // Saves the selected theme.
    private void saveThemePreference(
            boolean darkModeEnabled) {

        getSharedPreferences(
                PREFS_NAME,
                MODE_PRIVATE
        ).edit()
                .putBoolean(
                        KEY_DARK_MODE,
                        darkModeEnabled
                )
                .apply();
    }

    // Applies the selected theme.
    private void applyTheme(boolean darkModeEnabled) {
        int mode = darkModeEnabled
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        AppCompatDelegate.setDefaultNightMode(mode);
    }

    // Validates and saves settings.
    private void saveSettings() {
        String daysText =
                daysInput.getText()
                        .toString()
                        .trim();

        if (daysText.isEmpty()) {
            showDaysError(
                    R.string.expiry_days_required
            );
            return;
        }

        int warningDays;

        try {
            warningDays = Integer.parseInt(daysText);
        } catch (NumberFormatException exception) {
            showDaysError(
                    R.string.invalid_expiry_days
            );
            return;
        }

        if (warningDays < 1) {
            showDaysError(
                    R.string.invalid_expiry_days
            );
            return;
        }

        Object selectedUnits =
                unitsSpinner.getSelectedItem();

        String units = selectedUnits == null
                ? "metric"
                : selectedUnits.toString()
                .trim()
                .toLowerCase(Locale.ROOT);

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
                units
        );

        Toast.makeText(
                this,
                R.string.settings_saved,
                Toast.LENGTH_SHORT
        ).show();
    }

    // Shows warning-days feedback.
    private void showDaysError(int message) {
        daysInput.setError(getString(message));
        daysInput.requestFocus();
    }

    @Override
    protected void onDestroy() {
        if (pantryDb != null) {
            pantryDb.close();
        }

        super.onDestroy();
    }
}