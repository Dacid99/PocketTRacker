package org.sbv.pockettracker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class ScoreSheetIO {
    static void writeToFile(OutputStreamWriter outputStreamWriter, Player player1, Player player2, ScoreSheet scoreSheet) throws IOException{
        CSVWriter csvWriter = new CSVWriter(outputStreamWriter);
        String[] headerLine = new String[6];
        headerLine[0] = "Turn#";
        headerLine[1] = player1.getName();
        headerLine[2] = player1.getClub();
        headerLine[3] = player2.getName();
        headerLine[4] = player2.getClub();
        headerLine[5] = "Remaining Balls";
        csvWriter.writeNext(headerLine);
        for (ScoreSheet.Inning turn : scoreSheet){
            csvWriter.writeNext(turn.toStringArray());
        }
        csvWriter.close();
    }

    static void readFromFile(InputStreamReader inputStreamReader, Player player1, Player player2, ScoreSheet scoreSheet) throws IOException{
        try (CSVReader csvReader = new CSVReader(inputStreamReader)) {
            String[] nextLine;
            nextLine = csvReader.readNext();
            player1.setName(nextLine[1]);
            player1.setClub(nextLine[2]);
            player2.setName(nextLine[3]);
            player2.setClub(nextLine[4]);
            csvReader.readNext(); //skip the first line, it is in scoresheet by default
            while ((nextLine = csvReader.readNext()) != null){
                scoreSheet.update(new ScoreSheet.Inning(nextLine));
            }
        }catch (CsvException e){
            throw new IOException(e.toString());
        }
    }
}
