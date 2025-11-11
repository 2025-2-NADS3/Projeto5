package br.com.menuux.comedoriadatia.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.menuux.comedoriadatia.Adapter.CartAdapter;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.databinding.ActivityCartBinding;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference cartRef;
    private ValueEventListener cartEventListener;
    private static final String TAG = "CartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        initCart();
        setVariable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachCartDatabaseListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachCartDatabaseListener();
    }

    private void initCart() {
        adapter = new CartAdapter(new ArrayList<>(), new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateQuantityInFirebase(position, newQuantity);
            }

            @Override
            public void onItemRemoved(int position) {
                removeItemFromFirebase(position);
            }
        });

        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cartRecyclerView.setAdapter(adapter);
    }

    private void attachCartDatabaseListener() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        cartRef = database.getReference("Carts").child(userId);

        if (cartEventListener == null) {
            cartEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<ItemDomain> cartItems = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        cartItems.add(itemSnapshot.getValue(ItemDomain.class));
                    }
                    Log.d(TAG, "Cart data changed: " + cartItems.size() + " items found.");
                    adapter.updateCartItems(cartItems);
                    checkEmptyState(cartItems);
                    updateCartSummary(cartItems);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Erro ao carregar o carrinho", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onCancelled: ", error.toException());
                }
            };
            cartRef.addValueEventListener(cartEventListener);
        }
    }

    private void detachCartDatabaseListener() {
        if (cartRef != null && cartEventListener != null) {
            cartRef.removeEventListener(cartEventListener);
            cartEventListener = null;
        }
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        binding.applyCouponBtn.setOnClickListener(v -> {
            String couponCode = binding.couponEditText.getText().toString().trim();
            if (!couponCode.isEmpty()) {
                applyCoupon(couponCode);
            } else {
                Toast.makeText(this, "Digite um código de cupom", Toast.LENGTH_SHORT).show();
            }
        });

        binding.paymentBtn.setOnClickListener(v -> {
            if (adapter.getItemCount() > 0) {
                Toast.makeText(this, "Redirecionando para pagamento...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Carrinho vazio", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmptyState(List<ItemDomain> cartItems) {
        if (cartItems.isEmpty()) {
            binding.emptyCartLayout.setVisibility(View.VISIBLE);
            binding.cartContentLayout.setVisibility(View.GONE);
            binding.fixedBottomLayout.setVisibility(View.GONE);
        } else {
            binding.emptyCartLayout.setVisibility(View.GONE);
            binding.cartContentLayout.setVisibility(View.VISIBLE);
            binding.fixedBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateCartSummary(List<ItemDomain> cartItems) {
        double subtotal = 0;
        for (ItemDomain item : cartItems) {
            subtotal += item.getPrice() * item.getWeight();
        }

        double discount = 0;
        double total = subtotal - discount;

        binding.subtotalTxt.setText("R$" + String.format("%.2f", subtotal));
        binding.discountTxt.setText("R$" + String.format("%.2f", discount));
        binding.totalTxt.setText("R$" + String.format("%.2f", total));
    }

    private String sanitizeKey(String key) {
        return key.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "").replace("/", "");
    }

    private void updateQuantityInFirebase(int position, int newQuantity) {
        String userId = mAuth.getCurrentUser().getUid();
        ItemDomain item = adapter.getCartItems().get(position);
        String itemKey = sanitizeKey(item.getTitle());
        DatabaseReference itemRef = database.getReference("Carts").child(userId).child(itemKey);
        item.setWeight(newQuantity);
        itemRef.setValue(item);
    }

    private void removeItemFromFirebase(int position) {
        String userId = mAuth.getCurrentUser().getUid();
        ItemDomain item = adapter.getCartItems().get(position);
        String itemKey = sanitizeKey(item.getTitle());
        DatabaseReference itemRef = database.getReference("Carts").child(userId).child(itemKey);
        itemRef.removeValue();
    }

    private void applyCoupon(String couponCode) {
        Toast.makeText(this, "Cupom aplicado: " + couponCode, Toast.LENGTH_SHORT).show();
    }
}
