package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.com.menuux.comedoriadatia.Domain.BannerDomain;
import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.R;

public class AdminEditItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView itemImage, backBtn;
    private TextView btnChangeImage, headerTitle;
    private EditText editName;
    private LinearLayout nameLayout;
    private View btnSave;

    private String itemType;
    private String itemId; // This will be the Firebase Key (e.g., "-Nxyz...")
    private String imageUrl;
    private String itemName;
    private Uri imageUri;
    private boolean isNewItem;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_item);

        database = FirebaseDatabase.getInstance();

        itemImage = findViewById(R.id.itemImage);
        backBtn = findViewById(R.id.backBtn);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        headerTitle = findViewById(R.id.header_title);
        btnSave = findViewById(R.id.btnSave);
        editName = findViewById(R.id.editName);
        nameLayout = findViewById(R.id.nameLayout);

        Intent intent = getIntent();
        itemType = intent.getStringExtra("itemType");
        itemId = intent.getStringExtra("itemId");
        imageUrl = intent.getStringExtra("imageUrl");
        itemName = intent.getStringExtra("itemName");

        isNewItem = (itemId == null);

        setupUI();

        btnChangeImage.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveChanges());
        backBtn.setOnClickListener(v -> finish());
    }

    private void setupUI() {
        String action = isNewItem ? "Novo" : "Editar";
        ((TextView) btnSave).setText(isNewItem ? "Adicionar Item" : "Salvar Alterações");

        if (itemType != null) {
            if (itemType.equals("banner")) {
                headerTitle.setText(action + " Banner");
                nameLayout.setVisibility(View.GONE);
            } else { // category
                headerTitle.setText(action + " Categoria");
                nameLayout.setVisibility(View.VISIBLE);
                if (!isNewItem) {
                    editName.setText(itemName);
                }
            }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Glide.with(this).load(imageUrl).into(itemImage);
            } else {
                try {
                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(this).asBitmap().load(decodedString).into(itemImage);
                } catch (Exception e) {
                    itemImage.setImageResource(R.drawable.logo);
                }
            }
        } else {
            itemImage.setImageResource(R.drawable.logo);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            itemImage.setImageURI(imageUri);
        }
    }

    private void saveChanges() {
        String name = editName.getText().toString().trim();

        if (itemType.equals("category") && name.isEmpty()) {
            editName.setError("O nome da categoria é obrigatório");
            editName.requestFocus();
            return;
        }

        if (isNewItem && imageUri == null) {
            Toast.makeText(this, "Por favor, selecione uma imagem", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                handleUpload(name, base64Image);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Falha ao processar a imagem", Toast.LENGTH_SHORT).show();
            }
        } else if (!isNewItem) {
            // If it's not a new item and no new image was selected, save with the existing image URL
            handleUpload(name, imageUrl);
        }
    }

    private void handleUpload(String name, String imageString) {
        if (itemType.equals("category") && isNewItem) {
            // For new categories, we must first find the next available numeric ID
            findNextCategoryId(name, imageString);
        } else {
            // For banners or editing existing categories, we can save directly
            uploadToFirebase(name, imageString, -1); // -1 indicates we don't need a new numeric ID
        }
    }

    private void findNextCategoryId(String name, String imageString) {
        DatabaseReference categoryRef = database.getReference("Category");
        Query lastCategoryQuery = categoryRef.orderByChild("id").limitToLast(1);

        lastCategoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long lastId = -1;
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        CategoryDomain lastCategory = childSnapshot.getValue(CategoryDomain.class);
                        if (lastCategory != null) {
                            lastId = lastCategory.getId();
                        }
                    }
                }
                long newId = lastId + 1;
                uploadToFirebase(name, imageString, newId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminEditItemActivity.this, "Falha ao obter novo ID de categoria.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadToFirebase(String name, String imageString, long newNumericId) {
        DatabaseReference databaseReference;
        String keyToSave = itemId;

        if (itemType.equals("banner")) {
            databaseReference = database.getReference("Banners");
            if (isNewItem) {
                keyToSave = databaseReference.push().getKey();
            }
            BannerDomain banner = new BannerDomain(imageString);
            databaseReference.child(keyToSave).setValue(banner)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminEditItemActivity.this, "Item salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminEditItemActivity.this, "Falha ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else { // category
            databaseReference = database.getReference("Category");
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("name", name);
            categoryData.put("imagePath", imageString);

            if (isNewItem) {
                keyToSave = databaseReference.push().getKey();
                categoryData.put("id", newNumericId); // Add the new numeric ID
            }

            if (keyToSave == null) {
                Toast.makeText(this, "Não foi possível salvar o item.", Toast.LENGTH_SHORT).show();
                return;
            }

            databaseReference.child(keyToSave).updateChildren(categoryData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminEditItemActivity.this, "Item salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminEditItemActivity.this, "Falha ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
