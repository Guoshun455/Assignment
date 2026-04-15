package com.example.spacecolony;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.spacecolony.model.MissionControl;
import com.example.spacecolony.model.Storage;
import com.example.spacecolony.ui.HomeFragment;
import com.example.spacecolony.ui.MissionFragment;
import com.example.spacecolony.ui.QuartersFragment;
import com.example.spacecolony.ui.RecruitFragment;
import com.example.spacecolony.ui.SimulatorFragment;
import com.example.spacecolony.ui.StatsFragment;
import com.example.spacecolony.util.DataManager;

public class MainActivity extends AppCompatActivity implements
        HomeFragment.OnHomeActionListener,
        RecruitFragment.OnRecruitListener {

    private BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;
    private DataManager dataManager;

    private HomeFragment homeFragment;
    private QuartersFragment quartersFragment;
    private SimulatorFragment simulatorFragment;
    private MissionFragment missionFragment;
    private StatsFragment statsFragment;
    private RecruitFragment recruitFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataManager = new DataManager(this);

        initializeGameComponents();

        bottomNav = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        setupBottomNavigation();
        initializeFragments();

        showFragment(homeFragment);
    }

    private void initializeGameComponents() {
        if (dataManager.hasSavedData()) {
            boolean loaded = dataManager.loadGameData();
            if (loaded) {
                Toast.makeText(this, "Game loaded!", Toast.LENGTH_SHORT).show();
            } else {
                Storage.resetInstance();
                Storage.getInstance();
                Toast.makeText(this, "Failed to load, starting new game", Toast.LENGTH_SHORT).show();
            }
        } else {
            Storage.getInstance();
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                showFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_quarters) {
                showFragment(quartersFragment);
                return true;
            } else if (itemId == R.id.nav_simulator) {
                showFragment(simulatorFragment);
                return true;
            } else if (itemId == R.id.nav_mission) {
                showFragment(missionFragment);
                return true;
            } else if (itemId == R.id.nav_stats) {
                showFragment(statsFragment);
                return true;
            }

            return false;
        });
    }

    private void initializeFragments() {
        homeFragment = new HomeFragment();
        homeFragment.setOnHomeActionListener(this);

        quartersFragment = new QuartersFragment();
        simulatorFragment = new SimulatorFragment();
        missionFragment = new MissionFragment();
        statsFragment = new StatsFragment();

        recruitFragment = new RecruitFragment();
        recruitFragment.setOnRecruitListener(this);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onRecruitClick() {
        showFragment(recruitFragment);
    }

    @Override
    public void onNavigateToQuarters() {
        bottomNav.setSelectedItemId(R.id.nav_quarters);
        showFragment(quartersFragment);
    }

    @Override
    public void onNavigateToSimulator() {
        bottomNav.setSelectedItemId(R.id.nav_simulator);
        showFragment(simulatorFragment);
    }

    @Override
    public void onNavigateToMissionControl() {
        bottomNav.setSelectedItemId(R.id.nav_mission);
        showFragment(missionFragment);
    }

    @Override
    public void onSaveGame() {
        dataManager.saveGameData();
        Toast.makeText(this, "Game saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadGame() {
        if (dataManager.hasSavedData()) {
            MissionControl.resetInstance();

            boolean loaded = dataManager.loadGameData();
            if (loaded) {
                Toast.makeText(this, "Game loaded!", Toast.LENGTH_SHORT).show();
                initializeFragments();
                if (homeFragment != null) {
                    homeFragment.updateStats();
                }
                bottomNav.setSelectedItemId(R.id.nav_home);
                showFragment(homeFragment);
            } else {
                Toast.makeText(this, "Failed to load game", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No saved game found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCrewCreated() {
        Toast.makeText(this, "Crew member recruited!", Toast.LENGTH_SHORT).show();

        // 关键修复：延迟刷新确保Storage已更新
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (homeFragment != null) {
                homeFragment.updateStats();
            }
            // 强制刷新MissionFragment（如果它是当前显示的）
            if (missionFragment != null && missionFragment.isVisible()) {
                // 已经在Mission页面，刷新它
            }
        }, 100);

        bottomNav.setSelectedItemId(R.id.nav_home);
        showFragment(homeFragment);
    }

    @Override
    public void onCancel() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        showFragment(homeFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataManager.saveGameData();
    }
}