package org.sbv.pockettracker.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import org.sbv.pockettracker.model.Players;
import org.sbv.pockettracker.model.PlayersViewModel;
import org.sbv.pockettracker.model.ScoreSheet;
import org.sbv.pockettracker.model.ScoreSheetViewModel;

public class ScoreSheetIO {
    public static void writeToFile(OutputStreamWriter outputStreamWriter, Players players, ScoreSheet scoreSheet) throws IOException{
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

    public static void readFromFile(InputStreamReader inputStreamReader, PlayersViewModel playersViewModel, ScoreSheetViewModel scoreSheetViewModel) throws IOException{
        try (CSVReader csvReader = new CSVReader(inputStreamReader)) {
            String[] nextLine;
            nextLine = csvReader.readNext();
            playersViewModel.updatePlayerName(0, nextLine[1]);
            playersViewModel.updateClubName(0, nextLine[2]);
            playersViewModel.updatePlayerName(1, nextLine[3]);
            playersViewModel.updateClubName(1, nextLine[4]);

            csvReader.readNext(); //skip the first line, it is in scoresheet by default

            while ((nextLine = csvReader.readNext()) != null){
                scoreSheetViewModel.append(new ScoreSheet.Inning(nextLine));
            }
        }catch (CsvException e){
            throw new IOException(e.toString());
        }
    }
}
