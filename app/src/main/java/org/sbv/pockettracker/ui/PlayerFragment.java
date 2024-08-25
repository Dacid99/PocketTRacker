package org.sbv.pockettracker.ui;

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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.sbv.pockettracker.utils.GameStatistics;
import org.sbv.pockettracker.model.Players;
import org.sbv.pockettracker.model.PlayersViewModel;
import org.sbv.pockettracker.R;
import org.sbv.pockettracker.model.ScoreBoard;
import org.sbv.pockettracker.model.ScoreBoardViewModel;
import org.sbv.pockettracker.model.ScoreSheet;
import org.sbv.pockettracker.model.ScoreSheetViewModel;

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

    private int playerNumber;
    private PlayersViewModel playersViewModel;
    private ScoreBoardViewModel scoreBoardViewModel;
    private ScoreSheetViewModel scoreSheetViewModel;
    private TextInputLayout playerClubLayout;
    private AutoCompleteTextView playerNameInput, playerClubInput;
    private MaterialButton toOtherPlayerButton, rightToOtherPlayerButton, swapPlayersButton, rightSwapPlayersButton;
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
        }else {
            Log.d("ProgrammingError", "No playernumber passed to PlayerFragment!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate((playerNumber == Players.PLAYER_1_NUMBER) ? R.layout.fragment_player_right : R.layout.fragment_player_left, container, false);

        playerNameInput = view.findViewById(R.id.playerName);
        playerClubLayout = view.findViewById(R.id.playerClubLayout);
        playerClubInput = view.findViewById(R.id.playerClub);
        clubPreferences = requireActivity().getSharedPreferences(CLUB_AUTOCOMPLETEPREFERENCES, Context.MODE_PRIVATE);

        playerScoreView = view.findViewById(R.id.playerScore);

        inningsView = view.findViewById(R.id.inningsPlayer_view);
        meanInningView = view.findViewById(R.id.meanInningPlayer_view);
        meanRunView = view.findViewById(R.id.meanRunPlayer_view);
        maxRunView = view.findViewById(R.id.maxRunPlayer_view);

        toOtherPlayerButton = view.findViewById(R.id.toOtherPlayerButton);
        swapPlayersButton = view.findViewById(R.id.swapButton);

        if (!Players.haveClubs){
            playerClubLayout.setVisibility(View.GONE);
            playerClubInput.setVisibility(View.GONE);
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
                    inningsView.setText(getResources().getString(R.string.player_innings_format, scoreSheet.innings()[playerNumber]));
                    meanInningView.setText(getResources().getString(R.string.meanInning_format, GameStatistics.meanInnings(scoreSheet)[playerNumber]));
                    meanRunView.setText(getResources().getString(R.string.meanRun_format, GameStatistics.meanRuns(scoreSheet)[playerNumber]));
                    maxRunView.setText(getResources().getString(R.string.player_maxrun_format, GameStatistics.maxRuns(scoreSheet)[playerNumber]));
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

        toOtherPlayerButton.setOnClickListener(v -> switchToOtherPlayer());

        swapPlayersButton.setOnClickListener(v -> listener.onSwapButtonClick());


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null){
            float horizontalScreenFraction = requireActivity().getResources().getFraction(R.fraction.playerFragment_horizontal_screenfraction,1, 1);
            float verticalScreenFraction = requireActivity().getResources().getFraction(R.fraction.playerFragment_vertical_screenfraction,1, 1);
            if (height > width){
                width = (int) (width * horizontalScreenFraction);
                height = (int) (height * verticalScreenFraction);
            } else {
                width = (int) (width * verticalScreenFraction);
                height = (int) (height * horizontalScreenFraction);
            }
            window.setLayout(width, height);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
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