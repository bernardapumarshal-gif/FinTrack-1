package com.example.fintrack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    private Spinner currencySpinner;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "FinTrackSettings";
    private static final String KEY_CURRENCY = "currency";
    private static final String DEFAULT_CURRENCY = "RM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SettingsActivity", "onCreate called");
        setContentView(R.layout.activity_settings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // Initialize views
        currencySpinner = findViewById(R.id.spinner_currency);
        MaterialButton saveButton = findViewById(R.id.button_save);

        // Set up currency spinner
        setupCurrencySpinner();

        // Load saved settings
        loadSettings();

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            Log.d("SettingsActivity", "Save button clicked");
            saveSettings();
        });
    }

    private void setupCurrencySpinner() {
        Log.d("SettingsActivity", "Setting up currency spinner");
        String[] currencies = {
            "RM (Malaysian Ringgit)",
            "USD (US Dollar)",
            "EUR (Euro)",
            "GBP (British Pound)",
            "SGD (Singapore Dollar)",
            "JPY (Japanese Yen)",
            "AUD (Australian Dollar)",
            "CAD (Canadian Dollar)",
            "CHF (Swiss Franc)",
            "CNY (Chinese Yuan)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);
        
        // Set a default selection if none is set
        currencySpinner.setSelection(0);
        Log.d("SettingsActivity", "Currency spinner setup complete");
    }

    private void loadSettings() {
        String savedCurrency = sharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY);
        Log.d("SettingsActivity", "Loading settings, saved currency: " + savedCurrency);
        
        // Find the index of the saved currency
        String[] currencies = {
            "RM", "USD", "EUR", "GBP", "SGD", "JPY", "AUD", "CAD", "CHF", "CNY"
        };
        
        for (int i = 0; i < currencies.length; i++) {
            if (currencies[i].equals(savedCurrency)) {
                currencySpinner.setSelection(i);
                Log.d("SettingsActivity", "Set spinner to position: " + i);
                break;
            }
        }
    }

    private void saveSettings() {
        String[] currencies = {
            "RM", "USD", "EUR", "GBP", "SGD", "JPY", "AUD", "CAD", "CHF", "CNY"
        };
        
        int selectedPosition = currencySpinner.getSelectedItemPosition();
        String selectedCurrency = currencies[selectedPosition];
        
        Log.d("SettingsActivity", "Selected currency: " + selectedCurrency + " at position: " + selectedPosition);
        
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CURRENCY, selectedCurrency);
        editor.apply();
        
        Toast.makeText(this, "Settings saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Static method to get current currency from any activity
    public static String getCurrentCurrency(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY);
    }

    // Static method to get currency symbol
    public static String getCurrencySymbol(String currency) {
        switch (currency) {
            case "RM":
                return "RM";
            case "USD":
                return "$";
            case "EUR":
                return "€";
            case "GBP":
                return "£";
            case "SGD":
                return "S$";
            case "JPY":
                return "¥";
            case "AUD":
                return "A$";
            case "CAD":
                return "C$";
            case "CHF":
                return "CHF";
            case "CNY":
                return "¥";
            default:
                return "RM";
        }
    }
} 