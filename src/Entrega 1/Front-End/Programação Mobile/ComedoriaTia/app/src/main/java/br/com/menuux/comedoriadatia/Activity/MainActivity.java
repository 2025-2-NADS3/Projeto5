package br.com.menuux.comedoriadatia.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

// Importações necessárias do Firebase
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.menuux.comedoriadatia.Domain.LocationDomain;
import br.com.menuux.comedoriadatia.R;
import br.com.menuux.comedoriadatia.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    private FirebaseDatabase database; // Variável para a instância do Firebase Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // CORREÇÃO: Atribuição correta do View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Linha corrigida
        setContentView(binding.getRoot());

        // Inicializa a instância do Firebase Database
        database = FirebaseDatabase.getInstance();

        initLocation();
    }

    private void initLocation() {
        // Sintaxe correta para obter a referência do Database
        DatabaseReference myref=database.getReference("Location");
        ArrayList<LocationDomain> list=new ArrayList<>();
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(LocationDomain.class));
                    }
                    ArrayAdapter<LocationDomain> adapter=new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.locationSp.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // É uma boa prática registrar o erro para fins de depuração
                // ex: Log.w("MainActivity", "Falha ao ler dados.", error.toException());
            }
        });
    }
}
