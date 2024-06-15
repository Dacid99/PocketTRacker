package org.sbv.pockettracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class NumberPaneActivity extends AppCompatActivity {
    private int maxNumber;

    private final MaterialButton[] buttonArray = new MaterialButton[15];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numberpane);

        getButtons();

        Intent intent = getIntent();
        maxNumber = intent.getIntExtra("maxNumberOfBalls", 15);

        configurePanels();

    }

    private void configurePanels(){
        for (int i = 15; i>maxNumber; i--){
            buttonArray[i-1].setVisibility(View.GONE);
        }
    }

    private void getButtons(){
        buttonArray[0]= findViewById(R.id.button_1);
        buttonArray[1]= findViewById(R.id.button_2);
        buttonArray[2]= findViewById(R.id.button_3);
        buttonArray[3]= findViewById(R.id.button_4);
        buttonArray[4]= findViewById(R.id.button_5);
        buttonArray[5]= findViewById(R.id.button_6);
        buttonArray[6]= findViewById(R.id.button_7);
        buttonArray[7]= findViewById(R.id.button_8);
        buttonArray[8]= findViewById(R.id.button_9);
        buttonArray[9]= findViewById(R.id.button_10);
        buttonArray[10]= findViewById(R.id.button_11);
        buttonArray[11]= findViewById(R.id.button_12);
        buttonArray[12]= findViewById(R.id.button_13);
        buttonArray[13]= findViewById(R.id.button_14);
        buttonArray[14]= findViewById(R.id.button_15);
    }
}