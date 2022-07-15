package com.hth.newCustomerServiceFeature.View;// Java program to implement
// a Simple Registration Form
// using Java Swing

import com.hth.backend.iSeries;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_PromptButton;
import com.hth.images.HTH_Image;
import com.hth.newCustomerServiceFeature.CustomerServiceApp;
import com.hth.newCustomerServiceFeature.DomainModel.CrmLogRecord;
import com.hth.newCustomerServiceFeature.Repository.Repository;
import com.hth.newCustomerServiceFeature.UppercaseDocumentFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.Map;

public class MyFrame
        extends JFrame implements ActionListener, WindowListener {

    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);
    String user = "SALAHM";

    // Components of the Form
    private Container container;
    private JLabel title;
    private JLabel referenceNumber;
    private JTextField tReferenceNumber;
    private JLabel refNumValidation;
    private HTH_PromptButton pReferenceNumber;
    private JButton bReferenceNumber;

    private JLabel provider;
    private JLabel providerValidation;
    private JComboBox tProvider;
    private HTH_PromptButton pProvider; //p -> pop up

    private JLabel phoneNum;
    private JLabel phoneValidation;
    private JTextField tPhoneNum;
    private MaskFormatter phoneFormatter;
    private JLabel name;
    private JLabel nameValidation;
    public static JTextField tFName;
    public static JTextField tLName;
    private HTH_PromptButton searchName;
    private JLabel companyCallingFrom;
    private JLabel companyValidation;
    private JTextField tCompanyCallingFrom;
    private JLabel memberId;
    private JLabel memberIdValidation;
    public static JTextField tMemberId;
    private JLabel claimNumber;
    private JLabel claimNumberValidation;
    private JTextField tClaimNumber;
    private JTextField tClaimNumber2;

    private JButton ok;
    private JButton exit;
    private String providerOrMember[] = {"Provider", "Member"};

    private Thread t1;
    public String[][] insureData;

    public static MyFrame myFrame = null;
    //uppercase filter
    DocumentFilter filter = new UppercaseDocumentFilter();

    //    protected static final Dimension FRAME_SIZE = new Dimension(1220, 900);
    protected static final Dimension FRAME_SIZE = new Dimension(1180, 800);

    private String title2;

    private JPanel headerPanel;
    private JPanel functionPanel;
    private JPanel functionKeyPanel;
    private JLabel titleLabel;


    public MyFrame() {
        myFrame = this;
        setTitle("Customer Service ");
        filter = new UppercaseDocumentFilter();
        setBounds(300, 90, 900, 520);
        //       setBounds(300, 90, 85, 49);
        addWindowListener(this);
        // setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
//        this.setOpaque(true);
//        setBackground(Color.WHITE);


        container = getContentPane();
        container.setLayout(null);


        try {
            setIconImage(ImageIO.read(HTH_Image.getImageURL("hth_block.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        setContentPane(contentPanel);

        initHeaderPanel();
        initFunctionPanel();
//
        add(headerPanel, BorderLayout.NORTH);
        add(functionPanel, BorderLayout.WEST);
        // getInsureData();

        setBorder();
        setReferenceNum();
        setProvider();
        setPhone();
        setFNameLName();
        setCompanyCallingFrom();
        setMember();
        setClaimNum();
        setButtons();

        UpdateDocumentListener();

        revalidate();
        repaint();
        setPreferredSize(FRAME_SIZE);
        pack();
        relocateWindow();
        this.getContentPane().setBackground(Color.WHITE);

        setVisible(true);

    }

    public MyFrame(Map inputMap) {
        myFrame = this;
        setTitle("Customer Service new feature ");
        setBounds(300, 90, 900, 520);
        //       setBounds(300, 90, 85, 49);
        addWindowListener(this);
        // setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
//        this.setOpaque(true);
//        setBackground(Color.WHITE);


        container = getContentPane();
        container.setLayout(null);


        try {
            setIconImage(ImageIO.read(HTH_Image.getImageURL("hth_block.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        setContentPane(contentPanel);

        initHeaderPanel();
        initFunctionPanel();
//
        add(headerPanel, BorderLayout.NORTH);
        add(functionPanel, BorderLayout.WEST);
        // getInsureData();

        setBorder();
        setReferenceNum();
        setProvider();
        setPhone();
        setFNameLName();
        setCompanyCallingFrom();
        setMember();
        setClaimNum();
        setButtons();

        UpdateDocumentListener();

        revalidate();
        repaint();
        setPreferredSize(FRAME_SIZE);
        pack();
        relocateWindow();
        this.getContentPane().setBackground(Color.WHITE);

        showUpdatedValues();
        setVisible(true);

    }

    private void showUpdatedValues() {


        tClaimNumber.setText(Util.inputMap.get("claim"));
        tCompanyCallingFrom.setText(Util.inputMap.get("company"));
        tPhoneNum.setText(Util.inputMap.get("phoneNum"));
        tReferenceNumber.setText(Util.inputMap.get("referenceNum"));
        tFName.setText(Util.inputMap.get("fName"));
        tLName.setText(Util.inputMap.get("lName"));
        tMemberId.setText(Util.inputMap.get("ssn"));

    }

    private void setButtons() {
        String exitKey = "exitBtn";
        String okKey = "okBtn";
        Action exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10110L;

            @Override
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        };

        Action submitAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10110L;

            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance(user);
                if (checkData()) {

                    System.out.println("submit button clicked");
                    CrmLogRecord record = getData();
                    repo.insertIntoCrmLog(record);

                    String loadCardCL = "CALL PDLIB/CRMCLMC PARM(TR1)";

                    iSeries.executeCL(loadCardCL);
                    close();
                }
            }
        };
//        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
//        exitBtn.setToolTipText("F3=Exit");
//        exitBtn.addActionListener(exitAction);
//        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
//        exitBtn.getActionMap().put(exitKey, exitAction);

        exit = new HTH_ControlButton("Exit");
        exit.setToolTipText("F3=Exit");
        exit.addActionListener(exitAction);
        exit.setFont(new Font("Arial", Font.PLAIN, 14));
        //cancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exit.setSize(75, 27);
//        cancel.setForeground(Color.gray);
//        cancel.setBackground(Color.gray);
        exit.setLocation(815, 580);
        add(exit);

        ok = new HTH_ControlButton("Ok");
        ok.setToolTipText("ENTER=OK");
        ok.addActionListener(submitAction);
        ok.setFont(new Font("Arial", Font.PLAIN, 14));
        ok.setSize(75, 27);
        ok.setLocation(900, 580);
        add(ok);

        //---- need to be investigated
        tClaimNumber2 = new JTextField();
        tClaimNumber2.setFont(new Font("Arial", Font.PLAIN, 18));
        tClaimNumber2.setSize(80, 30);
        tClaimNumber2.setLocation(7500, 3400);
        tClaimNumber2.setVisible(false);
//        container.add(tClaimNumber);
        add(tClaimNumber2);
    }

    private void close() {
        System.exit(0);
    }

    public static MyFrame getInstance() {
        if (myFrame != null) {

            return myFrame;
        }
        return null;
    }

    private void getInsureData() {
        t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //---static
                // insureData = Repository.getInstance("GW1").getInsureData();
                System.out.println("size of data: " + insureData.length);
                // insureData = new String[][]{{"MOOHAM","SALAH","20202"}, {"HHH","SSS","4444"},{"EEE","LLL","5555"}};
                System.out.println("---got insure Data");
            }
        });
        t1.start();
    }


    // get the inpute  Data to be inserted in the DataBase
    private CrmLogRecord getData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String formattedPhoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        String lName = tLName.getText().trim().toUpperCase();
        String company = tCompanyCallingFrom.getText().trim().toUpperCase();
        String ssn = tMemberId.getText().trim().toUpperCase();
        String claimNum = tClaimNumber.getText().trim().toUpperCase();

        CrmLogRecord record = new CrmLogRecord.Builder()
                .withRefNum(refNum)
                .withClaimType(providerOrMember)
                .withPhoneNum(gePhoneNum(formattedPhoneNum))
                .withFName(fName)
                .withLName(lName)
                .withCompanyName(company)
                .withSSN(ssn)
                .withClaimNum(claimNum)
                .withUser(CustomerServiceApp.user)
                .build();

        return record;
    }

    private String gePhoneNum(String formattedPhoneNum) {

        String phone = formattedPhoneNum.replaceAll("\\(", "").replaceAll("\\)", "").
                replaceAll(" ", "").replaceAll("-", "");
        return phone;
    }

    // checkData before inserting into dataBase
    private boolean checkData() {
        String refNum = tReferenceNumber.getText().trim().toUpperCase();
        String providerOrMember = "P";
        if (tProvider.getSelectedIndex() == 1) {
            providerOrMember = "M";
        }
        String phoneNum = tPhoneNum.getText().trim();
        String fName = tFName.getText().trim().toUpperCase();
        String lName = tLName.getText().trim().toUpperCase();
        String company = tCompanyCallingFrom.getText().trim().toUpperCase();
        String ssn = tMemberId.getText().trim().toUpperCase();
        String claimNum = tClaimNumber.getText().trim().toUpperCase();

        if (refNum.length() != 10) {
            refNumValidation.setVisible(true);
            System.out.println("validation error checkData: refname");
            return false;
        }
        if (ssn.length() != 9) {
            memberIdValidation.setVisible(true);
            System.out.println("validation error checkData: memberid");

            return false;
        }
        if (claimNum.length() != 7) {
            claimNumberValidation.setVisible(true);
            System.out.println("validation error checkData: claimNum");

            return false;
        }

        if (phoneNum.length() != 14) {
            phoneValidation.setVisible(true);
            System.out.println(phoneNum.length() + ": MyFrame");
            System.out.println("validation error checkData: phoneNum");

            return false;
        }
        if (fName.length() < 2) {
            nameValidation.setVisible(true);
            System.out.println("validation error checkData: fName");

            return false;
        }
        if (lName.length() < 2) {
            nameValidation.setVisible(true);
            System.out.println("validation error checkData: lName");

            return false;
        }
        if (company.length() < 2) {
            companyValidation.setVisible(true);
            System.out.println("validation error checkData: company");

            return false;
        }

        return true;

    }

    private JLabel getLabel(String txt) {
        JLabel label = new JLabel(txt);
        label.setFont(HTH_FONT);
        int txtW = label.getFontMetrics(HTH_FONT).stringWidth(txt);
        int txtH = label.getFontMetrics(HTH_FONT).getHeight();
        label.setPreferredSize(new Dimension(txtW, txtH));

        return label;
    }

    private void setBorder() {
        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        // borderPanel.setBackground(Color.WHITE);
        borderPanel.setBounds(230, 260, 800, 370);
        borderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Customer Service",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        add(borderPanel);
    }

    private void setReferenceNum() {
        referenceNumber = new JLabel("Reference Number");
        referenceNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        referenceNumber.setSize(200, 30);
        referenceNumber.setLocation(250, 300);
        //container.add(referenceNumber);
        add(referenceNumber);


        refNumValidation = new JLabel("Invalid");
        refNumValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        refNumValidation.setForeground(Color.RED);
        refNumValidation.setSize(40, 30);
        refNumValidation.setLocation(505, 300);
        refNumValidation.setVisible(false);
//        container.add(refNumValidation);
        add(refNumValidation);

        tReferenceNumber = new JTextField();
        tReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 15));
        tReferenceNumber.setSize(100, 30);
        tReferenceNumber.setLocation(550, 300);
        //tReferenceNumber.setInputVerifier(new RefNumValidation());
