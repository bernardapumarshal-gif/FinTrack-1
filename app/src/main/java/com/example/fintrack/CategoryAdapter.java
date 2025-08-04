package com.example.fintrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<CategorySummary> categories;

    public static class CategorySummary {
        public String category;
        public double amount;
        public double percentage;

        public CategorySummary(String category, double amount, double percentage) {
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
        }
    }

    public CategoryAdapter(List<CategorySummary> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategorySummary category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<CategorySummary> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;
        private TextView tvAmount;
        private TextView tvPercentage;
        private View colorIndicator;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
        }

        public void bind(CategorySummary category) {
            tvCategory.setText(category.category);
            tvAmount.setText(String.format("%s%.2f", getCurrencySymbol(), category.amount));
            tvPercentage.setText(String.format("%.1f%%", category.percentage));
            
            // Set color based on category
            int color = getCategoryColor(category.category);
            colorIndicator.setBackgroundColor(color);
        }

        private String getCurrencySymbol() {
            // Get currency from SharedPreferences
            android.content.SharedPreferences prefs = itemView.getContext()
                .getSharedPreferences("FinTrackSettings", android.content.Context.MODE_PRIVATE);
            String currency = prefs.getString("currency", "RM");
            return SettingsActivity.getCurrencySymbol(currency);
        }

        private int getCategoryColor(String category) {
            switch (category) {
                case "Food & Dining":
                    return itemView.getContext().getColor(R.color.category_food);
                case "Transportation":
                    return itemView.getContext().getColor(R.color.category_transport);
                case "Shopping":
                    return itemView.getContext().getColor(R.color.category_shopping);
                case "Entertainment":
                    return itemView.getContext().getColor(R.color.category_entertainment);
                case "Healthcare":
                    return itemView.getContext().getColor(R.color.category_healthcare);
                case "Education":
                    return itemView.getContext().getColor(R.color.category_education);
                case "Utilities":
                    return itemView.getContext().getColor(R.color.category_utilities);
                default:
                    return itemView.getContext().getColor(R.color.category_other);
            }
        }
    }
} 