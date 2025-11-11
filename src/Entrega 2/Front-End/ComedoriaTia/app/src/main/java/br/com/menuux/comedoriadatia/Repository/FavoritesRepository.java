package br.com.menuux.comedoriadatia.Repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;

public class FavoritesRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseDatabase database;

    public FavoritesRepository(FirebaseAuth mAuth, FirebaseDatabase database) {
        this.mAuth = mAuth;
        this.database = database;
    }

    private String sanitizeKey(String key) {
        return key.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "").replace("/", "");
    }

    public void addToFavorites(ItemDomain item, OnCompleteListener<Void> onCompleteListener) {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoritesRef = database.getReference("Favorites").child(userId);
        String itemKey = sanitizeKey(item.getTitle());

        favoritesRef.child(itemKey).setValue(item).addOnCompleteListener(onCompleteListener);
    }

    public void removeFromFavorites(ItemDomain item, OnCompleteListener<Void> onCompleteListener) {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoritesRef = database.getReference("Favorites").child(userId);
        String itemKey = sanitizeKey(item.getTitle());

        favoritesRef.child(itemKey).removeValue().addOnCompleteListener(onCompleteListener);
    }
}
