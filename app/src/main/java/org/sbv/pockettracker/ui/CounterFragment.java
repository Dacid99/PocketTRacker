package org.sbv.pockettracker.ui;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.math.NumberUtils;
import org.sbv.pockettracker.model.Players;
import org.sbv.pockettracker.model.PlayersViewModel;
import org.sbv.pockettracker.model.PoolTable;
import org.sbv.pockettracker.model.PoolTableViewModel;
import org.sbv.pockettracker.R;
import org.sbv.pockettracker.model.ScoreBoard;
import org.sbv.pockettracker.model.ScoreBoardViewModel;
import org.sbv.pockettracker.model.ScoreSheet;
import org.sbv.pockettracker.model.ScoreSheetViewModel;

import java.util.Objects;

public class CounterFragment extends Fragment{
    public interface CounterFragmentListener{
        void onPlayerCardClick(int playerNumber);
        void onBallsOnTableFloatingButtonClick();
    }
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private PlayersViewModel playersViewModel;
    private ScoreBoardViewModel scoreBoardViewModel;
    private PoolTableViewModel poolTableViewModel;
    private ScoreSheetViewModel scoreSheetViewModel;
    private TextView player1NameView, player2NameView, player1ClubView, player2ClubView, player1ScoreView, player2ScoreView, ballsOnTableFloatingButton;
    private TextInputEditText winningPointsInput;
    private MaterialCardView player1Card, player2Card;
    private MaterialButton foulButton, missButton, safeButton, redoButton, undoButton;
    private CounterFragmentListener listener;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (CounterFragmentListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement CounterFragmentListener!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_counter, container,false);

        player1ScoreView = view.findViewById(R.id.player1ScoreView);
        player2ScoreView = view.findViewById(R.id.player2ScoreView);

        player1NameView = view.findViewById(R.id.player1NameView);
        player2NameView = view.findViewById(R.id.player2NameView);

        player1ClubView = view.findViewById(R.id.player1ClubView);
        player2ClubView = view.findViewById(R.id.player2ClubView);

        if (!Players.haveClubs){
            player1ClubView.setVisibility(View.GONE);
            player2ClubView.setVisibility(View.GONE);
        }

        player1Card = view.findViewById(R.id.player1CardView);
        player2Card = view.findViewById(R.id.player2CardView);

        ballsOnTableFloatingButton = view.findViewById(R.id.ballsOnTableFloatingButton);

        winningPointsInput = view.findViewById(R.id.winningPointsInput);

        missButton = view.findViewById(R.id.missButton);
        safeButton = view.findViewById(R.id.safeButton);
        foulButton = view.findViewById(R.id.foulButton);
        undoButton = view.findViewById(R.id.undoButton);
        redoButton = view.findViewById(R.id.redoButton);

        applyPreferences();

        assignViewModels();

        winningPointsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int cursorPosition = winningPointsInput.getSelectionStart();
                String newText = s.toString();
                if ( NumberUtils.isParsable(newText)) {
                    if (Integer.parseInt(newText) > 0) {
                        winningPointsInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.score_color));
                        int newWinnerPoints = Integer.parseInt(newText);
                        scoreBoardViewModel.updateWinnerPoints(newWinnerPoints);
                    } else {
                        winningPointsInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_color));
                    }
                } else {
                    winningPointsInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.warning_color));
                }
                winningPointsInput.setSelection(cursorPosition);
            }
        });

        player1Card.setOnClickListener(v -> listener.onPlayerCardClick(Players.PLAYER_1_NUMBER));

        player2Card.setOnClickListener(v -> listener.onPlayerCardClick(Players.PLAYER_2_NUMBER));

        ballsOnTableFloatingButton.setOnClickListener(v -> listener.onBallsOnTableFloatingButtonClick() );

        missButton.setOnClickListener(v -> {
            assignPoints();
            newTurn(getString(R.string.miss_string));
        });

        safeButton.setOnClickListener(v -> {
            assignPoints();
            newTurn(getString(R.string.safe_string));
        });

        foulButton.setOnClickListener(v -> {
            assignPoints();
            scoreBoardViewModel.addPoints( scoreSheetViewModel.turnplayerNumber(), (scoreSheetViewModel.currentTurn() == 0) ? -2:-1 );
            newTurn(getString(R.string.foul_string));
        });

        undoButton.setOnClickListener(v -> scoreSheetViewModel.rollback());

        undoButton.setOnLongClickListener(v -> {
            scoreSheetViewModel.toStart();
            return true;
        });

        redoButton.setOnClickListener(v -> scoreSheetViewModel.progress());

        redoButton.setOnLongClickListener(v -> {
            scoreSheetViewModel.toLatest();
            return true;
        });

        return view;
    }

    private void assignViewModels(){

        playersViewModel = new ViewModelProvider(requireActivity()).get(PlayersViewModel.class);

        playersViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<Players>() {
            @Override
            public void onChanged(Players players) {
                if (players != null){
                    player1NameView.setText(getString(R.string.player_name_format, players.getNames()[Players.PLAYER_1_NUMBER]));
                    player1ClubView.setText(getString(R.string.player_club_format, players.getClubs()[Players.PLAYER_1_NUMBER]));
                    player2NameView.setText(getString(R.string.player_name_format, players.getNames()[Players.PLAYER_2_NUMBER]));
                    player2ClubView.setText(getString(R.string.player_club_format, players.getClubs()[Players.PLAYER_2_NUMBER]));
                }
            }
        });

        scoreBoardViewModel = new ViewModelProvider(requireActivity()).get(ScoreBoardViewModel.class);

        scoreBoardViewModel.getScoreBoard().observe(getViewLifecycleOwner(), new Observer<ScoreBoard>() {
            @Override
            public void onChanged(ScoreBoard scoreBoard) {
                if (scoreBoard != null){
                    player1ScoreView.setText(getString(R.string.player_score_format, scoreBoard.getPlayerScores()[Players.PLAYER_1_NUMBER]));
                    player2ScoreView.setText(getString(R.string.player_score_format, scoreBoard.getPlayerScores()[Players.PLAYER_2_NUMBER]));
                    winningPointsInput.setText(getString(R.string.winnerPoints_format, scoreBoard.getWinnerPoints()));
                    if (scoreBoard.getWinner() == Players.PLAYER_1_NUMBER){
                        player1Card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.winner_color));
                    }else if (scoreBoard.getWinner() == Players.PLAYER_2_NUMBER){
                        player2Card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.winner_color));
                    } else {
                        player1Card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.notturnplayer_color));
                        player2Card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.notturnplayer_color));
                    }
                }
            }
        });

        poolTableViewModel = new ViewModelProvider(requireActivity()).get(PoolTableViewModel.class);

        poolTableViewModel.getPoolTable().observe(getViewLifecycleOwner(), new Observer<PoolTable>() {
            @Override
            public void onChanged(PoolTable poolTable) {
                if (poolTable != null) {
                    ballsOnTableFloatingButton.setText(getString(R.string.ballsOnTable_format, poolTable.getNumberOfBalls()));
                }
            }
        });

        scoreSheetViewModel = new ViewModelProvider(requireActivity()).get(ScoreSheetViewModel.class);
        scoreSheetViewModel.getScoreSheet().observe(getViewLifecycleOwner(), new Observer<ScoreSheet>() {
            @Override
            public void onChanged(ScoreSheet scoreSheet) {
                if (scoreSheet != null) {
                    boolean isTurnplayer1Boolean = scoreSheet.turnplayerNumber() == Players.PLAYER_1_NUMBER;
                    player1ScoreView.setEnabled(isTurnplayer1Boolean);
                    player1NameView.setEnabled(isTurnplayer1Boolean);
                    player1ClubView.setEnabled(isTurnplayer1Boolean);
                    player2ScoreView.setEnabled(!isTurnplayer1Boolean);
                    player2NameView.setEnabled(!isTurnplayer1Boolean);
                    player2ClubView.setEnabled(!isTurnplayer1Boolean);
                    player1Card.setCardElevation(getResources().getInteger(isTurnplayer1Boolean ? R.integer.turnplayer_cardelevation : R.integer.notturnplayer_cardelevation) );
                    player2Card.setCardElevation(getResources().getInteger(!isTurnplayer1Boolean ? R.integer.turnplayer_cardelevation : R.integer.notturnplayer_cardelevation) );

                    if (!(player1Card.getCardBackgroundColor().getDefaultColor() == ContextCompat.getColor(requireContext(), R.color.winner_color))) {
                        player1Card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), isTurnplayer1Boolean ? R.color.turnplayer_color : R.color.notturnplayer_color));
                    }
                    if (!(player2Card.getCardBackgroundColor().getDefaultColor() == ContextCompat.getColor(requireContext(), R.color.winner_color))) {
                        player2Card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), !isTurnplayer1Boolean ? R.color.turnplayer_color : R.color.notturnplayer_color));
                    }

                    if (scoreSheetViewModel.isLatest()) {
                        redoButton.setVisibility(View.INVISIBLE);
                    } else {
                        redoButton.setVisibility(View.VISIBLE);
                    }
                    if (scoreSheetViewModel.isStart()) {
                        undoButton.setVisibility(View.INVISIBLE);
                    } else {
                        undoButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void applyPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        System.out.println(preferences.getBoolean("AOD_toggle", true));
        if (preferences.getBoolean("AOD_toggle", true)) {
            requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                if (key != null) {
                    switch (key) {
                        case "winnerPoints_default":
                            String winnerPointsString = sharedPreferences.getString(key, "40");
                            int oldWinnerPoints = ScoreBoard.defaultWinnerPoints;
                            try{
                                ScoreBoard.defaultWinnerPoints = Integer.parseInt(winnerPointsString);
                                if (Objects.requireNonNull(winningPointsInput.getText()).toString().isEmpty() || winningPointsInput.getText().toString().equals(String.valueOf(oldWinnerPoints))){
                                    winningPointsInput.setText(winnerPointsString);
                                }
                            }catch (NumberFormatException e) {
                                Log.d("Bad preference","In SettingsActivity.onSharedPreferenceChanged: winnerpoints_default is not a parseable String! e");
                                ScoreBoard.defaultWinnerPoints = 40;
                            }
                            break;

                        case "player1_name_default":
                            String newNamePlayer1 = sharedPreferences.getString(key,"");
                            if (player1NameView.getText().toString().isEmpty() || player1NameView.getText().toString().equals(Players.defaultPlayerNames[0])){
                                playersViewModel.updatePlayerName(Players.PLAYER_1_NUMBER, newNamePlayer1);
                            }
                            Players.defaultPlayerNames[Players.PLAYER_1_NUMBER] = newNamePlayer1;
                            break;

                        case "player2_name_default":
                            String newNamePlayer2 = sharedPreferences.getString(key,"");
                            if (player2NameView.getText().toString().isEmpty() || player2NameView.getText().toString().equals(Players.defaultPlayerNames[1])){
                                playersViewModel.updatePlayerName(Players.PLAYER_2_NUMBER, newNamePlayer2);
                            }
                            Players.defaultPlayerNames[Players.PLAYER_2_NUMBER] = newNamePlayer2;
                            break;

                        case "player1_club_default":
                            String newClubPlayer1 = sharedPreferences.getString(key,"");
                            if (player1ClubView.getText().toString().isEmpty() || player1ClubView.getText().toString().equals(Players.defaultPlayerClubs[0])){
                                playersViewModel.updateClubName(Players.PLAYER_1_NUMBER, newClubPlayer1);
                            }
                            Players.defaultPlayerClubs[Players.PLAYER_1_NUMBER] = newClubPlayer1;
                            break;

                        case "player2_club_default":
                            String newClubPlayer2 = sharedPreferences.getString(key,"");
                            if (player2ClubView.getText().toString().isEmpty() || player2ClubView.getText().toString().equals(Players.defaultPlayerClubs[1])){
                                playersViewModel.updateClubName(Players.PLAYER_2_NUMBER, newClubPlayer2);
                            }
                            Players.defaultPlayerClubs[Players.PLAYER_2_NUMBER] = newClubPlayer2;
                            break;

                        case "club_toggle":
                            Players.haveClubs = sharedPreferences.getBoolean(key, true);
                            if (!Players.haveClubs){
                                player1ClubView.setVisibility(View.GONE);
                                player2ClubView.setVisibility(View.GONE);
                            }else {
                                player1ClubView.setVisibility(View.VISIBLE);
                                player2ClubView.setVisibility(View.VISIBLE);
                            }
                            break;
                        case "AOD_toggle":
                            System.out.println(preferences.getBoolean(key, true));
                            if (sharedPreferences.getBoolean(key, true)) {
                                requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            } else {
                                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            }
                    }
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void newTurn(String reason){
        scoreSheetViewModel.update(reason);
    }


    private void assignPoints(){
        int points = poolTableViewModel.evaluate();
        scoreBoardViewModel.addPoints(scoreSheetViewModel.turnplayerNumber(), points);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroy(){
        if (sharedPreferenceChangeListener != null) {
            preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }
        super.onDestroy();
    }
}
