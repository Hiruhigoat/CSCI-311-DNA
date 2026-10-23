package userinterface;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.List;

import algorithms.EditDistance;
import algorithms.NeedlemanWunsch;


public class Main extends Application{

    private File targetFile;
    private File sequencesFile;

    @Override 
    public void start(Stage primStage) {
        primStage.setTitle("DNA Matcher");
        
        

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("FASTA Files", "*.fasta", "*.fa", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );

        Label labelFile1 = new Label("Target Sequence:");
        Label pathLabel1 = new Label("No file selected");
        Button btnSelect1 = new Button("Browse Target File...");
        btnSelect1.setOnAction(e -> {
            File selected = fileChooser.showOpenDialog(primStage);
            if (selected != null) {
                targetFile = selected;
                pathLabel1.setText(targetFile.getName());
            }
        });

        Label labelFile2 = new Label("Sequences File:");
        Label pathLabel2 = new Label("No file selected");
        Button btnSelect2 = new Button("Browse Sequences File...");
        btnSelect2.setOnAction(e -> {
            File selected = fileChooser.showOpenDialog(primStage);
            if (selected != null) {
                sequencesFile = selected;
                pathLabel2.setText(sequencesFile.getName());
            }
        });

        Label algoLabel = new Label("Select Algorithm:");
        ComboBox<String> algorithmPicker = new ComboBox<>();

        algorithmPicker.getItems().addAll(
            "Needleman-Wunsch",
            "Edit Distance",
            "Longest Common Subsequence (LCS)",
            "Longest Common Substring"
        );

        algorithmPicker.setValue("Needleman-Wunsch");

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        Button btnRun = new Button("Run DNA Alignment");
        btnRun.setStyle("-fx-font-weight: bold; -fx-padding: 8 15 8 15;");
        btnRun.setOnAction(e -> {
            if (targetFile == null || sequencesFile == null) {
                statusLabel.setText("Select both files before proceeding.");
                return;
            } 

            DNASequence targetDnaSequence = FileHandling.readTargetFile(targetFile.getAbsolutePath());
            List<DNASequence> sequences = FileHandling.readFile(sequencesFile.getAbsolutePath());;
            DNASequence bestDnaSequence = new DNASequence("N/A", "N/A");
            String targetAlignment = "";
            String sequenceAlignment = "";



            switch (algorithmPicker.getValue()) {
                case "Needleman-Wunsch":
                    {
                        int maxScore = Integer.MIN_VALUE;
                        for (DNASequence dnaSequence : sequences) {
                            NeedlemanWunsch needlemanWunsch = new NeedlemanWunsch(targetDnaSequence, dnaSequence);
                            if (needlemanWunsch.getScore() > maxScore) {
                                maxScore = needlemanWunsch.getScore();
                                bestDnaSequence.setHeader(dnaSequence.getHeader());
                                bestDnaSequence.setSequence(dnaSequence.getSequence());
                                targetAlignment = needlemanWunsch.getTargetString();
                                sequenceAlignment = needlemanWunsch.getSeqString();
                            }
                        }
                        break;
                    }
                case "Edit Distance":
                    {
                        double maxSim = -Double.MAX_VALUE;
                        for (DNASequence dnaSequence : sequences) {
                            double sim = EditDistance.computeSim(dnaSequence.getSequence(), targetDnaSequence.getSequence());
                            if (sim > maxSim) {
                                maxSim = sim;
                                bestDnaSequence.setHeader(dnaSequence.getHeader());
                                bestDnaSequence.setSequence(dnaSequence.getSequence());
                            }
                        }
                        targetAlignment = targetDnaSequence.getSequence();
                        sequenceAlignment = bestDnaSequence.getSequence();
                        break;
                    }
                case "Longest Common Subsequence (LCS)":
                    break;
                case "Longest Common Substring":
                    break;
            }

                System.out.println("Target File: " + targetFile.getAbsolutePath());
                System.out.println("Sequences File: " + sequencesFile.getAbsolutePath());
                System.out.println("Best Allignment: " + bestDnaSequence.getHeader() + "\n");
                System.out.println("Target  : " + targetAlignment + "\n");
                System.out.println("Sequence: " + sequenceAlignment);

        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        grid.add(labelFile1, 0, 0);
        grid.add(btnSelect1, 1, 0);
        grid.add(pathLabel1, 2, 0);

        grid.add(labelFile2, 0, 1);
        grid.add(btnSelect2, 1, 1);
        grid.add(pathLabel2, 2, 1);

        grid.add(algoLabel, 0, 2);
        grid.add(algorithmPicker, 1, 2);

        VBox root = new VBox(20, grid, btnRun, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        
        Scene scene = new Scene(root, 1920, 1080);
        primStage.setScene(scene);
        primStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
