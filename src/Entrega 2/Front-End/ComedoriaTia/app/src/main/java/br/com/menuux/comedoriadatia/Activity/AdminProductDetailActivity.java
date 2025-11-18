package br.com.menuux.comedoriadatia.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.Domain.Product;
import br.com.menuux.comedoriadatia.R;

public class AdminProductDetailActivity extends AppCompatActivity {

    private ImageView productImage, backBtn;
    private TextView headerTitle, btnChangeImage;
    private EditText editName, editPrice, editRating, editDescription;
    private Spinner categorySpinner;
    private Button btnSave;

    private DatabaseReference productRef, categoryRef;

    private String productId;
    private Product currentProduct;
    private Uri imageUri;
    private List<CategoryDomain> categoryList = new ArrayList<>();
    private ArrayAdapter<CategoryDomain> categoryAdapter;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    Glide.with(this).load(imageUri).into(productImage);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_detail);

        initViews();
        initFirebase();

        productId = getIntent().getStringExtra("PRODUCT_ID");

        setupClickListeners();
        loadCategories();

        if (productId != null && !productId.isEmpty()) {
            headerTitle.setText("Editar Produto");
            loadProductDetails();
        } else {
            headerTitle.setText("Adicionar Produto");
        }
    }

    private void initViews() {
        productImage = findViewById(R.id.productImage);
        backBtn = findViewById(R.id.backBtn);
        headerTitle = findViewById(R.id.header_title);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        editName = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editPrice);
        editRating = findViewById(R.id.editRating);
        editDescription = findViewById(R.id.editDescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        btnSave = findViewById(R.id.btnSave);
    }

    private void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        productRef = database.getReference("Items");
        categoryRef = database.getReference("Category");
    }

    private void setupClickListeners() {
        backBtn.setOnClickListener(v -> finish());
        btnChangeImage.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    private void loadCategories() {
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CategoryDomain category = dataSnapshot.getValue(CategoryDomain.class);
                    if (category != null) {
                        category.setKey(dataSnapshot.getKey());
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                if (currentProduct != null) {
                    setSpinnerSelection();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProductDetailActivity.this, "Falha ao carregar categorias", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails() {
        productRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentProduct = snapshot.getValue(Product.class);
                if (currentProduct != null) {
                    currentProduct.setId(snapshot.getKey());
                    editName.setText(currentProduct.getTitle());
                    editPrice.setText(String.valueOf(currentProduct.getPrice()));
                    editRating.setText(String.valueOf(currentProduct.getStar()));
                    editDescription.setText(currentProduct.getDescription());

                    if (currentProduct.getImagePath() != null && !currentProduct.getImagePath().isEmpty()) {
                        String imagePath = currentProduct.getImagePath();
                        try {
                            byte[] decodedString = Base64.decode(imagePath, Base64.DEFAULT);
                            Glide.with(AdminProductDetailActivity.this).load(decodedString).into(productImage);
                        } catch (IllegalArgumentException e) {
                            Glide.with(AdminProductDetailActivity.this).load(imagePath).into(productImage);
                        }
                    }

                    setSpinnerSelection();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminProductDetailActivity.this, "Falha ao carregar detalhes do produto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection() {
        if (currentProduct == null || categoryList.isEmpty()) {
            return;
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == currentProduct.getCategoryId()) {
                categorySpinner.setSelection(i);
                break;
            }
        }
    }

    private void saveProduct() {
        String name = editName.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String ratingStr = editRating.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || ratingStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (categorySpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null && productId == null) {
            Toast.makeText(this, "Por favor, selecione uma imagem para o novo produto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] byteArray = baos.toByteArray();
                String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                saveProductToDatabase(base64Image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao processar a imagem", Toast.LENGTH_SHORT).show();
            }
        } else {
            saveProductToDatabase(currentProduct.getImagePath());
        }
    }

    private void saveProductToDatabase(String imageBase64) {
        String name = editName.getText().toString().trim();
        double price = Double.parseDouble(editPrice.getText().toString().trim());
        double rating = Double.parseDouble(editRating.getText().toString().trim());
        String description = editDescription.getText().toString().trim();
        CategoryDomain selectedCategory = (CategoryDomain) categorySpinner.getSelectedItem();
        int categoryId = selectedCategory.getId();

        Product product = new Product();
        product.setTitle(name);
        product.setPrice(price);
        product.setStar(rating);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        product.setImagePath(imageBase64);

        if (productId != null && !productId.isEmpty()) {
            product.setId(productId);
            productRef.child(productId).setValue(product).addOnSuccessListener(aVoid -> {
                Toast.makeText(AdminProductDetailActivity.this, "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Falha ao atualizar produto.", Toast.LENGTH_SHORT).show());
        } else {
            String newProductId = productRef.push().getKey();
            product.setId(newProductId);
            productRef.child(newProductId).setValue(product).addOnSuccessListener(aVoid -> {
                Toast.makeText(AdminProductDetailActivity.this, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Falha ao adicionar produto.", Toast.LENGTH_SHORT).show());
        }
    }
}
