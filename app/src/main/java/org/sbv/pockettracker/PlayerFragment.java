package org.sbv.pockettracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


public class PlayerFragment extends DialogFragment {

    public interface PlayerFragmentProvider {
        void onNameInput(int playerNumber, String name);
        void onClubInput(int playerNumber, String club);
        void onSwapButtonClick();
        Player requestPlayer(int playerNumber);
    }
    private static final String NAME_AUTOCOMPLETEPREFERENCES = "player_autocompletepreferences";
    private static final String CLUB_AUTOCOMPLETEPREFERENCES = "club_autocompletepreferences";
    private static final String NAME_HISTORY_KEY = "player_history";
    private static final String CLUB_HISTORY_KEY = "club_history";
    private static final String PLAYERNUMBERPARAMETER = "playerNumber";
    private static final String SCORESHEETPARAMETER = "scoresheet";

    private int playerNumber;
    private ScoreSheet scoreSheet;
    private View view;
    private TextInputLayout playerClubLayout;
    private AutoCompleteTextView playerNameInput, playerClubInput;
    private MaterialButton leftToOtherPlayerButton, rightToOtherPlayerButton, leftSwapPlayersButton, rightSwapPlayersButton;
    private TextView playerScoreView, inningsView, meanInningView, meanRunView, maxRunView;
    private SharedPreferences namePreferences, clubPreferences;
    private Set<String> nameHistorySet, clubHistorySet;

    private PlayerFragmentProvider listener;
    public PlayerFragment() {
        // Required empty public constructor
    }

    public static PlayerFragment newInstance(int playerNumber, ScoreSheet scoreSheet) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putInt(PLAYERNUMBERPARAMETER, playerNumber);
        args.putParcelable(SCORESHEETPARAMETER, scoreSheet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (PlayerFragment.PlayerFragmentProvider) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement PlayerFragmentProvider");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerNumber = getArguments().getInt(PLAYERNUMBERPARAMETER);
            scoreSheet = getArguments().getParcelable(SCORESHEETPARAMETER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_player, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        playerNameInput = view.findViewById(R.id.playerName);
        playerClubLayout = view.findViewById(R.id.playerClubLayout);
        playerClubInput = view.findViewById(R.id.playerClub);
        clubPreferences = requireActivity().getSharedPreferences(CLUB_AUTOCOMPLETEPREFERENCES, Context.MODE_PRIVATE);

        playerScoreView = view.findViewById(R.id.playerScore);

        inningsView = view.findViewById(R.id.inningsPlayer_view);
        meanInningView = view.findViewById(R.id.meanInningPlayer_view);
        meanRunView = view.findViewById(R.id.meanRunPlayer_view);
        maxRunView = view.findViewById(R.id.maxRunPlayer_view);

        inningsView.setText(getResources().getString(R.string.player_innings_format, GameStatistics.playerInnings(scoreSheet)[playerNumber - 1]));
        meanInningView.setText(getResources().getString(R.string.meanInning_format, GameStatistics.meanInnings(scoreSheet)[playerNumber - 1]));
        meanRunView.setText(getResources().getString(R.string.meanRun_format, GameStatistics.meanRuns(scoreSheet)[playerNumber - 1]));
        maxRunView.setText(getResources().getString(R.string.player_maxrun_format, GameStatistics.maxRuns(scoreSheet)[playerNumber - 1]));

        leftToOtherPlayerButton = view.findViewById(R.id.left_toOtherPlayerButton);
        rightToOtherPlayerButton = view.findViewById(R.id.right_toOtherPlayerButton);
        leftSwapPlayersButton = view.findViewById(R.id.left_swapButton);
        rightSwapPlayersButton = view.findViewById(R.id.right_swapButton);

        if (!Player.hasClub){
            playerClubLayout.setVisibility(View.INVISIBLE);
            playerClubInput.setVisibility(View.INVISIBLE);
        }

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
        namePreferences = requireActivity().getSharedPreferences(NAME_AUTOCOMPLETEPREFERENCES, Context.MODE_PRIVATE);
        nameHistorySet = new HashSet<>(namePreferences.getStringSet(NAME_HISTORY_KEY, new HashSet<>()));
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, new ArrayList<>(nameHistorySet));
        playerNameInput.setAdapter(nameAdapter);
        playerNameInput.setThreshold(1);

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
        clubPreferences = requireActivity().getSharedPreferences(CLUB_AUTOCOMPLETEPREFERENCES, Context.MODE_PRIVATE);
        clubHistorySet = new HashSet<>(clubPreferences.getStringSet(CLUB_HISTORY_KEY, new HashSet<>()));
        ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, new ArrayList<>(clubHistorySet));
        playerClubInput.setAdapter(clubAdapter);
        playerClubInput.setThreshold(1);

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
    @Override
    public void onCancel(@NonNull DialogInterface dialogInterface){
        super.onCancel(dialogInterface);

        String input = playerClubInput.getText().toString();
        if(!input.isEmpty()){
            clubHistorySet.add(input);
            clubPreferences.edit().putStringSet(CLUB_HISTORY_KEY, clubHistorySet).apply();
        }
        input = playerNameInput.getText().toString();
        if(!input.isEmpty()){
            nameHistorySet.add(input);
            namePreferences.edit().putStringSet(NAME_HISTORY_KEY, nameHistorySet).apply();
        }
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialogInterface){
        super.onDismiss(dialogInterface);

        String input = playerClubInput.getText().toString();
        if(!input.isEmpty()){
            clubHistorySet.add(input);
            clubPreferences.edit().putStringSet(CLUB_HISTORY_KEY, clubHistorySet).apply();
        }
        input = playerNameInput.getText().toString();
        if(!input.isEmpty()){
            nameHistorySet.add(input);
            namePreferences.edit().putStringSet(NAME_HISTORY_KEY, nameHistorySet).apply();
        }
    }
    private void updatePlayerFields(){
        Player player = listener.requestPlayer(playerNumber);
        System.out.println(player.getScore() + player.getName());
        playerNameInput.setText(getString(R.string.player_name_format, player.getName()));
        playerClubInput.setText(getString(R.string.player_club_format, player.getClub()));
        playerScoreView.setText(getString(R.string.player_score_format, player.getScore()));
    }

    private void switchToOtherPlayer(){
        FragmentManager fragmentManager = getParentFragmentManager();
        int otherPlayerNumber = (playerNumber == 1) ? 2 : 1 ;
        PlayerFragment otherPlayerFragment = PlayerFragment.newInstance(otherPlayerNumber, scoreSheet);
        otherPlayerFragment.show(fragmentManager, "Player"+otherPlayerNumber+"Fragment");
        dismiss();
    }
}