package com.hth.report;

import com.hth.crmlog.beans.CRMLOGS;
import com.hth.id_card.user_interface.*;
import com.hth.images.HTH_Image;
import com.toedter.calendar.JDateChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Report extends JFrame {
    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 15);
    private Container c;
    private JDateChooser pickerFrom;
    private JDateChooser pickerTo;
    private JComboBox dateDropdown;
    private JComboBox providerDropdown;
    private JComboBox serviceTypeDropdown;
    private JComboBox exclusionDropdown;
    private JLayeredPane contentScreen;
    private JTextField cptText;
    private JTextField providerText;
    private JTextField serviceText;
    private JTextField exclusionCodeText;
    private JButton searchData;
    private JPanel functionPanel;
    private JPanel functionKeyPanel;
    private JLabel titleLabel;
    private JPanel mainPanel;
    String okKey = "okBtn";
    private String dateList[] = {"Date Of Service", "Process date", "Receieve Date"};
    private String queryList[] = {"Or", "And"};

    public Report(String name) {
        setTitle("Report Logs");
        setBounds(355, 140, 1180, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.white);
        setResizable(false);
        c = getContentPane();
        c.setLayout(null);

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentScreen = new JLayeredPane();
        contentScreen.setOpaque(false);
        contentPanel.add(contentScreen, BorderLayout.CENTER);
        //contentPanel.add(controlPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        try {
            setIconImage(ImageIO.read(HTH_Image.getImageURL("hth_block.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setHeaderPanel();
        c.add(mainPanel, BorderLayout.NORTH);

        setSideBar();
        c.add(functionPanel, BorderLayout.WEST);

        setBorder();

        queryField();
        this.getContentPane().setBackground(Color.WHITE);

        setVisible(true);
    }

    public Report() {
        c.addMouseListener(new Report.PromptMouseListener());
        //new Report("hello");
    }

    private void setBorder() {
        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        // borderPanel.setBackground(Color.WHITE);
        borderPanel.setBounds(230, 260, 800, 400);
        borderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Report Data",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        add(borderPanel);
    }

    public void queryField() {
        dateDropdown = new JComboBox(dateList);
        dateDropdown.setFont(new Font("Arial", Font.PLAIN, 15));
        dateDropdown.setSize(200, 30);
        dateDropdown.setLocation(250, 340);
        dateDropdown.setForeground(new Color(79, 145, 200));
        dateDropdown.setBackground(Color.white);
        //select.addActionListener(providerS);
        c.add(dateDropdown);

        JLabel startLabel = new JLabel("Enter Start Date");
        startLabel.setSize(200, 30);
        startLabel.setLocation(500, 300);
        startLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(startLabel);

        pickerFrom = new JDateChooser();
        pickerFrom.setDateFormatString("MM/dd/yyyy");
        pickerFrom.setDate(Calendar.getInstance().getTime());
        //pickerFrom.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        pickerFrom.setSize(200, 30);
        pickerFrom.setLocation(500, 340);
        pickerFrom.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(pickerFrom);

        JLabel endLabel = new JLabel("Enter End Date");
        endLabel.setSize(200, 30);
        endLabel.setLocation(750, 300);
        endLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(endLabel);

        pickerTo = new JDateChooser();
        pickerTo.setDate(Calendar.getInstance().getTime());
        //pickerTo.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        pickerTo.setDateFormatString("MM/dd/yyyy");
        pickerTo.setSize(200, 30);
        pickerTo.setLocation(750, 340);
        pickerTo.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(pickerTo);

        JLabel cptLabel = new JLabel("CPT Code");
        cptLabel.setSize(200, 30);
        cptLabel.setLocation(250, 400);
        cptLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(cptLabel);

        cptText = new HTH_TextField(100, HTH_FONT);
        cptText.setForeground(new Color(0, 0, 150));
        cptText.setFont(new Font("Arial", Font.PLAIN, 18));
        cptText.setSize(200, 30);
        cptText.setLocation(250, 430);
        //((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
        c.add(cptText);

        providerDropdown = new JComboBox(queryList);
        providerDropdown.setFont(new Font("Arial", Font.PLAIN, 15));
        providerDropdown.setSize(60, 30);
        providerDropdown.setLocation(500, 430);
        providerDropdown.setForeground(new Color(79, 145, 200));
        providerDropdown.setBackground(Color.white);
        //select.addActionListener(providerS);
        c.add(providerDropdown);

        JLabel providerLabel = new JLabel("Provider Name");
        providerLabel.setSize(200, 30);
        providerLabel.setLocation(610, 400);
        providerLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(providerLabel);

        providerText = new HTH_TextField(100, HTH_FONT);
        providerText.setForeground(new Color(0, 0, 150));
        providerText.setFont(new Font("Arial", Font.PLAIN, 18));
        providerText.setSize(200, 30);
        providerText.setLocation(610, 430);
        //((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
        c.add(providerText);

        serviceTypeDropdown = new JComboBox(queryList);
        serviceTypeDropdown.setFont(new Font("Arial", Font.PLAIN, 15));
        serviceTypeDropdown.setSize(60, 30);
        serviceTypeDropdown.setLocation(860, 430);
        serviceTypeDropdown.setForeground(new Color(79, 145, 200));
        serviceTypeDropdown.setBackground(Color.white);
        //select.addActionListener(providerS);
        c.add(serviceTypeDropdown);

        JLabel serviceLabel = new JLabel("Service Type");
        serviceLabel.setSize(200, 30);
        serviceLabel.setLocation(250, 490);
        serviceLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(serviceLabel);

        serviceText = new HTH_TextField(100, HTH_FONT);
        serviceText.setForeground(new Color(0, 0, 150));
        serviceText.setFont(new Font("Arial", Font.PLAIN, 18));
        serviceText.setSize(200, 30);
        serviceText.setLocation(250, 520);
        //((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
        c.add(serviceText);

        exclusionDropdown = new JComboBox(queryList);
        exclusionDropdown.setFont(new Font("Arial", Font.PLAIN, 15));
        exclusionDropdown.setSize(60, 30);
        exclusionDropdown.setLocation(500, 520);
        exclusionDropdown.setForeground(new Color(79, 145, 200));
        exclusionDropdown.setBackground(Color.white);
        //select.addActionListener(providerS);
        c.add(exclusionDropdown);

        JLabel exclusionCodeLabel = new JLabel("Exclusion Code");
        exclusionCodeLabel.setSize(200, 30);
        exclusionCodeLabel.setLocation(610, 490);
        exclusionCodeLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        c.add(exclusionCodeLabel);

        exclusionCodeText = new HTH_TextField(100, HTH_FONT);
        exclusionCodeText.setForeground(new Color(0, 0, 150));
        exclusionCodeText.setFont(new Font("Arial", Font.PLAIN, 18));
        exclusionCodeText.setSize(200, 30);
        exclusionCodeText.setLocation(610, 520);
        //((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
        c.add(exclusionCodeText);

        searchData = new HTH_ControlButton("OK");
        searchData.setFont(new Font("Arial", Font.PLAIN, 15));
        searchData.setSize(100, 27);
        searchData.setLocation(900, 600);
        searchData.addMouseListener(new Report.PromptMouseListener());
        searchData.addActionListener(searchDatabase);
        //claimListBTN2.addActionListener(claimList);
        c.add(searchData);
    }

    Action searchDatabase = new AbstractAction(okKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e) {
            if(checkDate()) {
                Date from = pickerFrom.getDate();
                DateFormat fromFormat = new SimpleDateFormat("yyMMdd");
                String fromDate = fromFormat.format(from);

                Date to = pickerTo.getDate();
                DateFormat toFormat = new SimpleDateFormat("yyMMdd");
                String toDate = toFormat.format(to);

                String cpt = cptText.getText().toUpperCase().trim();
                String provider = providerText.getText().toUpperCase().trim();
                String serviceType = serviceText.getText().toUpperCase().trim();
                String exclusionCode = exclusionCodeText.getText().toUpperCase().trim();
                String providerQuery = "or";
                if (providerDropdown.getSelectedIndex() == 1) {
                    providerQuery = "and";
                }

                String serviceQuery = "or";
                if (serviceTypeDropdown.getSelectedIndex() == 1) {
                    serviceQuery = "and";
                }

                String exclusionQuery = "or";
                if (exclusionDropdown.getSelectedIndex() == 1) {
                    exclusionQuery = "and";
                }
                List<String[]> data = null;
                try {
                    data = CRMLOGS.reportData(fromDate, toDate, cpt, provider, serviceType, exclusionCode, providerQuery, serviceQuery, exclusionQuery);
                    if (data.size() == 0) {
                        showDialog("Data Not Found");
                        //JOptionPane.showMessageDialog(new JLabel(), "No data found");
                    } else {
                        setVisible(false);
                        new Report("harsh");
                        showDataFunction(data);
                        searchData.addMouseListener(new Report.PromptMouseListener());

                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    };

    public boolean checkDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        Date from = pickerFrom.getDate();
        DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = fromFormat.format(from);

        Date to = pickerTo.getDate();
        DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
        String toDate = toFormat.format(to);

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date cd = null;  // current date
        Date fromDateFormat = null;
        Date toDateFormat = null;

        try {
            cd = sdf.parse(dtf.format(now));
            fromDateFormat = sdf.parse(fromDate);
            toDateFormat = sdf.parse(toDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long diff = cd.getTime() - fromDateFormat.getTime();
        int diffDays = (int) (diff / (24 * 1000 * 60 * 60));
        if (diffDays < 0) {
            showDialog("Invalid Start date");
            return false;
        }

        long diffTo = cd.getTime() - toDateFormat.getTime();
        int diffDaysTo = (int) (diffTo / (24 * 1000 * 60 * 60));
        if (diffDaysTo < 0) {
            showDialog("Invalid End date");
            return false;
        }

        long diffToFrom = fromDateFormat.getTime() - toDateFormat.getTime();
        int diffDaysToFrom = (int) (diffToFrom / (24 * 1000 * 60 * 60));
        System.out.println(diffDaysToFrom);
        if (diffDaysToFrom > 0) {
            showDialog("Invalid End date");
            return false;
        }

        return true;
    }

    private void showDataFunction(List<String[]> showData) {
        try {
            String[][] data = new String[showData.size()][];
            for (int idx = 0; idx < showData.size(); idx++) {
                String[] result = new String[showData.get(idx).length];
                result = showData.get(idx);
                Date d = new SimpleDateFormat("yyMMdd").parse(result[3]);
                SimpleDateFormat d2 = new SimpleDateFormat("MM/dd/yy");
                result[3] = d2.format(d).toString();

                data[idx] = result;
            }
            ReportData rd = ReportData.singleton();
            rd.setReportData(data);
            String[] columnNames = {"PROCEDURE","CLAIM_NUMBER", "LINE_NO", "DATE_OF_SERVICE", "DIVISION", "POLICY_ID", "PATIENT_NAME", "DEPENDENT_CODE", "COVERAGE", "AMOUNT_CLAIMED", "DAMTEX", "TOTAL_PAID", "DEXCD", "13", "HICD1", "HICD2", "HICD3", "HICD4", "HICD5", "HICD6", "HICD7", "HICD8", "HICD9", "HICD10", "TYPE_OF_SERVICE", "PROVIDER_ID", "PROVIDER_NAME"};
            ReportTable rt = new ReportTable();
            rt.reportTable(columnNames, data, true, false, true);

        } catch (Exception e) {

        }
    }

    public void setHeaderPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setSize(1180, 148);
        mainPanel.setLocation(0, 0);
        //mainPanel.setPreferredSize(new Dimension(0, 140));

        JPanel logo = new JPanel(new BorderLayout());
        logo.setLayout(new FlowLayout(FlowLayout.LEADING));
        //logo.setPreferredSize(new Dimension(0, 105));
        logo.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel(new ImageIcon(HTH_Image.getImageURL("hth_logo.png")));
        logo.add(logoLabel);

        JPanel title = new JPanel();
        title.setLayout(new BorderLayout());
        //title.setPreferredSize(new Dimension(0, 35));
        title.setBackground(new Color(82, 144, 202));

        titleLabel = new JLabel("Customer Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        title.add(titleLabel, BorderLayout.SOUTH);

        mainPanel.add(logo, BorderLayout.NORTH);
        mainPanel.add(title, BorderLayout.SOUTH);
    }

    public void setSideBar() {
        functionPanel = new JPanel();
        functionPanel.setLayout(new BorderLayout());
        functionPanel.setBackground(new Color(83, 89, 105));
        functionPanel.setPreferredSize(new Dimension(200, 0));
        functionPanel.setLocation(0, 148);
        functionPanel.setSize(220, 800);

        JPanel dummyPanel = new JPanel();
        dummyPanel.setOpaque(false);
        //dummyPanel.setPreferredSize(new Dimension(0, 80));
        functionPanel.add(dummyPanel, BorderLayout.NORTH);
        functionKeyPanel = new JPanel();
        functionKeyPanel.setOpaque(false);
        functionKeyPanel.setLayout(new BoxLayout(functionKeyPanel, BoxLayout.Y_AXIS));
        functionPanel.add(functionKeyPanel, BorderLayout.CENTER);
    }

    public void showDialog(String errMsg) {
        HTH_Dialog.showMessageDialog(new HTH_Frame("") {
            @Override
            protected void addFunctionKeys(HTH_FunctionButton... keyList) {
                super.addFunctionKeys(keyList);
            }
        }, "", errMsg, new
                Dimension(300, 150), JOptionPane.YES_OPTION);
    }

    public void set(){
        c.addMouseListener(new Report.PromptMouseListener());
    }

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
