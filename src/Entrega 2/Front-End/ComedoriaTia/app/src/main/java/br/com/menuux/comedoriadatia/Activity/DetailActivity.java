package br.com.menuux.comedoriadatia.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Adapter.SimilarAdapter;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.Repository.FavoritesRepository;
import br.com.menuux.comedoriadatia.databinding.ActivityDetailBinding;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private ItemDomain object;
    private int weight = 1;
    private FirebaseAuth mAuth;
    private FavoritesRepository favoritesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        favoritesRepository = new FavoritesRepository(mAuth, database);

        getBundles();
        setVariable();
        initSimilarList();
    }

    private void initSimilarList() {
        DatabaseReference myRef = database.getReference("Items");
        binding.progressBarSimiliar.setVisibility(View.VISIBLE);
        ArrayList<ItemDomain> items = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(ItemDomain.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewsimiliar.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewsimiliar.setAdapter(new SimilarAdapter(items));
                    }
                    binding.progressBarSimiliar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.img);

        binding.priceKgtxt.setText("R$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingBar.setRating((float) object.getStar());
        binding.ratingTxt.setText("(" + object.getStar() + ")");
        binding.totaltxt.setText("R$" + (weight * object.getPrice()));

        binding.plusBtn.setOnClickListener(v -> {
            weight = weight + 1;
            binding.weightTxt.setText(weight + "kg");
            binding.totaltxt.setText("R$" + (weight * object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (weight > 1) {
                weight = weight - 1;
                binding.weightTxt.setText(weight + "kg");
                binding.totaltxt.setText("R$" + (weight * object.getPrice()));
            }
        });

        binding.AddBtn.setOnClickListener(v -> {
            addToCart();
        });

        binding.favBtn.setOnClickListener(v -> {
            addToFavorites();
        });
    }

    private void getBundles() {
        object = (ItemDomain) getIntent().getSerializableExtra("object");
    }
    private String sanitizeKey(String key) {
        return key.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "").replace("/", "");
    }

    private void addToCart() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Você precisa estar logado para adicionar itens ao carrinho", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = database.getReference("Carts").child(userId);

        object.setWeight(weight);
        String itemKey = sanitizeKey(object.getTitle());

        cartRef.child(itemKey).setValue(object).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                Toast.makeText(this, "Item adicionado ao carrinho!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Falha ao adicionar o item.", Toast.LENGTH_SHORT).show();
                Log.e("DetailActivity", "Could not add item to cart", task.getException());
            }
        });
    }

    private void addToFavorites() {
        favoritesRepository.addToFavorites(object, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Adicionado aos Favoritos!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Falha ao adicionar aos favoritos.", Toast.LENGTH_SHORT).show();
                Log.e("DetailActivity", "Could not add item to favorites", task.getException());
            }
        });
    }
}
