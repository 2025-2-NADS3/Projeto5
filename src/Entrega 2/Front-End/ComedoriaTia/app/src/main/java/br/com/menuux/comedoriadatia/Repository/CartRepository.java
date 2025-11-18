package br.com.menuux.comedoriadatia.Repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.menuux.comedoriadatia.Domain.Product;

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

    public void addItemToCart(Product item, int quantity, OnCompleteListener<Void> onCompleteListener) {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = database.getReference("Carts").child(userId);

        // O método setWeight não existe em Product, então essa linha precisa ser removida ou adaptada.
        // Por enquanto, vou remover.
        // item.setWeight(quantity);
        String itemKey = sanitizeKey(item.getTitle());

        cartRef.child(itemKey).setValue(item).addOnCompleteListener(onCompleteListener);
    }
}
