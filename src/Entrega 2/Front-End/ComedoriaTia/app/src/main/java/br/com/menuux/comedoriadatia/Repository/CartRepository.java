package br.com.menuux.comedoriadatia.Repository;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.menuux.comedoriadatia.Domain.ItemDomain;

public class CartRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase database;

    public CartRepository(FirebaseAuth mAuth, FirebaseDatabase database) {
        this.mAuth = mAuth;
        this.database = database;
    }

    private String sanitizeKey(String key) {
        return key.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "").replace("/", "");
    }

    public void addItemToCart(ItemDomain item, int quantity, OnCompleteListener<Void> onCompleteListener) {
        if (mAuth.getCurrentUser() == null) {
            // Trate o caso em que o usuário não está logado.
            // Você pode, por exemplo, lançar uma exceção ou chamar um callback de erro.
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = database.getReference("Carts").child(userId);

        item.setWeight(quantity);
        String itemKey = sanitizeKey(item.getTitle());

        cartRef.child(itemKey).setValue(item).addOnCompleteListener(onCompleteListener);
    }
}
