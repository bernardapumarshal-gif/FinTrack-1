package com.example.fintrack;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {
    private AutoCompleteTextView currencyAutoComplete;
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
        currencyAutoComplete = findViewById(R.id.auto_complete_currency);
        MaterialButton saveButton = findViewById(R.id.button_save);

        // Set up currency dropdown
        setupCurrencyDropdown();

        // Load saved settings
        loadSettings();
        
        // Set up click listener for the dropdown
        currencyAutoComplete.setOnClickListener(v -> {
            currencyAutoComplete.showDropDown();
        });
        
        // Set up item click listener for the dropdown
        currencyAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("SettingsActivity", "Currency selected: " + parent.getItemAtPosition(position));
        });
        
        // Set up focus change listener
        currencyAutoComplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                currencyAutoComplete.showDropDown();
            }
        });
        
        // Set up touch listener to handle dropdown dismissal
        currencyAutoComplete.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                if (!currencyAutoComplete.isPopupShowing()) {
                    currencyAutoComplete.showDropDown();
                }
                return true;
            }
            return false;
        });

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            Log.d("SettingsActivity", "Save button clicked");
            saveSettings();
        });
    }

    private void setupCurrencyDropdown() {
        Log.d("SettingsActivity", "Setting up currency dropdown");
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
            android.R.layout.simple_dropdown_item_1line, currencies) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View view = super.getView(position, convertView, parent);
                if (view instanceof android.widget.TextView) {
                    android.widget.TextView textView = (android.widget.TextView) view;
                    textView.setTextColor(getResources().getColor(R.color.text_primary));
                    textView.setTextSize(16);
                    textView.setPadding(16, 12, 16, 12);
                }
                return view;
            }
        };
        currencyAutoComplete.setAdapter(adapter);
        
        // Set dropdown properties
        currencyAutoComplete.setThreshold(1);
        currencyAutoComplete.setDropDownWidth(android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        
        // Set a default selection if none is set
        currencyAutoComplete.setText(currencies[0], false);
        Log.d("SettingsActivity", "Currency dropdown setup complete");
    }

    private void loadSettings() {
        String savedCurrency = sharedPreferences.getString(KEY_CURRENCY, DEFAULT_CURRENCY);
        Log.d("SettingsActivity", "Loading settings, saved currency: " + savedCurrency);
        
        // Find the display text for the saved currency
        String[] currencyCodes = {
            "RM", "USD", "EUR", "GBP", "SGD", "JPY", "AUD", "CAD", "CHF", "CNY"
        };
        String[] currencyDisplayNames = {
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
        
        for (int i = 0; i < currencyCodes.length; i++) {
            if (currencyCodes[i].equals(savedCurrency)) {
                currencyAutoComplete.setText(currencyDisplayNames[i], false);
                Log.d("SettingsActivity", "Set dropdown to: " + currencyDisplayNames[i]);
                break;
            }
        }
    }

    private void saveSettings() {
        String[] currencyCodes = {
            "RM", "USD", "EUR", "GBP", "SGD", "JPY", "AUD", "CAD", "CHF", "CNY"
        };
        String[] currencyDisplayNames = {
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
        
        String selectedDisplayText = currencyAutoComplete.getText().toString();
        String selectedCurrency = DEFAULT_CURRENCY; // Default fallback
        
        // Find the currency code for the selected display text
        for (int i = 0; i < currencyDisplayNames.length; i++) {
            if (currencyDisplayNames[i].equals(selectedDisplayText)) {
                selectedCurrency = currencyCodes[i];
                break;
            }
        }
        
        Log.d("SettingsActivity", "Selected currency: " + selectedCurrency + " for display: " + selectedDisplayText);
        
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