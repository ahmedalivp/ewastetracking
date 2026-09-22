package com.ewaste.app.ui.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.ewaste.app.EWasteApplication;
import com.ewaste.app.R;
import com.ewaste.app.data.local.SessionManager;
import com.ewaste.app.databinding.ActivityBusinessMainBinding;
import com.ewaste.app.ui.auth.LoginActivity;

public class BusinessMainActivity extends AppCompatActivity {

    private ActivityBusinessMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = EWasteApplication.getInstance().getSessionManager();

        setupToolbar();
        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadFragment(new BrowseComponentsFragment(), false);
        }
    }

    private void setupToolbar() {
        if (sessionManager.getFullName() != null) {
            binding.toolbar.setSubtitle(sessionManager.getFullName() + " (Business)");
        }
        binding.toolbar.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
    }

    private boolean onToolbarMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.clearSession();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_browse) {
                loadFragment(new BrowseComponentsFragment(), false);
                return true;
            } else if (itemId == R.id.nav_requests) {
                loadFragment(new MyRequestsFragment(), false);
                return true;
            }
            return false;
        });
    }

    public void navigateToRequests() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_requests);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        var transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
