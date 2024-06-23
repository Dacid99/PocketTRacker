package org.sbv.pockettracker;

import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.color.utilities.Score;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public class ScoreSheetIO {
    static final int REQUEST_CODE_PERMISSIONS = 100;
    static final int REQUEST_CODE_CREATE_DOCUMENT = 101;
    static final int REQUEST_CODE_READ= 102;
    static boolean isExternalStorageWriteable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    static void writeToFile(OutputStreamWriter outputStreamWriter, ScoreSheet scoreSheet) throws IOException{
        CSVWriter csvWriter = new CSVWriter(outputStreamWriter);
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer1ScoresList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer1IncrementsList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer2ScoresList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getPlayer1IncrementsList()));
        csvWriter.writeNext(convertToStringArray(scoreSheet.getBallsOnTableList()));
        csvWriter.close();
    }

    static void loadFromFile(InputStreamReader inputStreamReader, ScoreSheet scoreSheet) throws IOException{
        try (CSVReader csvReader = new CSVReader(inputStreamReader)) {
            List<String[]> csvContent = csvReader.readAll();
            if (ScoreSheet.isHealthyList(csvContent)) {
                scoreSheet.fromList(csvContent);
            }else throw new IOException("CSV has wrong format!");
        }catch (CsvException e){
            throw new IOException(e.toString());
        }
    }

    static String[] convertToStringArray(ArrayList<Integer> arrayList){
        String[] array = new String[arrayList.size()];
        for (int index = 0; index < arrayList.size(); index++){
            array[index] = arrayList.get(index).toString();
        }
        return array;
    }

    static ArrayList<Integer> convertStringArrayToIntegerArrayList(String[] stringArray) throws IOException{
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        try {
            for (String element : stringArray) {
                integerArrayList.add(Integer.parseInt(element));
            }
        }catch (NumberFormatException e){
            throw new IOException("CSV contains false data");
        }
        return integerArrayList;
    }

}
