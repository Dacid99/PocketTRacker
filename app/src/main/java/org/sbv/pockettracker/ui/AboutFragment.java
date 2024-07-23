package org.sbv.pockettracker.ui;
import org.sbv.pockettracker.BuildConfig;
import org.sbv.pockettracker.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textview.MaterialTextView;


public class AboutFragment extends BottomSheetDialogFragment {

    private MaterialTextView versionCodeTextView, versionNameTextView, authorTextView, gitlabTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container,false);

        versionCodeTextView = view.findViewById(R.id.about_versionCode);
        versionCodeTextView.setText(getString(R.string.versionCode_format, BuildConfig.VERSION_CODE));

        versionNameTextView = view.findViewById(R.id.about_versionName);
        versionNameTextView.setText(getString(R.string.versionName_format, BuildConfig.VERSION_NAME));

        authorTextView = view.findViewById(R.id.about_author);
        authorTextView.setText(getString(R.string.author_format, BuildConfig.AUTHOR));

        gitlabTextView = view.findViewById(R.id.about_gitlab);
        gitlabTextView.setText(getString(R.string.gitlab_format, BuildConfig.GITLAB));


        return view;
    }
}
