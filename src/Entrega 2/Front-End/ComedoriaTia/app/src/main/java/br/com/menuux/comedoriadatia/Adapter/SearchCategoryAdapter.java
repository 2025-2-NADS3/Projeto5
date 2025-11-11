package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.databinding.ViewholderCategorySearchBinding;

public class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.Viewholder> {
    ArrayList<CategoryDomain> items;
    Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, CategoryDomain category);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public SearchCategoryAdapter(ArrayList<CategoryDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SearchCategoryAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderCategorySearchBinding binding = ViewholderCategorySearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCategoryAdapter.Viewholder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getName());

        Glide.with(context)
                .load(items.get(position).getImagePath())
                .into(holder.binding.img);

        // Adicionar clique no item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, items.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCategorySearchBinding binding;

        public Viewholder(ViewholderCategorySearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
