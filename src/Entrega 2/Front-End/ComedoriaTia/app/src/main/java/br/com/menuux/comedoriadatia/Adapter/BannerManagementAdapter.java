package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import br.com.menuux.comedoriadatia.Activity.AdminEditItemActivity;
import br.com.menuux.comedoriadatia.Domain.BannerDomain;
import br.com.menuux.comedoriadatia.R;

public class BannerManagementAdapter extends RecyclerView.Adapter<BannerManagementAdapter.ViewHolder> {

    private final ArrayList<BannerDomain> items;

    public BannerManagementAdapter(ArrayList<BannerDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_banner_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BannerDomain banner = items.get(position);
        String imageUrl = banner.getUrl();
        Context context = holder.itemView.getContext();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.logo)
                        .into(holder.bannerImage);
            } else {
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(context)
                            .asBitmap()
                            .load(decodedString)
                            .placeholder(R.drawable.logo)
                            .into(holder.bannerImage);
                } catch (Exception e) {
                    holder.bannerImage.setImageResource(R.drawable.logo);
                }
            }
        } else {
            holder.bannerImage.setImageResource(R.drawable.logo);
        }

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminEditItemActivity.class);
            intent.putExtra("itemType", "banner");
            intent.putExtra("itemId", banner.getId());
            intent.putExtra("imageUrl", banner.getUrl());
            context.startActivity(intent);
        });

        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Banner")
                    .setMessage("Tem certeza de que deseja excluir este banner?")
                    .setPositiveButton("Excluir", (dialog, which) -> {
                        deleteBanner(banner, position);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void deleteBanner(BannerDomain banner, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Banners").child(banner.getId());
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                Toast.makeText(null, "Banner excluído com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(null, "Falha ao excluir o banner.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage, editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
