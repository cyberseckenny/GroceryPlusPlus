package me.grocery.grocerylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private final Context context;
    private final ArrayList<CategoryModel> categories;

    public CategoriesAdapter(Context context, ArrayList<CategoryModel> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categories.get(position);
        holder.categoryName.setText(category.getCategoryName());

        // Set up the inner RecyclerView
        ItemsAdapter itemsAdapter = new ItemsAdapter(category.getItems());
        holder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.itemsRecyclerView.setAdapter(itemsAdapter);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        RecyclerView itemsRecyclerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryNameTextView);
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
        }
    }
}
