package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Adapter.BestDealAdapter;
import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.databinding.ActivityCategoryProductsBinding;

public class CategoryProductsActivity extends BaseActivity {
    private ActivityCategoryProductsBinding binding;
    private CategoryDomain category;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        getCategoryFromIntent();
        setVariable();
        loadCategoryProducts();
    }

    private void getCategoryFromIntent() {
        category = (CategoryDomain) getIntent().getSerializableExtra("category");
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.cartBtn.setOnClickListener(v -> {
            startActivity(new Intent(CategoryProductsActivity.this, CartActivity.class));
        });

        if (category != null) {
            binding.categoryTitle.setText(category.getName());
        }
    }

    private void loadCategoryProducts() {
        if (category == null) return;

        DatabaseReference itemsRef = database.getReference("Items");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<ItemDomain> categoryItems = new ArrayList<>();

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ItemDomain item = issue.getValue(ItemDomain.class);
                        if (item != null && item.getCategoryId() == category.getId()) {
                            categoryItems.add(item);
                        }
                    }

                    if (!categoryItems.isEmpty()) {
                        binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(CategoryProductsActivity.this, 2));
                        binding.productsRecyclerView.setAdapter(new BestDealAdapter(categoryItems, mAuth, database));
                    } else {
                        binding.emptyState.setVisibility(View.VISIBLE);
                        binding.productsRecyclerView.setVisibility(View.GONE);
                    }
                } else {
                    binding.emptyState.setVisibility(View.VISIBLE);
                    binding.productsRecyclerView.setVisibility(View.GONE);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
                binding.productsRecyclerView.setVisibility(View.GONE);
            }
        });
    }
}
