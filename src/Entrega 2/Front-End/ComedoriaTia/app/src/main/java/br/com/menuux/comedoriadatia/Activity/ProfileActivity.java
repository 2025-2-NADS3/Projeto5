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
import br.com.menuux.comedoriadatia.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userRef = database.getReference("usuarios");

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            String nome = user.getNome() != null ? user.getNome() : "";
                            String sobrenome = user.getSobrenome() != null ? user.getSobrenome() : "";
                            String fullName = (nome + " " + sobrenome).trim();

                            if (!fullName.isEmpty()) {
                                binding.userNameText.setText(fullName);
                                Log.d(TAG, "User name set to: " + fullName);
                            } else {
                                binding.userNameText.setText("Usuário");
                                Log.d(TAG, "User full name is empty. Setting to default.");
                            }
                        } else {
                             Log.w(TAG, "User object is null.");
                             binding.userNameText.setText("Usuário"); // Default text
                        }
                    } else {
                        Log.w(TAG, "No data found for user: " + userId);
                        binding.userNameText.setText("Usuário"); // Default text
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error fetching user data from Realtime Database", databaseError.toException());
                    binding.userNameText.setText("Usuário"); // Default text on error
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
