package br.com.menuux.comedoriadatia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Certifique-se de que a dependência do Glide está no seu build.gradle

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Domain.BannerDomain; // Precisaremos criar esta classe
import br.com.menuux.comedoriadatia.R;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private ArrayList<BannerDomain> items;
    private Context context;

    public BannerAdapter(ArrayList<BannerDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // O contexto é obtido a partir do 'parent'
        context = parent.getContext();
        // Aqui o seu viewholder_banner.xml é "inflado" (criado) para se tornar uma View
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_banner, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Pega o item da posição atual
        BannerDomain currentItem = items.get(position);

        // Obtém a URL da imagem do item atual
        String url = currentItem.getUrl();

        // Usa a biblioteca Glide para carregar a imagem da URL na ImageView
        // 'pic' é o ID que darei como exemplo para a ImageView no seu viewholder_banner.xml
        Glide.with(context)
                .load(url)
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        // Retorna o número total de itens na lista
        return items.size();
    }

    // Esta classe interna representa a view de cada item (seu viewholder_banner.xml)
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView pic; // A ImageView que está dentro do seu banner

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encontra a ImageView pelo ID dentro do layout do item
            // ATENÇÃO: Substitua 'pic' pelo ID real da sua ImageView no arquivo viewholder_banner.xml
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
