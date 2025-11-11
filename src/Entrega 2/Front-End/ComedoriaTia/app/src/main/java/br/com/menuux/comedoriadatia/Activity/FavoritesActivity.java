package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Adapter.FavoritesAdapter;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.databinding.ActivityFavoritesBinding;

public class FavoritesActivity extends BaseActivity {
    private ActivityFavoritesBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FavoritesAdapter adapter;
    private ArrayList<ItemDomain> favoritesList = new ArrayList<>();
    private DatabaseReference favoritesRef;
    private ValueEventListener favoritesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        setupRecyclerView();
        initFavoritesList();
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void initFavoritesList() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        favoritesRef = database.getReference("Favorites").child(mAuth.getCurrentUser().getUid());

        favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoritesList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ItemDomain item = issue.getValue(ItemDomain.class);
                        if (item != null) {
                            favoritesList.add(item);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Tratar erro
            }
        };
        favoritesRef.addValueEventListener(favoritesListener);
    }

    private void setupRecyclerView() {
        binding.favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritesAdapter(favoritesList, mAuth, database);
        binding.favoritesRecyclerView.setAdapter(adapter);
    }

    private void updateUI() {
        if (favoritesList.isEmpty()) {
            binding.emptyFavoritesLayout.setVisibility(View.VISIBLE);
            binding.favoritesContentLayout.setVisibility(View.GONE);
        } else {
            binding.emptyFavoritesLayout.setVisibility(View.GONE);
            binding.favoritesContentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (favoritesRef != null && favoritesListener != null) {
            favoritesRef.removeEventListener(favoritesListener);
        }
    }

    // Métodos para navegação do menu inferior
    public void openHome(View view) {
        startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
        finish();
    }

    public void openCart(View view) {
        startActivity(new Intent(FavoritesActivity.this, CartActivity.class));
        finish();
    }

    public void openFavorites(View view) {
    }

    public void openWallet(View view) {
        startActivity(new Intent(FavoritesActivity.this, CarteiraActivity.class));
        finish();
    }

    public void openProfile(View view) {
        startActivity(new Intent(FavoritesActivity.this, ProfileActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FavoritesActivity.this, MainActivity.class));
        finish();
    }
}
