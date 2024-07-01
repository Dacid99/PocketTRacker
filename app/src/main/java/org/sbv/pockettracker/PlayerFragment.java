package org.sbv.pockettracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

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
        void onSwapButtonClick();
        Player requestPlayer(int playerNumber);
        ScoreSheet requestScoreSheet();
    }
    private static final String PLAYERNUMBERPARAMETER = "playerNumber";

    private int playerNumber;
    private GameStatistics gameStatistics;
    private View view;
    private TextInputLayout playerNameLayout, playerClubLayout;
    private TextInputEditText playerNameInput, playerClubInput;
    private MaterialButton leftToOtherPlayerButton, rightToOtherPlayerButton, leftSwapPlayersButton, rightSwapPlayersButton;
    private TextView playerScoreView, inningsView, meanInningView, meanRunView, maxRunView;


    private PlayerFragmentProvider listener;
    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(int playerNumber) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(PLAYERNUMBERPARAMETER, playerNumber);
        //args.putParcelable(SCORESHEETPARAMETER, scoreSheet);
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
        gameStatistics = new GameStatistics(listener.requestScoreSheet());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_player, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        playerNameInput = view.findViewById(R.id.playerName);
        playerClubInput = view.findViewById(R.id.playerClub);
        playerScoreView = view.findViewById(R.id.playerScore);

        inningsView = view.findViewById(R.id.inningsPlayer_view);
        meanInningView = view.findViewById(R.id.meanInningPlayer_view);
        meanRunView = view.findViewById(R.id.meanRunPlayer_view);
        maxRunView = view.findViewById(R.id.maxRunPlayer_view);

        if (playerNumber == 1){
            inningsView.setText(getResources().getString(R.string.player_innings_format, gameStatistics.player1Innings()));
            meanInningView.setText(getResources().getString(R.string.meanInning_format, gameStatistics.meanInningPlayer1()));
            meanRunView.setText(getResources().getString(R.string.meanRun_format, gameStatistics.meanRunPlayer1()));
            maxRunView.setText(getResources().getString(R.string.player_maxrun_format, gameStatistics.maxRunPlayer1()));
        }else if (playerNumber == 2){
            inningsView.setText(getResources().getString(R.string.player_innings_format, gameStatistics.player2Innings()));
            meanInningView.setText(getResources().getString(R.string.meanInning_format, gameStatistics.meanInningPlayer2()));
            meanRunView.setText(getResources().getString(R.string.meanRun_format, gameStatistics.meanRunPlayer2()));
            maxRunView.setText(getResources().getString(R.string.player_maxrun_format, gameStatistics.maxRunPlayer2()));
        }else {
            Log.d("failed ifelse", "In PlayerFragment.onCreateView");
        }

        leftToOtherPlayerButton = view.findViewById(R.id.left_toOtherPlayerButton);
        rightToOtherPlayerButton = view.findViewById(R.id.right_toOtherPlayerButton);
        leftSwapPlayersButton = view.findViewById(R.id.left_swapButton);
        rightSwapPlayersButton = view.findViewById(R.id.right_swapButton);

        if (playerNumber == 1){
            leftToOtherPlayerButton.setVisibility(View.INVISIBLE);
            leftSwapPlayersButton.setVisibility(View.INVISIBLE);
        }else if (playerNumber == 2){
            rightToOtherPlayerButton.setVisibility(View.INVISIBLE);
            rightSwapPlayersButton.setVisibility(View.INVISIBLE);
        }else {
            Log.d("bad parameter", "In PlayerFragment.onCreateView: playerNumber is neither 0 or 1!");
        }

        updatePlayerFields();

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

        leftSwapPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSwapButtonClick();
                updatePlayerFields();
            }
        });

        rightSwapPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSwapButtonClick();
                updatePlayerFields();
            }
        });

        return view;
    }

    private void updatePlayerFields(){
        Player player = listener.requestPlayer(playerNumber);
        playerNameInput.setText(getString(R.string.player_name_format, player.getName()));
        playerClubInput.setText(getString(R.string.player_club_format, player.getClub()));
        playerScoreView.setText(getString(R.string.player_score_format, player.getScore()));
    }

    private void switchToOtherPlayer(){
        FragmentManager fragmentManager = getParentFragmentManager();
        int otherPlayerNumber = (playerNumber == 1) ? 2 : 1 ;
        PlayerFragment otherPlayerFragment = PlayerFragment.newInstance(otherPlayerNumber);
        otherPlayerFragment.show(fragmentManager, "Player"+otherPlayerNumber+"Fragment");
        dismiss();
    }
}