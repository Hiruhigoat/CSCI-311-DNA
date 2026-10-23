package userinterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Class for file handling
public class FileHandling {

    public static List<DNASequence> readFile(String filePath) {
        // List of dana sequences
        List<DNASequence> sequences = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String header = null;
            String line;
            StringBuilder  currSequence = new StringBuilder();

            // While there are lines left do:
            while ((line = bufferedReader.readLine()) != null) {
                // Trim and skip blanks
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // If header identifier is seen either it is the first header in that case set it as first header or its a new header 
                // in that case add the current sequence to the list and reset for next header
                if (line.startsWith(">")) {
                    if (header != null) {
                        sequences.add(new DNASequence(header, currSequence.toString()));
                        currSequence.setLength(0);
                    }
                    header = line.substring(1).trim();
                // If line isn't a header file append to the current sequence string as uppercase.
                } else {
                    currSequence.append(line.toUpperCase());
                }
            }

            // Last sequence won't have a header after so if header exsists we add it to the list
            if (header != null) {
                sequences.add(new DNASequence(header, currSequence.toString()));
            }
            
        } catch (IOException e) {
            System.err.println("error in reading file " + e.getMessage());
        }
        // return the list of sequnces
        return sequences;
    } 


    public static DNASequence readTargetFile(String filePath) {
        
        DNASequence sequence = new DNASequence("", "");

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder currSequence = new StringBuilder();

            // While there are lines left do:
            while ((line = bufferedReader.readLine()) != null) {
                // Trim and skip blanks
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                currSequence.append(line.toUpperCase());
            }

            sequence.setHeader("Target DNA");
            sequence.setSequence(currSequence.toString());
            
        } catch (IOException e) {
            System.err.println("error in reading file " + e.getMessage());
        }
        // return the list of sequnces

        return sequence;
    } 
}
