package com.ewaste.app.ui.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.ewaste.app.R;
import com.ewaste.app.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {

    private ActivityAdminMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(new AdminOverviewFragment());
            binding.bottomNavigation.setSelectedItemId(R.id.nav_admin_overview);
        }
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_overview) {
                loadFragment(new AdminOverviewFragment());
                return true;
            } else if (itemId == R.id.nav_admin_centers) {
                loadFragment(AdminCentersFragment.newInstance(false));
                return true;
            } else if (itemId == R.id.nav_admin_locations) {
                loadFragment(AdminDropOffFragment.newInstance());
                return true;
            } else if (itemId == R.id.nav_admin_access) {
                loadFragment(AdminStaffFragment.newInstance());
                return true;
            }
            return false;
        });
    }

    public void navigateToPendingCenters() {
        AdminCentersFragment fragment = AdminCentersFragment.newInstance(true);
        loadFragment(fragment);
        binding.bottomNavigation.setSelectedItemId(R.id.nav_admin_centers);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
