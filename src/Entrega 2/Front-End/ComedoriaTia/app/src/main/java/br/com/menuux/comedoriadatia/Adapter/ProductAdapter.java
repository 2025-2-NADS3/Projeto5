package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.Domain.Product;
import br.com.menuux.comedoriadatia.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> items;
    private final List<CategoryDomain> categories;
    private final Context context;
    private final OnProductListener onProductListener;

    public interface OnProductListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> items, List<CategoryDomain> categories, OnProductListener onProductListener) {
        this.context = context;
        this.items = items;
        this.categories = categories;
        this.onProductListener = onProductListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = items.get(position);

        holder.productName.setText(product.getTitle());

        String categoryName = "";
        for (CategoryDomain category : categories) {
            if (product.getCategoryId() == category.getId()) {
                categoryName = category.getName();
                break;
            }
        }
        holder.productCategory.setText(categoryName);
        holder.productPrice.setText(String.format("R$ %.2f", product.getPrice()));

        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("http")) {
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.logo)
                        .into(holder.productImage);
            } else {
                try {
                    byte[] decodedString = Base64.decode(imagePath, Base64.DEFAULT);
                    Glide.with(context)
                            .asBitmap()
                            .load(decodedString)
                            .placeholder(R.drawable.logo)
                            .into(holder.productImage);
                } catch (Exception e) {
                    holder.productImage.setImageResource(R.drawable.logo);
                }
            }
        } else {
            holder.productImage.setImageResource(R.drawable.logo);
        }


        holder.itemView.findViewById(R.id.editGroup).setOnClickListener(v -> {
            if (onProductListener != null) {
                onProductListener.onEditClick(product);
            }
        });

        holder.itemView.findViewById(R.id.deleteGroup).setOnClickListener(v -> {
            if (onProductListener != null) {
                onProductListener.onDeleteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productCategory, productPrice;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            productPrice = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}
