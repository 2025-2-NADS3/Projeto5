package br.com.menuux.comedoriadatia.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.com.menuux.comedoriadatia.Adapter.UserAdapter;
import br.com.menuux.comedoriadatia.Domain.User;
import br.com.menuux.comedoriadatia.R;

public class AcessoActivity extends AppCompatActivity implements UserAdapter.OnPermissionChangeListener {

    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private FirebaseFirestore db;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acesso);

        db = FirebaseFirestore.getInstance();

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UserAdapter(userList, this);
        usersRecyclerView.setAdapter(userAdapter);

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadUsers();
    }

    private void loadUsers() {
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setUid(document.getId());
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AcessoActivity.this, "Erro ao carregar usuários.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPermissionChange(User user, boolean isChecked) {
        if (isChecked) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmar Permissão")
                    .setMessage("Tem certeza de que deseja conceder permissão de administrador a " + user.getNome() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        showAdminPasswordDialog(user);
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        userAdapter.notifyDataSetChanged(); // Reverte o switch
                    })
                    .setOnCancelListener(dialog -> {
                        userAdapter.notifyDataSetChanged(); // Reverte o switch em caso de cancelamento
                    })
                    .show();
        } else {
            // Lógica para remover permissão (se necessário)
            updateUserRole(user, "user");
        }
    }

    private void showAdminPasswordDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_admin_password, null);
        builder.setView(dialogView);

        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        builder.setTitle("Senha do Administrador")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String password = passwordEditText.getText().toString();
                    verifyAdminPasswordAndGrantPermission(user, password);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    userAdapter.notifyDataSetChanged(); // Reverte o switch
                })
                .setOnCancelListener(dialog -> {
                    userAdapter.notifyDataSetChanged(); // Reverte o switch em caso de cancelamento
                })
                .show();
    }

    private void verifyAdminPasswordAndGrantPermission(User userToGrant, String password) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateUserRole(userToGrant, "admin");
                        } else {
                            Toast.makeText(AcessoActivity.this, "Senha incorreta.", Toast.LENGTH_SHORT).show();
                            userAdapter.notifyDataSetChanged(); // Reverte o switch
                        }
                    });
        }
    }

    private void updateUserRole(User user, String role) {
        db.collection("Users").document(user.getUid())
                .update("role", role)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AcessoActivity.this, "Permissão de " + user.getNome() + " atualizada para " + role, Toast.LENGTH_SHORT).show();
                    loadUsers(); // Recarrega para refletir a mudança
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AcessoActivity.this, "Erro ao atualizar permissão.", Toast.LENGTH_SHORT).show();
                    userAdapter.notifyDataSetChanged(); // Reverte o switch
                });
    }
}
