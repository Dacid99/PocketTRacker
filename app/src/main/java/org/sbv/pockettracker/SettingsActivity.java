package org.sbv.pockettracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;
import java.util.prefs.Preferences;

public class SettingsActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private MaterialButton counterButton, scoreSheetButton, settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        counterButton = findViewById(R.id.counter_button);
        scoreSheetButton = findViewById(R.id.scoresheet_button);
        settingsButton = findViewById(R.id.settings_button);

        settingsButton.setEnabled(false);

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
        private EditTextPreference winnerPointsDefault;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

            winnerPointsDefault = findPreference("winnerPoints_default");
            if (winnerPointsDefault != null) {
                winnerPointsDefault.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                        String newValueString = (String) newValue;
                        try{
                            Integer.parseInt(newValueString);
                            return true;
                        }catch (NumberFormatException e){
                            Log.d("Bad user input", "In SettingsActivity.winnerpointsDefault.onPreferenceChangeListener: User input is not a valid integer String!",e);
                            return false;
                        }

                    }
                });
            }
            sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                    if (key != null) {
                        switch (key) {
                            case "winnerPoints_default":
                                String winnerPointsString = sharedPreferences.getString(key, "40");
                                try{
                                    Player.winningPoints = Integer.parseInt(winnerPointsString);
                                }catch (NumberFormatException e) {
                                    Log.d("Bad preference","In SettingsActivity.onSharedPreferenceChanged: winnerpoints_default is not a parseable String! e");
                                    Player.winningPoints = 40;
                                }
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