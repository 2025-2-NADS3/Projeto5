package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.com.menuux.comedoriadatia.R;

public class CarteiraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carteira);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openHome(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void openExplorer(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void openCart(View view) {
        startActivity(new Intent(this, CartActivity.class));
    }

    public void openFavorites(View view) {
        startActivity(new Intent(this, FavoritesActivity.class));
    }

    public void openWallet(View view) {
        // Não faz nada, já está na tela da carteira
    }

    public void openProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}