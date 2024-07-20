package org.sbv.pockettracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.button.MaterialButton;


public class SettingsFragment extends Fragment {
    private View view;
    private MaterialButton aboutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        if (savedInstanceState == null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsSubFragment())
                    .commit();
        }

        aboutButton = view.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.show(getChildFragmentManager(), "AboutFragment");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        return super.onOptionsItemSelected(menuItem);
    }

    public static class SettingsSubFragment extends PreferenceFragmentCompat {
        private EditTextPreference winnerPointsDefault;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

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
        }
    }
}