//        container.add(tReferenceNumber);
        tReferenceNumber.setFocusable(true);
        add(tReferenceNumber);


//        pReferenceNumber = new HTH_PromptButton();
//        pReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 18));
//        pReferenceNumber.setSize(15, 15);
//        pReferenceNumber.setLocation(642, 305);
////        container.add(pReferenceNumber);
//        add(pReferenceNumber);

        bReferenceNumber = new HTH_ControlButton("Generate");
       // bReferenceNumber.setFont(new Font("Arial", Font.PLAIN, 14));
        bReferenceNumber.setSize(75, 28);
        bReferenceNumber.setLocation(670, 300);
        //bReferenceNumber.addActionListener(this);

        bReferenceNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance("");
                Integer reference = repo.generateRefNum();
                tReferenceNumber.setText(reference.toString());
            }
        });
//        container.add(bReferenceNumber);
        add(bReferenceNumber);

    }

    private void setProvider() {
        provider = new JLabel("Provider Or Member");
        provider.setFont(new Font("Arial", Font.PLAIN, 18));
        provider.setSize(200, 30);
        provider.setLocation(250, 340);
//        container.add(provider);
        add(provider);

        tProvider = new JComboBox(providerOrMember);
        tProvider.setFont(new Font("Arial", Font.PLAIN, 15));
        tProvider.setSize(100, 30);
        tProvider.setLocation(550, 340);
        tProvider.setForeground(new Color(79, 145, 200));
        tProvider.setBackground(Color.white);


//        container.add(tProvider);
        add(tProvider);

//
    }

    private void setPhone() {
        phoneNum = new JLabel("<html><nobr>Phone Number <font color='#ffbebe'>*</font></nobr></html>");
        phoneNum.setFont(new Font("Arial", Font.PLAIN, 18));
        phoneNum.setSize(200, 30);
        phoneNum.setLocation(250, 380);
//        container.add(phoneNum);
        add(phoneNum);

        phoneValidation = new JLabel("Invalid");
        phoneValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        phoneValidation.setForeground(Color.RED);
        phoneValidation.setSize(40, 30);
        phoneValidation.setLocation(505, 380);
        phoneValidation.setVisible(false);
//        container.add(phoneValidation);
        // add(phoneValidation);

        try {
            phoneFormatter = new MaskFormatter("(###) ###-####");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tPhoneNum = new JFormattedTextField(phoneFormatter);
        //tPhoneNum.setHorizontalAlignment(JTextField.LEFT);
        //tPhoneNum.setComponentOrientation();
        tPhoneNum.setFont(new Font("Arial", Font.PLAIN, 18));
        tPhoneNum.setSize(135, 30);
        tPhoneNum.setLocation(550, 380);
//        container.add(tPhoneNum);
        add(tPhoneNum);

    }

    public void showUpdatedContent(String firstName, String lastName, String ssn) {
        Component[] comps = MyFrame.this.getContentPane().getComponents();
        for (Component comp : comps) {
            if (comp.getName().equals("tFName")) {
                JTextField tFName = (JTextField) comp;
                tFName.setText(firstName);
            } else if (comp.getName().equals("tLName")) {
                JTextField tLName = (JTextField) comp;
                tLName.setText(lastName);
            } else if (comp.getName().equals("tMemberId")) {
                JTextField tMemberId = (JTextField) comp;
                tMemberId.setText(ssn);
            }

            comp.setVisible(true);
        }
    }


    private void setFNameLName() {
        name = new JLabel("<html><nobr>First/Last Name <font color='#ffbebe'>*</font></nobr></html>");
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setSize(200, 30);
        name.setLocation(250, 420);
//        container.add(name);
        add(name);

        nameValidation = new JLabel("Invalid");
        nameValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        nameValidation.setForeground(Color.RED);
        nameValidation.setSize(40, 30);
        nameValidation.setLocation(505, 420);
        nameValidation.setVisible(false);
//        container.add(nameValidation);
        add(nameValidation);

        tFName = new JTextField();
        tFName.setFont(new Font("Arial", Font.PLAIN, 18));
        tFName.setSize(199, 30);
        tFName.setLocation(550, 420);
        ((AbstractDocument) tFName.getDocument()).setDocumentFilter(filter);

//        container.add(tFName);
        add(tFName);

        tLName = new JTextField();
        tLName.setFont(new Font("Arial", Font.PLAIN, 18));
        tLName.setSize(199, 30);
        tLName.setLocation(751, 420);
        ((AbstractDocument) tLName.getDocument()).setDocumentFilter(filter);
//        container.add(tLName);
        add(tLName);

        searchName = new HTH_PromptButton();
        searchName.setFont(new Font("Arial", Font.PLAIN, 18));
        searchName.setSize(21, 21);
        searchName.setLocation(953, 423);
        searchName.addActionListener(new SearchByName());
        add(searchName);
    }

    public class SearchByName implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (tLName.getText().length() > 2) {
                showSearchFrame(tLName.getText().toUpperCase());
            } else if (tFName.getText().length() > 2) {
                showSearchFrame(tFName.getText().toUpperCase());
            }

        }

        private void showSearchFrame(String nameChars) {
            hideContents();
            updateInputDataMap();
            //---member
            String[][] insureData = Repository.getInstance("tr1").getInsureData(nameChars);
            new SearchFrame(insureData);
        }

        private void updateInputDataMap() {
            Util.inputMap.put("referenceNum", tReferenceNumber.getText());
            Util.inputMap.put("phoneNum", tPhoneNum.getText());
            Util.inputMap.put("company", tCompanyCallingFrom.getText());
            Util.inputMap.put("claim", tClaimNumber.getText());

        }

        private void hideContents() {
            Component[] comps = MyFrame.this.getContentPane().getComponents();
            for (Component comp : comps) {

                comp.setVisible(false);
            }
        }


        private JPanel setGroupRowPanel() {
            final JPanel rowPanel = new JPanel();
            rowPanel.setOpaque(true);
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
            rowPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

            Font font = HTH_FONT.deriveFont(15.0f);
            JLabel idField = getLabel(10, font);
            idField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));
            JLabel vipField = getLabel(3, font);
            vipField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));
            JLabel nameField = getLabel(50, font);
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));
            JLabel blockField = getLabel(10, font);
            blockField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));
            JLabel statusField = getLabel(10, font);
            statusField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)));

            int rowW = idField.getPreferredSize().width + vipField.getPreferredSize().width + nameField.getPreferredSize().width + blockField.getPreferredSize().width + statusField.getPreferredSize().width;
            int rowH = idField.getPreferredSize().height;
            rowPanel.setPreferredSize(new Dimension(rowW, rowH));
            rowPanel.setMinimumSize(new Dimension(rowW, rowH));
            rowPanel.setMaximumSize(new Dimension(rowW, rowH));


            idField.setText("Group ID");
            idField.setForeground(Color.BLACK);

            vipField.setText("VIP");
            vipField.setForeground(Color.BLACK);

            nameField.setText("Name");
            nameField.setForeground(Color.BLACK);

            blockField.setText("Block ID");
            blockField.setForeground(Color.BLACK);

            statusField.setText("Status");
            statusField.setForeground(Color.BLACK);


