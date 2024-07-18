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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;


public class PlayerFragment extends DialogFragment {

    public interface PlayerFragmentProvider {
        void onNameInput(int playerNumber, String name);
        void onClubInput(int playerNumber, String club);
        void onSwapButtonClick();
    }
    private static final String NAME_AUTOCOMPLETEPREFERENCES = "player_autocompletepreferences";
    private static final String CLUB_AUTOCOMPLETEPREFERENCES = "club_autocompletepreferences";
    private static final String NAME_HISTORY_KEY = "player_history";
    private static final String CLUB_HISTORY_KEY = "club_history";
    private static final String PLAYERNUMBERPARAMETER = "playerNumber";
    private static final String SCORESHEETPARAMETER = "scoresheet";

    private int playerNumber;
    private PlayersViewModel playersViewModel;
    private ScoreBoardViewModel scoreBoardViewModel;
    private ScoreSheetViewModel scoreSheetViewModel;
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
            throw new ClassCastException(context + "must implement PlayerFragmentProvider");
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
        playerClubLayout = view.findViewById(R.id.playerClubLayout);
        playerClubInput = view.findViewById(R.id.playerClub);
        clubPreferences = requireActivity().getSharedPreferences(CLUB_AUTOCOMPLETEPREFERENCES, Context.MODE_PRIVATE);

        playerScoreView = view.findViewById(R.id.playerScore);

        inningsView = view.findViewById(R.id.inningsPlayer_view);
        meanInningView = view.findViewById(R.id.meanInningPlayer_view);
        meanRunView = view.findViewById(R.id.meanRunPlayer_view);
        maxRunView = view.findViewById(R.id.maxRunPlayer_view);

        leftToOtherPlayerButton = view.findViewById(R.id.left_toOtherPlayerButton);
        rightToOtherPlayerButton = view.findViewById(R.id.right_toOtherPlayerButton);
        leftSwapPlayersButton = view.findViewById(R.id.left_swapButton);
        rightSwapPlayersButton = view.findViewById(R.id.right_swapButton);

        if (!Players.haveClubs){
            playerClubLayout.setVisibility(View.GONE);
            playerClubInput.setVisibility(View.GONE);
        }

        if (playerNumber == 0){
            leftToOtherPlayerButton.setVisibility(View.INVISIBLE);
            leftSwapPlayersButton.setVisibility(View.INVISIBLE);
        }else if (playerNumber == 1){
            rightToOtherPlayerButton.setVisibility(View.INVISIBLE);
            rightSwapPlayersButton.setVisibility(View.INVISIBLE);
        }else {
            Log.d("Bad parameter", "In PlayerFragment.onCreateView: playerNumber is neither 0 or 1!");
        }

        playersViewModel = new ViewModelProvider(requireActivity()).get(PlayersViewModel.class);
        playersViewModel.getPlayers().observe(this, new Observer<Players>(){
            @Override
            public void onChanged(Players players) {
                if (players != null) {
                    playerNameInput.setText(getString(R.string.player_name_format, players.getNames()[playerNumber]));
                    playerClubInput.setText(getString(R.string.player_club_format, players.getClubs()[playerNumber]));
                }
            }
        });

        scoreBoardViewModel = new ViewModelProvider(requireActivity()).get(ScoreBoardViewModel.class);
        scoreBoardViewModel.getScoreBoard().observe(this, new Observer<ScoreBoard>() {
            @Override
            public void onChanged(ScoreBoard scoreBoard) {
                playerScoreView.setText(getString(R.string.player_score_format, scoreBoard.getPlayerScores()[playerNumber]));
            }
        });

        scoreSheetViewModel = new ViewModelProvider(requireActivity()).get(ScoreSheetViewModel.class);
        scoreSheetViewModel.getScoreSheet().observe(this, new Observer<ScoreSheet>() {
            @Override
            public void onChanged(ScoreSheet scoreSheet) {
                if (scoreSheet != null) {
                    inningsView.setText(getResources().getString(R.string.player_innings_format, GameStatistics.playerInnings(Objects.requireNonNull(scoreSheetViewModel.getScoreSheet().getValue()))[playerNumber]));
                    meanInningView.setText(getResources().getString(R.string.meanInning_format, GameStatistics.meanInnings(Objects.requireNonNull(scoreSheetViewModel.getScoreSheet().getValue()))[playerNumber]));
                    meanRunView.setText(getResources().getString(R.string.meanRun_format, GameStatistics.meanRuns(Objects.requireNonNull(scoreSheetViewModel.getScoreSheet().getValue()))[playerNumber]));
                    maxRunView.setText(getResources().getString(R.string.player_maxrun_format, GameStatistics.maxRuns(Objects.requireNonNull(scoreSheetViewModel.getScoreSheet().getValue()))[playerNumber]));
                }
            }
        });

        playerNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int cursorPosition = playerNameInput.getSelectionStart();
                String newName = s.toString();
                listener.onNameInput(playerNumber, newName);
                playerNameInput.setSelection(cursorPosition);
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
                int cursorPosition = playerClubInput.getSelectionStart();
                String newName = s.toString();
                listener.onClubInput(playerNumber, newName);
                playerClubInput.setSelection(cursorPosition);
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
            }
        });

        rightSwapPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSwapButtonClick();
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
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);

        String input = playerClubInput.getText().toString();
        if (!input.isEmpty()) {
            clubHistorySet.add(input);
            clubPreferences.edit().putStringSet(CLUB_HISTORY_KEY, clubHistorySet).apply();
        }
        input = playerNameInput.getText().toString();
        if (!input.isEmpty()) {
            nameHistorySet.add(input);
            namePreferences.edit().putStringSet(NAME_HISTORY_KEY, nameHistorySet).apply();
        }
    }

    private void switchToOtherPlayer(){
        FragmentManager fragmentManager = getParentFragmentManager();
        int otherPlayerNumber = Math.abs(playerNumber-1) ;
        PlayerFragment otherPlayerFragment = PlayerFragment.newInstance(otherPlayerNumber);
        otherPlayerFragment.show(fragmentManager, "Player"+otherPlayerNumber+"Fragment");
        dismiss();
    }
}