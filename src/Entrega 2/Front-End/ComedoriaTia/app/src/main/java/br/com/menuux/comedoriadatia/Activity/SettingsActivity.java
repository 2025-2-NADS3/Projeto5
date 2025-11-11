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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.menuux.comedoriadatia.R;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.logoutBtn).setOnClickListener(v -> showLogoutConfirmationDialog());
        findViewById(R.id.deleteAccountBtn).setOnClickListener(v -> showDeleteAccountConfirmationDialog());
    }

    public void openSecurity(View view) {
        startActivity(new Intent(SettingsActivity.this, SecurityActivity.class));
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_exit, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.confirmBtn).setOnClickListener(v -> {
            logout();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancelBtn).setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteAccountConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.confirmBtn).setOnClickListener(v -> {
            dialog.dismiss();
            showPasswordConfirmationDialog();
        });

        dialogView.findViewById(R.id.cancelBtn).setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
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

        confirmBtn.setText("Excluir");
        confirmBtn.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("A senha é obrigatória para excluir a conta.");
                return;
            }
            reauthenticateAndDeleteAccount(password);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();
    }

    private void reauthenticateAndDeleteAccount(String password) {
        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    deleteAccountData();
                } else {
                    Toast.makeText(SettingsActivity.this, "Autenticação falhou. Verifique sua senha.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteAccountData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Excluir dados do Realtime Database
            mDatabase.child("Users").child(userId).removeValue();
            mDatabase.child("Carts").child(userId).removeValue();
            mDatabase.child("Favorites").child(userId).removeValue();

            // Excluir usuário do Firebase Authentication
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Conta excluída com sucesso.", Toast.LENGTH_SHORT).show();
                    logout();
                } else {
                    Toast.makeText(SettingsActivity.this, "Falha ao excluir a conta.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Métodos de navegação
    public void openHome(View view) {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        finish();
    }

    public void openCart(View view) {
        startActivity(new Intent(SettingsActivity.this, CartActivity.class));
        finish();
    }

    public void openFavorites(View view) {
        startActivity(new Intent(SettingsActivity.this, FavoritesActivity.class));
        finish();
    }

    public void openWallet(View view) {
        startActivity(new Intent(SettingsActivity.this, CarteiraActivity.class));
        finish();
    }

    public void openProfile(View view) {
        startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
        finish();
    }
}
