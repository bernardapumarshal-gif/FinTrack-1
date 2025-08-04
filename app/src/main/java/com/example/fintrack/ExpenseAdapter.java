package com.example.fintrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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
        private TextView titleText, amountText, dateText, categoryText;
        private ImageView photoImage;
        private ImageButton shareButton, deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.text_title);
            amountText = itemView.findViewById(R.id.text_amount);
            dateText = itemView.findViewById(R.id.text_date);
            categoryText = itemView.findViewById(R.id.text_category);
            photoImage = itemView.findViewById(R.id.image_photo);
            shareButton = itemView.findViewById(R.id.button_share);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }

        public void bind(final Expense expense) {
            titleText.setText(expense.getTitle());
            amountText.setText("$" + String.format("%.2f", expense.getAmount()));
            dateText.setText(expense.getDate() + " " + expense.getTime());
            categoryText.setText(expense.getCategory());

            // Load photo if available
            if (expense.getPhotoPath() != null && !expense.getPhotoPath().isEmpty()) {
                photoImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(expense.getPhotoPath())
                        .centerCrop()
                        .into(photoImage);
            } else {
                photoImage.setVisibility(View.GONE);
            }

            // Share button
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).shareExpense(expense);
                    }
                }
            });

            // Delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).deleteExpense(expense);
                    }
                }
            });
        }
    }
} 