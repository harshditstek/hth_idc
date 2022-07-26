package com.hth.newCustomerServiceFeature.crmLogsView;

import com.hth.backend.beans.CRMLOGS;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_FunctionButton;
import com.hth.id_card.user_interface.HTH_PromptButton;
import com.hth.id_card.user_interface.HTH_TextField;
import com.hth.images.HTH_Image;
import com.hth.newCustomerServiceFeature.CRMLogsFiles;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord2;
import com.hth.newCustomerServiceFeature.Repository.Repository;
import com.hth.newCustomerServiceFeature.UppercaseDocumentFilter;
import com.hth.util.Insure;
import com.opencsv.CSVWriter;
import org.jdesktop.swingx.JXDatePicker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CrmLogFrame extends JFrame implements ActionListener, KeyListener, ListSelectionListener {

    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);
    private Container c;
    private JXDatePicker pickerFrom;
    private JXDatePicker pickerTo;
    private JFrame auditLog;
    private JFrame showDataF;
    private JFrame searchNameFrame;
    private JTable logTable;
    private JTable showTable;
    private JTable searchNameTable;

    private JLabel title;
    private JLabel referenceNumber;
    private JTextField tReferenceNumber;
    private JButton bReferenceNumber;
    private JLabel provider;
    private JComboBox tProvider;
    private JComboBox select;
    private JLabel phoneNum;
    private JLabel nameValidation;
    private JTextField tPhoneNum;
    private MaskFormatter phoneFormatter;

    private JLabel phoneValidation;
    private JLabel name;
    private JTextField tFName;
    private JTextField tLName;
    private JLabel company;
    private JTextField tCompanyName;
    private JLayeredPane contentScreen, promptScreen;
    private JLabel customerGroup;
    private JTextField tCustomerGroup;
    private JLabel ssn;
    private JTextField tssn;
    private MaskFormatter ssnFormatter;
    private JLabel claim;
    private JTextField tClaim;
    private JTextField startDate;
    private JTextField endDate;
    private JTextField showingSelect;
    private MaskFormatter claimFormatter;
    private JLabel phoneNotes;
    private JTextField tPhoneNotes;
    private JLabel callNotes;
    private JTextArea tCallNotes;
    private JButton exit;
    private JButton ok;
    private JButton searchData;
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
    private String selectList[] = {"Service"};
    DocumentFilter filter = new UppercaseDocumentFilter();

    private JButton claimListBTN;
    private HTH_FunctionButton claimListBTN2;

    Integer reference = 0;
    String referenceS = "";
    boolean checkReference = true;

    private final List<String> keywords;

    public CrmLogFrame() {
        setTitle("Crm Log Files");
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
        setReference();
        setProvider();
        setPhone();

        name = new JLabel("<html><nobr>First/Last Name <font color='#ffbebe'>*</font></nobr></html>");
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setSize(200, 30);
        name.setLocation(250, 420);
        c.add(name);

        tFName = new HTH_TextField(25, HTH_FONT);

        keywords = new ArrayList<String>(5);
        keywords.add("harsh");
        keywords.add("harshit");
        keywords.add("harash");
        Autocomplete autoComplete = new Autocomplete(tFName, keywords);
        tFName.getDocument().addDocumentListener(autoComplete);

        tFName.setFont(new Font("Arial", Font.PLAIN, 15));
        tFName.setSize(200, 30);
        tFName.setLocation(550, 420);
        ((AbstractDocument) tFName.getDocument()).setDocumentFilter(filter);
        c.add(tFName);

        tLName = new HTH_TextField(25, HTH_FONT);
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
        c.add(searchName);

        setCompanyName();
        setCustomerGroup();
        setCustomerSSN();
        setClaim();

        setNote();
        setButton();
        //queryField();
        this.getContentPane().setBackground(Color.WHITE);

        setVisible(true);
    }

    public void setReference() {
        referenceNumber = new JLabel("Refrence Number");
        referenceNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        referenceNumber.setSize(200, 30);
        referenceNumber.setLocation(250, 300);
        c.add(referenceNumber);

        tReferenceNumber = new HTH_TextField(10, HTH_FONT);
        tReferenceNumber.setForeground(new Color(0, 0, 150));
        tReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 15));
        tReferenceNumber.setSize(100, 30);
        tReferenceNumber.setLocation(550, 300);
        tReferenceNumber.addKeyListener(refrenceText);
        c.add(tReferenceNumber);

        bReferenceNumber = new HTH_ControlButton("Generate");
        bReferenceNumber.setSize(75, 28);
        bReferenceNumber.setLocation(660, 300);
        bReferenceNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance("");
                Integer reference = repo.generateRefNumTest();
                referenceS = String.valueOf(reference);
                tReferenceNumber.setText(reference.toString());
                clearFormGenerate();
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
        tPhoneNum.setForeground(new Color(0, 0, 150));
        tPhoneNum.setFont(new Font("Arial", Font.PLAIN, 15));
        tPhoneNum.setSize(200, 30);
        tPhoneNum.setLocation(550, 380);
        tPhoneNum.addKeyListener(this);
        c.add(tPhoneNum);
    }

    public void setCompanyName() {
        company = new JLabel("<html><nobr>Company Name <font color='#ffbebe'>*</font></nobr></html>");
        company.setFont(new Font("Arial", Font.PLAIN, 18));
        company.setSize(200, 30);
        company.setLocation(250, 460);
        c.add(company);


        tCompanyName = new HTH_TextField(25, HTH_FONT);
        tCompanyName.setForeground(new Color(0, 0, 150));
        tCompanyName.setFont(new Font("Arial", Font.PLAIN, 15));
        tCompanyName.setSize(200, 30);
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

        tCustomerGroup = new HTH_TextField(10, HTH_FONT);
        tCustomerGroup.setForeground(new Color(0, 0, 150));
        tCustomerGroup.setFont(new Font("Arial", Font.PLAIN, 15));
        tCustomerGroup.setSize(200, 30);
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

        tssn = new HTH_TextField(10, HTH_FONT);
        tssn.setForeground(new Color(0, 0, 150));
        tssn.setFont(new Font("Arial", Font.PLAIN, 15));
        tssn.setSize(200, 30);
        tssn.setLocation(550, 540);
        c.add(tssn);
    }

    public void setClaim() {
        claim = new JLabel("Claim Number");
        claim.setFont(new Font("Arial", Font.PLAIN, 18));
        claim.setSize(200, 30);
        claim.setLocation(250, 580);
        c.add(claim);

        tClaim = new HTH_TextField(7, HTH_FONT);
        tClaim.setForeground(new Color(0, 0, 150));
        tClaim.setFont(new Font("Arial", Font.PLAIN, 15));
        tClaim.setSize(200, 30);
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
        tCallNotes.setForeground(new Color(0, 0, 150));
        tCallNotes.setFont(new Font("Arial", Font.PLAIN, 15));
        tCallNotes.setSize(410, 60);
        tCallNotes.setLocation(550, 620);
        tCallNotes.setBackground(new Color(255, 255, 255));
        tCallNotes.setBorder(BorderFactory.createLineBorder(Color.GRAY));
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

//    public void queryField() {
//        JLabel startLabel = new JLabel("Enter Start Date");
//        startLabel.setSize(200, 30);
//        startLabel.setLocation(250, 760);
//        startLabel.setFont(new Font("Arial", Font.PLAIN, 18));
//        c.add(startLabel);
//
//        pickerFrom = new JXDatePicker();
//        pickerFrom.setDate(Calendar.getInstance().getTime());
//        pickerFrom.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
//        pickerFrom.setSize(200, 30);
//        pickerFrom.setLocation(250, 790);
//        c.add(pickerFrom);
//
//        JLabel endLabel = new JLabel("Enter End Date");
//        endLabel.setSize(200, 30);
//        endLabel.setLocation(550, 760);
//        endLabel.setFont(new Font("Arial", Font.PLAIN, 18));
//        c.add(endLabel);
//
//        pickerTo = new JXDatePicker();
//        pickerTo.setDate(Calendar.getInstance().getTime());
//        pickerTo.setFormats(new SimpleDateFormat("dd/MM/yyyy"));
//        pickerTo.setSize(200, 30);
//        pickerTo.setLocation(550, 790);
//        c.add(pickerTo);
//
//        select = new JComboBox(selectList);
//        select.setFont(new Font("Arial", Font.PLAIN, 15));
//        select.setSize(200, 30);
//        select.setLocation(250, 840);
//        select.setForeground(new Color(79, 145, 200));
//        select.setBackground(Color.white);
//        //select.addActionListener(providerS);
//        c.add(select);
//
//        showingSelect = new HTH_TextField(6, HTH_FONT);
//        showingSelect.setForeground(new Color(0, 0, 150));
//        showingSelect.setFont(new Font("Arial", Font.PLAIN, 15));
//        showingSelect.setSize(200, 30);
//        showingSelect.setLocation(550, 840);
//        ((AbstractDocument) tClaim.getDocument()).setDocumentFilter(filter);
//        c.add(showingSelect);
//
//        searchData = new HTH_ControlButton("Search Data");
//        searchData.setFont(new Font("Arial", Font.PLAIN, 15));
//        searchData.setSize(100, 27);
//        searchData.setLocation(900, 880);
//        searchData.addActionListener(searchDatabase);
//        //claimListBTN2.addActionListener(claimList);
//        c.add(searchData);
//    }

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
            String reference = tReferenceNumber.getText().trim();
            if (reference.equals(referenceS) || checkReference) {
                if (checkData()) {
                    System.out.println("Submit button clicked");
                    CrmLogRecord2 record = getData();
                    CRMLOGS.insertCrmLogs(record);

                    JOptionPane j = new JOptionPane("KK");
                    j.setSize(250, 250);
                    j.showMessageDialog(null, "Data Inserted Successfully done.", "INFORMATION", JOptionPane.PLAIN_MESSAGE);
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(new JLabel(), "Please regenerate Reference Number");
                clearForm();
            }
        }
    };

