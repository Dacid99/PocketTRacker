package org.sbv.pockettracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import org.sbv.pockettracker.R;


public class SettingsActivity extends AppCompatActivity {

    private ImageView aboutIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setHomeAsUpIndicator(R.drawable.ic_back);
        }

        aboutIcon = findViewById(R.id.about_icon);
        aboutIcon.setOnClickListener(v -> {
            AboutFragment aboutFragment = new AboutFragment();
            aboutFragment.show(getSupportFragmentManager(), "about_bottomsheet");
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            EditTextPreference winnerPointsDefault = findPreference("winnerPoints_default");
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

            ListPreference themePreference = findPreference("theme");
            if (themePreference != null){
                themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                        requireActivity().recreate();
                        return true;
                    }
                });
            }
        }
    }
}