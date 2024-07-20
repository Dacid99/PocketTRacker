package org.sbv.pockettracker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class AboutFragment extends DialogFragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about, container,false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }
}
