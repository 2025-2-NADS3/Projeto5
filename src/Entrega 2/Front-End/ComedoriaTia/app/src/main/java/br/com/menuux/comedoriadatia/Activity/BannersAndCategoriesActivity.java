package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Adapter.BannerManagementAdapter;
import br.com.menuux.comedoriadatia.Adapter.CategoryManagementAdapter;
import br.com.menuux.comedoriadatia.Domain.BannerDomain;
import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.R;
import br.com.menuux.comedoriadatia.databinding.ActivityBannersAndCategoriesBinding;

public class BannersAndCategoriesActivity extends AppCompatActivity {

    private ActivityBannersAndCategoriesBinding binding;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBannersAndCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        initBanners();
        initCategories();

        binding.addNewButton.setOnClickListener(v -> showAddItemDialog());
        binding.imageView2.setOnClickListener(v -> finish());
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_item, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnNewBanner = dialogView.findViewById(R.id.btnNewBanner);
        Button btnNewCategory = dialogView.findViewById(R.id.btnNewCategory);

        btnNewBanner.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEditItemActivity.class);
            intent.putExtra("itemType", "banner");
            startActivity(intent);
            dialog.dismiss();
        });

        btnNewCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminEditItemActivity.class);
            intent.putExtra("itemType", "category");
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void initBanners() {
        DatabaseReference bannersRef = database.getReference("Banners");
        binding.bannersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bannersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BannerDomain> banners = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BannerDomain banner = dataSnapshot.getValue(BannerDomain.class);
                    if (banner != null) {
                        banner.setId(dataSnapshot.getKey());
                        banners.add(banner);
                    }
                }
                binding.bannersRecyclerView.setAdapter(new BannerManagementAdapter(banners));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void initCategories() {
        DatabaseReference categoriesRef = database.getReference("Category");
        binding.categoriesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<CategoryDomain> categories = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryDomain category = dataSnapshot.getValue(CategoryDomain.class);
                    if (category != null) {
                        // Guarda a chave do Firebase (ex: "-Nxyz")
                        category.setKey(dataSnapshot.getKey());
                        categories.add(category);
                    }
                }
                binding.categoriesRecyclerView.setAdapter(new CategoryManagementAdapter(categories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
