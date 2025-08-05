package com.example.fintrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private Context context;

    public ExpenseAdapter(List<Expense> expenseList, Context context) {
        this.expenseList = expenseList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvAmount, tvDate, tvCategory, tvTime;
        private View categoryIndicator;
        private ImageButton btnShare, btnDelete, btnEdit;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTime = itemView.findViewById(R.id.tv_time);
            categoryIndicator = itemView.findViewById(R.id.category_indicator);
            btnShare = itemView.findViewById(R.id.btn_share);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }

        public void bind(final Expense expense) {
            tvTitle.setText(expense.getTitle());
            tvAmount.setText(String.format("%s%.2f", getCurrencySymbol(), expense.getAmount()));
            tvDate.setText(expense.getDate());
            tvCategory.setText(expense.getCategory());
            tvTime.setText(expense.getTime());

            // Set category color indicator
            int color = getCategoryColor(expense.getCategory());
            categoryIndicator.setBackgroundColor(color);

            // Share button
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).shareExpense(expense);
                    }
                }
            });

            // Delete button
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).deleteExpense(expense);
                    }
                }
            });

            // Edit button
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.content.Intent intent = new android.content.Intent(context, EditExpenseActivity.class);
                    intent.putExtra("expense_id", expense.getId());
                    context.startActivity(intent);
                }
            });
        }

        private int getCategoryColor(String category) {
            switch (category) {
                case "Food & Dining":
                    return context.getColor(R.color.category_food);
                case "Transportation":
                    return context.getColor(R.color.category_transport);
                case "Shopping":
                    return context.getColor(R.color.category_shopping);
                case "Entertainment":
                    return context.getColor(R.color.category_entertainment);
                case "Healthcare":
                    return context.getColor(R.color.category_healthcare);
                case "Education":
                    return context.getColor(R.color.category_education);
                case "Utilities":
                    return context.getColor(R.color.category_utilities);
                default:
                    return context.getColor(R.color.category_other);
            }
        }

        private String getCurrencySymbol() {
            // Get currency from SharedPreferences
            android.content.SharedPreferences prefs = context.getSharedPreferences("FinTrackSettings", android.content.Context.MODE_PRIVATE);
            String currency = prefs.getString("currency", "RM");
            return SettingsActivity.getCurrencySymbol(currency);
        }
    }
} 