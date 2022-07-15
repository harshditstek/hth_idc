package com.hth.newCustomerServiceFeature.crmLogsView;

import com.hth.backend.beans.CRMLOGS;
import com.hth.backend.iSeries;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_PromptButton;
import com.hth.images.HTH_Image;
import com.hth.newCustomerServiceFeature.CRMLogsFiles;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2;
import com.hth.newCustomerServiceFeature.Repository.Repository;
import com.hth.newCustomerServiceFeature.UppercaseDocumentFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.List;

public class CrmLogFrame extends JFrame implements ActionListener, KeyListener {

    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);

    // Components of the Form
    private Container c;

    private JFrame auditLog;
    private JFrame searchNameFrame;
    // Table
    private JTable logTable;
    private JTable searchNameTable;

    private JLabel title;
    private JLabel referenceNumber;
    private JTextField tReferenceNumber;
    private JButton bReferenceNumber;
    private JLabel provider;
    private JComboBox tProvider;
    private JLabel phoneNum;
    private JTextField tPhoneNum;
    private MaskFormatter phoneFormatter;
    private JLabel name;
    private JTextField tFName;
    private JTextField tLName;
    private JLabel company;
    private JTextField tCompanyName;
    private JLabel customerGroup;
    private JTextField tCustomerGroup;
    private JLabel ssn;
    private JTextField tssn;
    private JLabel claim;
    private JTextField tClaim;
    private JLabel phoneNotes;
    private JTextField tPhoneNotes;
    private JLabel callNotes;
    private JTextArea tCallNotes;
    private JButton exit;
    private JButton ok;
    private JPanel functionPanel;
    private JPanel functionKeyPanel;
    private JLabel titleLabel;
    private JPanel mainPanel;
    String exitKey = "exitBtn";
    String okKey = "okBtn";
    String listKey = "listBtn";
    String providerKey = "providerCombo";

    String submitKey = "submitBtn";
    private HTH_PromptButton searchName;
    private String providerOrMember[] = {"Provider", "Member"};
    DocumentFilter filter = new UppercaseDocumentFilter();

    public CrmLogFrame() {
        setTitle("Crm Log Files");
        setBounds(355, 140, 1180, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.white);
        setResizable(false);
        c = getContentPane();
        c.setLayout(null);
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
        setReference();
        setProvider();
        setPhone();
        setName();
        setCompanyName();
        setCustomerGroup();
        setCustomerSSN();
        setClaim();
        //setName();
        name = new JLabel("<html><nobr>First/Last Name <font color='#ffbebe'>*</font></nobr></html>");
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setSize(200, 30);
        name.setLocation(250, 420);
        //name.getText();
        c.add(name);

        tFName = new JTextField();
        tFName.setFont(new Font("Arial", Font.PLAIN, 15));
        tFName.setSize(200, 30);
        tFName.setLocation(550, 420);
        ((AbstractDocument) tFName.getDocument()).setDocumentFilter(filter);
        c.add(tFName);

        tLName = new JTextField();
        tLName.setFont(new Font("Arial", Font.PLAIN, 15));
        tLName.setSize(200, 30);
        tLName.setLocation(751, 420);
        ((AbstractDocument) tLName.getDocument()).setDocumentFilter(filter);
        c.add(tLName);

        searchName = new HTH_PromptButton();
        searchName.setFont(new Font("Arial", Font.PLAIN, 18));
        searchName.setSize(21, 21);
        searchName.setLocation(955, 423);
        searchName.addActionListener(new SearchByName());
        add(searchName);
        setNote();
        setButton();
        this.getContentPane().setBackground(Color.WHITE);

        setVisible(true);
    }

    public void setReference() {
        referenceNumber = new JLabel("Refrence Number");
        referenceNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        referenceNumber.setSize(200, 30);
        referenceNumber.setLocation(250, 300);
        c.add(referenceNumber);

        tReferenceNumber = new JTextField();
        tReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 15));
        tReferenceNumber.setSize(100, 30);
        tReferenceNumber.setLocation(550, 300);
        //tReferenceNumber.setEditable(false);
        c.add(tReferenceNumber);

        bReferenceNumber = new HTH_ControlButton("Generate");
        bReferenceNumber.setSize(75, 28);
        bReferenceNumber.setLocation(660, 300);
        bReferenceNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance("");
                Integer reference = repo.generateRefNumTest();
                tReferenceNumber.setText(reference.toString());
            }
        });
        add(bReferenceNumber);
    }

    public void setProvider() {
        provider = new JLabel("Provider Or Member");
        provider.setFont(new Font("Arial", Font.PLAIN, 18));
        provider.setSize(200, 30);
        provider.setLocation(250, 340);
        c.add(provider);

        tProvider = new JComboBox(providerOrMember);
        tProvider.setFont(new Font("Arial", Font.PLAIN, 15));
        tProvider.setSize(100, 30);
        tProvider.setLocation(550, 340);
        tProvider.setForeground(new Color(79, 145, 200));
        tProvider.setBackground(Color.white);
        tProvider.addActionListener(providerS);
        c.add(tProvider);
    }

    public void setPhone() {
        phoneNum = new JLabel("<html><nobr>Phone Number <font color='#ffbebe'>*</font></nobr></html>");
        phoneNum.setFont(new Font("Arial", Font.PLAIN, 18));
        phoneNum.setSize(200, 30);
        phoneNum.setLocation(250, 380);
        c.add(phoneNum);

        try {
            phoneFormatter = new MaskFormatter("(###) ###-####");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tPhoneNum = new JFormattedTextField(phoneFormatter);
        tPhoneNum.setFont(new Font("Arial", Font.PLAIN, 15));
        tPhoneNum.setSize(130, 30);
        tPhoneNum.setLocation(550, 380);
        tPhoneNum.addKeyListener(this);
        c.add(tPhoneNum);
    }

    public void setName() {

    }

    public void setCompanyName() {
        company = new JLabel("Company Name");
        company.setFont(new Font("Arial", Font.PLAIN, 18));
        company.setSize(200, 30);
        company.setLocation(250, 460);
        c.add(company);

        tCompanyName = new JTextField();
        tCompanyName.setFont(new Font("Arial", Font.PLAIN, 15));
        tCompanyName.setSize(190, 30);
        tCompanyName.setLocation(550, 460);
        ((AbstractDocument) tCompanyName.getDocument()).setDocumentFilter(filter);
        c.add(tCompanyName);
    }
    public void setCustomerGroup() {
        customerGroup = new JLabel("Customer Group");
        customerGroup.setFont(new Font("Arial", Font.PLAIN, 18));
        customerGroup.setSize(200, 30);
        customerGroup.setLocation(250, 500);
        c.add(customerGroup);

        tCustomerGroup = new JTextField();
        tCustomerGroup.setFont(new Font("Arial", Font.PLAIN, 15));
        tCustomerGroup.setSize(100, 30);
        tCustomerGroup.setLocation(550, 500);
        ((AbstractDocument) tCustomerGroup.getDocument()).setDocumentFilter(filter);
        c.add(tCustomerGroup);
    }

    public void setCustomerSSN() {
        ssn = new JLabel("SSN/MemberID");
        ssn.setFont(new Font("Arial", Font.PLAIN, 18));
        ssn.setSize(200, 30);
        ssn.setLocation(250, 540);
        c.add(ssn);

        tssn = new JTextField();
        tssn.setFont(new Font("Arial", Font.PLAIN, 15));
        tssn.setSize(190, 30);
        tssn.setLocation(550, 540);
        c.add(tssn);
    }

    public void setClaim() {
        claim = new JLabel("Claim Number");
        claim.setFont(new Font("Arial", Font.PLAIN, 18));
        claim.setSize(200, 30);
        claim.setLocation(250, 580);
        c.add(claim);

        tClaim = new JTextField();
        tClaim.setFont(new Font("Arial", Font.PLAIN, 15));
        tClaim.setSize(100, 30);
        tClaim.setLocation(550, 580);
        ((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
        c.add(tClaim);
    }

    public void setNote() {
        callNotes = new JLabel("Notes From The  Call");
        callNotes.setFont(new Font("Arial", Font.PLAIN, 18));
        callNotes.setSize(200, 30);
        callNotes.setLocation(250, 620);
        c.add(callNotes);

        tCallNotes = new JTextArea();
        tCallNotes.setFont(new Font("Arial", Font.PLAIN, 15));
        tCallNotes.setSize(410, 60);
        //tCallNotes.setLocation(550, 620);
        tCallNotes.setLocation(550, 620);
        //tCallNotes.setBackground(Color.lightGray);
        tCallNotes.setBackground(new Color(235, 241, 246));
        //tProvider.setBackground(Color.white);
        c.add(tCallNotes);

    }

    public void setButton() {
        exit = new HTH_ControlButton("Exit");
        exit.setToolTipText("F3=Exit");
        exit.addActionListener(exitAction);
        exit.setFont(new Font("Arial", Font.PLAIN, 14));
        exit.setSize(75, 27);
        exit.setLocation(815, 720);
        exit.addActionListener(exitAction);
        c.add(exit);

        ok = new HTH_ControlButton("OK");
        ok.setFont(new Font("Arial", Font.PLAIN, 15));
        ok.setSize(75, 27);
        ok.setLocation(900, 720);
        ok.addActionListener(submitAction);
        c.add(ok);

    }

    Action exitAction = new AbstractAction(exitKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    Action submitAction = new AbstractAction(okKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e) {
            Repository repo = Repository.getInstance("");
            //if (checkData()) {

            System.out.println("submit button clicked");
            CrmLogRecord2 record = getData();
            CRMLOGS.insertCrmLogs(record);
            //repo.insertIntoCrmLog(record);

            //String loadCardCL = "CALL PDLIB.CRMLOG PARM(TRT)";

            //iSeries.executeCL(loadCardCL);
            close();
        }

    };

    Action claimList = new AbstractAction(listKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e) {
            showAuditLog();
        }
    };

    Action providerS = new AbstractAction(providerKey) {
        public void actionPerformed(ActionEvent e) {
            tProvider = (JComboBox) e.getSource();
            String provider = (String) tProvider.getSelectedItem();
            System.out.println(provider);
            if (provider.trim().equals("Member")) {
                company.setText("<html><nobr>Company Name <font color='#ffbebe'>*</font></nobr></html>");
            }else{
                company.setText("Company Name");
            }
        }
    };

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        String phone = tPhoneNum.getText().trim();
        String format = gePhoneNum(phone);
        if (format.length() == 10) {
            List<String[]> resultList = CRMLOGS.searchByPhone(format);
            //String[] result;
            tReferenceNumber.setText(resultList.get(0)[0].trim());
            tFName.setText(resultList.get(0)[1].trim());
            tLName.setText(resultList.get(0)[2].trim());
            tCompanyName.setText(resultList.get(0)[3].trim());
            tCustomerGroup.setText(resultList.get(0)[4].trim());
            //tCustomerGroup.setText(resultList.get(0)[5].trim());
            tssn.setText(resultList.get(0)[5].trim());
            tClaim.setText(resultList.get(0)[6].trim());
            tCallNotes.setText(resultList.get(0)[7].trim());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public class SearchByName implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //CRMLOGS.searchByName("HAR");
            if (tLName.getText().length() > 2) {
                nameSearchFromDataBase(tLName.getText().trim().toUpperCase());
            } else if (tFName.getText().length() > 2) {
                nameSearchFromDataBase(tFName.getText().trim().toUpperCase());
            }
        }
    }

    private void close() {
        System.exit(0);
    }

    private CrmLogRecord2 getData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String formattedPhoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        System.out.println("fN:" + fName);
        String lName = tLName.getText().trim().toUpperCase();
        System.out.println("ln:" + lName);
        String company = tCompanyName.getText().trim().toUpperCase();
        String customerGroup = tCustomerGroup.getText().trim().toUpperCase();
        String ssn = tssn.getText().trim().toUpperCase();
        String claimNum = tClaim.getText().trim().toUpperCase();
        //String timeOfEntry = tEntryTime.getText().trim();
        //String dateOfEntry = tEntryDate.getText().trim();
        //String user = tUser.getText().trim().toUpperCase();
        String note = tCallNotes.getText().trim().toUpperCase();
        //String filler = tFiller.getText().trim().toUpperCase();

        CrmLogRecord2 record = new CrmLogRecord2.Builder()
                .withRefNum(refNum)
                .withClaimType(providerOrMember)
                .withPhoneNum(gePhoneNum(formattedPhoneNum))
                .withFName(fName)
                .withLName(lName)
                .withCompanyName(company)
                .withCustomerGroup(customerGroup)
                .withSSN(ssn)
                .withClaimNum(claimNum)
                //.withTime(timeOfEntry)
                //.withDate(dateOfEntry)
                .withNote(note)
                //.withFiller(filler)
                .withUser(CRMLogsFiles.user)
                .build();

        return record;
    }

    private String gePhoneNum(String formattedPhoneNum) {

        String phone = formattedPhoneNum.replaceAll("\\(", "").replaceAll("\\)", "").
                replaceAll(" ", "").replaceAll("-", "");
        return phone;
    }

    private void setBorder() {
        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        // borderPanel.setBackground(Color.WHITE);
        borderPanel.setBounds(230, 260, 800, 500);
        borderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "CRM Log Files",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        add(borderPanel);
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

        titleLabel = new JLabel("Customer Service ");
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
        functionPanel.setSize(220, 710);

        HTH_ControlButton claimListBTN = new HTH_ControlButton("Audit Log");
        //claimListBTN.setToolTipText("F3=Exit");
        //exitBtn.setBackground(Color.yellow);
        claimListBTN.setSize(220, 32);
        claimListBTN.setLocation(0, 100);
        claimListBTN.addActionListener(claimList);
        claimListBTN.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), listKey);
        claimListBTN.getActionMap().put(listKey, claimList);
        functionPanel.add(claimListBTN);


        JPanel dummyPanel = new JPanel();
        dummyPanel.setOpaque(false);
        //dummyPanel.setPreferredSize(new Dimension(0, 80));
        functionPanel.add(dummyPanel, BorderLayout.NORTH);

        functionKeyPanel = new JPanel();
        functionKeyPanel.setOpaque(false);
        functionKeyPanel.setLayout(new BoxLayout(functionKeyPanel, BoxLayout.Y_AXIS));

        functionPanel.add(functionKeyPanel, BorderLayout.CENTER);
    }

    private void showAuditLog() {
        auditLog = new JFrame();
        auditLog.setBounds(400, 90, 1180, 800);

        // Frame Title
        auditLog.setTitle("Claim List");

        List<String[]> resultList = CRMLOGS.getCrmLogs();
        String[] result;
        String[][] data = new String[resultList.size()][];
        for (int idx = 0; idx < resultList.size(); idx++) {
            result = resultList.get(idx);
            data[idx] = result;
        }
        String[] columnNames = {"REFRENCE#", "Type", "Phone Number", "FName", "LName", "Company Name", "Customer Group", "Customer SSN", "Claim", "Time", "Date", "user", "filler", "Note"};

        logTable = new JTable(data, columnNames);
        logTable.setBounds(30, 40, 200, 300);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(logTable);
        auditLog.add(sp);
        auditLog.setSize(800, 800);
        auditLog.setVisible(true);
    }

    private void nameSearchFromDataBase(String name) {
        String[] result;
        List<String[]> resultList = CRMLOGS.searchByName(name);
        String[] columnNames = {"Last Name", "First Name", "EmployeeID"};
        String[][] data = new String[resultList.size()][];
        for (int idx = 0; idx < resultList.size(); idx++) {
            result = resultList.get(idx);
            data[idx] = result;
        }
        searchNameFrame = new JFrame();
        searchNameFrame.setBounds(400, 90, 1180, 800);

        // Frame Title
        searchNameFrame.setTitle("Name Search From Databse");

        searchNameTable = new JTable(data, columnNames);
        searchNameTable.setBounds(30, 40, 200, 300);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(searchNameTable);
        searchNameFrame.add(sp);
        searchNameFrame.setSize(800, 800);
        searchNameFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

