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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.com.menuux.comedoriadatia.R;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText edtNome, edtSobrenome, edtCelular, edtCPF, edtEmail, edtSenha;
    private Button btnCadastrar;
    private TextView txtVoltarLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inicializarComponentes();
        configurarListeners();
    }

    private void inicializarComponentes() {
        edtNome = findViewById(R.id.edtNome);
        edtSobrenome = findViewById(R.id.edtSobrenome);
        edtCelular = findViewById(R.id.edtCelular);
        edtCPF = findViewById(R.id.edtCPF);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        txtVoltarLogin = findViewById(R.id.txtVoltarLogin);
    }

    private void configurarListeners() {
        btnCadastrar.setOnClickListener(v -> realizarCadastro());
        txtVoltarLogin.setOnClickListener(v -> voltarParaLogin());
    }

    private void realizarCadastro() {
        String nome = edtNome.getText().toString().trim();
        String sobrenome = edtSobrenome.getText().toString().trim();
        String celular = edtCelular.getText().toString().trim();
        String cpf = edtCPF.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (nome.isEmpty() || sobrenome.isEmpty() || celular.isEmpty() ||
                cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValido(email)) {
            Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        // Após criar no Auth, salva os dados no Firestore
                        salvarDadosUsuario(task.getResult().getUser(), nome, sobrenome, celular, cpf, email);
                    } else {
                        Toast.makeText(CadastroActivity.this, "Falha no cadastro: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void salvarDadosUsuario(FirebaseUser user, String nome, String sobrenome, String celular, String cpf, String email) {
        if (user == null) return;

        String userId = user.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("nome", nome + " " + sobrenome);
        userData.put("email", email);
        userData.put("celular", celular);
        userData.put("cpf", cpf);
        userData.put("role", "cliente"); // <<-- IMPORTANTE: Define a role padrão
        userData.put("dataCriacao", FieldValue.serverTimestamp());
        userData.put("ultimoAcesso", FieldValue.serverTimestamp());

        db.collection("usuarios").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CadastroActivity", "Documento do usuário criado no Firestore.");
                    irParaTelaPrincipal(); // Sucesso, vai para a tela principal
                })
                .addOnFailureListener(e -> {
                    Log.w("CadastroActivity", "Erro ao escrever documento do usuário", e);
                    // Mesmo se falhar, o usuário está no Auth, então pode logar
                    Toast.makeText(CadastroActivity.this, "Erro ao salvar dados, mas cadastro OK.", Toast.LENGTH_SHORT).show();
                    irParaTelaPrincipal();
                });
    }

    private boolean isEmailValido(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void voltarParaLogin() {
        finish(); // Apenas fecha a tela atual, voltando para a de login
    }

    private void irParaTelaPrincipal() {
        Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
