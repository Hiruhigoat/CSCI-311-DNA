package userinterface;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;

import algorithms.EditDistance;
import algorithms.LCS;
import algorithms.NeedlemanWunsch;
import algorithms.LongestCommonSubstring;

//I Used Ai to help make the ui cause i am not familiar with javafx i still understand it it ofc and didn't just copy paste but it is ai core so u know in advance. 
public class Main extends Application{

    private File targetFile;
    private File sequencesFile;

    @Override 
    public void start(Stage primStage) {
        primStage.setMaximized(true);
        primStage.setTitle("DNA Sequence Matcher");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15, 50, 15, 50));
        header.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");

        ImageView logoView = new ImageView();
        logoView.setFitWidth(1500);
        logoView.setFitHeight(500);
        logoView.setPreserveRatio(false);

        try {
            Image logo = new Image(new FileInputStream("assets/dna.jpg"));
            logoView.setImage(logo);
        } catch (FileNotFoundException e) {
            Label placeholderLogo = new Label(":( DNA Helix no load");
            placeholderLogo.setStyle("-fx-font-size: 32px;");
            header.getChildren().add(placeholderLogo);
        }

        if (logoView.getImage() != null) {
            header.getChildren().add(logoView);
        }
        
        VBox titleBox = new VBox(2);
        Label titleLabel = new Label("DNA Sequence Alignment Studio");
        titleLabel.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 20px; -fx-font-weight: bold;");

        header.getChildren().add(titleBox);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("FASTA Files", "*.fasta", "*.fa", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );

        GridPane fileGrid = new GridPane();
        fileGrid.setHgap(15);
        fileGrid.setVgap(12);
        fileGrid.setPadding(new Insets(15, 25, 15, 25));
        fileGrid.setAlignment(Pos.CENTER);
        fileGrid.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");

        Label targetStatusLabel = new Label("Query Data");
        targetStatusLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
        Label targetPathLabel = new Label("No file selected");
        targetPathLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
        
        Label sequenceStatusLabel = new Label("Sequences Data");
        sequenceStatusLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
        Label sequencePathLabel = new Label("No file selected");
        sequencePathLabel.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");

        

        Button btnSelect1 = new Button("Browse Target File...");
        btnSelect1.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");

        Button btnSelect2 = new Button("Browse Sequences File...");
        btnSelect2.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;");
        
        btnSelect1.setOnAction(e -> {
            File selected = fileChooser.showOpenDialog(primStage);
            if (selected != null) {
                targetFile = selected;
                targetPathLabel.setText(targetFile.getName());
                targetPathLabel.setStyle("-fx-text-fill: #34d399; -fx-font-weight: bold; -fx-font-size: 13px;");
            }
        });
        btnSelect2.setOnAction(e -> {
            File selected = fileChooser.showOpenDialog(primStage);
            if (selected != null) {
                sequencesFile = selected;
                sequencePathLabel.setText(sequencesFile.getName());
                sequencePathLabel.setStyle("-fx-text-fill: #34d399; -fx-font-weight: bold; -fx-font-size: 13px;");
            }
        });

        fileGrid.add(targetStatusLabel, 0, 0);
        fileGrid.add(btnSelect1, 1, 0);
        fileGrid.add(targetPathLabel, 2, 0);

        fileGrid.add(sequenceStatusLabel, 0, 1);
        fileGrid.add(btnSelect2, 1, 1);
        fileGrid.add(sequencePathLabel, 2, 1);

        HBox algoBox = new HBox(15);
        algoBox.setAlignment(Pos.CENTER);
        algoBox.setPadding(new Insets(15));
        algoBox.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");

        Label algoLabel = new Label("Select Algorithm:");
        algoLabel.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 14px; -fx-font-weight: bold;");
        ComboBox<String> algorithmPicker = new ComboBox<>();

        algorithmPicker.getItems().addAll(
            "Needleman-Wunsch",
            "Edit Distance",
            "Longest Common Subsequence (LCS)",
            "Longest Common Subsequence (LCS) - Normalized",
            "Longest Common Substring"
        );

        algorithmPicker.setValue("Needleman-Wunsch");
        algorithmPicker.setStyle("-fx-background-color: #dce0e6; -fx-text-fill: white; -fx-font-size: 13px; -fx-background-radius: 6;");
        algoBox.getChildren().addAll(algoLabel, algorithmPicker);   


        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");

        Button btnRun = new Button("Run DNA Alignment");
        btnRun.setStyle(
            "-fx-background-color: #0284c7; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 15px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10 25 10 25; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        Label lblResultHeader = new Label("Best Match:");
        lblResultHeader.setStyle("-fx-text-fill: #34d399; -fx-font-size: 14px; -fx-font-weight: bold;");
        lblResultHeader.setWrapText(true);

        TextArea targetArea = new TextArea();
        targetArea.setEditable(false);
        targetArea.setWrapText(true);
        targetArea.setPrefRowCount(4);
        targetArea.setPromptText("Target alignment sequence");
        targetArea.setStyle(
            "-fx-control-inner-background: #0f172a; " +
            "-fx-text-fill: #e2e8f0; " +
            "-fx-font-family: 'Courier New', monospace;" +
            "-fx-font-size: 20px;"
        );

        TextArea sequenceArea = new TextArea();
        sequenceArea.setEditable(false);
        sequenceArea.setWrapText(true);
        sequenceArea.setPrefRowCount(4);
        sequenceArea.setPromptText("Matched sequence alignment");
        sequenceArea.setStyle(
            "-fx-control-inner-background: #0f172a; " +
            "-fx-text-fill: #e2e8f0; " +
            "-fx-font-family: 'Courier New', monospace;" +
            "-fx-font-size: 20px;"
        );

        Label lblTargetTitle = new Label("Target Alignment:");
        lblTargetTitle.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");

        Label lblSeqTitle = new Label("Best Match Sequence Alignment:");
        lblSeqTitle.setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;");

        HBox resultHeaderBox = new HBox(15, lblResultHeader);
        resultHeaderBox.setAlignment(Pos.CENTER_LEFT);


        VBox resultsBox = new VBox(8, 
            resultHeaderBox, 
            lblTargetTitle, 
            targetArea, 
            lblSeqTitle, 
            sequenceArea
        );
        resultsBox.setPadding(new Insets(15));
        resultsBox.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;");

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



            switch (algorithmPicker.getValue()) 
            {
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
                    {
                        double maxSim = -Double.MAX_VALUE;
                        for (DNASequence dnaSequence : sequences) {
                            double sim = LCS.computeSim(dnaSequence.getSequence(), targetDnaSequence.getSequence());
                            if (sim > maxSim) {
                                maxSim = sim;
                                bestDnaSequence.setHeader(dnaSequence.getHeader());
                                bestDnaSequence.setSequence(LCS.lcsString(dnaSequence.getSequence(), targetDnaSequence.getSequence()));
                            }
                        }
                        targetAlignment = targetDnaSequence.getSequence();
                        sequenceAlignment = bestDnaSequence.getSequence();
                        break;
                    }
                case "Longest Common Subsequence (LCS) - Normalized":
                    {
                        double maxSim = -Double.MAX_VALUE;
                        for (DNASequence dnaSequence : sequences) {
                            double sim = LCS.normalizedSim(dnaSequence.getSequence(), targetDnaSequence.getSequence());
                            if (sim > maxSim) {
                                maxSim = sim;
                                bestDnaSequence.setHeader(dnaSequence.getHeader());
                                bestDnaSequence.setSequence(LCS.lcsString(dnaSequence.getSequence(), targetDnaSequence.getSequence()));
                            }
                        }
                        targetAlignment = targetDnaSequence.getSequence();
                        sequenceAlignment = bestDnaSequence.getSequence();
                        break;
                    }
                case "Longest Common Substring":
                    {
                        double maxSim = -Double.MAX_VALUE;
                        for (DNASequence dnaSequence : sequences) {
                            double sim = LongestCommonSubstring.computeSim(dnaSequence.getSequence(), targetDnaSequence.getSequence());
                            if (sim > maxSim) {
                                maxSim = sim;
                                bestDnaSequence.setHeader(dnaSequence.getHeader());
                                bestDnaSequence.setSequence(LongestCommonSubstring.substringString(dnaSequence.getSequence(), targetDnaSequence.getSequence()));
                            }
                        }
                        targetAlignment = targetDnaSequence.getSequence();
                        sequenceAlignment = bestDnaSequence.getSequence();
                        break;
                    }
            }
            
            lblResultHeader.setText("Best Match: " + bestDnaSequence.getHeader());
            targetArea.setText(targetAlignment);
            sequenceArea.setText(sequenceAlignment);
            System.out.println("Target File: " + targetFile.getAbsolutePath());
            System.out.println("Sequences File: " + sequencesFile.getAbsolutePath());
            System.out.println("Algorithim : " + algorithmPicker.getValue() + "\n");
            System.out.println("Best Allignment: " + bestDnaSequence.getHeader() + "\n");
            System.out.println("Target  : " + targetAlignment + "\n");
            System.out.println("Sequence: " + sequenceAlignment);
        });

        VBox.setVgrow(resultsBox, Priority.ALWAYS);
        VBox.setVgrow(targetArea, Priority.ALWAYS);
        VBox.setVgrow(sequenceArea, Priority.ALWAYS);


        VBox root = new VBox(20, header, fileGrid, algoBox, btnRun, statusLabel, resultsBox);
            root.setAlignment(Pos.TOP_CENTER);
            root.setPadding(new Insets(25));
            root.setStyle("-fx-background-color: #0f172a;");
        
        Scene scene = new Scene(root);
        primStage.setScene(scene);
        primStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
