package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.menuux.comedoriadatia.Domain.Product;
import br.com.menuux.comedoriadatia.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<Product> items;
    private final Context context;
    private final OnProductListener onProductListener;

    public interface OnProductListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> items, OnProductListener onProductListener) {
        this.context = context;
        this.items = items;
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
        holder.productCategory.setText(String.valueOf(product.getCategoryId()));
        holder.productPrice.setText(String.format("R$ %.2f", product.getPrice()));

        Glide.with(context)
                .load(product.getImagePath())
                .placeholder(R.drawable.logo) // Imagem provisória
                .into(holder.productImage);

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
