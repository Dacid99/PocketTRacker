package org.sbv.pockettracker.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.window.layout.WindowMetrics;
import androidx.window.layout.WindowMetricsCalculator;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import org.sbv.pockettracker.R;
import org.sbv.pockettracker.model.PoolTable;

import java.util.Objects;

public class NumberPaneFragment extends DialogFragment {

    private static final String NUMBERPARAMETER = "maxNumber";
    public interface NumberPaneFragmentProvider {
        void onNumberPaneClick(int number);
    }
    private NumberPaneFragmentProvider listener;
    private View view;
    private int maxNumber;

    private GridLayout grid;
    private final MaterialButton[] buttonArray = new MaterialButton[PoolTable.MAXBALLNUMBER];

    public static NumberPaneFragment newInstance(int maxNumber){
        NumberPaneFragment fragment = new NumberPaneFragment();
        Bundle args = new Bundle();
        args.putInt(NUMBERPARAMETER, maxNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (NumberPaneFragmentProvider) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement CustomDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (getArguments() == null){
            dismiss();
        }
        maxNumber = getArguments().getInt("maxNumber");
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_numberpane, container,false);
        grid = view.findViewById(R.id.numberGrid);
        findButtons();
        setButtonListeners();

        configurePanels();

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        WindowMetrics windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity());
        int height = windowMetrics.getBounds().height();
        int width = windowMetrics.getBounds().width();

        Window window = Objects.requireNonNull(getDialog()).getWindow();
        if (window != null){
            float horizontalScreenFraction = getResources().getFraction(R.fraction.numberpaneFragment_horizontal_screenfraction,1, 1);
            float verticalScreenFraction = getResources().getFraction(R.fraction.numberpaneFragment_vertical_screenfraction,1, 1);
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

    private void configurePanels(){
        int requiredRows = (int) Math.ceil(maxNumber * 1.0 / grid.getColumnCount());
        int requiredCells = requiredRows * grid.getColumnCount();
        for (int i = buttonArray.length; i>maxNumber; i--){
            if (i > requiredCells){
                buttonArray[i-1].setVisibility(View.GONE); //lets other buttons take over the empty row
            }else {
                buttonArray[i-1].setVisibility(View.GONE);
                buttonArray[i-1].setClickable(false);
            }
        }
    }

    private void findButtons(){
        buttonArray[0]= view.findViewById(R.id.button_1);
        buttonArray[1]= view.findViewById(R.id.button_2);
        buttonArray[2]= view.findViewById(R.id.button_3);
        buttonArray[3]= view.findViewById(R.id.button_4);
        buttonArray[4]= view.findViewById(R.id.button_5);
        buttonArray[5]= view.findViewById(R.id.button_6);
        buttonArray[6]= view.findViewById(R.id.button_7);
        buttonArray[7]= view.findViewById(R.id.button_8);
        buttonArray[8]= view.findViewById(R.id.button_9);
        buttonArray[9]= view.findViewById(R.id.button_10);
        buttonArray[10]= view.findViewById(R.id.button_11);
        buttonArray[11]= view.findViewById(R.id.button_12);
        buttonArray[12]= view.findViewById(R.id.button_13);
        buttonArray[13]= view.findViewById(R.id.button_14);
        buttonArray[14]= view.findViewById(R.id.button_15);
    }

    private void setButtonListeners(){
        for (int number = 1; number< buttonArray.length + 1; number++){
            int buttonReturnNumber = number;
            buttonArray[number - 1].setOnClickListener(v -> {
                listener.onNumberPaneClick(buttonReturnNumber);
                dismiss();
            });
        }
    }
}