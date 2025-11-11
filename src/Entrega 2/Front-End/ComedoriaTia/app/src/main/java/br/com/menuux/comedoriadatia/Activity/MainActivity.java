package br.com.menuux.comedoriadatia.Activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.menuux.comedoriadatia.Adapter.BannerAdapter;
import br.com.menuux.comedoriadatia.Adapter.BestDealAdapter;
import br.com.menuux.comedoriadatia.Adapter.CategoryAdapter;
import br.com.menuux.comedoriadatia.Domain.BannerDomain;
import br.com.menuux.comedoriadatia.Domain.CategoryDomain;
import br.com.menuux.comedoriadatia.Domain.ItemDomain;
import br.com.menuux.comedoriadatia.R;
import br.com.menuux.comedoriadatia.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ArrayList<ItemDomain> allProducts = new ArrayList<>();
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private ValueAnimator progressAnimator;
    private final List<View> indicatorItems = new ArrayList<>();
    private static final long SLIDER_DELAY = 3000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        database = FirebaseDatabase.getInstance();

        initBanner();
        initCategoryList();
        initBestDealList();

        binding.editTextText.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));

        binding.seeAllTxt.setOnClickListener(v -> {
            binding.bestDealView.setAdapter(new BestDealAdapter(allProducts, mAuth, database));
            binding.seeAllTxt.setVisibility(GONE);
            binding.bestDealView.setVisibility(VISIBLE);
            binding.noItemsTxt.setVisibility(GONE);
        });
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<BannerDomain> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(BannerDomain.class));
                    }
                    if (!list.isEmpty()) {
                        banners(list);
                    } else {
                        binding.indicatorContainer.setVisibility(View.GONE);
                    }
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarBanner.setVisibility(View.GONE);
            }
        });
    }

    private void banners(ArrayList<BannerDomain> list) {
        binding.bannerView.setAdapter(new BannerAdapter(list));
        binding.bannerView.setClipToPadding(false);
        binding.bannerView.setClipChildren(false);
        binding.bannerView.setOffscreenPageLimit(3);
        binding.bannerView.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.bannerView.setPageTransformer(compositePageTransformer);

        setupIndicators(list.size());

        sliderRunnable = () -> {
            if (binding.bannerView.getAdapter() == null || binding.bannerView.getAdapter().getItemCount() == 0) return;
            int nextItem = binding.bannerView.getCurrentItem() + 1;
            if (nextItem >= binding.bannerView.getAdapter().getItemCount()) {
                nextItem = 0; // Loop
            }
            binding.bannerView.setCurrentItem(nextItem, true);
        };

        binding.bannerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
                // Reset and restart the timer whenever a page is selected (manually or automatically)
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, SLIDER_DELAY);
            }
        });

        // This is the key fix: Post the initial setup to run after the layout is drawn.
        binding.indicatorContainer.post(() -> updateIndicators(0));
    }

    private void setupIndicators(int count) {
        binding.indicatorContainer.removeAllViews();
        indicatorItems.clear();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < count; i++) {
            View indicator = inflater.inflate(R.layout.indicator_item, binding.indicatorContainer, false);
            binding.indicatorContainer.addView(indicator);
            indicatorItems.add(indicator);
        }
    }

    private void updateIndicators(int position) {
        if (progressAnimator != null && progressAnimator.isRunning()) {
            progressAnimator.cancel();
        }

        for (int i = 0; i < indicatorItems.size(); i++) {
            View indicator = indicatorItems.get(i);
            View progressView = indicator.findViewById(R.id.indicatorProgress);
            ViewGroup.LayoutParams params = progressView.getLayoutParams();
            int fullWidth = indicator.getWidth();

            if (fullWidth == 0) {
                // If the views haven't been measured, post this whole method to run again after the layout pass.
                indicator.post(() -> updateIndicators(position));
                return; // Stop execution for now, it will run again shortly.
            }

            if (i < position) {
                params.width = fullWidth; // Instantly fill past indicators
            } else if (i > position) {
                params.width = 0; // Instantly empty future indicators
            } else { // Current indicator
                startIndicatorAnimation(indicator, progressView);
            }
            progressView.setLayoutParams(params);
        }
    }

    private void startIndicatorAnimation(View indicator, View progressView) {
        int startWidth = 0;
        int endWidth = indicator.getWidth();

        // If we are on the first page and coming back to the app, the progress might already be partway.
        // This logic is more for onResume, but it's safe to keep it here.
        if (progressView.getWidth() > 0 && progressView.getWidth() < endWidth) {
            startWidth = progressView.getWidth();
        }

        progressAnimator = ValueAnimator.ofInt(startWidth, endWidth);
        progressAnimator.setDuration(SLIDER_DELAY - 200); // Animate slightly faster than the delay
        progressAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = progressView.getLayoutParams();
            params.width = (int) animation.getAnimatedValue();
            progressView.setLayoutParams(params);
        });
        progressAnimator.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
        if (progressAnimator != null && progressAnimator.isRunning()) {
            // We don't cancel, we just pause it so it can resume from the same spot
            progressAnimator.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the timer and animation when the app comes back
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, SLIDER_DELAY);
        }
        if (progressAnimator != null && progressAnimator.isPaused()) {
            progressAnimator.resume();
        }
    }

    private void initBestDealList() {
        DatabaseReference myRef = database.getReference("Items");
        binding.progressBarBestDeal.setVisibility(VISIBLE);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    allProducts.clear();
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        allProducts.add(issue.getValue(ItemDomain.class));
                    }
                    if (!allProducts.isEmpty()) {
                        binding.bestDealView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        binding.bestDealView.setAdapter(new BestDealAdapter(allProducts, mAuth, database));
                        binding.bestDealView.setVisibility(VISIBLE);
                        binding.noItemsTxt.setVisibility(GONE);
                    } else {
                        binding.bestDealView.setVisibility(GONE);
                        binding.noItemsTxt.setVisibility(VISIBLE);
                    }
                    binding.progressBarBestDeal.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initCategoryList() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(VISIBLE);
        ArrayList<CategoryDomain> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(CategoryDomain.class));
                    }
                    if (!list.isEmpty()) {
                        binding.catView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        CategoryAdapter adapter = new CategoryAdapter(list);
                        binding.catView.setAdapter(adapter);
                        adapter.setOnItemClickListener((position, category) -> {
                            ArrayList<ItemDomain> filteredList = new ArrayList<>();
                            for (ItemDomain item : allProducts) {
                                if (item.getCategoryId() == category.getId()) {
                                    filteredList.add(item);
                                }
                            }
                            if (filteredList.isEmpty()) {
                                binding.bestDealView.setVisibility(GONE);
                                binding.noItemsTxt.setVisibility(VISIBLE);
                            } else {
                                binding.bestDealView.setAdapter(new BestDealAdapter(filteredList, mAuth, database));
                                binding.bestDealView.setVisibility(VISIBLE);
                                binding.noItemsTxt.setVisibility(GONE);
                            }
                            binding.seeAllTxt.setVisibility(VISIBLE);
                        });
                    }
                    binding.progressBarCategory.setVisibility(GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Métodos de navegação
    public void openHome(View view) {}
    public void openCart(View view) { startActivity(new Intent(MainActivity.this, CartActivity.class)); }
    public void openFavorites(View view) { startActivity(new Intent(MainActivity.this, FavoritesActivity.class)); }
    public void openWallet(View view) { startActivity(new Intent(MainActivity.this, CarteiraActivity.class)); }
    public void openProfile(View view) { startActivity(new Intent(MainActivity.this, ProfileActivity.class)); }
}
