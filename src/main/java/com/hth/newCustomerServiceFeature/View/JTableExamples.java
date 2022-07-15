package com.hth.newCustomerServiceFeature.View;

import javax.swing.*;
import java.awt.*;

public class JTableExamples {
    // frame
    JFrame f;
    // Table
    JTable j;

    private JLabel fNameLabel;
    private JTextField fNameInput;

    private JLabel lNameLabel;
    private JTextField lNameInput;

    // Constructor
    JTableExamples()
    {
        // Frame initiallization
        f = new JFrame();

        // Frame Title
        f.setTitle("Search By Name");

        // Data to be displayed in the JTable
        String[][] data = {
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Kundan Kumar Jha", "4031", "CSE" },
                { "Anand Jha", "6014", "IT" }
        };

        // Column Names
        String[] columnNames = { "Last Name ", "First Name", "Employ ID" };

        // Initializing the JTable
        j = new JTable(data, columnNames);
        j.setBounds(30, 40, 200, 300);

        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(j);
        f.add(sp);
        // Frame Size
        f.setBounds( 250,300 ,850, 540);

        fNameLabel = new JLabel("First Name");
        fNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        fNameLabel.setSize(120, 30);
        fNameLabel.setLocation(255, 305);
        //container.add(referenceNumber);
       // f.add(fNameLabel);

        fNameInput = new JTextField();
        fNameInput.setFont(new Font("Arial", Font.PLAIN, 15));
        fNameInput.setSize(90, 30);
        fNameInput.setLocation(380, 305);
        //tReferenceNumber.setInputVerifier(new RefNumValidation());
//        container.add(tReferenceNumber);

       // f.add(fNameInput);

        lNameLabel = new JLabel("Last Name");
        lNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        lNameLabel.setSize(120, 30);
        lNameLabel.setLocation(405, 305);
        //container.add(referenceNumber);
      //  f.add(lNameLabel);

        lNameInput = new JTextField();
        lNameInput.setFont(new Font("Arial", Font.PLAIN, 15));
        lNameInput.setSize(90, 30);
        lNameInput.setLocation(630, 305);
        //tReferenceNumber.setInputVerifier(new RefNumValidation());
//        container.add(tReferenceNumber);
        lNameInput.setFocusable(true);
       // f.add(lNameInput);
        f.setFocusable(true);
        // Frame Visible = true
        f.setVisible(true);
    }


}