//            if (group == null) {
//                idField.setText("Group ID");
//                idField.setForeground(Color.BLACK);
//
//                vipField.setText("VIP");
//                vipField.setForeground(Color.BLACK);
//
//                nameField.setText("Name");
//                nameField.setForeground(Color.BLACK);
//
//                blockField.setText("Block ID");
//                blockField.setForeground(Color.BLACK);
//
//                statusField.setText("Status");
//                statusField.setForeground(Color.BLACK);
//            } else {
//                rowPanel.addKeyListener(new KeyListener() {
//                    @Override
//                    public void keyPressed(KeyEvent e) {
//                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                            grpField.setText(group.getID());
//                            divField.setText("");
//                            crdField.setText("");
//
//                            //removePromptScreen();
//                            grpField.requestFocus();
//                        }
//                    }
//                    @Override
//                    public void keyReleased(KeyEvent e) {
//                    }
//                    @Override
//                    public void keyTyped(KeyEvent e) {
//                    }
//                });
//
            rowPanel.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    rowPanel.setBackground(Color.LIGHT_GRAY);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    rowPanel.setBackground(Color.WHITE);
                }
            });
//                rowPanel.addMouseListener(new MouseListener() {
//
//                    @Override
//                    public void mouseClicked(MouseEvent arg0) {
//                    }
//
//                    @Override
//                    public void mouseEntered(MouseEvent e) {
//                        rowPanel.setBackground(Color.LIGHT_GRAY);
//                    }
//
//                    @Override
//                    public void mouseExited(MouseEvent e) {
//                        rowPanel.setBackground(Color.WHITE);
//                    }
////
//                    @Override
//                    public void mousePressed(MouseEvent e) {
//                        grpField.setText(group.getID());
//                        divField.setText("");
//                        crdField.setText("");
//
//                       // removePromptScreen();
//                        grpField.requestFocus();
//                    }
//
//                    @Override
//                    public void mouseReleased(MouseEvent e) {
//                    }
//                });
//
//                idField.setText(group.getID());
//                idField.setForeground(new Color(0, 0, 150));
//
//                if (group.isVIP()) {
//                    vipField.setText("VIP");
//                    vipField.setForeground(new Color(0, 0, 150));
//                }
//
//                nameField.setText(group.getName());
//                nameField.setForeground(new Color(0, 0, 150));
//
//                blockField.setText(group.getCarrier());
//                blockField.setForeground(new Color(0, 0, 150));
//
//                switch(group.getStatus()) {
//                    case TERMINATED:
//                        statusField.setText("TERMINATED");
//                        break;
//                    case RUN_OUT:
//                        statusField.setText("RUN OUT");
//                        break;
//                    default:
//                        statusField.setText("ACTIVE");
//                }
//                statusField.setForeground(new Color(0, 0, 150));
//            }
//
            rowPanel.add(idField);
            rowPanel.add(vipField);
            rowPanel.add(nameField);
            rowPanel.add(blockField);
            rowPanel.add(statusField);

            return rowPanel;
        }

        private JLabel getLabel(int charCount, Font font) {
            String str = "";
            for (int c = 0; c <= charCount; c++) {
                str += "M";
            }

            JLabel label = new JLabel();
            label.setFont(font);
            int txtW = label.getFontMetrics(font).stringWidth(str);
            int txtH = label.getFontMetrics(font).getHeight();
            label.setPreferredSize(new Dimension(txtW, txtH));
            label.addMouseListener(new SearchByName.LabelMouseListener(label));

            return label;
        }

        private class LabelMouseListener implements MouseListener {
            private JLabel field;

            public LabelMouseListener(JLabel field) {
                this.field = field;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (field.getParent() != null) {
                    MouseListener[] listeners = field.getParent().getMouseListeners();
                    for (MouseListener listener : listeners) {
                        listener.mouseClicked(e);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (field.getParent() != null) {
                    MouseListener[] listeners = field.getParent().getMouseListeners();
                    for (MouseListener listener : listeners) {
                        listener.mouseEntered(e);
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (field.getParent() != null) {
                    MouseListener[] listeners = field.getParent().getMouseListeners();
                    for (MouseListener listener : listeners) {
                        listener.mouseExited(e);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (field.getParent() != null) {
                    MouseListener[] listeners = field.getParent().getMouseListeners();
                    for (MouseListener listener : listeners) {
                        listener.mousePressed(e);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (field.getParent() != null) {
                    MouseListener[] listeners = field.getParent().getMouseListeners();
                    for (MouseListener listener : listeners) {
                        listener.mouseReleased(e);
                    }
                }
            }
        }
    }

//        private void setPromptFunctionKeys() {
//            String exitKey = "ExitFunction";
//            Action exitAction;
//            exitAction = new AbstractAction(exitKey) {
//                private static final long serialVersionUID = 10108L;
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    removePromptScreen();
//                }
//            };
//            HTH_FunctionButton exitBtn = new HTH_FunctionButton("Exit");
//            exitBtn.setToolTipText("F3=Exit");
//            exitBtn.addActionListener(exitAction);
//            exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
//            exitBtn.getActionMap().put(exitKey, exitAction);
//            setFunctionKeys(new HTH_FunctionButton[] {exitBtn});
//        }
//
//        private void setPromptControlKeys() {
//            controlPanel.removeAll();
//
//            String exitKey = "Exit";
//            Action exitAction;
//            exitAction = new AbstractAction(exitKey) {
//                private static final long serialVersionUID = 10109L;
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    removePromptScreen();
//                }
//            };
//            HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
//            exitBtn.setToolTipText("F3=Exit");
//            exitBtn.addActionListener(exitAction);
//            exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
//            exitBtn.getActionMap().put(exitKey, exitAction);
//            controlPanel.add(exitBtn);
//        }


//    private void showGroupPrompt(final HTH_TextField grpField, final HTH_TextField divField, final HTH_TextField cardField) {
//        compReqFocus = grpField;
//        hideContents();
//        setPromptFunctionKeys();
//        setPromptControlKeys();
//
//        setHeaderTitle("Select Group");
//        selGroupList = groupList;
//
//        JPanel headerPanel = setGroupRowPanel(null, null, null, null);
//        headerPanel.setBounds(15, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
//        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);
//
//        final JScrollPane informationPane = new JScrollPane(setGroupPrompt(grpField, divField, cardField));
//        informationPane.setBounds(15, 40 + headerPanel.getPreferredSize().height, headerPanel.getPreferredSize().width + 15, contentScreen.getSize().height * 80 / 100);
//        informationPane.getVerticalScrollBar().setPreferredSize(new Dimension(15,informationPane.getBounds().height));
//        informationPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
//        promptScreen.add(informationPane, JLayeredPane.MODAL_LAYER);
//
//        JLabel searchLabel = getLabel("Search");
//        searchLabel.setBounds(20, 10, searchLabel.getPreferredSize().width, searchLabel.getPreferredSize().height);
//        promptScreen.add(searchLabel, JLayeredPane.MODAL_LAYER);
//
//        final HTH_TextField searchField = new HTH_TextField(30, HTH_FONT);
//        searchField.setBounds(30 + searchLabel.getPreferredSize().width, 5, searchField.getPreferredSize().width, searchField.getPreferredSize().height);
//        searchField.setFocusTraversalKeysEnabled(false);
//        searchField.addKeyListener(new KeyListener() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if (e.getKeyCode() == KeyEvent.VK_TAB) {
//                    setFocusTraversals(informationPane);
//                    JPanel grpPanel = (JPanel) informationPane.getViewport().getView();
//                    grpPanel.getComponent(0).requestFocus();
//                }
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                if (e.getKeyCode() != KeyEvent.VK_TAB) {
//                    selGroupList = searchGroup(searchField.getText().trim());
//                    informationPane.setViewportView(setGroupPrompt(grpField, divField, cardField));
//                }
//            }
//
//            @Override
//            public void keyTyped(KeyEvent e) {
//            }
//        });
//        searchField.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                selGroupList = searchGroup(searchField.getText().trim());
//                informationPane.setViewportView(setGroupPrompt(grpField, divField, cardField));
//            }
//        });
//        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);
//
//        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);
//
//        revalidate();
//        repaint();
//
//        searchField.requestFocus();
//    }

    private void setCompanyCallingFrom() {
        companyCallingFrom = new JLabel("<html><nobr>Company Calling from <font color='#ffbebe'>*</font></nobr></html>");
        companyCallingFrom.setFont(new Font("Arial", Font.PLAIN, 18));
        companyCallingFrom.setSize(200, 30);
        companyCallingFrom.setLocation(250, 460);
//        container.add(companyCallingFrom);
        add(companyCallingFrom);

        companyValidation = new JLabel("Invalid");
        companyValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        companyValidation.setForeground(Color.RED);
        companyValidation.setSize(40, 30);
        companyValidation.setLocation(505, 460);
        companyValidation.setVisible(false);
//        container.add(companyValidation);
        add(companyValidation);

        tCompanyCallingFrom = new JTextField();
        tCompanyCallingFrom.setFont(new Font("Arial", Font.PLAIN, 18));
        tCompanyCallingFrom.setSize(200, 30);
        tCompanyCallingFrom.setLocation(550, 460);
        ((AbstractDocument) tCompanyCallingFrom.getDocument()).setDocumentFilter(filter);

//        container.add(tCompanyCallingFrom);
        add(tCompanyCallingFrom);

    }

    private void setMember() {
        memberId = new JLabel("SSN/MemberID");
        memberId.setFont(new Font("Arial", Font.PLAIN, 18));
        memberId.setSize(400, 30);
        memberId.setLocation(250, 500);
//        container.add(memberId);
        add(memberId);


        memberIdValidation = new JLabel("Invalid");
        memberIdValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        memberIdValidation.setForeground(Color.RED);
        memberIdValidation.setSize(40, 30);
        memberIdValidation.setLocation(405, 500);
        memberIdValidation.setVisible(false);
//        container.add(memberIdValidation);
        add(memberIdValidation);

        tMemberId = new JTextField();
        tMemberId.setFont(new Font("Arial", Font.PLAIN, 18));
        tMemberId.setSize(100, 30);
        tMemberId.setLocation(550, 500);
//        container.add(tMemberId);
        add(tMemberId);
    }

    private void setClaimNum() {
        claimNumber = new JLabel("Claim Number");
        claimNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        claimNumber.setSize(200, 30);
        claimNumber.setLocation(250, 540);
//        container.add(claimNumber);
        add(claimNumber);

        claimNumberValidation = new JLabel("Invalid");
        claimNumberValidation.setFont(new Font("Arial", Font.PLAIN, 10));
        claimNumberValidation.setForeground(Color.RED);
        claimNumberValidation.setSize(40, 30);
        claimNumberValidation.setLocation(505, 540);
        claimNumberValidation.setVisible(false);
//        container.add(claimNumberValidation);
        add(claimNumberValidation);

        tClaimNumber = new JTextField();
        tClaimNumber.setFont(new Font("Arial", Font.PLAIN, 18));
        tClaimNumber.setSize(90, 30);
        tClaimNumber.setLocation(550, 540);
        ((AbstractDocument) tClaimNumber.getDocument()).setDocumentFilter(filter);

//        container.add(tClaimNumber);
        add(tClaimNumber);


    }

    private void UpdateDocumentListener() {
        //  autoFile  document listener for memberId
        DocumentListener autoFillListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateName();
            }

            private void updateName() {
                String ssn = tMemberId.getText();
                if (ssn.length() == 9) {
                    Repository repo = Repository.getInstance("SALAHM");
                    String fullName = repo.getName(ssn);
                    if (fullName.length() > 2) {
                        String[] fullNameArray = fullName.split(",");
                        String fName = fullNameArray[0];
                        String lName = fullNameArray[1];
                        tFName.setText(fName);
                        tLName.setText(lName);
                    }
                } else {
                    tFName.setText("");
                    tLName.setText("");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateName();
            }
        };
        tMemberId.getDocument().addDocumentListener(autoFillListener);


        //  Reference Number validation listener for memberId
        DocumentListener refNumValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateRefNum();
            }

            private void validateRefNum() {
                String refNum = tReferenceNumber.getText();

                if (refNum.length() > 10) {
                    refNumValidation.setVisible(true);
                } else {
                    refNumValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateRefNum();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateRefNum();
            }
        };
        tReferenceNumber.getDocument().addDocumentListener(refNumValidationListener);


        //  Phone Number validation listener for memberId
        DocumentListener phoneNumValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validatePhoneNum();
            }

            private void validatePhoneNum() {
                String refNum = tPhoneNum.getText();

                if (refNum.length() > 10) {
                    phoneValidation.setVisible(true);
                } else {
                    phoneValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validatePhoneNum();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validatePhoneNum();
            }
        };
        tPhoneNum.getDocument().addDocumentListener(phoneNumValidationListener);


        //  namee validation listener for memberId
        DocumentListener nameValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateName();
            }

            private void validateName() {
                String fName = tFName.getText();
                String lName = tLName.getText();

                if (fName.length() > 25 || lName.length() > 25) {
                    nameValidation.setVisible(true);
                } else {
                    nameValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateName();
            }
        };
        tFName.getDocument().addDocumentListener(nameValidationListener);
        tLName.getDocument().addDocumentListener(nameValidationListener);


        //  ssn/memberId validation listener for memberId
        DocumentListener ssnValidationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateMemberId();
            }

            private void validateMemberId() {
                String memberId = tMemberId.getText();

                if (memberId.length() > 9) {
                    memberIdValidation.setVisible(true);
                } else {
                    memberIdValidation.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateMemberId();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateMemberId();
            }
        };
        tMemberId.getDocument().addDocumentListener(ssnValidationListener);


        //  claimNumber validation listener
//        DocumentListener claimNumValidationListener = new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                validateClaimNum();
//            }
//
//            private void validateClaimNum() {
//                String claimNumber = tClaimNumber.getText();
//                Repository repo = Repository.getInstance(user);
//
//                if (claimNumber.length() > 7 || !repo.validateClaimNumber(claimNumber)) {
//                    claimNumberValidation.setVisible(true);
//                } else {
//                    claimNumberValidation.setVisible(false);
//                }
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                validateClaimNum();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                validateClaimNum();
//            }
//        };
//        tClaimNumber.getDocument().addDocumentListener(claimNumValidationListener);


    }


    private void validateClaimNum() {
        String claimNumber = tClaimNumber.getText();
        Repository repo = Repository.getInstance(user);

        if (claimNumber.length() > 7 || !repo.validateClaimNumber(claimNumber)) {
            claimNumberValidation.setVisible(true);
        } else {
            claimNumberValidation.setVisible(false);
        }
    }

    private void initHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 140));

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        logoPanel.setPreferredSize(new Dimension(0, 105));
        logoPanel.setBackground(Color.WHITE);

        JLabel logoLabel = new JLabel(new ImageIcon(HTH_Image.getImageURL("hth_logo.png")));
        logoPanel.add(logoLabel);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setPreferredSize(new Dimension(0, 35));
        titlePanel.setBackground(new Color(82, 144, 202));

        titleLabel = new JLabel("Customer Service ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        titlePanel.add(titleLabel, BorderLayout.SOUTH);

        headerPanel.add(logoPanel, BorderLayout.NORTH);
        headerPanel.add(titlePanel, BorderLayout.SOUTH);
    }

    private void initFunctionPanel() {
        functionPanel = new JPanel();
        functionPanel.setLayout(new BorderLayout());
        functionPanel.setBackground(new Color(83, 89, 105));
        functionPanel.setPreferredSize(new Dimension(200, 0));

        JPanel dummyPanel = new JPanel();
        dummyPanel.setOpaque(false);
        dummyPanel.setPreferredSize(new Dimension(0, 80));
        functionPanel.add(dummyPanel, BorderLayout.NORTH);

        functionKeyPanel = new JPanel();
        functionKeyPanel.setOpaque(false);
        functionKeyPanel.setLayout(new BoxLayout(functionKeyPanel, BoxLayout.Y_AXIS));

        functionPanel.add(functionKeyPanel, BorderLayout.CENTER);
    }

    private void relocateWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int scMidWidth = screenSize.width / 2;
        int scMidHeight = screenSize.height / 2;
        int frameMidWidth = FRAME_SIZE.width / 2;
        int frameMidHeight = FRAME_SIZE.height / 2;

        setLocation(scMidWidth - frameMidWidth, scMidHeight - frameMidHeight);
    }


    private void exitProgram() {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("---called");
        exitProgram();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}


//        title = new JLabel(" ");
//        title.setFont(new Font("Arial", Font.PLAIN, 20));
//        title.setSize(900, 30);
//        title.setLocation(0, 420);
//        title.setForeground(Color.white);
//        title.setOpaque(true);
//        title.setBackground(Color.decode("#478CCA"));
//        container.add(title);