//    Action searchDatabase = new AbstractAction(okKey) {
//        private static final long serialVersionUID = 10110L;
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            Date from = pickerFrom.getDate();
//            DateFormat fromFormat = new SimpleDateFormat("yyMMdd");
//            String fromDate = fromFormat.format(from);
//
//            Date to = pickerTo.getDate();
//            DateFormat toFormat = new SimpleDateFormat("yyMMdd");
//            String toDate = toFormat.format(to);
//
//            String service = "Serivce";
//            String keyword = showingSelect.getText().trim();
//
//            List<String[]> data = null;
//            try {
//                data = CRMLOGS.searchData(fromDate, toDate, keyword);
//            } catch (Exception ex) {
//                System.out.println(ex.getMessage());
//            }
//            showDataFunction(data);
//        }
//    };


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
            if (provider.trim().equals("Provider")) {
                company.setText("<html><nobr>Company Name <font color='#ffbebe'>*</font></nobr></html>");
            } else {
                company.setText("Company Name");
            }
        }
    };

    @Override
    public void keyTyped(KeyEvent e) {
    }

    KeyListener refrenceText = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {
            CRMLogsSingleton cr = CRMLogsSingleton.singleton();
            String reference = tReferenceNumber.getText().trim();
            if (reference.length() == 10) {
                //List<String[]> resultList = CRMLOGS.searchByReferenceNumber(reference);
                List<String[]> resultList = cr.getCrmlogList();
                //System.out.println(resultList.get(0)[0]);
                String[] result;
                for (int i = 0; i < resultList.size(); i++) {
                    System.out.println(i + " " + resultList.get(i)[0].trim());
                    if (resultList.get(i)[0].trim().equals(reference)) {
                        //result = resultList.get(idx);
                        //for (int i = 0; i < result.length; i++) {
                        System.out.println(resultList.get(i)[0]);

                        System.out.println("con:" + resultList.get(i)[0].trim());
                        checkReference = false;
                        //tReferenceNumber.setText(resultList.get(0)[0].trim());
                        String provider = resultList.get(i)[1].trim();
                        if (provider.equals("M")) {
                            tProvider.setSelectedIndex(1);
                        } else {
                            tProvider.setSelectedIndex(0);
                        }
                        tPhoneNum.setText(resultList.get(i)[2].trim());
                        tFName.setText(resultList.get(i)[3].trim());
                        tLName.setText(resultList.get(i)[4].trim());
                        tCompanyName.setText(resultList.get(i)[5].trim());
                        tCustomerGroup.setText(resultList.get(i)[6].trim());
                        tssn.setText(resultList.get(i)[7].trim());
                        tClaim.setText(resultList.get(i)[8].trim());
                        tCallNotes.setText(resultList.get(i)[12].trim());

                    }
                }

//                if (resultList.size() > 0) {
//                    checkReference = false;
//                    //tReferenceNumber.setText(resultList.get(0)[0].trim());
//                    String provider = resultList.get(0)[1].trim();
//                    if (provider.equals("M")) {
//                        tProvider.setSelectedIndex(1);
//                    } else {
//                        tProvider.setSelectedIndex(0);
//                    }
//                    tPhoneNum.setText(resultList.get(0)[2].trim());
//                    tFName.setText(resultList.get(0)[3].trim());
//                    tLName.setText(resultList.get(0)[4].trim());
//                    tCompanyName.setText(resultList.get(0)[5].trim());
//                    tCustomerGroup.setText(resultList.get(0)[6].trim());
//                    tssn.setText(resultList.get(0)[7].trim());
//                    tClaim.setText(resultList.get(0)[8].trim());
//                    tCallNotes.setText(resultList.get(0)[12].trim());
//                }
            }
        }
    };

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        String phone = tPhoneNum.getText().trim();
        String format = gePhoneNum(phone);
        if (format.length() == 9) {
//            List<String[]> resultList = CRMLOGS.searchByPhone(format);
//            if (resultList.size() > 0) {
//                //tReferenceNumber.setText(resultList.get(0)[0].trim());
//                tFName.setText(resultList.get(0)[1].trim());
//                tLName.setText(resultList.get(0)[2].trim());
//                tssn.setText(resultList.get(0)[3].trim());
//            }
            InsureDataSingleton isd = InsureDataSingleton.singleton();
            List<Insure[]> list = isd.getInsureList();
            System.out.println("size:" + list.size());

            for (int i = 0; i < list.size(); i++) {
                for (Insure data : list.get(i)) {
                    System.out.println("pp:" + data.getPhone());
                    if (data.getPhone().contains(format)) {
                        System.out.println("phone:" + data.getPhone());
                        tFName.setText(data.getfName());
                        tLName.setText(data.getlName());
                        tssn.setText(data.getSsn());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {

    }

    public class SearchByName implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tLName.getText().length() > 2) {
                nameSearchFromDataBase(tLName.getText().trim().toUpperCase());
            } else if (tFName.getText().length() > 2) {
                nameSearchFromDataBase(tFName.getText().trim().toUpperCase());
            }
        }
    }

    private boolean checkData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String phoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        String lName = tLName.getText().trim().toUpperCase();
        String company = tCompanyName.getText().trim().toUpperCase();
        String ssn = tssn.getText().trim().toUpperCase();
        String claimNum = tClaim.getText().trim().toUpperCase();
        String errMsg = "";
        if (refNum.length() != 10) {
            errMsg = "Invalid Reference Number";
            contentScreen = new JLayeredPane();
            contentScreen.setOpaque(false);
            //Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
            //showMessage("CRM LOgsFile", errMsg, dialogSize, JOptionPane.ERROR_MESSAGE);
            JOptionPane.showMessageDialog(new JLabel(), errMsg);
            System.out.println("validation error checkData: refname");
            return false;
        }
        if (phoneNum.length() != 14) {
            errMsg = "Invalid Phone Number";
            System.out.println(phoneNum.length() + ": MyFrame");
            JOptionPane.showMessageDialog(new JLabel(), errMsg);
            System.out.println("validation error checkData: phoneNum");
            return false;
        }

        if (fName.length() < 2) {
            errMsg = "Invalid First Name";
            JOptionPane.showMessageDialog(new JLabel(), errMsg);
            System.out.println("validation error checkData: fName");
            return false;
        }
        if (lName.length() < 2) {
            errMsg = "Invalid Last Name";
            JOptionPane.showMessageDialog(new JLabel(), errMsg);
            System.out.println("validation error checkData: lName");
            return false;
        }
        if (providerOrMember.equals("P")) {
            if (company.length() < 3) {
                errMsg = "Invalid Company";
                JOptionPane.showMessageDialog(new JLabel(), errMsg);
                System.out.println("validation error checkData: company");
                return false;
            }
        }
        if (ssn.length() != 9) {
            errMsg = "Invalid SSN Number";
            JOptionPane.showMessageDialog(new JLabel(), errMsg);
            System.out.println("validation error checkData: memberid");
            return false;
        }
        if (claimNum.length() != 7) {
            errMsg = "Invalid Claim Number";
            JOptionPane.showMessageDialog(new JLabel(), errMsg);
            System.out.println("validation error checkData: claimNum");
            return false;
        }
        return true;
    }

    private CrmLogRecord2 getData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String formattedPhoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        String lName = tLName.getText().trim().toUpperCase();
        String company = tCompanyName.getText().trim().toUpperCase();
        String customerGroup = tCustomerGroup.getText().trim().toUpperCase();
        String ssn = tssn.getText().trim().toUpperCase();
        String claimNum = tClaim.getText().trim().toUpperCase();
        String note = tCallNotes.getText().trim().toUpperCase();

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
                .withNote(note)
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
        functionPanel.setSize(220, 800);

        claimListBTN2 = new HTH_FunctionButton("Audit Log");
        claimListBTN2.setSize(220, 32);
        claimListBTN2.setLocation(0, 100);

        //claimListBTN2.addMouseListener(this);

        claimListBTN2.addActionListener(claimList);
        //claimListBTN.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), listKey);
        //claimListBTN.getActionMap().put(listKey, claimList);
        functionPanel.add(claimListBTN2);


        JPanel dummyPanel = new JPanel();
        dummyPanel.setOpaque(false);
        //dummyPanel.setPreferredSize(new Dimension(0, 80));
        functionPanel.add(dummyPanel, BorderLayout.NORTH);
        functionKeyPanel = new JPanel();
        functionKeyPanel.setOpaque(false);
        functionKeyPanel.setLayout(new BoxLayout(functionKeyPanel, BoxLayout.Y_AXIS));
        functionPanel.add(functionKeyPanel, BorderLayout.CENTER);
    }

    private List<String[]> showAuditLog() {
        CRMLogsSingleton cr = CRMLogsSingleton.singleton();
        List<String[]> resultList = CRMLOGS.getCrmLogs(cr.getMaxId());
        if (resultList.size() > 0) {
            cr.setCrmlogList(resultList);
        }
        List<String[]> newResultList = cr.getCrmlogList();
        //CRMLogsSingleton.Singleton();
        String[] result;
        final Object[][] data = new Object[newResultList.size()][];
        for (int idx = 0; idx < newResultList.size(); idx++) {
            result = newResultList.get(idx);
            data[idx] = result;
        }
        final String[] columnNames = {"REFRENCE#", "Type", "Phone Number", "FName", "LName", "Company Name", "Customer Group", "Customer SSN", "Claim", "Time", "Date", "user", "Note", "Filler"};

        auditLog = new JFrame("Claim List");
        auditLog.setBounds(400, 90, 1180, 800);
        auditLog.setTitle("Name Search From Databse");
        TableModel model = new AbstractTableModel() {
            public int getColumnCount() {
                return columnNames.length;
            }

            public int getRowCount() {
                return data.length;
            }

            public Object getValueAt(int row, int col) {
                return data[row][col];
            }

            public String getColumnName(int column) {
                return columnNames[column];
            }

            public Class getColumnClass(int col) {
                return getValueAt(0, col).getClass();
            }

            public void setValueAt(Object aValue, int row, int column) {
                data[row][column] = aValue;
            }
        };

        logTable = new JTable(model);
        ListSelectionModel listModel = logTable.getSelectionModel();
        listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModel.addListSelectionListener(auditLogList);
        JScrollPane scroll = new JScrollPane(logTable);
        scroll.setPreferredSize(new Dimension(300, 300));
        auditLog.getContentPane().add(scroll);
        auditLog.setSize(800, 800);
        auditLog.setVisible(true);

        return resultList;

    }

    ListSelectionListener auditLogList = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int[] sel;
            Object value;
            if (!e.getValueIsAdjusting()) {
                sel = logTable.getSelectedRows();
                if (sel.length > 0) {
                    TableModel tm = logTable.getModel();
                    checkReference = false;
                    auditLog.setVisible(false);
                    tReferenceNumber.setText(tm.getValueAt(sel[0], 0).toString().trim());
                    String provider = tm.getValueAt(sel[0], 1).toString().trim();
                    if (provider.equals("M")) {
                        tProvider.setSelectedIndex(1);
                    } else {
                        tProvider.setSelectedIndex(0);
                    }
                    tPhoneNum.setText(tm.getValueAt(sel[0], 2).toString().trim());
                    tLName.setText(tm.getValueAt(sel[0], 3).toString().trim());
                    tFName.setText(tm.getValueAt(sel[0], 4).toString().trim());
                    tCompanyName.setText(tm.getValueAt(sel[0], 5).toString().trim());
                    tCustomerGroup.setText(tm.getValueAt(sel[0], 6).toString().trim());
                    tssn.setText(tm.getValueAt(sel[0], 7).toString().trim());
                    tClaim.setText(tm.getValueAt(sel[0], 8).toString().trim());
                    tCallNotes.setText(tm.getValueAt(sel[0], 12).toString().trim());
                }
            }
        }
    };

    private void nameSearchFromDataBase(String name) {
        InsureDataSingleton isd = InsureDataSingleton.singleton();
        List<Insure[]> dataI = isd.getInsureList();
        System.out.println("nam:" + dataI.size());

        List<Insure> matched = new ArrayList<Insure>();
        for (int i = 0; i < dataI.size(); i++) {
            for (Insure search : dataI.get(i)) {
                if (search.getfName().contains(name) || search.getlName().contains(name)) {
                    matched.add(search);
                }
            }
        }
        final Object[][] data = new Object[matched.size()][];
        for (int i = 0; i < matched.size(); i++) {
            String[] insert = new String[4];
            insert[0] = matched.get(i).getlName();
            insert[1] = matched.get(i).getfName();
            insert[2] = matched.get(i).getSsn();
            //insert[3] = matched.get(i).getPhone();
            data[i] = insert;
        }

        final String[] columnNames = {"Last Name", "First Name", "EmployeeID"};

        searchNameFrame = new JFrame();
        searchNameFrame.setBounds(400, 90, 1180, 800);
        searchNameFrame.setTitle("Name Search From Databse");
        TableModel model = new AbstractTableModel() {
            public int getColumnCount() {
                return columnNames.length;
            }

            public int getRowCount() {
                return data.length;
            }

            public Object getValueAt(int row, int col) {
                return data[row][col];
            }

            public String getColumnName(int column) {
                return columnNames[column];
            }

            public Class getColumnClass(int col) {
                return getValueAt(0, col).getClass();
            }

            public void setValueAt(Object aValue, int row, int column) {
                data[row][column] = aValue;
            }
        };

        searchNameTable = new JTable(model);
        ListSelectionModel listModel = searchNameTable.getSelectionModel();
        listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModel.addListSelectionListener(fLastNameList);
        JScrollPane scroll = new JScrollPane(searchNameTable);
        scroll.setPreferredSize(new Dimension(300, 300));
        searchNameFrame.getContentPane().add(scroll);
        searchNameFrame.setSize(800, 800);
        searchNameFrame.setVisible(true);
    }

    ListSelectionListener fLastNameList = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int[] sel;
            Object value;
            if (!e.getValueIsAdjusting()) {
                sel = searchNameTable.getSelectedRows();
                if (sel.length > 0) {
                    TableModel tm = searchNameTable.getModel();
                    searchNameFrame.setVisible(false);
                    tLName.setText(tm.getValueAt(sel[0], 0).toString().trim());
                    tFName.setText(tm.getValueAt(sel[0], 1).toString().trim());
                    tssn.setText(tm.getValueAt(sel[0], 2).toString().trim());
                    //tPhoneNum.setText(tm.getValueAt(sel[0], 3).toString().trim());
                }
            }
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    void clearForm() {
        tReferenceNumber.setText("");
        tCallNotes.setText("");
        tClaim.setText("");
        tssn.setText("");
        tCompanyName.setText("");
        tCustomerGroup.setText("");
        tFName.setText("");
        tLName.setText("");
        tPhoneNum.setText("");
    }

    void clearFormGenerate() {
        //tReferenceNumber.setText("");
        tCallNotes.setText("");
        tClaim.setText("");
        tssn.setText("");
        tCompanyName.setText("");
        tCustomerGroup.setText("");
        tFName.setText("");
        tLName.setText("");
        tPhoneNum.setText("");
    }

