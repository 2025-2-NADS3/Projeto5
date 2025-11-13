package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.menuux.comedoriadatia.Domain.User;
import br.com.menuux.comedoriadatia.R;
import br.com.menuux.comedoriadatia.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = database.getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG, "DataSnapshot exists for user: " + userId);
                        User user = snapshot.getValue(User.class);
                        if (user != null && user.getNome() != null && user.getSobrenome() != null) {
                            String fullName = user.getNome() + " " + user.getSobrenome();
                            binding.userNameText.setText(fullName);
                            Log.d(TAG, "User name set to: " + fullName);
                        } else {
                            Log.w(TAG, "User object is null or fields are missing.");
                            // Fallback para o DisplayName se não houver dados no banco
                            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                                binding.userNameText.setText(currentUser.getDisplayName());
                            }
                        }
                    } else {
                        Log.w(TAG, "DataSnapshot does not exist for user: " + userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error: " + error.getMessage());
                }
            });
        }
    }

    public void openHome(View view) {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }

    public void openCart(View view) {
        startActivity(new Intent(ProfileActivity.this, CartActivity.class));
    }

    public void openFavorites(View view) {
        startActivity(new Intent(ProfileActivity.this, FavoritesActivity.class));
    }

    public void openWallet(View view) {
        startActivity(new Intent(ProfileActivity.this, CarteiraActivity.class));
    }

    public void openProfile(View view) {
        // Não faz nada, já está na tela de perfil
    }

    public void openAccountData(View view) {
        startActivity(new Intent(ProfileActivity.this, AccountDataActivity.class));
    }

    public void openSettings(View view) {
        startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }
}
