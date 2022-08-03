package com.hth.newCustomerServiceFeature.report;

import com.hth.crmlog.beans.CRMLOGS;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_TextField;
import com.hth.images.HTH_Image;
import org.jdesktop.swingx.JXDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Report extends JFrame {
    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 15);
    private Container c;
    private JXDatePicker pickerFrom;
    private JXDatePicker pickerTo;
    private JComboBox dateDropdown;
    private JComboBox providerDropdown;
    private JComboBox serviceTypeDropdown;
    private JComboBox exclusionDropdown;
    private JLayeredPane contentScreen, promptScreen;
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
    private String queryList[] = {"OR", "And"};

    public Report() {
        setTitle("Report Logs");
        setBounds(355, 140, 1080, 700);
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
        startLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        c.add(startLabel);

        pickerFrom = new JXDatePicker();
        pickerFrom.setDate(Calendar.getInstance().getTime());
        pickerFrom.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        pickerFrom.setSize(200, 30);
        pickerFrom.setLocation(500, 340);
        c.add(pickerFrom);

        JLabel endLabel = new JLabel("Enter End Date");
        endLabel.setSize(200, 30);
        endLabel.setLocation(750, 300);
        endLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        c.add(endLabel);

        pickerTo = new JXDatePicker();
        pickerTo.setDate(Calendar.getInstance().getTime());
        pickerTo.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
        pickerTo.setSize(200, 30);
        pickerTo.setLocation(750, 340);
        c.add(pickerTo);

        JLabel cptLabel = new JLabel("CPT Code");
        cptLabel.setSize(200, 30);
        cptLabel.setLocation(250, 400);
        cptLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        c.add(cptLabel);

        cptText = new HTH_TextField(100, HTH_FONT);
        cptText.setForeground(new Color(0, 0, 150));
        cptText.setFont(new Font("Arial", Font.PLAIN, 15));
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
        providerLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        c.add(providerLabel);

        providerText = new HTH_TextField(100, HTH_FONT);
        providerText.setForeground(new Color(0, 0, 150));
        providerText.setFont(new Font("Arial", Font.PLAIN, 15));
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
        serviceLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        c.add(serviceLabel);

        serviceText = new HTH_TextField(100, HTH_FONT);
        serviceText.setForeground(new Color(0, 0, 150));
        serviceText.setFont(new Font("Arial", Font.PLAIN, 15));
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
        exclusionCodeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        c.add(exclusionCodeLabel);

        exclusionCodeText = new HTH_TextField(100, HTH_FONT);
        exclusionCodeText.setForeground(new Color(0, 0, 150));
        exclusionCodeText.setFont(new Font("Arial", Font.PLAIN, 15));
        exclusionCodeText.setSize(200, 30);
        exclusionCodeText.setLocation(610, 520);
        //((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
        c.add(exclusionCodeText);

        searchData = new HTH_ControlButton("OK");
        searchData.setFont(new Font("Arial", Font.PLAIN, 15));
        searchData.setSize(100, 27);
        searchData.setLocation(900, 600);
        searchData.addActionListener(searchDatabase);
        //claimListBTN2.addActionListener(claimList);
        c.add(searchData);
    }

    Action searchDatabase = new AbstractAction(okKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e) {
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
                    JOptionPane.showMessageDialog(new JLabel(), "No data found");
                } else {
                    showDataFunction(data);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    };
    private void showDataFunction(List<String[]> showData) {
        try {
            String[][] data = new String[showData.size()][];
            for (int idx = 0; idx < showData.size(); idx++) {
                String[] result = new String[showData.get(idx).length];
                result = showData.get(idx);
                //result[1] = "abc,xyz";
                Date d = new SimpleDateFormat("yyMMdd").parse(result[3]);
                SimpleDateFormat d2 = new SimpleDateFormat("MM/dd/yy");
                result[3] = d2.format(d).toString();
//                for(int i=0 ;i<result.length;i++){
//                    if(result[i].contains(",")){
//                        result[i] = "\""+result[i]+"\"";
//                    }
//                }
                data[idx] = result;
            }
            ReportData rd = ReportData.singleton();
            rd.setReportData(data);
            String[] columnNames = {"CLAIM_NUMBER", "LINE_NO", "3", "DATE_OF_SERVICE", "DIVISION", "POLICY_ID", "PATIENT_NAME", "DEPENDENT_CODE", "COVERAGE", "AMOUNT_CLAIMED", "DAMTEX", "TOTAL_PAID", "DEXCD", "13", "HICD1", "HICD2", "HICD3", "HICD4", "HICD5", "HICD6", "HICD7", "HICD8", "HICD9", "HICD10", "TYPE_OF_SERVICE", "PROVIDER_ID", "PROVIDER_NAME"};
            new ReportTable(columnNames, data);

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
}
