package com.example.fintrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView categoryRecyclerView;
    private ExpenseAdapter adapter;
    private CategoryAdapter categoryAdapter;
    private List<Expense> expenseList;
    private List<CategoryAdapter.CategorySummary> categoryList;
    private ExpenseDatabase database;
    private SharedPreferences sharedPreferences;
    
    // Dashboard views
    private TextView tvTotalExpenses;
    private TextView tvMonthAmount;
    private TextView tvAvgPerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database and preferences
        database = ExpenseDatabase.getInstance(this);
        sharedPreferences = getSharedPreferences("FinTrackSettings", MODE_PRIVATE);

        // Set up toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("FinTrack");
        }

        // Initialize dashboard views
        tvTotalExpenses = findViewById(R.id.tv_total_expenses);
        tvMonthAmount = findViewById(R.id.tv_month_amount);
        tvAvgPerDay = findViewById(R.id.tv_avg_per_day);

        // Initialize expense RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(expenseList, this);
        recyclerView.setAdapter(adapter);

        // Initialize category RecyclerView
        categoryRecyclerView = findViewById(R.id.rv_categories);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Set up FloatingActionButton
        ExtendedFloatingActionButton fab = findViewById(R.id.fab_add_expense);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        // Load expenses from database
        loadExpenses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");
        loadExpenses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MainActivity", "Creating options menu");
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Log.d("MainActivity", "Menu inflated successfully");
        
        // Debug: Check if menu items are created
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Log.d("MainActivity", "Menu item: " + item.getTitle() + " with ID: " + item.getItemId());
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            Log.d("MainActivity", "Settings menu item clicked");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void loadExpenses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Expense> expenses = database.expenseDao().getAllExpenses();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        expenseList.clear();
                        expenseList.addAll(expenses);
                        adapter.notifyDataSetChanged();
                        
                        // Update dashboard statistics
                        updateDashboardStatistics(expenses);
                        updateCategoryBreakdown(expenses);
                    }
                });
            }
        }).start();
    }

    private void updateDashboardStatistics(List<Expense> expenses) {
        double totalAmount = 0;
        double monthAmount = 0;
        
        Calendar currentMonth = Calendar.getInstance();
        int currentYear = currentMonth.get(Calendar.YEAR);
        int currentMonthNum = currentMonth.get(Calendar.MONTH);
        
        for (Expense expense : expenses) {
            totalAmount += expense.getAmount();
            
            // Calculate this month's expenses
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar expenseDate = Calendar.getInstance();
                expenseDate.setTime(sdf.parse(expense.getDate()));
                
                if (expenseDate.get(Calendar.YEAR) == currentYear && 
                    expenseDate.get(Calendar.MONTH) == currentMonthNum) {
                    monthAmount += expense.getAmount();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Calculate average per day
        double avgPerDay = 0;
        if (!expenses.isEmpty()) {
            Calendar firstExpense = Calendar.getInstance();
            Calendar lastExpense = Calendar.getInstance();
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                firstExpense.setTime(sdf.parse(expenses.get(expenses.size() - 1).getDate()));
                lastExpense.setTime(sdf.parse(expenses.get(0).getDate()));
                
                long daysDiff = (lastExpense.getTimeInMillis() - firstExpense.getTimeInMillis()) / (24 * 60 * 60 * 1000);
                if (daysDiff > 0) {
                    avgPerDay = totalAmount / (daysDiff + 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        String currency = SettingsActivity.getCurrentCurrency(sharedPreferences);
        String currencySymbol = SettingsActivity.getCurrencySymbol(currency);
        
        tvTotalExpenses.setText(String.format("Total Expenses: %s%.2f", currencySymbol, totalAmount));
        tvMonthAmount.setText(String.format("%s%.2f", currencySymbol, monthAmount));
        tvAvgPerDay.setText(String.format("%s%.2f", currencySymbol, avgPerDay));
    }

    private void updateCategoryBreakdown(List<Expense> expenses) {
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalAmount = 0;
        
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double currentTotal = categoryTotals.getOrDefault(category, 0.0);
            categoryTotals.put(category, currentTotal + expense.getAmount());
            totalAmount += expense.getAmount();
        }
        
        categoryList.clear();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double percentage = totalAmount > 0 ? (entry.getValue() / totalAmount) * 100 : 0;
            categoryList.add(new CategoryAdapter.CategorySummary(
                entry.getKey(), entry.getValue(), percentage));
        }
        
        categoryAdapter.notifyDataSetChanged();
    }



    public void shareExpense(Expense expense) {
        String currency = SettingsActivity.getCurrentCurrency(sharedPreferences);
        String currencySymbol = SettingsActivity.getCurrencySymbol(currency);
        
        String shareText = "Expense: " + expense.getTitle() + 
                          "\nAmount: " + currencySymbol + expense.getAmount() + 
                          "\nDate: " + expense.getDate() + 
                          "\nTime: " + expense.getTime() + 
                          "\nCategory: " + expense.getCategory() + 
                          "\nNotes: " + expense.getNotes();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Expense Record");
        
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public void deleteExpense(Expense expense) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete", (dialog, which) -> {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        database.expenseDao().deleteExpense(expense);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadExpenses();
                                Toast.makeText(MainActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 