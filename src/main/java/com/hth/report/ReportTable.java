package com.hth.report;

import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.images.HTH_Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ReportTable extends JFrame {
    private JTextField searchField;
    private JLabel searchLbl;
    private TableModel model;
    private JTable table;
    private TableRowSorter sorter;
    private JScrollPane jsp;

    String okKey = "okBtn";
    String[] columnNames;
    String[][] rowData;

    public ReportTable(String[] columnNames, String[][] rowData) {
        this.columnNames = columnNames;
        this.rowData = rowData;
        setTitle("Report Data");
        searchField = new JTextField(15);
        searchLbl = new JLabel("Search");
        JButton download = new HTH_ControlButton("Download");
        model = new DefaultTableModel(rowData, columnNames);
        sorter = new TableRowSorter<>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        jsp = new JScrollPane(table);
        jsp.setPreferredSize(new Dimension(1500, 1000));
        download.addActionListener(searchDatabase);
        add(searchLbl);
        add(searchField);
        add(download);
        add(jsp);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchField.getText().toUpperCase());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchField.getText().toUpperCase());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchField.getText().toUpperCase());
            }

            public void search(String str) {
                if (str.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter(str));
                }
            }
        });
        setSize(1600, 1180);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        try {
            setIconImage(ImageIO.read(HTH_Image.getImageURL("hth_block.png")));
        }catch (Exception e){

        }
        setVisible(true);
    }

    Action searchDatabase = new AbstractAction(okKey) {
        private static final long serialVersionUID = 10110L;
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            String[] args = new String[0];
            Helper h = new Helper();
            h.saveFile(args);
        }
    };
}
