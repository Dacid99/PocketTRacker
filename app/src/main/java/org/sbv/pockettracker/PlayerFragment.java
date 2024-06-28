package org.sbv.pockettracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class PlayerFragment extends DialogFragment {

    public interface PlayerFragmentProvider {
        void onNameInput(int playerNumber, String name);
        void onClubInput(int playerNumber, String club);
        Player requestPlayer(int playerNumber);
        ScoreSheet requestScoreSheet();
    }
    private static final String PLAYERNUMBERPARAMETER = "playerNumber";

    private int playerNumber;
    private View view;
    private TextInputLayout playerNameLayout, playerClubLayout;
    private TextInputEditText playerNameInput, playerClubInput;
    private MaterialButton leftToOtherPlayerButton, rightToOtherPlayerButton;

    private TextView playerScoreView;


    private PlayerFragmentProvider listener;
    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(int playerNumber) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(PLAYERNUMBERPARAMETER, playerNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (PlayerFragment.PlayerFragmentProvider) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement PlayerFragmentProvider");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerNumber = getArguments().getInt(PLAYERNUMBERPARAMETER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_player, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        playerNameInput = view.findViewById(R.id.playerName);
        playerClubInput = view.findViewById(R.id.playerClub);
        playerScoreView = view.findViewById(R.id.playerScore);

        leftToOtherPlayerButton = view.findViewById(R.id.left_toOtherPlayerButton);
        rightToOtherPlayerButton = view.findViewById(R.id.right_toOtherPlayerButton);

        if (playerNumber == 1){
            leftToOtherPlayerButton.setVisibility(View.INVISIBLE);
        }else if (playerNumber == 2){
            rightToOtherPlayerButton.setVisibility(View.INVISIBLE);
        }else {
            Log.d("bad parameter", "In PlayerFragment.onCreateView: playerNumber is neither 0 or 1!");
        }

        Player player = listener.requestPlayer(playerNumber);
        playerNameInput.setText(getString(R.string.player_name_format, player.getName()));
        playerClubInput.setText(getString(R.string.player_club_format, player.getClub()));
        playerScoreView.setText(getString(R.string.player_score_format, player.getScore()));

        playerNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newName = s.toString();
                listener.onNameInput(playerNumber, newName);
            }
        });

        playerClubInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newName = s.toString();
                listener.onClubInput(playerNumber, newName);
            }
        });

        leftToOtherPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToOtherPlayer();
            }
        });

        rightToOtherPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToOtherPlayer();
            }
        });

        return view;
    }



    private void switchToOtherPlayer(){
        FragmentManager fragmentManager = getParentFragmentManager();
        int otherPlayerNumber = (playerNumber == 1) ? 2 : 1 ;
        PlayerFragment otherPlayerFragment = PlayerFragment.newInstance(otherPlayerNumber);
        otherPlayerFragment.show(fragmentManager, "Player"+otherPlayerNumber+"Fragment");
        dismiss();
    }
}