//    private void showDataFunction(List<String[]> showData) {
//        try {
//            String[][] data = new String[showData.size()][];
//            for (int idx = 0; idx < showData.size(); idx++) {
//                String[] result = new String[showData.get(idx).length];
//                result = showData.get(idx);
//                //for (int i = 0; i < result.length; i++) {
//                    //if (i == 3) {
//                        Date d = new SimpleDateFormat("yyMMdd").parse(result[3]);
//                        SimpleDateFormat d2 = new SimpleDateFormat("MM/dd/yy");
//                        System.out.println(d2.format(d));
//                        //result[i] = d2.format(d);
//                        //result[3] = result[3].replaceAll(result[3],d2.format(d));
//                        result[3] = d2.format(d).toString();
//                    //}
//                //}
//                data[idx] = result;
//            }
//            final String[] columnNames = {"CLAIM_NUMBER", "LINE_NO", "3", "DATE_OF_SERVICE", "DIVISION", "POLICY_ID", "PATIENT_NAME", "DEPENDENT_CODE", "COVERAGE", "AMOUNT_CLAIMED", "DAMTEX", "TOTAL_PAID", "DEXCD", "13", "HICD1", "HICD2", "HICD3", "HICD4", "HICD5", "HICD6", "HICD7", "HICD8", "HICD9", "HICD10", "TYPE_OF_SERVICE", "25", "26"};
//            writeDataLineByLine(columnNames, data);
////        showDataF = new JFrame("Show Data");
////        showDataF.setBounds(400, 90, 1180, 800);
////
////        showTable = new JTable(data, columnNames);
////        JScrollPane scroll = new JScrollPane(showTable);
////        scroll.setPreferredSize(new Dimension(300, 300));
////        showDataF.getContentPane().add(scroll);
////        showDataF.setSize(800, 800);
////        showDataF.setVisible(true);
//        }catch (Exception e){
//
//        }
//    }

//    public static void writeDataLineByLine(String[] header, String data[][]) {
//        File file = null;
//        JFrame parentFrame = new JFrame();
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Select a Location");
//        int userSelection = fileChooser.showSaveDialog(parentFrame);
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
//            File fileToSave = fileChooser.getSelectedFile();
//            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
//            file = new File(fileToSave.getAbsolutePath()+".csv");
//        }
//        try {
//
//            FileWriter outputfile = new FileWriter(file);
//
//            CSVWriter writer = new CSVWriter(outputfile);
//            //CSVWriter writer = new CSVWriter(outputfile,',',CSVWriter.NO_QUOTE_CHARACTER);
//
//            writer.writeNext(header);
//
//            for(int i=0;i< data.length;i++){
//                writer.writeNext(data[i]);
//            }
//            writer.close();
//        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}

