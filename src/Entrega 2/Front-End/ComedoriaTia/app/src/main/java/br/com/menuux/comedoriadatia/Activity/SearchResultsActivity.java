package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Adapter.BestDealAdapter;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.databinding.ActivitySearchResultsBinding;

public class SearchResultsActivity extends BaseActivity {
    private ActivitySearchResultsBinding binding;
    private ArrayList<ItemDomain> searchResults;
    private String searchQuery;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        getResultsFromIntent();
        setVariable();
        displaySearchResults();
    }

    private void getResultsFromIntent() {
        searchResults = (ArrayList<ItemDomain>) getIntent().getSerializableExtra("searchResults");
        searchQuery = getIntent().getStringExtra("searchQuery");
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(SearchResultsActivity.this, CartActivity.class));
        });

        if (searchQuery != null) {
            binding.searchQueryText.setText("Resultados para: \"" + searchQuery + "\"");
        }
    }

    private void displaySearchResults() {
        if (searchResults != null && !searchResults.isEmpty()) {
            binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            binding.productsRecyclerView.setAdapter(new BestDealAdapter(searchResults, mAuth, database));
            binding.emptyState.setVisibility(View.GONE);
            binding.productsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.productsRecyclerView.setVisibility(View.GONE);
        }
    }
}
