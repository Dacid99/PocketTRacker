package org.sbv.pockettracker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class ScoreSheetIO {
    static void writeToFile(OutputStreamWriter outputStreamWriter, Players players, ScoreSheet scoreSheet) throws IOException{
        CSVWriter csvWriter = new CSVWriter(outputStreamWriter);
        String[] headerLine = new String[6];
        headerLine[0] = "Turn#";
        headerLine[1] = players.getNames()[0];
        headerLine[2] = players.getClubs()[0];
        headerLine[3] = players.getNames()[1];
        headerLine[4] = players.getClubs()[1];
        headerLine[5] = "Remaining Balls";
        csvWriter.writeNext(headerLine);
        for (ScoreSheet.Inning turn : scoreSheet){
            csvWriter.writeNext(turn.toStringArray());
        }
        csvWriter.close();
    }

    static void readFromFile(InputStreamReader inputStreamReader, Players players, ScoreSheet scoreSheet) throws IOException{
        try (CSVReader csvReader = new CSVReader(inputStreamReader)) {
            String[] nextLine;
            nextLine = csvReader.readNext();
            players.setName(1,nextLine[1]);
            players.setClub(1,nextLine[2]);
            players.setName(2,nextLine[3]);
            players.setClub(2,nextLine[4]);
            csvReader.readNext(); //skip the first line, it is in scoresheet by default
            while ((nextLine = csvReader.readNext()) != null){
                scoreSheet.update(new ScoreSheet.Inning(nextLine));
            }
        }catch (CsvException e){
            throw new IOException(e.toString());
        }
    }
}
