package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.menuux.comedoriadatia.R;

public class SecurityActivity extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button savePasswordBtn;
    private TextView forgotPasswordTextView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        savePasswordBtn = findViewById(R.id.savePasswordBtn);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        savePasswordBtn.setOnClickListener(v -> changePassword());
        forgotPasswordTextView.setOnClickListener(v -> forgotPassword());
    }

    private void forgotPassword() {
        if (currentUser != null && currentUser.getEmail() != null) {
            mAuth.sendPasswordResetEmail(currentUser.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SecurityActivity.this, "E-mail de redefinição de senha enviado.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SecurityActivity.this, "Falha ao enviar e-mail de redefinição de senha.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordEditText.setError("A senha atual é obrigatória.");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("A nova senha é obrigatória.");
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordEditText.setError("A nova senha deve ter pelo menos 6 caracteres.");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("A confirmação da nova senha é obrigatória.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("As senhas não correspondem.");
            return;
        }

        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(SecurityActivity.this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(SecurityActivity.this, "Falha ao alterar a senha.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    currentPasswordEditText.setError("Senha atual incorreta.");
                }
            });
        }
    }

    // Métodos de navegação
    public void openHome(View view) {
        startActivity(new Intent(SecurityActivity.this, MainActivity.class));
        finish();
    }

    public void openCart(View view) {
        startActivity(new Intent(SecurityActivity.this, CartActivity.class));
        finish();
    }

    public void openFavorites(View view) {
        startActivity(new Intent(SecurityActivity.this, FavoritesActivity.class));
        finish();
    }

    public void openWallet(View view) {
        startActivity(new Intent(SecurityActivity.this, CarteiraActivity.class));
        finish();
    }

    public void openProfile(View view) {
        startActivity(new Intent(SecurityActivity.this, ProfileActivity.class));
        finish();
    }

    public void openSettings(View view) {
        startActivity(new Intent(SecurityActivity.this, SettingsActivity.class));
        finish();
    }
}
