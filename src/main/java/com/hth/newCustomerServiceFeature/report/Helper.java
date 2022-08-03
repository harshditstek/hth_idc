package com.hth.newCustomerServiceFeature.report;

import com.opencsv.CSVWriter;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Helper extends Application {
    String paths = "";

    @Override
    public void start(Stage primaryStage) throws IOException {
        String[] header = {"CLAIM_NUMBER", "LINE_NO", "3", "DATE_OF_SERVICE", "DIVISION", "POLICY_ID", "PATIENT_NAME", "DEPENDENT_CODE", "COVERAGE", "AMOUNT_CLAIMED", "DAMTEX", "TOTAL_PAID", "DEXCD", "13", "HICD1", "HICD2", "HICD3", "HICD4", "HICD5", "HICD6", "HICD7", "HICD8", "HICD9", "HICD10", "TYPE_OF_SERVICE", "PROVIDER_ID", "PROVIDER_NAME"};
        List<String[][]> showData = ReportData.singleton().getReportData();
        primaryStage.setTitle("JavaFX App");
        FileChooser fileChooser = new FileChooser();

        //File selectedFile = fileChooser.showOpenDialog(primaryStage);
        File selectedFile = fileChooser.showSaveDialog(primaryStage);
        System.out.println(":'."+selectedFile.getAbsolutePath());
        String path = selectedFile.getAbsolutePath();
        if(path.endsWith(".csv")){
            path = path;
        }else{
            path = path+".csv";
        }

        File csvFile = new File(path);
        FileWriter fileWriter = new FileWriter(csvFile);

        //write header line here if you need.
        for(int id=0;id<showData.size();id++)
        for (String[] data : showData.get(0)) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                if(data[i].contains(",")) {
                    line.append("\"");
                    line.append(data[i].replaceAll("\"", "\"\""));
                    line.append("\"");
                    if (i != data.length - 1) {
                        line.append(',');
                    }
                }else{
                    line.append(data[i].replaceAll("\"", "\"\""));
                    if (i != data.length - 1) {
                        line.append(',');
                    }
                }
            }
            line.append("\n");
            fileWriter.write(line.toString());
        }
        fileWriter.close();


//        try {
//
//            FileWriter outputfile = new FileWriter(path);
//            CSVWriter writer = new CSVWriter(outputfile);
//            //CSVWriter writer = new CSVWriter(outputfile,',',CSVWriter.);
//            writer.writeNext(header);
//
//            for (int i = 0; i < data.size(); i++) {
//                for (int j = 0; j < data.get(i).length; j++) {
//                    writer.writeNext(data.get(i)[j]);
//                }
//            }
//            writer.close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
    }
    public void saveFile(String[] args){
        launch(args);
    }

}
