package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Adapter.SearchCategoryAdapter;
import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.R;
import br.com.menuux.comedoriadatia.databinding.ActivitySearchBinding;

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding binding;
    private ArrayList<CategoryDomain> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCategories();
        setVariable();
        setupSearchBar();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, CartActivity.class));
        });
    }

    private void setupSearchBar() {
        binding.searchBtn.setOnClickListener(v -> {
            String searchText = binding.searchEditText.getText().toString().trim();
            if (!searchText.isEmpty()) {
                performSearch(searchText);
            }
        });
    }

    private void performSearch(String searchQuery) {
        DatabaseReference itemsRef = database.getReference("Items");
        ArrayList<ItemDomain> searchResults = new ArrayList<>();

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ItemDomain item = issue.getValue(ItemDomain.class);
                        if (item != null && containsIgnoreCaseAndAccents(item.getTitle(), searchQuery)) {
                            searchResults.add(item);
                        }
                    }

                    if (!searchResults.isEmpty()) {
                        Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
                        intent.putExtra("searchResults", searchResults);
                        intent.putExtra("searchQuery", searchQuery);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SearchActivity.this, "Nenhum produto encontrado", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Erro na busca", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean containsIgnoreCaseAndAccents(String text, String query) {
        if (text == null || query == null) return false;

        // Remove acentos e converte para minúsculo
        String normalizedText = removeAccents(text.toLowerCase());
        String normalizedQuery = removeAccents(query.toLowerCase());

        return normalizedText.contains(normalizedQuery);
    }

    private String removeAccents(String text) {
        return text.replaceAll("[áàâãä]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[íìîï]", "i")
                .replaceAll("[óòôõö]", "o")
                .replaceAll("[úùûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[ñ]", "n");
    }

    private void initCategories() {
        DatabaseReference categoryRef = database.getReference("Category");
        binding.categoriesRecyclerView.setVisibility(View.GONE);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        CategoryDomain category = issue.getValue(CategoryDomain.class);
                        if (category != null) {
                            categoryList.add(category);
                        }
                    }
                    setupCategoryGrid();
                    binding.categoriesRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupCategoryGrid() {
        // Usar GridLayoutManager com 2 colunas
        binding.categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        SearchCategoryAdapter adapter = new SearchCategoryAdapter(categoryList);
        adapter.setOnItemClickListener((position, category) -> {
            openCategoryProducts(category);
        });

        binding.categoriesRecyclerView.setAdapter(adapter);
    }

    private void openCategoryProducts(CategoryDomain category) {
        Intent intent = new Intent(SearchActivity.this, CategoryProductsActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
