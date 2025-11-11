package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Activity.DetailActivity;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.Repository.CartRepository;
import br.com.menuux.comedoriadatia.Repository.FavoritesRepository;
import br.com.menuux.comedoriadatia.databinding.ViewholderBestDealBinding;

public class BestDealAdapter extends RecyclerView.Adapter<BestDealAdapter.Viewholder> {
    private final ArrayList<ItemDomain> items;
    private Context context;
    private final CartRepository cartRepository;
    private final FavoritesRepository favoritesRepository;

    public BestDealAdapter(ArrayList<ItemDomain> items, FirebaseAuth mAuth, FirebaseDatabase database) {
        this.items = items;
        this.cartRepository = new CartRepository(mAuth, database);
        this.favoritesRepository = new FavoritesRepository(mAuth, database);
    }

    @NonNull
    @Override
    public BestDealAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderBestDealBinding binding = ViewholderBestDealBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BestDealAdapter.Viewholder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getTitle());
        holder.binding.priceTxt.setText("R$" + items.get(position).getPrice());

        Glide.with(context)
                .load(items.get(position).getImagePath())
                .into(holder.binding.img);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });

        holder.binding.plusBtn.setOnClickListener(v -> {
            addToCart(items.get(position));
        });

        holder.binding.favBtn.setOnClickListener(v -> {
            addToFavorites(items.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderBestDealBinding binding;

        public Viewholder(ViewholderBestDealBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void addToCart(ItemDomain item) {
        cartRepository.addItemToCart(item, 1, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Item adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Falha ao adicionar o item.", Toast.LENGTH_SHORT).show();
                Log.e("BestDealAdapter", "Could not add item to cart", task.getException());
            }
        });
    }

    private void addToFavorites(ItemDomain item) {
        favoritesRepository.addToFavorites(item, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Adicionado aos Favoritos!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Falha ao adicionar aos favoritos.", Toast.LENGTH_SHORT).show();
                Log.e("BestDealAdapter", "Could not add item to favorites", task.getException());
            }
        });
    }
}
