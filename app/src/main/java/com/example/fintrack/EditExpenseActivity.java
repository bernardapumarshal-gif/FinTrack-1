package com.example.fintrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditExpenseActivity extends AppCompatActivity {
    private EditText titleEditText, amountEditText, notesEditText;
    private Button dateButton, timeButton, photoButton, saveButton;
    private AutoCompleteTextView categorySpinner;
    private ImageView photoImageView;

    private String selectedDate = "";
    private String selectedTime = "";
    private String photoPath = "";
    private int expenseId = -1;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense); // reuse the add layout

        // Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Edit Expense");
        }

        // Views
        titleEditText = findViewById(R.id.edit_text_title);
        amountEditText = findViewById(R.id.edit_text_amount);
        notesEditText = findViewById(R.id.edit_text_notes);
        dateButton = findViewById(R.id.button_date);
        timeButton = findViewById(R.id.button_time);
        photoButton = findViewById(R.id.button_photo);
        saveButton = findViewById(R.id.button_save);
        categorySpinner = findViewById(R.id.spinner_category);
        photoImageView = findViewById(R.id.image_view_photo);

        // Category spinner
        String[] categories = {"Food & Dining", "Transportation", "Shopping", "Entertainment", "Healthcare", "Education", "Utilities", "Other"};
        android.widget.ArrayAdapter<String> categoryAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(categoryAdapter);

        // Get expense ID from intent
        expenseId = getIntent().getIntExtra("expense_id", -1);
        if (expenseId == -1) {
            Toast.makeText(this, "Invalid expense", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load expense data
        loadExpense();

        // Date picker
        dateButton.setOnClickListener(v -> showDatePicker());

        // Time picker
        timeButton.setOnClickListener(v -> showTimePicker());

        // Photo picker
        photoButton.setOnClickListener(v -> openImagePicker());

        // Save button
        saveButton.setOnClickListener(v -> updateExpense());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            (view, year1, month1, dayOfMonth) -> {
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                dateButton.setText("Date: " + selectedDate);
            }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            (view, hourOfDay, minute1) -> {
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                timeButton.setText("Time: " + selectedTime);
            }, hour, minute, true);
        timePickerDialog.show();
    }

    @SuppressWarnings("deprecation")
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    photoImageView.setImageBitmap(bitmap);
                    photoImageView.setVisibility(View.VISIBLE);

                    // Save image to internal storage
                    photoPath = saveImageToInternalStorage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String saveImageToInternalStorage(Bitmap bitmap) {
        String fileName = "expense_photo_" + System.currentTimeMillis() + ".jpg";
        File file = new File(getFilesDir(), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void loadExpense() {
        new Thread(() -> {
            Expense expense = ExpenseDatabase.getInstance(this).expenseDao().getExpenseById(expenseId);
            if (expense != null) {
                runOnUiThread(() -> {
                    titleEditText.setText(expense.getTitle());
                    amountEditText.setText(String.valueOf(expense.getAmount()));
                    notesEditText.setText(expense.getNotes());
                    selectedDate = expense.getDate();
                    selectedTime = expense.getTime();
                    dateButton.setText("Date: " + selectedDate);
                    timeButton.setText("Time: " + selectedTime);
                    categorySpinner.setText(expense.getCategory(), false);
                    photoPath = expense.getPhotoPath();
                    if (photoPath != null && !photoPath.isEmpty()) {
                        Bitmap bitmap = android.graphics.BitmapFactory.decodeFile(photoPath);
                        if (bitmap != null) {
                            photoImageView.setImageBitmap(bitmap);
                            photoImageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void updateExpense() {
        String title = titleEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        String category = categorySpinner.getText().toString();
        String notes = notesEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        if (amountStr.isEmpty()) {
            amountEditText.setError("Amount is required");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            amountEditText.setError("Invalid amount");
            return;
        }

        Expense updatedExpense = new Expense(title, amount, selectedDate, selectedTime, category, notes, photoPath);
        updatedExpense.setId(expenseId);

        new Thread(() -> {
            ExpenseDatabase.getInstance(this).expenseDao().updateExpense(updatedExpense);
            runOnUiThread(() -> {
                Toast.makeText(this, "Expense updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}