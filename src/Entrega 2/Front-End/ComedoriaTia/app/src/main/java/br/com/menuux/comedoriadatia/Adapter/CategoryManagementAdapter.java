package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import br.com.menuux.comedoriadatia.Activity.AdminEditItemActivity;
import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.R;

public class CategoryManagementAdapter extends RecyclerView.Adapter<CategoryManagementAdapter.ViewHolder> {

    private final ArrayList<CategoryDomain> items;

    public CategoryManagementAdapter(ArrayList<CategoryDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryDomain category = items.get(position);
        String imageUrl = category.getImagePath();
        String name = category.getName();
        Context context = holder.itemView.getContext();

        holder.categoryName.setText(name);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.logo)
                        .into(holder.categoryImage);
            } else {
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(context)
                            .asBitmap()
                            .load(decodedString)
                            .placeholder(R.drawable.logo)
                            .into(holder.categoryImage);
                } catch (Exception e) {
                    holder.categoryImage.setImageResource(R.drawable.logo);
                }
            }
        } else {
            holder.categoryImage.setImageResource(R.drawable.logo);
        }

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminEditItemActivity.class);
            intent.putExtra("itemType", "category");
            intent.putExtra("itemId", category.getKey());
            intent.putExtra("imageUrl", category.getImagePath());
            intent.putExtra("itemName", category.getName());
            context.startActivity(intent);
        });

        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Categoria")
                    .setMessage("Tem certeza de que deseja excluir esta categoria?")
                    .setPositiveButton("Excluir", (dialog, which) -> {
                        deleteCategory(category, position);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void deleteCategory(CategoryDomain category, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category").child(category.getKey());
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                Toast.makeText(null, "Categoria excluída com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(null, "Falha ao excluir a categoria.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage, editBtn, deleteBtn;
        TextView categoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}
