package com.hth.id_card.user_interface.printer;

import com.hth.images.HTH_Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class IDRecord extends JFrame {
    private JTextField searchField;
    private JLabel searchLbl;
    private TableModel model;
    private JTable table;
    private TableRowSorter sorter;
    private JScrollPane jsp;
    String okKey = "okBtn";
    String[] columnNames;
    String[][] rowData;

    public JTable idRecord(String[] columnNames, String[][] rowData, boolean buttonShow, boolean hideFrame) {
        this.columnNames = columnNames;
        this.rowData = rowData;
        setTitle("ID Record");
        setBackground(Color.WHITE);
        searchField = new JTextField(15);
        searchLbl = new JLabel("Search");
        searchLbl.setLayout(new BoxLayout(searchLbl,BoxLayout.X_AXIS));
        model = new DefaultTableModel(rowData, columnNames);
        sorter = new TableRowSorter<>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        setLayout(new FlowLayout(FlowLayout.CENTER));
        jsp = new JScrollPane(table);
        jsp.setPreferredSize(new Dimension(780, 450));
        add(searchLbl);
        add(searchField);
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
                    sorter.setRowFilter(RowFilter.regexFilter(str));
                }
            }
        });
        setBounds(620, 340, 880, 550);
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
