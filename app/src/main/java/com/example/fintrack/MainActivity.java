package com.example.fintrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ExpenseAdapter adapter;
    private List<Expense> expenseList;
    private ExpenseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        database = ExpenseDatabase.getInstance(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(expenseList, this);
        recyclerView.setAdapter(adapter);

        // Set up FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.fab_add_expense);
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
        loadExpenses();
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
                    }
                });
            }
        }).start();
    }

    public void shareExpense(Expense expense) {
        String shareText = "Expense: " + expense.getTitle() + 
                          "\nAmount: $" + expense.getAmount() + 
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
    }
} 