package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.R;
import br.com.menuux.comedoriadatia.Repository.FavoritesRepository;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private ArrayList<ItemDomain> items;
    private FavoritesRepository favoritesRepository;
    private Context context;

    public FavoritesAdapter(ArrayList<ItemDomain> items, FirebaseAuth mAuth, FirebaseDatabase database) {
        this.items = items;
        this.favoritesRepository = new FavoritesRepository(mAuth, database);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDomain item = items.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.priceTxt.setText("R$" + item.getPrice());

        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImagePath())
                    .into(holder.img);
        }

        holder.favoriteBtn.setOnClickListener(v -> {
            removeFromFavorites(item, position);
        });
    }

    private void removeFromFavorites(ItemDomain item, int position) {
        favoritesRepository.removeFromFavorites(item, task -> {
            if (task.isSuccessful()) {
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                Toast.makeText(context, "Removido dos Favoritos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Falha ao remover dos favoritos", Toast.LENGTH_SHORT).show();
                Log.e("FavoritesAdapter", "Could not remove item from favorites", task.getException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt;
        ImageView img, favoriteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            img = itemView.findViewById(R.id.img);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
        }
    }
}
