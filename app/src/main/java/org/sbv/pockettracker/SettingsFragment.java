package org.sbv.pockettracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;



public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.settings, new SettingsSubFragment()).commit();
            fragmentManager.beginTransaction().replace(R.id.about_container, new AboutFragment()).commit();
        }
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