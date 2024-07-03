package org.sbv.pockettracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;
import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
        private SharedPreferences sharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                    if (key != null) {
                        switch (key) {
                            case "winnerPoints_default":
                                Player.winningPoints = sharedPreferences.getInt(key, 40);
                                break;

                            case "player1_name_default":
                                Player.defaultPlayerNames[0] = sharedPreferences.getString(key, "");
                                break;

                            case "player2_name_default":
                                Player.defaultPlayerNames[1] = sharedPreferences.getString(key, "");
                                break;

                            case "player1_club_default":
                                Player.defaultPlayerClubs[0] = sharedPreferences.getString(key, "");
                                break;

                            case "player2_club_default":
                                Player.defaultPlayerClubs[1] = sharedPreferences.getString(key, "");
                                break;

                            case "club_toggle":
                                Player.hasClub = sharedPreferences.getBoolean(key, true);
                                break;
                        }
                    }
                }
            };
            sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }

    }
}