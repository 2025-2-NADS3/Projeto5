package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.databinding.ViewholderCartBinding;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    private ArrayList<ItemDomain> items;
    private Context context;
    private CartListener listener;

    public interface CartListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemRemoved(int position);
    }

    public CartAdapter(ArrayList<ItemDomain> items, CartListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ItemDomain item = items.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText("R$" + item.getPrice());
        holder.binding.quantityTxt.setText(String.valueOf(item.getWeight()));
        holder.binding.totalItemTxt.setText("R$" + (item.getPrice() * item.getWeight()));

        Glide.with(context)
                .load(item.getImagePath())
                .into(holder.binding.img);

        holder.binding.plusBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(position, item.getWeight() + 1);
            }
        });

        holder.binding.minusBtn.setOnClickListener(v -> {
            if (item.getWeight() > 1) {
                if (listener != null) {
                    listener.onQuantityChanged(position, item.getWeight() - 1);
                }
            } else {
                if (listener != null) {
                    listener.onItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateCartItems(List<ItemDomain> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public List<ItemDomain> getCartItems() {
        return items;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
