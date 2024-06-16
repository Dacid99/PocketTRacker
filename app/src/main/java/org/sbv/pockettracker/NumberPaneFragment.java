package org.sbv.pockettracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

public class NumberPaneFragment extends DialogFragment {
    public interface CustomDialogListener {
        void onDialogClick(int number);
    }
    private CustomDialogListener listener;
    private View view;
    private int maxNumber;

    private GridLayout grid;
    private final MaterialButton[] buttonArray = new MaterialButton[15];

    public static NumberPaneFragment newInstance(int maxNumber){
        NumberPaneFragment fragment = new NumberPaneFragment();
        Bundle args = new Bundle();
        args.putInt("maxNumber", maxNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (CustomDialogListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement CustomDialogListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_numberpane, container,false);

        grid = view.findViewById(R.id.numberGrid);
        findButtons();
        setButtonListeners();
        if (getArguments() == null){
            dismiss();
        }
        maxNumber = getArguments().getInt("maxNumber");

        configurePanels();

        return view;
    }

    private void configurePanels(){
        int requiredRows = (int) Math.ceil(maxNumber * 1.0 / grid.getColumnCount());
        int requiredCells = requiredRows * grid.getColumnCount();
        for (int i = 15; i>maxNumber; i--){
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
            buttonArray[number - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDialogClick(buttonReturnNumber);
                    dismiss();
                }
            });
        }
    }
}