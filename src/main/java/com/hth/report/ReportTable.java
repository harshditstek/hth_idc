package com.hth.report;

import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.images.HTH_Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ReportTable extends JFrame {
    private JTextField searchField;
    private JLabel searchLbl;
    private TableModel model;
    private JTable table;
    private JButton download;
    private TableRowSorter sorter;
    private JScrollPane jsp;
    String okKey = "okBtn";
    String[] columnNames;
    String[][] rowData;

    public JTable reportTable(String[] columnNames, String[][] rowData, boolean buttonShow, boolean hideFrame, boolean searchString) {
        this.columnNames = columnNames;
        this.rowData = rowData;
        setTitle("Report Data");
        searchField = new JTextField(15);
        searchLbl = new JLabel("Search");
        download = new HTH_ControlButton("Download");
        model = new DefaultTableModel(rowData, columnNames);
        sorter = new TableRowSorter<>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        jsp = new JScrollPane(table);
        jsp.setPreferredSize(new Dimension(1500, 900));
        download.addActionListener(searchDatabase);
        //download.addMouseListener(new ReportTable.PromptMouseListener());
        add(searchLbl);
        add(searchField);
        if (buttonShow) {
            add(download);
        }
        add(jsp);

        ListSelectionModel listModel = table.getSelectionModel();
        listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (hideFrame) {
            listModel.addListSelectionListener(auditLogList);
        }


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
                    if(searchString){
                        sorter.setRowFilter(RowFilter.regexFilter(str));
                    }else{
                        sorter.setRowFilter(RowFilter.regexFilter(str, 0,7));
                    }

                }
            }
        });
        setSize(1600, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        try {
            setIconImage(ImageIO.read(HTH_Image.getImageURL("hth_block.png")));
        } catch (Exception e) {

        }
        setVisible(true);
        return table;
    }

    ListSelectionListener auditLogList = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int[] sel;
            Object value;
            if (!e.getValueIsAdjusting()) {
                sel = table.getSelectedRows();
                if (sel.length > 0) {
                    setVisible(false);
                }
            }
        }
    };

    Action searchDatabase = new AbstractAction(okKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e){
            setVisible(false);
            String[] args = new String[0];
            Helper h = new Helper();
           // download.addMouseListener(new ReportTable.PromptMouseListener());
            h.saveFile(args);
           // download.addMouseListener(new ReportTable.PromptMouseListener());
        }
    };

    private class PromptMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
