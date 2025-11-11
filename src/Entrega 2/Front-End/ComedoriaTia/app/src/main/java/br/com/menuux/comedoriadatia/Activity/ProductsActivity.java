package br.com.menuux.comedoriadatia.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.menuux.comedoriadatia.Adapter.ProductAdapter;
import br.com.menuux.comedoriadatia.Domain.Product;
import br.com.menuux.comedoriadatia.R;

public class ProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductListener {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        recyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyStateText = findViewById(R.id.emptyStateText);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList, this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addProductCard).setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, AdminProductDetailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setId(dataSnapshot.getKey());
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();

                if (productList.isEmpty()) {
                    emptyStateText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductsActivity.this, "Erro ao buscar produtos.", Toast.LENGTH_SHORT).show();
                emptyStateText.setText("Erro ao carregar produtos.");
                emptyStateText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onEditClick(Product product) {
        Intent intent = new Intent(ProductsActivity.this, AdminProductDetailActivity.class);
        intent.putExtra("PRODUCT_ID", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir o produto \"" + product.getTitle() + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> deleteProductFromDatabase(product))
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteProductFromDatabase(Product product) {
        databaseReference.child(product.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProductsActivity.this, "Produto excluído!", Toast.LENGTH_SHORT).show();
                    // O ValueEventListener irá atualizar a lista automaticamente
                })
                .addOnFailureListener(e -> Toast.makeText(ProductsActivity.this, "Erro ao excluir o produto.", Toast.LENGTH_SHORT).show());
    }

    // Métodos de navegação do menu inferior
    public void openHomeAdmin(View view) {
        startActivity(new Intent(this, HomeAdminActivity.class));
        finish();
    }

    public void openCart(View view) {
        Toast.makeText(this, "Seção de pedidos em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    public void openFavorites(View view) {
        startActivity(new Intent(this, ProductsActivity.class));
        finish();
    }

    public void openWallet(View view) {
        Toast.makeText(this, "Seção de relatórios em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    public void openProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }
}
