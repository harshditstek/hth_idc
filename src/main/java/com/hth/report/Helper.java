package com.hth.report;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Helper extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        //Stage stage = new Stage();
        String[] header = {"PROCEDURE","CLAIM_NUMBER", "LINE_NO", "DATE_OF_SERVICE", "DIVISION", "POLICY_ID", "PATIENT_NAME", "DEPENDENT_CODE", "COVERAGE", "AMOUNT_CLAIMED", "DAMTEX", "TOTAL_PAID", "DEXCD", "13", "HICD1", "HICD2", "HICD3", "HICD4", "HICD5", "HICD6", "HICD7", "HICD8", "HICD9", "HICD10", "TYPE_OF_SERVICE", "PROVIDER_ID", "PROVIDER_NAME"};
        List<String[][]> showData = ReportData.singleton().getReportData();
        primaryStage.setTitle("JavaFX App");
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("TEXT files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);


       // File selectedFile = fileChooser.showOpenDialog(primaryStage);
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        System.out.println(":'." + selectedFile.getAbsolutePath());
        String path = selectedFile.getAbsolutePath();
//        if (path.endsWith(".csv")) {
//            path = path;
//        } else {
//            path = path + ".csv";
//        }

        File csvFile = new File(path);
        FileWriter fileWriter = new FileWriter(csvFile);
        StringBuilder line = new StringBuilder();
        line.append(String.join(",",header));
        line.append("\n");
        fileWriter.write(line.toString());
        for (int id = 0; id < showData.size(); id++) {
            for (String[] data : showData.get(0)) {
                 line = new StringBuilder();
                for (int i = 0; i < data.length; i++) {
                    if (data[i].contains(",")) {
                        line.append("\"");
                        line.append(data[i].replaceAll("\"", "\"\""));
                        line.append("\"");
                        if (i != data.length - 1) {
                            line.append(',');
                        }
                    } else {
                        line.append(data[i].replaceAll("\"", "\"\""));
                        if (i != data.length - 1) {
                            line.append(',');
                        }
                    }
                }
                line.append("\n");
                fileWriter.write(line.toString());
            }
        }
        fileWriter.close();
        new Report();
       // r.set();
    }

    public void saveFile(String[] args){

        launch(args);
    }

}
