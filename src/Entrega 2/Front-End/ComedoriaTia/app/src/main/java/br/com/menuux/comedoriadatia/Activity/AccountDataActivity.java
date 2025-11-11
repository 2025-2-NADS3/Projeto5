package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.menuux.comedoriadatia.Domain.User;
import br.com.menuux.comedoriadatia.R;

public class AccountDataActivity extends AppCompatActivity {

    private EditText nameEditText, lastNameEditText, emailEditText, phoneEditText, cpfEditText;
    private Button saveBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdata);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        nameEditText = findViewById(R.id.nameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        cpfEditText = findViewById(R.id.cpfEditText);
        saveBtn = findViewById(R.id.saveBtn);

        loadUserData();

        saveBtn.setOnClickListener(v -> showPasswordConfirmationDialog());
    }

    private void loadUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        nameEditText.setText(user.getNome());
                        lastNameEditText.setText(user.getSobrenome());
                        emailEditText.setText(user.getEmail());
                        phoneEditText.setText(user.getCelular());
                        cpfEditText.setText(user.getCpf());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AccountDataActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showPasswordConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password_confirmation, null);
        builder.setView(dialogView);

        final EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        Button confirmBtn = dialogView.findViewById(R.id.confirmBtn);
        Button cancelBtn = dialogView.findViewById(R.id.cancelBtn);

        final AlertDialog dialog = builder.create();

        confirmBtn.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required.");
                return;
            }
            reauthenticateAndSaveChanges(password);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void reauthenticateAndSaveChanges(String password) {
        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        updateUserData();
                    } else {
                        Toast.makeText(AccountDataActivity.this, "Authentication failed. Please check your password.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateUserData() {
        String name = nameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String cpf = cpfEditText.getText().toString().trim();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("nome", name);
            userUpdates.put("sobrenome", lastName);
            userUpdates.put("email", email);
            userUpdates.put("celular", phone);
            userUpdates.put("cpf", cpf);

            mDatabase.child("Users").child(userId).updateChildren(userUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Also update email in Firebase Auth if it has changed
                            if (!email.equals(currentUser.getEmail())) {
                                currentUser.updateEmail(email);
                            }
                            Toast.makeText(AccountDataActivity.this, "User data updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AccountDataActivity.this, "Failed to update user data.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Navigation methods
    public void openHome(View view) {
        startActivity(new Intent(AccountDataActivity.this, MainActivity.class));
        finish();
    }

    public void openCart(View view) {
        startActivity(new Intent(AccountDataActivity.this, CartActivity.class));
        finish();
    }

    public void openFavorites(View view) {
        startActivity(new Intent(AccountDataActivity.this, FavoritesActivity.class));
        finish();
    }

    public void openWallet(View view) {
        startActivity(new Intent(AccountDataActivity.this, CarteiraActivity.class));
        finish();
    }

    public void openProfile(View view) {
        startActivity(new Intent(AccountDataActivity.this, ProfileActivity.class));
        finish();
    }

    public void openSettings(View view) {
        startActivity(new Intent(AccountDataActivity.this, SettingsActivity.class));
        finish();
    }
}
