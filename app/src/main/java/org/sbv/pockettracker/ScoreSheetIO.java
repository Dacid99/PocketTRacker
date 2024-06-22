package org.sbv.pockettracker;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.opencsv.CSVWriter;

public class ScoreSheetIO {
    static final int REQUEST_CODE_PERMISSIONS = 100;
    static final int REQUEST_CODE_CREATE_DOCUMENT = 101;
    static boolean isExternalStorageWriteable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    static void writeToFile(OutputStreamWriter outputStreamWriter, ScoreSheet scoreSheet){
        CSVWriter csvWriter = new CSVWriter(outputStreamWriter);

        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer1ScoresList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer1IncrementsList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer2ScoresList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer1IncrementsList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getBallsOnTableList()));
    }

    static String[] convertToStringArray(ArrayList<Integer> arrayList){
        String[] array = new String[arrayList.size()];
        for (int index = 0; index < arrayList.size(); index++){
            array[index] = arrayList.get(index).toString();
        }
        return array;
    }


}
