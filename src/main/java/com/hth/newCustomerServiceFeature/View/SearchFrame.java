package com.hth.newCustomerServiceFeature.View;

import com.hth.newCustomerServiceFeature.Repository.Repository;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;

public class SearchFrame extends JFrame {
    private JTextField fNameTF;
    private JTextField lNameTF;
    private String fNameSearchString = "-1";
    private String lNameSearchString = "-1";
    private JLabel fNameSearchLb;
    private JLabel lNameSearchLb;
    private TableModel model;
    private JTable table;
    private TableRowSorter sorter;
    private JScrollPane jsp;

    String[] columnNames = {"Last Name", "First Name", "Employ ID"};
    String[][] insureData;
    private boolean isAlreadyClicked;

    public SearchFrame(String[][] insureData) {
        this.insureData = insureData;
        setTitle("Name Search From Database");
        //---- fix static naming
        initComponents();
        addTableListner();

        // table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        //table.getCellEditor().cancelCellEditing();
        add(lNameSearchLb);
        add(lNameTF);
        add(fNameSearchLb);
        add(fNameTF);
        add(jsp);

        fNameTF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fNameSearchString = fNameTF.getText();
                search(fNameSearchString);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fNameSearchString = fNameTF.getText();
                search(fNameSearchString);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fNameSearchString = fNameTF.getText();
                search(fNameSearchString);
            }

            public void search(String fName) {
                fName = fName.trim().toUpperCase();
                if (fName.length() == 0 && lNameTF.getText().length() == 0) {
                    lNameSearchString = "-1";
                    fNameSearchString = "-1";
                    sorter.setRowFilter(null);
                } else {

                    ArrayList<RowFilter<Object, Object>> andFilters = new ArrayList<RowFilter<Object, Object>>();
                    andFilters.add(RowFilter.regexFilter(fName, 1));
                    andFilters.add(RowFilter.regexFilter(lNameTF.getText().toUpperCase(), 0));
                    sorter.setRowFilter(RowFilter.andFilter(andFilters));


                }
            }
        });
        lNameTF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lNameSearchString = lNameTF.getText();
                search(lNameSearchString);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lNameSearchString = lNameTF.getText();
                search(lNameSearchString);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lNameSearchString = lNameTF.getText();
                search(lNameSearchString);
            }

            public void search(String lName) {
                lName = lName.trim().toUpperCase();
                if (lName.length() == 0 && fNameTF.getText().length() == 0) {
                    lNameSearchString = "-1";
                    fNameSearchString = "-1";
                    sorter.setRowFilter(null);
                } else {
                    ArrayList<RowFilter<Object, Object>> andFilters = new ArrayList<RowFilter<Object, Object>>();
                    andFilters.add(RowFilter.regexFilter(lName, 0));
                    andFilters.add(RowFilter.regexFilter(fNameTF.getText().toUpperCase(), 1));
                    sorter.setRowFilter(RowFilter.andFilter(andFilters));
                }
//

            }

        });

        setSize(475, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void addTableListner() {
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                System.out.println("row: " + row);
                Object lName = table.getValueAt(row, 0);
                Object fName = table.getValueAt(row, 1);
                Object ssn = table.getValueAt(row, 2);
                System.out.println("lastName: " + lName + " ,firstName: " + fName + " ,SSN: " + ssn);

                dispose();

//                MyFrame.getInstance().showUpdatedContent(lName.toString().trim(),fName.toString().trim(),ssn.toString().trim());
                Util.inputMap.put("fName",fName.toString().trim());
                Util.inputMap.put("lName",lName.toString().trim());
                Util.inputMap.put("ssn",ssn.toString().trim());
                MyFrame oldFrame = MyFrame.getInstance();
                MyFrame updatedFrame = new MyFrame(Util.inputMap);
                oldFrame.dispose();


            }

            @Override
            public void mousePressed(MouseEvent e) {

            }
            @Override
            public void mouseReleased(MouseEvent e) {

            }
            @Override
            public void mouseEntered(MouseEvent e) {

            }
            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void initComponents() {
        fNameTF = new JTextField(10);
        fNameSearchLb = new JLabel("First Name");
        lNameTF = new JTextField(10);
        lNameSearchLb = new JLabel("Last Name");
        model = new DefaultTableModel(insureData, columnNames);
        sorter = new TableRowSorter<>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        jsp = new JScrollPane(table);
    }
}

