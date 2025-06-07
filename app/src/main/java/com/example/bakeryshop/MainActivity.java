package com.example.bakeryshop;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.bakeryshop.Fragments.CartFragment;
import com.example.bakeryshop.Fragments.FavoriteListFragment;
import com.example.bakeryshop.Fragments.FeedbackListFragment;
import com.example.bakeryshop.Fragments.ListProductFragment;
import com.example.bakeryshop.Fragments.NotificationFragment;
import com.example.bakeryshop.Fragments.ProfileFragment;
import com.example.bakeryshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ListProductFragment listProductFragment;
    private CartFragment cartFragment;
    private FeedbackListFragment feedbackListFragment;
    private FavoriteListFragment favoriteListFragment;
    private ProfileFragment profileFragment;
    private NotificationFragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        listProductFragment = new ListProductFragment();
        replaceFragment(listProductFragment);

        // Drawer setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_profile) {
                if (profileFragment == null) {
                    profileFragment = new ProfileFragment();
                }
                selectedFragment = profileFragment;
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            } else if (item.getItemId() == R.id.nav_feedback) {
                if (feedbackListFragment == null) {
                    feedbackListFragment = new FeedbackListFragment();
                }
                selectedFragment = feedbackListFragment;
                Toast.makeText(this, "Feed clicked", Toast.LENGTH_SHORT).show();
            }
            replaceFragment(selectedFragment);
            binding.drawerLayout.closeDrawers();
            return true;
        });

        // Bottom Navigation View setup
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_products) {
                selectedFragment = listProductFragment;
                Toast.makeText(this, "Products clicked", Toast.LENGTH_SHORT).show();
            }
            else if (item.getItemId() == R.id.nav_notifications) {
                if (notificationFragment == null) {
                    notificationFragment = new NotificationFragment();
                }
                selectedFragment = notificationFragment;
                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
            }
            else if (item.getItemId() == R.id.nav_favorites) {
                if (favoriteListFragment == null) {
                    favoriteListFragment = new FavoriteListFragment();
                }
                selectedFragment = favoriteListFragment;
                Toast.makeText(this, "Favorites clicked", Toast.LENGTH_SHORT).show();
            }
            else if (item.getItemId() == R.id.nav_carts) {
                if (cartFragment == null) {
                    cartFragment = new CartFragment();
                }
                selectedFragment = cartFragment;
                Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show();
            }
            replaceFragment(selectedFragment);
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}