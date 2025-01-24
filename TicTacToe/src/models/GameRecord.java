/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.DataOutputStream;

/**
 *
 * @author HP
 */
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameRecord {

    private  String file;
    String recordFileName;
     public GameRecord(String fileName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        this.recordFileName = "game_record_" + timestamp + ".txt";
        file = fileName;
    }

    public String getRecordFileName() {
        return recordFileName;
    }

    public void saveMove(Move move) {
    try  {
        BufferedWriter writer = new BufferedWriter(new FileWriter(recordFileName, true));
        writer.write(move.getPlayer() + " " + move.getCellId());
        writer.newLine();
        writer.flush(); 
        System.out.println("Saved move: " + move.getPlayer() + " " + move.getCellId());
    } catch (IOException ex) {
        System.err.println("Error writing to file: " + ex.getMessage());
    }
}
    public List<Move> readRecord(String recordName) {
        List<Move> moves = new ArrayList<>();
        File recFile = new File(recordName);
        if (!recFile.exists()) {
            return moves;
        }
       try  {
          BufferedReader reader = new BufferedReader(new FileReader(recFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 2) {
                String player = parts[0];
                String cellId = parts[1];
                moves.add(new Move(player, cellId));
            } else {
                System.err.println("Invalid line format: " + line);
            }
        }
    } catch (FileNotFoundException ex) {
        Logger.getLogger(GameRecord.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
        Logger.getLogger(GameRecord.class.getName()).log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
        System.err.println("Error parsing move coordinates: " + ex.getMessage());
    }

    return moves;
    }

    public void saveRecordName() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(recordFileName);
            writer.newLine();
            writer.flush();
             System.out.println("Saved record name: " + recordFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllRecords() {
        List<String> recordNames = new ArrayList<>();
        File indexFile = new File(file);
        if (!indexFile.exists()) {
            return recordNames;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(indexFile));
            String line;
            while ((line = reader.readLine()) != null) {
                recordNames.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recordNames;
    }
}