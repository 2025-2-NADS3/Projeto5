package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.menuux.comedoriadatia.Helper.SessionManager;
import br.com.menuux.comedoriadatia.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtSenha;
    private Button btnLogin, btnGoogle;
    private TextView txtCadastrar;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        // Verifica se o usuário já está logado ANTES de mostrar qualquer tela
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Se estiver logado, não mostra a tela de login. A tela ficará em branco
            // enquanto busca a role e redireciona, o que é quase instantâneo.
            fetchUserRoleAndProceed(currentUser.getUid(), false);
        } else {
            // Se não estiver logado, aí sim mostra a tela de login normalmente.
            setContentView(R.layout.activity_login);
            inicializarComponentes();
            configurarListeners();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // A lógica de verificação foi movida para o onCreate para evitar o "flash" da tela de login.
    }

    private void inicializarComponentes() {
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtCadastrar = findViewById(R.id.txtCadastrar);
    }

    private void configurarListeners() {
        btnLogin.setOnClickListener(v -> realizarLogin());

        txtCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Login com Google em desenvolvimento", Toast.LENGTH_SHORT).show();
        });
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            fetchUserRoleAndProceed(user.getUid(), true);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "E-mail ou senha incorretos",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserRoleAndProceed(String userId, boolean showToast) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            String userRole = "cliente"; // Padrão para cliente
            if (documentSnapshot.exists()) {
                String roleFromDb = documentSnapshot.getString("role");
                if (roleFromDb != null) {
                    userRole = roleFromDb;
                }
            }
            sessionManager.setUserRole(userRole);
            Log.d("LoginActivity", "Role do usuário: " + userRole);

            if (showToast) {
                Toast.makeText(LoginActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
            }

            if (sessionManager.isAdmin()) {
                irParaTelaAdmin();
            } else {
                irParaTelaPrincipal();
            }

        }).addOnFailureListener(e -> {
            // Se falhar, assume 'cliente' e continua para a tela principal
            Log.e("LoginActivity", "Erro ao buscar role. Assumindo 'cliente'.", e);
            sessionManager.setUserRole("cliente");
            irParaTelaPrincipal();
        });
    }


    private void irParaTelaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void irParaTelaAdmin() {
        Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
