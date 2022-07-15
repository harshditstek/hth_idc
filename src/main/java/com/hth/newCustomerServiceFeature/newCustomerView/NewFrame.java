package com.hth.newCustomerServiceFeature.newCustomerView;

import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_PromptButton;
import com.hth.images.HTH_Image;
import com.hth.newCustomerServiceFeature.Repository.Repository;
import com.hth.newCustomerServiceFeature.UppercaseDocumentFilter;
import com.hth.newCustomerServiceFeature.View.MyFrame;
import com.sun.glass.ui.Pixels;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.ParseException;

public class NewFrame
        extends JFrame
        implements ActionListener {
    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);

    // Components of the Form
    private Container c;

    private JLabel title;
    private JLabel name;
    private JTextField tname;
    private JButton bReferenceNumber;
    private JLabel Provider;
    private JComboBox tProvider;
    private JLabel phone;
    private JTextField tphone;
    private MaskFormatter phoneFormatter;
    private JLabel flName;
    private JLabel company;
    private JTextField tcompany;
    private JLabel ssn;
    private JTextField tssn;
    private JLabel claim;
    private JTextField tclaim;

    private  JLabel phoneNotes;

    private JTextField tPhoneNotes;

    private JLabel dayEndReport;
    private  JTextField tDayEndReport;
    private JTextField tfName;
    private JTextField tlName;
    private JButton exit;
    private JButton ok;
    private JPanel headerPanel;
    private JPanel functionPanel;
    private JPanel functionKeyPanel;
    private JLabel titleLabel;
    private JPanel mainPanel;
    String exitKey = "exitBtn";
    private HTH_PromptButton searchName;
    private String providerOrMember[] = {"Provider", "Member"};
    DocumentFilter filter = new UppercaseDocumentFilter();

    public NewFrame() {
        setTitle("New Customer Service");
        setBounds(400, 90, 1180, 800);
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

        //header panel
        setHeaderPanel();
        c.add(mainPanel, BorderLayout.NORTH);

        //Left Menu
        setSideBar();
        c.add(functionPanel, BorderLayout.WEST);


//        title = new JLabel("Customer Service");
//        title.setFont(new Font("Arial", Font.PLAIN, 30));
//        title.setSize(300, 30);
//        title.setLocation(300, 30);
//        title.setBackground(Color.white);
//        c.add(title);

        setBorder();
        setReference();
        setProvider();
        setPhone();

        flName = new JLabel("<html><nobr>First/Last Name <font color='#ffbebe'>*</font></nobr></html>");
        flName.setFont(new Font("Arial", Font.PLAIN, 18));
        flName.setSize(200, 30);
        flName.setLocation(250, 420);
        flName.getText();
        c.add(flName);

        tfName = new JTextField();
        tfName.setFont(new Font("Arial", Font.PLAIN, 15));
        tfName.setSize(200, 30);
        tfName.setLocation(550, 420);
        ((AbstractDocument) tfName.getDocument()).setDocumentFilter(filter);
        tfName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {

                if(!(Character.isLetter(evt.getKeyChar()))){
                    evt.consume();
                }
            }
        });
        c.add(tfName);

        tlName = new JTextField();
        tlName.setFont(new Font("Arial", Font.PLAIN, 15));
        tlName.setSize(200, 30);
        tlName.setLocation(751, 420);
        ((AbstractDocument) tlName.getDocument()).setDocumentFilter(filter);
        tlName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if(!(Character.isLetter(evt.getKeyChar()))){
                    evt.consume();
                }
            }
        });
        c.add(tlName);

        searchName = new HTH_PromptButton();
        searchName.setFont(new Font("Arial", Font.PLAIN, 18));
        searchName.setSize(21, 21);
        searchName.setLocation(955, 423);
        add(searchName);

        company = new JLabel("<html><nobr>Company Calling From <font color='#ffbebe'>*</font></nobr></html>");
        company.setFont(new Font("Arial", Font.PLAIN, 18));
        company.setSize(200, 30);
        company.setLocation(250, 460);
        c.add(company);

        tcompany = new JTextField();
        tcompany.setFont(new Font("Arial", Font.PLAIN, 15));
        tcompany.setSize(190, 30);
        tcompany.setLocation(550, 460);
        c.add(tcompany);

        ssn = new JLabel("SSN/Member Id");
        ssn.setFont(new Font("Arial", Font.PLAIN, 18));
        ssn.setSize(200, 30);
        ssn.setLocation(250, 500);
        c.add(ssn);

        tssn = new JTextField();
        tssn.setFont(new Font("Arial", Font.PLAIN, 15));
        tssn.setSize(190, 30);
        tssn.setLocation(550, 500);
        c.add(tssn);

        claim = new JLabel("Claim Number");
        claim.setFont(new Font("Arial", Font.PLAIN, 18));
        claim.setSize(200, 30);
        claim.setLocation(250, 540);
        c.add(claim);

        tclaim = new JTextField();
        tclaim.setFont(new Font("Arial", Font.PLAIN, 15));
        tclaim.setSize(190, 30);
        tclaim.setLocation(550, 540);
        c.add(tclaim);

        phoneNotes = new JLabel("F7 Phone Notes");
        phoneNotes.setFont(new Font("Arial", Font.PLAIN, 18));
        phoneNotes.setSize(200, 30);
        phoneNotes.setLocation(250, 580);
        c.add(phoneNotes);

        tPhoneNotes = new JTextField();
        tPhoneNotes.setFont(new Font("Arial", Font.PLAIN, 15));
        tPhoneNotes.setSize(190, 30);
        tPhoneNotes.setLocation(550, 580);
        c.add(tPhoneNotes);

        dayEndReport = new JLabel("Report At End Of Day");
        dayEndReport.setFont(new Font("Arial", Font.PLAIN, 18));
        dayEndReport.setSize(200, 30);
        dayEndReport.setLocation(250, 620);
        c.add(dayEndReport);

        tDayEndReport = new JTextField();
        tDayEndReport.setFont(new Font("Arial", Font.PLAIN, 15));
        tDayEndReport.setSize(190, 30);
        tDayEndReport.setLocation(550, 620);
        c.add(tDayEndReport);


        exit = new HTH_ControlButton("Exit");
        exit.setToolTipText("F3=Exit");
        exit.addActionListener(exitAction);
        exit.setFont(new Font("Arial", Font.PLAIN, 14));
        exit.setSize(75, 27);
        exit.setLocation(815, 660);
        exit.addActionListener(exitAction);
        c.add(exit);

        ok = new HTH_ControlButton("Ok");
        ok.setFont(new Font("Arial", Font.PLAIN, 15));
        ok.setSize(75, 27);
        ok.setLocation(900, 660);
        ok.addActionListener(this);
        c.add(ok);

        this.getContentPane().setBackground(Color.WHITE);

        setVisible(true);
    }

    public void setReference(){
        name = new JLabel("Refrence Number");
        name.setFont(new Font("Arial", Font.PLAIN, 18));
        name.setSize(200, 30);
        name.setLocation(250, 300);
        c.add(name);

        tname = new JTextField();
        tname.setFont(new Font("Arial", Font.PLAIN, 15));
        tname.setSize(100, 30);
        tname.setLocation(550, 300);
        c.add(tname);

        bReferenceNumber = new HTH_ControlButton("Generate");
        bReferenceNumber.setSize(75, 28);
        bReferenceNumber.setLocation(660, 300);
        bReferenceNumber.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Repository repo = Repository.getInstance("");
                Integer reference = repo.generateRefNum();
                tname.setText(reference.toString());
            }
        });
        add(bReferenceNumber);

    }

    public void setProvider(){
        Provider = new JLabel("Provider Or Member");
        Provider.setFont(new Font("Arial", Font.PLAIN, 18));
        Provider.setSize(200, 30);
        Provider.setLocation(250, 340);
        c.add(Provider);

        tProvider = new JComboBox(providerOrMember);
        tProvider.setFont(new Font("Arial", Font.PLAIN, 15));
        tProvider.setSize(100, 30);
        tProvider.setLocation(550, 340);
        tProvider.setForeground(new Color(79, 145, 200));
        tProvider.setBackground(Color.white);
        c.add(tProvider);

    }

    public void setPhone(){
        phone = new JLabel("<html><nobr>Phone Number <font color='#ffbebe'>*</font></nobr></html>");
        phone.setFont(new Font("Arial", Font.PLAIN, 18));
        phone.setSize(200, 30);
        phone.setLocation(250, 380);
        c.add(phone);

        try {
            phoneFormatter = new MaskFormatter("(+##) #####-#####");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tphone = new JFormattedTextField(phoneFormatter);
        tphone.setFont(new Font("Arial", Font.PLAIN, 15));
        tphone.setSize(130, 30);
        tphone.setLocation(550, 380);
        c.add(tphone);
    }

    Action exitAction = new AbstractAction(exitKey) {
        private static final long serialVersionUID = 10110L;

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private void setBorder() {
        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        // borderPanel.setBackground(Color.WHITE);
        borderPanel.setBounds(230, 260, 800, 450);
        borderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "New Customer Service",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        add(borderPanel);
    }
    public void setHeaderPanel(){
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setSize(1180,200);
        mainPanel.setLocation(0,0);
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

    public void setSideBar(){
        functionPanel = new JPanel();
        functionPanel.setLayout(new BorderLayout());
        functionPanel.setBackground(new Color(83, 89, 105));
        //functionPanel.setPreferredSize(new Dimension(200, 0));
        functionPanel.setLocation(0,200);
        functionPanel.setSize(220,580);

        JButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        //exitBtn.setBackground(Color.yellow);
        exitBtn.setSize(220, 27);
        exitBtn.setLocation(0, 100);
        exitBtn.addActionListener(exitAction);
        //exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        functionPanel.add(exitBtn);


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