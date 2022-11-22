package com.hth.id_card.user_interface.printer;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.hth.backend.beans.*;
import com.hth.backend.iSeries;
import com.hth.id_card.HTH_IDC;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_DateField;
import com.hth.id_card.user_interface.HTH_Dialog;
import com.hth.id_card.user_interface.HTH_Frame;
import com.hth.id_card.user_interface.HTH_FunctionButton;
import com.hth.id_card.user_interface.HTH_PromptButton;
import com.hth.id_card.user_interface.HTH_TextField;
import com.hth.util.Division;
import com.hth.util.GroupMaster;
import com.hth.util.IDCard;
import com.hth.util.enums.CardType;

public class ID_PrinterSelection extends HTH_Frame implements WindowListener {
    //private static HTH_PromptButton searchName = null;
    private static final long serialVersionUID = 102L;
    private static final String SELECTION_TITLE = "ID Card Printing";
    private static Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);

    private static CardType typeChosen;
    private static ID_Printer printerWindow = null;
    private static ID_PrinterSelection session = null;
    private boolean isShiftPressed;
    private GroupMaster[] groupList, selGroupList;
    private Division[] divisionList, selDivisionList;
    private Component compReqFocus;
    private JPanel controlPanel;
    private JLayeredPane contentScreen, promptScreen;
    private HTH_TextField optionField;
    private HTH_TextField grpField, divField, nameStrField, nameEndField;
    private HTH_DateField dateField;
    private HTH_FunctionButton[] functionKeys;
    private HTH_ControlButton[] controlKeys;

    public static String group = "";
    public static String nameS = "";
    public static String nameE = "";
    public static String date = "";
    public static JTable logTable;
    public int buttonCount = 0;
    public static HTH_PromptButton searchName;
    public static HTH_TextField field;
    List<HTH_PromptButton> list = new ArrayList<HTH_PromptButton>();
    Map<Integer, HTH_TextField> fields = new HashMap<Integer, HTH_TextField>();
    Map<Integer, JLabel> names = new HashMap<Integer, JLabel>();
    Map<Integer, JLabel> groupIds = new HashMap<Integer, JLabel>();
    private JLabel name;
    private JLabel groupId;

    private int textFieldUpdate = 0;

    public ID_PrinterSelection() {
        super(SELECTION_TITLE);
        addWindowListener(this);

        initComponents();

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(contentScreen, BorderLayout.CENTER);
        contentPanel.add(controlPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);

        session = this;
    }

    public void

    begin() {
        promptScreen.setBounds(0, 0, contentScreen.getSize().width, contentScreen.getSize().height);
        setSelectionScreen();
//		startPrint();
    }

    private void initComponents() {
        UIManager.getDefaults().put("ToolTipUI", "javax.swing.plaf.basic.BasicToolTipUI");

        typeChosen = CardType.NONE;

        promptScreen = new JLayeredPane();
        promptScreen.setOpaque(true);
        promptScreen.setBackground(Color.WHITE);

        contentScreen = new JLayeredPane();
        contentScreen.setOpaque(false);

        controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        optionField = new HTH_TextField(11, HTH_FONT);
        grpField = new HTH_TextField(10, HTH_FONT);
        divField = new HTH_TextField(3, HTH_FONT);
        nameStrField = new HTH_TextField(10, HTH_FONT);
        nameEndField = new HTH_TextField(10, HTH_FONT);
        dateField = new HTH_DateField(8, HTH_FONT);

        functionKeys = new HTH_FunctionButton[0];
    }

    private void setSelectionScreen() {
        resetContent();
        setFunctionKeys(new HTH_FunctionButton[0]);
        setSelectionControlKeys();
        setSelectionLabels();
        setSelectionInputFields();

        revalidate();
        repaint();

        optionField.setText("");
        optionField.requestFocus();
    }

    private void setSelectionControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10102L;

            @Override
            public void actionPerformed(ActionEvent e) {
                typeChosen = CardType.NONE;
                for (CardType type : CardType.values()) {
                    if (type.toString().equals(optionField.getText())) {
                        typeChosen = type;
                        break;
                    }
                }

                switch (typeChosen) {
                    case GROUP:
                        setGroupPrinter();
                        groupList = GRPMST.getGroupList();
                        break;
                    case DIVISION:
                        setDivisionPrinter();
                        groupList = GRPMST.getGroupList();
                        break;
                    case EMPLOYEE:
                        setFamilyPrinter();
                        break;
                    case OUTSTANDING:
                        setOutstandingPrinter();
                        groupList = GRPMST.getGroupList();
                        break;
                    case INDIVIDUAL:
                        setIndividualPrinter();
                        break;
                    default:
                        String errMsg = "Invalid Type Code";
                        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
                        showMessage("Card Run Type Not Found", errMsg, dialogSize, JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        controlPanel.add(getGhostPanel(new Dimension(25, 25)));

        HTH_ControlButton okBtn = new HTH_ControlButton("OK");
        okBtn.setToolTipText("Enter=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        okBtn.addMouseListener(new PromptMouseListener());
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    private void setSelectionLabels() {
        int endLineX = 200;

        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        borderPanel.setBounds(30, 100, contentScreen.getSize().width * 8 / 10, 150);
        borderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Select Run Type",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(borderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel typeLabel = getLabel("ID Card Run Type");
        typeLabel.setBounds(endLineX - typeLabel.getPreferredSize().width, 160, typeLabel.getPreferredSize().width, typeLabel.getPreferredSize().height);
        contentScreen.add(typeLabel, JLayeredPane.DEFAULT_LAYER);
    }

    private void setSelectionInputFields() {
        int strLineX = 250;

        optionField.setBounds(strLineX, 155, optionField.getPreferredSize().width, optionField.getPreferredSize().height);
        contentScreen.add(optionField, JLayeredPane.DEFAULT_LAYER);
        optionField.requestFocus();

        HTH_PromptButton optPromptBtn = new HTH_PromptButton();
        optPromptBtn.setBounds(strLineX + optionField.getPreferredSize().width + 10, 160, optPromptBtn.getPreferredSize().width, optPromptBtn.getPreferredSize().height);
        optPromptBtn.addActionListener(new OptionPromptActionListener());
        optPromptBtn.addKeyListener(new OptionPromptKeyListener());
        optPromptBtn.addMouseListener(new PromptMouseListener());
        optPromptBtn.addFocusListener(new PromptFocusListener(optPromptBtn));
        optPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(optPromptBtn, JLayeredPane.DEFAULT_LAYER);
    }

    private void setGroupPrinter() {
        resetContent();
        setFunctionKeys(new HTH_FunctionButton[0]);
        setGroupControlKeys();
        setGroupLabels();
        setGroupInputFields();

        revalidate();
        repaint();
    }

    private void setGroupControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10102L;

            @Override
            public void actionPerformed(ActionEvent e) {
                loadGroupList();
            }
        };


        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        controlPanel.add(getGhostPanel(new Dimension(25, 25)));

        HTH_ControlButton okBtn = new HTH_ControlButton("OK");
        okBtn.setToolTipText("Enter=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    private void setGroupLabels() {
        int endLineX = 200;

        JPanel reqBorderPanel = new JPanel();
        reqBorderPanel.setOpaque(false);
        reqBorderPanel.setBounds(30, 100, contentScreen.getSize().width * 8 / 10, 200);
        reqBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Please Enter Group And Name Range",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(reqBorderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel grpLabel = getLabel("Group ID Print");
        grpLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 150, grpLabel.getPreferredSize().width, grpLabel.getPreferredSize().height);
        contentScreen.add(grpLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel nameStrLabel = getLabel("Beginning Name Range");
        nameStrLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 200, nameStrLabel.getPreferredSize().width, nameStrLabel.getPreferredSize().height);
        contentScreen.add(nameStrLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel nameEndLabel = getLabel("Ending Name Range");
        nameEndLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 250, nameEndLabel.getPreferredSize().width, nameEndLabel.getPreferredSize().height);
        contentScreen.add(nameEndLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel optBorderPanel = new JPanel();
        optBorderPanel.setOpaque(false);
        optBorderPanel.setBounds(30, 300, contentScreen.getSize().width * 8 / 10, 150);
        optBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Optional",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(optBorderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel dateLabel = getLabel("Run Date");
        dateLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 350, dateLabel.getPreferredSize().width, dateLabel.getPreferredSize().height);
        contentScreen.add(dateLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel msg1Label = getLabel("*Run Date will be used to determine if Members are active as of this date.");
        msg1Label.setForeground(Color.RED);
        msg1Label.setBounds(35, 400, msg1Label.getPreferredSize().width, msg1Label.getPreferredSize().height);
        contentScreen.add(msg1Label, JLayeredPane.DEFAULT_LAYER);

        JLabel msg2Label = getLabel("Leaving this field blank will default to the current date.");
        msg2Label.setForeground(Color.RED);
        msg2Label.setBounds(35, 400 + msg1Label.getPreferredSize().height, msg2Label.getPreferredSize().width, msg2Label.getPreferredSize().height);
        contentScreen.add(msg2Label, JLayeredPane.DEFAULT_LAYER);
    }

    private void setGroupInputFields() {
        int strLineX = 350;
        grpField.setBounds(strLineX, 145, grpField.getPreferredSize().width, grpField.getPreferredSize().height);
        contentScreen.add(grpField, JLayeredPane.DEFAULT_LAYER);
        grpField.requestFocus();

        HTH_PromptButton grpPromptBtn = new HTH_PromptButton();
        grpPromptBtn.setBounds(strLineX + grpField.getPreferredSize().width + 10, 150, grpPromptBtn.getPreferredSize().width, grpPromptBtn.getPreferredSize().height);
        grpPromptBtn.addActionListener(new GroupPromptActionListener());
        grpPromptBtn.addKeyListener(new GroupPromptKeyListener());
        grpPromptBtn.addMouseListener(new PromptMouseListener());
        grpPromptBtn.addFocusListener(new PromptFocusListener(grpPromptBtn));
        grpPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(grpPromptBtn, JLayeredPane.DEFAULT_LAYER);

        nameStrField.setBounds(strLineX, 195, nameStrField.getPreferredSize().width, nameStrField.getPreferredSize().height);
        contentScreen.add(nameStrField, JLayeredPane.DEFAULT_LAYER);

        nameEndField.setBounds(strLineX, 245, nameEndField.getPreferredSize().width, nameEndField.getPreferredSize().height);
        contentScreen.add(nameEndField, JLayeredPane.DEFAULT_LAYER);

        dateField.setBounds(strLineX, 345, dateField.getPreferredSize().width, dateField.getPreferredSize().height);
        contentScreen.add(dateField, JLayeredPane.DEFAULT_LAYER);
    }

    private void setDivisionPrinter() {
        resetContent();
        setFunctionKeys(new HTH_FunctionButton[0]);
        setDivisionControlKeys();
        setDivisionLabels();
        setDivisionInputFields();

        revalidate();
        repaint();
    }

    private void setDivisionControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10102L;

            @Override
            public void actionPerformed(ActionEvent e) {
                loadDivisionList();
            }
        };

        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        controlPanel.add(getGhostPanel(new Dimension(25, 25)));

        HTH_ControlButton okBtn = new HTH_ControlButton("OK");
        okBtn.setToolTipText("Enter=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    private void setDivisionLabels() {
        int endLineX = 200;

        JPanel reqBorderPanel = new JPanel();
        reqBorderPanel.setOpaque(false);
        reqBorderPanel.setBounds(30, 100, contentScreen.getSize().width * 8 / 10, 250);
        reqBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Please Enter Group, Division And Name Range",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(reqBorderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel grpLabel = getLabel("Group ID");
        grpLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 150, grpLabel.getPreferredSize().width, grpLabel.getPreferredSize().height);
        contentScreen.add(grpLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel divLabel = getLabel("Division");
        divLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 200, divLabel.getPreferredSize().width, divLabel.getPreferredSize().height);
        contentScreen.add(divLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel nameStrLabel = getLabel("Beginning Name Range");
        nameStrLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 250, nameStrLabel.getPreferredSize().width, nameStrLabel.getPreferredSize().height);
        contentScreen.add(nameStrLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel nameEndLabel = getLabel("Ending Name Range");
        nameEndLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 300, nameEndLabel.getPreferredSize().width, nameEndLabel.getPreferredSize().height);
        contentScreen.add(nameEndLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel optBorderPanel = new JPanel();
        optBorderPanel.setOpaque(false);
        optBorderPanel.setBounds(30, 350, contentScreen.getSize().width * 8 / 10, 150);
        optBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Optional",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(optBorderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel dateLabel = getLabel("Run Date");
        dateLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 400, dateLabel.getPreferredSize().width, dateLabel.getPreferredSize().height);
        contentScreen.add(dateLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel msg1Label = getLabel("*Run Date will be used to determine if Members are active as of this date.");
        msg1Label.setForeground(Color.RED);
        msg1Label.setBounds(35, 450, msg1Label.getPreferredSize().width, msg1Label.getPreferredSize().height);
        contentScreen.add(msg1Label, JLayeredPane.DEFAULT_LAYER);

        JLabel msg2Label = getLabel("Leaving this field blank will default to the current date.");
        msg2Label.setForeground(Color.RED);
        msg2Label.setBounds(35, 450 + msg1Label.getPreferredSize().height, msg2Label.getPreferredSize().width, msg2Label.getPreferredSize().height);
        contentScreen.add(msg2Label, JLayeredPane.DEFAULT_LAYER);
    }

    private void setDivisionInputFields() {
        int strLineX = 350;
        grpField.setBounds(strLineX, 145, grpField.getPreferredSize().width, grpField.getPreferredSize().height);
        contentScreen.add(grpField, JLayeredPane.DEFAULT_LAYER);
        grpField.requestFocus();

        HTH_PromptButton grpPromptBtn = new HTH_PromptButton();
        grpPromptBtn.setBounds(strLineX + grpField.getPreferredSize().width + 10, 150, grpPromptBtn.getPreferredSize().width, grpPromptBtn.getPreferredSize().height);
        grpPromptBtn.addActionListener(new GroupPromptActionListener());
        grpPromptBtn.addKeyListener(new GroupPromptKeyListener());
        grpPromptBtn.addMouseListener(new PromptMouseListener());
        grpPromptBtn.addFocusListener(new PromptFocusListener(grpPromptBtn));
        grpPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(grpPromptBtn, JLayeredPane.DEFAULT_LAYER);

        divField.setBounds(strLineX, 195, divField.getPreferredSize().width, divField.getPreferredSize().height);
        contentScreen.add(divField, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton divPromptBtn = new HTH_PromptButton();
        divPromptBtn.setBounds(strLineX + divField.getPreferredSize().width + 10, 200, divPromptBtn.getPreferredSize().width, divPromptBtn.getPreferredSize().height);
        divPromptBtn.addActionListener(new DivisionPromptActionListener());
        divPromptBtn.addKeyListener(new DivisionPromptKeyListener());
        divPromptBtn.addMouseListener(new PromptMouseListener());
        divPromptBtn.addFocusListener(new PromptFocusListener(divPromptBtn));
        divPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(divPromptBtn, JLayeredPane.DEFAULT_LAYER);

        nameStrField.setBounds(strLineX, 245, nameStrField.getPreferredSize().width, nameStrField.getPreferredSize().height);
        contentScreen.add(nameStrField, JLayeredPane.DEFAULT_LAYER);

        nameEndField.setBounds(strLineX, 295, nameEndField.getPreferredSize().width, nameEndField.getPreferredSize().height);
        contentScreen.add(nameEndField, JLayeredPane.DEFAULT_LAYER);

        dateField.setBounds(strLineX, 395, dateField.getPreferredSize().width, dateField.getPreferredSize().height);
        contentScreen.add(dateField, JLayeredPane.DEFAULT_LAYER);
    }

    private void setOutstandingPrinter() {
        resetContent();
        setFunctionKeys(new HTH_FunctionButton[0]);
        setOutstandingControlKeys();
        setOutstandingLabels();
        setOutstandingInputFields();

        revalidate();
        repaint();
    }

    private void setOutstandingControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10102L;

            @Override
            public void actionPerformed(ActionEvent e) {
                loadOutstandingList();
            }
        };

        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        controlPanel.add(getGhostPanel(new Dimension(25, 25)));

        HTH_ControlButton okBtn = new HTH_ControlButton("OK");
        okBtn.setToolTipText("Enter=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    private void setOutstandingLabels() {
        int endLineX = 200;

        JPanel reqBorderPanel = new JPanel();
        reqBorderPanel.setOpaque(false);
        reqBorderPanel.setBounds(30, 100, contentScreen.getSize().width * 8 / 10, 150);
        reqBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Please Enter Group To Print",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(reqBorderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel grpLabel = getLabel("Group ID");
        grpLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 150, grpLabel.getPreferredSize().width, grpLabel.getPreferredSize().height);
        contentScreen.add(grpLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel msg1Label = getLabel("*Leave blank for all Groups.");
        msg1Label.setForeground(Color.RED);
        msg1Label.setBounds(35, 200, msg1Label.getPreferredSize().width, msg1Label.getPreferredSize().height);
        contentScreen.add(msg1Label, JLayeredPane.DEFAULT_LAYER);
    }

    private void setOutstandingInputFields() {
        int strLineX = 250;
        grpField.setBounds(strLineX, 145, grpField.getPreferredSize().width, grpField.getPreferredSize().height);
        contentScreen.add(grpField, JLayeredPane.DEFAULT_LAYER);
        grpField.requestFocus();

        HTH_PromptButton grpPromptBtn = new HTH_PromptButton();
        grpPromptBtn.setBounds(strLineX + grpField.getPreferredSize().width + 10, 150, grpPromptBtn.getPreferredSize().width, grpPromptBtn.getPreferredSize().height);
        grpPromptBtn.addActionListener(new GroupPromptActionListener());
        grpPromptBtn.addKeyListener(new GroupPromptKeyListener());
        grpPromptBtn.addMouseListener(new PromptMouseListener());
        grpPromptBtn.addFocusListener(new PromptFocusListener(grpPromptBtn));
        grpPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(grpPromptBtn, JLayeredPane.DEFAULT_LAYER);
    }

    private void setFamilyPrinter() {
        resetContent();
        setFunctionKeys(new HTH_FunctionButton[0]);
        setFamilyControlKeys();
        setIndividualLabels();
        setIndividualInputFields();

        revalidate();
        repaint();

        compReqFocus.requestFocus();
    }

    private void setFamilyControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10102L;

            @Override
            public void actionPerformed(ActionEvent e) {
                loadFamilyList();
            }
        };

        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        controlPanel.add(getGhostPanel(new Dimension(25, 25)));

        HTH_ControlButton okBtn = new HTH_ControlButton("OK");
        okBtn.setToolTipText("Enter=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    private void setIndividualPrinter() {
        resetContent();
        setFunctionKeys(new HTH_FunctionButton[0]);
        setIndividualControlKeys();
        setIndividualLabels();
        setIndividualInputFields();

        revalidate();
        repaint();

        compReqFocus.requestFocus();
    }

    private void setIndividualControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10101L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10102L;

            @Override
            public void actionPerformed(ActionEvent e) {
                loadIndividualList();
            }
        };

        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        controlPanel.add(getGhostPanel(new Dimension(25, 25)));

        HTH_ControlButton okBtn = new HTH_ControlButton("OK");
        okBtn.setToolTipText("Enter=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    private void setIndividualLabels() {
        JPanel reqBorderPanel = new JPanel();
        reqBorderPanel.setOpaque(false);
        reqBorderPanel.setBounds(10, 10, contentScreen.getSize().width - 20, contentScreen.getSize().height - 20);
        reqBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Please Enter ID's To Prints",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));

        reqBorderPanel.setFocusable(true);
        reqBorderPanel.setRequestFocusEnabled(true);
        contentScreen.add(reqBorderPanel, JLayeredPane.DEFAULT_LAYER);
        contentScreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String[] name;
                super.mouseClicked(e);
                Component obj = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if(obj.getName()!=null) {
                    textFieldUpdate = Integer.valueOf(obj.getName());
                    showLabel(textFieldUpdate);
                }
            }
        });

        int fieldCounter = 1;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 3; col++) {
                JLabel label = getLabel("ID " + fieldCounter + ":");

                name = new JLabel("Name");
                name.setFont(new Font("Arial", Font.BOLD, 16));

                groupId = new JLabel("Group");
                groupId.setFont(new Font("Arial", Font.BOLD, 16));

                label.setBounds(30 + (350 * col), 50 + (95 * row), label.getPreferredSize().width, label.getPreferredSize().height);
                name.setBounds(30 + (350 * col), 80 + (95 * row), 220, 30);
                groupId.setBounds(30 + (350 * col), 110 + (95 * row), 220, 30);
                contentScreen.add(label, JLayeredPane.DEFAULT_LAYER);
                contentScreen.add(name, JLayeredPane.DEFAULT_LAYER);
                contentScreen.add(groupId, JLayeredPane.DEFAULT_LAYER);

                name.setVisible(false);
                groupId.setVisible(false);
                names.put(fieldCounter, name);
                groupIds.put(fieldCounter, groupId);

                fieldCounter++;
            }
        }
    }

    private void setIndividualInputFields() {
        int buttonCounter = 1;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 3; col++) {
                field = new HTH_TextField(15, HTH_FONT, new Dimension(150, 32));
                field.setName(String.valueOf(buttonCounter));
                field.setActionCommand(String.valueOf(buttonCounter));
                searchName = new HTH_PromptButton();
                searchName.setFont(new Font("Arial", Font.PLAIN, 18));
                searchName.setBounds(90 + (350 * col) + field.getPreferredSize().width + 5, 50 + (95 * row), 21, 21);
                searchName.setActionCommand(String.valueOf(buttonCounter));
                field.setBounds(90 + (350 * col), 45 + (95 * row), field.getPreferredSize().width, field.getPreferredSize().height);
                contentScreen.add(field, JLayeredPane.DEFAULT_LAYER);
                contentScreen.add(searchName, JLayeredPane.DEFAULT_LAYER);
                list.add(searchName);
                fields.put(buttonCounter, field);
                field.requestFocus();
                field.addFocusListener(
                        new FocusListener() {
                            @Override
                            public void focusGained(FocusEvent e) {

                            }

                            @Override
                            public void focusLost(FocusEvent e) {
                                textFieldUpdate = Integer.valueOf(e.getComponent().getName());
                                showLabel(textFieldUpdate);
                            }
                        }
                );
                if (row == 0 && col == 0) {
                    compReqFocus = field;
                }
                for (Map.Entry<Integer, HTH_TextField> field : fields.entrySet()) {
                    field.getValue().addKeyListener(new KeyListener() {
                        @Override
                        public void keyPressed(KeyEvent e) {
//                            if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
//                                System.out.println("hello");
//                                //textFieldUpdate = Integer.valueOf(field.getValue().getName());
//                                //showLabel(textFieldUpdate);
//                            }
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            if (e.getKeyCode() != KeyEvent.VK_TAB) {

                            }
                        }

                        @Override
                        public void keyTyped(KeyEvent e) {
                            textFieldUpdate = Integer.valueOf(field.getValue().getName());
                            names.get(textFieldUpdate).setText("");
                            groupIds.get(textFieldUpdate).setText("");
                        }
                    });


                }
                //field.addMouseListener(mouse);
                buttonCounter++;
                searchName.addActionListener(new SearchByName());
                searchName.addMouseListener(new ID_PrinterSelection.PromptMouseListener());
            }
        }
    }

    public void showLabel(int textFieldUpdate){
        String[] name = null;
        if(names.get(textFieldUpdate).getText().equals("")) {
            if (fields.get(textFieldUpdate).getText().length() >= 7) {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                name = INSURE.getInsureData(fields.get(textFieldUpdate).getText().toUpperCase());
                if (name != null) {
                    names.get(textFieldUpdate).setVisible(true);
                    names.get(textFieldUpdate).setText(name[0].trim() + " " + name[1].trim());
                    groupIds.get(textFieldUpdate).setVisible(true);
                    groupIds.get(textFieldUpdate).setText(name[2].trim());
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    public class SearchByName implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evtt) {
            buttonCount = Integer.valueOf(evtt.getActionCommand());
            showIdRecord();
        }
    }

    public void showIdRecord() {
        List<String[]> newResultList = INSURE.getInsureList();
        String[] result;
        String[][] data = new String[newResultList.size()][];
        for (int idx = 0; idx < newResultList.size(); idx++) {
            result = newResultList.get(idx);
            data[idx] = result;
        }
        String[] columnNames = {"First Name", "Last Name", "MemberID", "EmployeeID"};
        IDRecord rt = new IDRecord();
        logTable = rt.idRecord(columnNames, data, false, true);
        logTable.setBackground(Color.white);
        ListSelectionModel listModel = logTable.getSelectionModel();
        listModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModel.addListSelectionListener(auditLogList);
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
                    fields.get(buttonCount).setText(tm.getValueAt(sel[0], 2).toString().trim());
                    fields.get(buttonCount).getName();
                    names.get(buttonCount).setVisible(true);
                    names.get(buttonCount).setText(tm.getValueAt(sel[0], 0).toString().trim() + " " + tm.getValueAt(sel[0], 1).toString().trim());
                    groupIds.get(buttonCount).setVisible(true);
                    groupIds.get(buttonCount).setText(tm.getValueAt(sel[0], 3).toString().trim());
                }
            }
        }
    };

    private void resetContent() {
        setHeaderTitle(null);
        controlPanel.removeAll();
        contentScreen.removeAll();

        grpField.setText("");
        divField.setText("");
        nameStrField.setText("");
        nameEndField.setText("");
        dateField.setText("");
    }

    private void hideContents() {
        Component[] comps = contentScreen.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        for (Component comp : comps) {
            comp.setVisible(false);
        }
    }

    private void removePromptScreen() {
        promptScreen.removeAll();
        contentScreen.remove(promptScreen);

        Component[] comps = contentScreen.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        for (Component comp : comps) {
            comp.setVisible(true);
        }


        restoreFunctionKeys();
        restoreControlKeys();
        revalidate();
        repaint();

        if (compReqFocus != null) {
            compReqFocus.requestFocus();
        }
    }

    private void restoreControlKeys() {
        controlPanel.removeAll();

        if (controlKeys.length >= 1) {
            controlPanel.add(controlKeys[0]);
            for (int idx = 1; idx < controlKeys.length; idx++) {
                controlPanel.add(getGhostPanel(new Dimension(25, 25)));
                controlPanel.add(controlKeys[idx]);
            }
        }
    }

    private void restoreFunctionKeys() {
        setFunctionKeys(functionKeys);
    }

    private void setFunctionKeys(HTH_FunctionButton[] buttons) {
        resetFunctionKeys();
        addFunctionKeys(buttons);
    }

    private JPanel getGhostPanel(Dimension size) {
        JPanel ghostPanel = new JPanel();
        ghostPanel.setPreferredSize(size);
        ghostPanel.setMaximumSize(size);
        ghostPanel.setMinimumSize(size);
        ghostPanel.setOpaque(false);

        return ghostPanel;
    }

    private JLabel getLabel(String txt) {
        JLabel label = new JLabel(txt);
        label.setFont(HTH_FONT);
        int txtW = label.getFontMetrics(HTH_FONT).stringWidth(txt);
        int txtH = label.getFontMetrics(HTH_FONT).getHeight();
        label.setPreferredSize(new Dimension(txtW, txtH));

        return label;
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
        label.addMouseListener(new LabelMouseListener(label));

        return label;
    }

    private void showOptionPrompt() {
        hideContents();
        setPromptFunctionKeys();
        setPromptControlKeys();

        setHeaderTitle("Select Card Run Type");

        JPanel headerPanel = setOptionRowPanel(CardType.NONE);
        headerPanel.setBounds(15, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setOptionPrompt());
        informationPane.setBounds(15, 40 + headerPanel.getPreferredSize().height, headerPanel.getPreferredSize().width + 15, headerPanel.getPreferredSize().height * 5 + 5);
        informationPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, informationPane.getBounds().height));
        informationPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        promptScreen.add(informationPane, JLayeredPane.MODAL_LAYER);
        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();
        setFocusTraversals(informationPane);
        JPanel optPanel = (JPanel) informationPane.getViewport().getView();
        optPanel.getComponent(0).requestFocus();
    }

    private JPanel setOptionRowPanel(final CardType type) {
        final JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(true);
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        rowPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        Font font = HTH_FONT;
        JLabel typeField = getLabel(11, font);
        typeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JLabel descField = getLabel(22, font);
        descField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        int rowW = typeField.getPreferredSize().width + descField.getPreferredSize().width;
        int rowH = typeField.getPreferredSize().height;
        rowPanel.setPreferredSize(new Dimension(rowW, rowH));
        rowPanel.setMinimumSize(new Dimension(rowW, rowH));
        rowPanel.setMaximumSize(new Dimension(rowW, rowH));

        if (type == CardType.NONE) {
            typeField.setText("Type");
            typeField.setForeground(Color.BLACK);

            descField.setText("Description");
            descField.setForeground(Color.BLACK);
        } else {
            rowPanel.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        optionField.setText(type.toString());

                        removePromptScreen();
                        optionField.requestFocus();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                }
            });
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
            rowPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent arg0) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    rowPanel.setBackground(Color.LIGHT_GRAY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    rowPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    optionField.setText(type.toString());

                    removePromptScreen();
                    optionField.requestFocus();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });

            typeField.setText(type.toString());
            typeField.setForeground(new Color(0, 0, 150));

            descField.setText(type.description());
            descField.setForeground(new Color(0, 0, 150));
        }

        rowPanel.add(typeField);
        rowPanel.add(descField);

        return rowPanel;
    }

    private JPanel setOptionPrompt() {
        JPanel optionPanel = new JPanel();
        optionPanel.setBackground(Color.WHITE);
        optionPanel.setLayout(new BoxLayout(optionPanel, BoxLayout.Y_AXIS));

        optionPanel.add(setOptionRowPanel(CardType.GROUP));
        optionPanel.add(setOptionRowPanel(CardType.DIVISION));
        optionPanel.add(setOptionRowPanel(CardType.EMPLOYEE));
        optionPanel.add(setOptionRowPanel(CardType.OUTSTANDING));
        optionPanel.add(setOptionRowPanel(CardType.INDIVIDUAL));

        return optionPanel;
    }

    private void showGroupPrompt(final HTH_TextField divField) {
        compReqFocus = grpField;
        hideContents();
        setPromptFunctionKeys();
        setPromptControlKeys();

        setHeaderTitle("Select Group");
        selGroupList = groupList;

        JPanel headerPanel = setGroupRowPanel(null, null);
        headerPanel.setBounds(15, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setGroupPrompt(divField));
        informationPane.setBounds(15, 40 + headerPanel.getPreferredSize().height, headerPanel.getPreferredSize().width + 15, contentScreen.getSize().height * 80 / 100);
        informationPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, informationPane.getBounds().height));
        informationPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        promptScreen.add(informationPane, JLayeredPane.MODAL_LAYER);

        JLabel searchLabel = getLabel("Search");
        searchLabel.setBounds(20, 10, searchLabel.getPreferredSize().width, searchLabel.getPreferredSize().height);
        promptScreen.add(searchLabel, JLayeredPane.MODAL_LAYER);

        final HTH_TextField searchField = new HTH_TextField(30, HTH_FONT);
        searchField.setBounds(30 + searchLabel.getPreferredSize().width, 5, searchField.getPreferredSize().width, searchField.getPreferredSize().height);
        searchField.setFocusTraversalKeysEnabled(false);
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    setFocusTraversals(informationPane);
                    JPanel grpPanel = (JPanel) informationPane.getViewport().getView();
                    grpPanel.getComponent(0).requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_TAB) {
                    selGroupList = searchGroup(searchField.getText().trim());
                    informationPane.setViewportView(setGroupPrompt(divField));
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selGroupList = searchGroup(searchField.getText().trim());
                informationPane.setViewportView(setGroupPrompt(divField));
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
    }

    private JPanel setGroupRowPanel(final GroupMaster group, final HTH_TextField divField) {
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

        if (group == null) {
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
        } else {
            rowPanel.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        grpField.setText(group.getID());
                        if (divField != null) {
                            divField.setText("");
                        }

                        removePromptScreen();
                        grpField.requestFocus();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                }
            });
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
            rowPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent arg0) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    rowPanel.setBackground(Color.LIGHT_GRAY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    rowPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    grpField.setText(group.getID());
                    if (divField != null) {
                        divField.setText("");
                    }

                    removePromptScreen();
                    grpField.requestFocus();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });

            idField.setText(group.getID());
            idField.setForeground(new Color(0, 0, 150));

            if (group.isVIP()) {
                vipField.setText("VIP");
                vipField.setForeground(new Color(0, 0, 150));
            }

            nameField.setText(group.getName());
            nameField.setForeground(new Color(0, 0, 150));

            blockField.setText(group.getCarrier());
            blockField.setForeground(new Color(0, 0, 150));

            switch (group.getStatus()) {
                case TERMINATED:
                    statusField.setText("TERMINATED");
                    break;
                case RUN_OUT:
                    statusField.setText("RUN OUT");
                    break;
                default:
                    statusField.setText("ACTIVE");
            }
            statusField.setForeground(new Color(0, 0, 150));
        }

        rowPanel.add(idField);
        rowPanel.add(vipField);
        rowPanel.add(nameField);
        rowPanel.add(blockField);
        rowPanel.add(statusField);

        return rowPanel;
    }

    private JPanel setGroupPrompt(HTH_TextField divField) {
        JPanel groupPanel = new JPanel();
        groupPanel.setBackground(Color.WHITE);

        if (selGroupList.length == 0) {
            groupPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            groupPanel.add(getLabel("No records found."));
        } else {
            groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
            for (GroupMaster group : selGroupList) {
                groupPanel.add(setGroupRowPanel(group, divField));
            }
        }

        return groupPanel;
    }

    private GroupMaster[] searchGroup(String key) {
        if (key.isEmpty()) {
            return groupList;
        } else {
            List<GroupMaster> matchedGroup = new ArrayList<GroupMaster>();
            for (GroupMaster group : groupList) {
                boolean isPossible = group.getName().contains(key) || group.getID().contains(key) || group.getCarrier().contains(key) || group.getStatus().toString().contains(key);
                if (isPossible) {
                    matchedGroup.add(group);
                }
            }

            return matchedGroup.toArray(new GroupMaster[0]);
        }
    }

    private void showDivisionPrompt() {
        compReqFocus = divField;
        hideContents();
        updateDivList();
        selDivisionList = divisionList;
        setPromptFunctionKeys();
        setPromptControlKeys();
        setHeaderTitle("Select Division");

        JPanel headerPanel = setDivisionRowPanel(null);
        headerPanel.setBounds(50, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setDivisionPrompt());
        informationPane.setBounds(50, 40 + headerPanel.getPreferredSize().height, headerPanel.getPreferredSize().width + 15, contentScreen.getSize().height * 70 / 100);
        informationPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, informationPane.getBounds().height));
        informationPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        promptScreen.add(informationPane, JLayeredPane.MODAL_LAYER);

        JLabel searchLabel = getLabel("Search");
        searchLabel.setBounds(20, 10, searchLabel.getPreferredSize().width, searchLabel.getPreferredSize().height);
        promptScreen.add(searchLabel, JLayeredPane.MODAL_LAYER);

        final HTH_TextField searchField = new HTH_TextField(30, HTH_FONT);
        searchField.setBounds(30 + searchLabel.getPreferredSize().width, 5, searchField.getPreferredSize().width, searchField.getPreferredSize().height);
        searchField.setFocusTraversalKeysEnabled(false);
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    setFocusTraversals(informationPane);
                    JPanel panel = (JPanel) informationPane.getViewport().getView();
                    panel.getComponent(0).requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() != KeyEvent.VK_TAB) {
                    selDivisionList = searchDivision(searchField.getText().trim());
                    informationPane.setViewportView(setDivisionPrompt());
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selDivisionList = searchDivision(searchField.getText().trim());
                informationPane.setViewportView(setDivisionPrompt());
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
    }

    private JPanel setDivisionRowPanel(final Division div) {
        final JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(true);
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        rowPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        Font font = HTH_FONT;
        JLabel idField = getLabel(8, font);
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JLabel nameField = getLabel(50, font);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        int rowW = idField.getPreferredSize().width + nameField.getPreferredSize().width;
        int rowH = idField.getPreferredSize().height;
        rowPanel.setPreferredSize(new Dimension(rowW, rowH));
        rowPanel.setMinimumSize(new Dimension(rowW, rowH));
        rowPanel.setMaximumSize(new Dimension(rowW, rowH));


        if (div == null) {
            idField.setText("Division");
            idField.setForeground(Color.BLACK);

            nameField.setText("Name");
            nameField.setForeground(Color.BLACK);
        } else {
            rowPanel.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        divField.setText(div.getID());

                        removePromptScreen();
                        divField.requestFocus();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                }
            });
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
            rowPanel.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent arg0) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    rowPanel.setBackground(Color.LIGHT_GRAY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    rowPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    divField.setText(div.getID());

                    removePromptScreen();
                    divField.requestFocus();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });

            idField.setText(div.getID());
            idField.setForeground(new Color(0, 0, 150));

            nameField.setText(div.getName());
            nameField.setForeground(new Color(0, 0, 150));
        }

        rowPanel.add(idField);
        rowPanel.add(nameField);

        return rowPanel;
    }

    private JPanel setDivisionPrompt() {
        JPanel divPanel = new JPanel();
        divPanel.setBackground(Color.WHITE);

        if (selDivisionList.length == 0) {
            divPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            divPanel.add(getLabel("No records found."));
        } else {
            divPanel.setLayout(new BoxLayout(divPanel, BoxLayout.Y_AXIS));

            for (Division div : selDivisionList) {
                divPanel.add(setDivisionRowPanel(div));
            }
        }

        return divPanel;
    }

    private void updateDivList() {
        String selectedGroup = grpField.getText().trim();

        if (selectedGroup.isEmpty()) {
            divisionList = new Division[0];
        } else {
            divisionList = DIVISN.getDivList(selectedGroup);
        }
    }

    private Division[] searchDivision(String key) {
        if (key.isEmpty()) {
            return divisionList;
        } else {
            List<Division> matchedDiv = new ArrayList<Division>();
            for (Division div : divisionList) {
                boolean isPossible = div.getID().contains(key) || div.getName().contains(key);
                if (isPossible) {
                    matchedDiv.add(div);
                }
            }

            return matchedDiv.toArray(new Division[0]);
        }
    }

    private void loadGroupList() {
        group = grpField.getText().trim();
        nameS = nameStrField.getText().trim();
        nameE = nameEndField.getText().trim();
        date = dateField.getText().trim().replaceAll("/", "");
        if (date.isEmpty()) {
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("MMddyy");
            format.setTimeZone(TimeZone.getTimeZone("GWT"));
            date = format.format(today);
        }

        boolean isGrpValid = false;
        for (GroupMaster grp : groupList) {
            if (grp.getID().equals(group)) {
                isGrpValid = true;
                break;
            }
        }

        String msg = "";
        if (!isGrpValid) {
            msg = "Group ID Not Found";
        } else if (nameS.isEmpty() || nameE.isEmpty()) {
            msg = "Name Range Cannot Be Empty";
        } else {
            String parameter = HTH_IDC.member + " " + HTH_IDC.userID + " " + group + " " + nameS + " " + nameE + " " + date;
            StringBuilder parm = new StringBuilder(parameter);
            while (parm.length() < 60) {
                parm.append(" ");
            }
//			String loadCardCL = "CALL DFLIB/LOADGRPID PARM('" + parm + "')";
            String loadCardCL = "CALL pdlib/LOADGRPID PARM('" + parm + "')";
            ID_Printer.PRINTER_NAME = "ID CARD PRINTING";


            iSeries.executeCL(loadCardCL);
            startPrint();
            return;
        }

        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
        showMessage("Invalid Information", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
    }

    private void loadDivisionList() {
        String group = grpField.getText().trim();
        String division = divField.getText().trim();
        String nameS = nameStrField.getText().trim();
        String nameE = nameEndField.getText().trim();
        String date = dateField.getText().trim().replaceAll("/", "");
        if (date.isEmpty()) {
            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat format = new SimpleDateFormat("MMddyy");
            format.setTimeZone(TimeZone.getTimeZone("GWT"));
            date = format.format(today);
        }

        boolean isGrpValid = false;
        for (GroupMaster grp : groupList) {
            if (grp.getID().equals(group)) {
                isGrpValid = true;
                break;
            }
        }

        boolean isDivValid = false;
        if (divisionList == null) {
            updateDivList();
        }
        for (Division div : divisionList) {
            if (div.getID().equals(division)) {
                isDivValid = true;
                break;
            }
        }

        String msg = "";
        if (!isGrpValid) {
            msg = "Group ID Not Found";
        } else if (!isDivValid) {
            msg = "Division Not Found";
        } else if (nameS.isEmpty() || nameE.isEmpty()) {
            msg = "Name Range Cannot Be Empty";
        } else {
            String parameter = HTH_IDC.member + " " + HTH_IDC.userID + " " + group + " " + division + " " + nameS + " " + nameE + " " + date;
            StringBuilder parm = new StringBuilder(parameter);
            while (parm.length() < 60) {
                parm.append(" ");
            }
            //----5
//			String loadCardCL = "CALL DFLIB/LOADDIVID PARM('" + parm + "')";
            String loadCardCL = "CALL PDLIB/LOADDIVID PARM('" + parm + "')";
//			ID_Printer.PRINTER_NAME = "Id Printer Division" ;
            ID_Printer.PRINTER_NAME = "ID CARD PRINTING";
            System.out.println("---division ");
            iSeries.executeCL(loadCardCL);
            startPrint();
            return;
        }

        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
        showMessage("Invalid Information", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
    }

    private void loadOutstandingList() {
        //---- convert from "\\*\\*\\*" to "***"
        String group = grpField.getText().trim().isEmpty() ? "ALLGROUPS" : grpField.getText().trim();

        boolean isGrpValid = false;
        for (GroupMaster grp : groupList) {
            if (grp.getID().equals(group)) {
                isGrpValid = true;
                break;
            }
        }

        String msg = "";
        //---- convert from "\\*\\*\\*" t "***"
        if (group.equals("ALLGROUPS") || isGrpValid) {
            String parameter = HTH_IDC.member + " " + HTH_IDC.userID + " " + group;
            StringBuilder parm = new StringBuilder(parameter);
            while (parm.length() < 30) {
                parm.append(" ");
            }
            System.out.println("---loadOutstandingList");
            //-----1
//			String loadCardCL = "CALL DFLIB/LOADOUTID PARM('" + parm + "')";
            String loadCardCL = "CALL PDLIB/LOADOUTID PARM('" + parm + "')";
//			ID_Printer.PRINTER_NAME = "Id Printer Outstanding" ;
            ID_Printer.PRINTER_NAME = "Id CARD PRINTING";
            iSeries.executeCL(loadCardCL);
            startPrint();
            return;
        } else {
            msg = "Group ID Not Found";
        }

        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
        showMessage("Invalid Information", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
    }

    private void loadFamilyList() {
        List<String> listedID = new ArrayList<String>();
        Component[] comp = contentScreen.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        for (Component c : comp) {
            try {
                String id = ((HTH_TextField) c).getText().trim();
                if (!id.isEmpty()) {
                    listedID.add(id);
                }
            } catch (Exception e) {
                // Skip labels.
            }
        }

        String msg = "";
        if (listedID.isEmpty()) {
            msg = "Enter At Least 1 ID Number";
        } else {
            StringBuilder idList = new StringBuilder(listedID.get(0).trim());
            for (int idx = 1; idx < listedID.size(); idx++) {
                idList.append("\\&").append(listedID.get(idx).trim());
            }
            String parameter = HTH_IDC.member + " " + HTH_IDC.userID + " " + idList.toString();
            StringBuilder parm = new StringBuilder(parameter);
            while (parm.length() < 225) {
                parm.append(" ");
            }
//			String loadCardCL = "CALL DFLIB/LOADFAMID PARM('" + parm + "')";
            String loadCardCL = "CALL pdlib/LOADFAMID PARM('" + parm + "')";
            ID_Printer.PRINTER_NAME = "ID CARD PRINTING";
            iSeries.executeCL(loadCardCL);
            System.out.println("Family Idcard");
            startPrint();
            return;
        }

        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
        showMessage("Invalid Information", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
    }

    private void loadIndividualList() {
        List<String> listedID = new ArrayList<String>();
        Component[] comp = contentScreen.getComponentsInLayer(JLayeredPane.DEFAULT_LAYER);
        for (Component c : comp) {
            try {
                String id = ((HTH_TextField) c).getText().trim();
                if (!id.isEmpty()) {
                    listedID.add(id);
                }
            } catch (Exception e) {
                // Skip labels.
            }
        }

        String msg = "";
        if (listedID.isEmpty()) {
            msg = "Enter At Least 1 ID Number";
        } else {
            StringBuilder idList = new StringBuilder(listedID.get(0).trim());
            for (int idx = 1; idx < listedID.size(); idx++) {
                idList.append("\\&").append(listedID.get(idx).trim());
            }
            String parameter = HTH_IDC.member + " " + HTH_IDC.userID + " " + idList.toString();
            StringBuilder parm = new StringBuilder(parameter);
            while (parm.length() < 225) {
                parm.append(" ");
            }
//			String loadCardCL = "CALL DFLIB/LOADIDVID PARM('" + parm + "')";
            String loadCardCL = "CALL pdlib/LOADIDVID PARM('" + parm + "')";
            ID_Printer.PRINTER_NAME = "ID CARD PRINTING";
            iSeries.executeCL(loadCardCL);
            startPrint();
            return;
        }

        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
        showMessage("Invalid Information", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
    }

    private void setPromptFunctionKeys() {
        String exitKey = "ExitFunction";
        Action exitAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10108L;

            @Override
            public void actionPerformed(ActionEvent e) {
                removePromptScreen();
            }
        };
        HTH_FunctionButton exitBtn = new HTH_FunctionButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        setFunctionKeys(new HTH_FunctionButton[]{exitBtn});
    }

    private void setPromptControlKeys() {
        controlPanel.removeAll();

        String exitKey = "Exit";
        Action exitAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10109L;

            @Override
            public void actionPerformed(ActionEvent e) {
                removePromptScreen();
            }
        };
        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);
    }

    private void setFocusTraversals(JScrollPane scrollPane) {
        final JPanel viewPanel = (JPanel) scrollPane.getViewport().getView();
        Component[] comps = viewPanel.getComponents();

        for (int idx = 0; idx < comps.length; idx++) {
            final Component[] prevComp = new Component[1];
            final Component[] nextComp = new Component[1];

            if (idx == 0) {
                prevComp[0] = comps[comps.length - 1];
            } else {
                prevComp[0] = comps[idx - 1];
            }

            if (idx == comps.length - 1) {
                nextComp[0] = comps[0];
            } else {
                nextComp[0] = comps[idx + 1];
            }

            comps[idx].setFocusTraversalKeysEnabled(false);
            comps[idx].addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                        isShiftPressed = true;
                    } else if (e.getKeyCode() == KeyEvent.VK_UP || (isShiftPressed && e.getKeyCode() == KeyEvent.VK_TAB)) {
                        viewPanel.scrollRectToVisible(prevComp[0].getBounds());
                        prevComp[0].requestFocus();
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN || (!isShiftPressed && e.getKeyCode() == KeyEvent.VK_TAB)) {
                        viewPanel.scrollRectToVisible(nextComp[0].getBounds());
                        nextComp[0].requestFocus();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                        isShiftPressed = false;
                    }
                }

                @Override
                public void keyTyped(KeyEvent e) {
                }
            });
        }
    }

    private void showMessage(String title, String msg, Dimension size, int type) {
        HTH_Dialog.showMessageDialog(this, title, msg, size, type);
    }

    private void startPrint() {
        //----4
        final String[] idList = IDWORK.getInQueueID(HTH_IDC.userID); // new String[] {"485235826"}; // new String[] {"003565315", "012521333", "237359347", "036607230"};
        final String[] grpList = IDWORK.getGrpList(); // new String[] {"NAIFA"}; // new String[] {"ARCB", "ARCB", "FLT", "FLT"};
        //final String[]idList = new String[]{"298382574", "298382574","298382574","298382574"};
        //final String[]grpList = new String[]{"CHLLC", "CHLLC","CHLLC","CHLLC"};

        System.out.println("ID_PrinterSelection: IDLIST from IDWORK: " + idList.length);
        //-------


        // Check if there exists any ID to print.
        if (idList.length == 0) {
            //----2
            // No ID in queue for printing.
            HTH_Dialog.showMessageDialog(this, "", "No ID cards ", new
                    Dimension(250, 150), JOptionPane.YES_OPTION);
            //System.exit(0);
        } else {
            // Initial UI while data is loading.
            printerWindow = new ID_Printer();

            Thread dataLoader = new Thread(new Runnable() {
                @Override
                public void run() {
                    //---3
//					String clCommand = "cIDC PARM('" + HTH_IDC.member + "' '" + HTH_IDC.DEVICE + " ' '%s'')";
                    String clCommand = "CALL DFLIB/LOADIDC PARM('" + HTH_IDC.member + "' '" + HTH_IDC.DEVICE + " ' '%s')";

                    // Compile CL statement to create 100 ID cards at a time.
                    System.out.println(clCommand);
                    System.out.println("idllist :" + idList.length);
                    StringBuilder idValue = new StringBuilder(idList[0]);
                    for (int idx = 1; idx < idList.length; idx++) {
                        //---2 print card cycle we added || idx == idList.length-1
//						if (idx % 30 == 0 || idx == idList.length-1) {
                        if (idx % 300 == 0 || idx == idList.length - 1) {
                            if (idx == idList.length - 1) {
                                idValue.append("\\&").append(idList[idx]);
                            }
                            while (idValue.length() < 1100) {
                                idValue.append(" ");
                            }
                            long timeBefore = System.currentTimeMillis();
                            System.out.println(String.format(clCommand, idValue));

                            iSeries.executeCL(String.format(clCommand, idValue));
                            long timeAfter = System.currentTimeMillis();
                            System.out.println("time for cl= " + (timeAfter - timeBefore));
                            System.out.println("RUN1: " + (String.format(clCommand, idValue)));

                            idValue = new StringBuilder(idList[idx]);
                        } else {

                            idValue.append("\\&").append(idList[idx]);
                        }
                    }
                    //---- added to solve the issue with one individual card
                    if (idList.length == 1) {
                        iSeries.executeCL(String.format(clCommand, idValue));
                    }
                    //--- I added this condition inside for
                    // Finish the left over ID cards.
//					while (idValue.length() < 1100) {
//						idValue.append(" ");
//					}
//					//System.out.println("RUN:2 " + (String.format(clCommand, idValue)));
//					iSeries.executeCL(String.format(clCommand, idValue));
//					System.out.println("RUN1: " + (String.format(clCommand, idValue)));


                    // Query out the ID cards that created for printing.

                    //generate id card
                    IDCard[] cardList = IDCPRV.generateIDCARD(grpList, idList, HTH_IDC.DEVICE);
                    //---test

                    // Wait up to 2 minutes if window is not ready.
                    int timer = 0;
                    while (printerWindow == null && timer <= 120) {
                        try {
                            Thread.sleep(1000);
                            timer++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // If window is not loaded, show error message for invalid parameters; Otherwise starts ID program.
                    if (printerWindow == null) {
                        JOptionPane.showInternalMessageDialog(null, "Invalid parameters passed.", "Error Occurred", JOptionPane.ERROR_MESSAGE);
                    } else {
                        printerWindow.begin(cardList);
                    }
                }
            });
            dataLoader.start();
            session.dispose();
        }
    }


    private void exitProgram() {
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
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

    private class OptionPromptActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showOptionPrompt();
        }
    }

    private class OptionPromptKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                showOptionPrompt();
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                optionField.requestFocus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private class GroupPromptActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (typeChosen == CardType.GROUP || typeChosen == CardType.OUTSTANDING) {
                showGroupPrompt(null);
            } else if (typeChosen == CardType.DIVISION) {
                showGroupPrompt(divField);
            }
        }
    }

    private class GroupPromptKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (typeChosen == CardType.GROUP || typeChosen == CardType.OUTSTANDING) {
                    showGroupPrompt(null);
                } else if (typeChosen == CardType.DIVISION) {
                    showGroupPrompt(divField);
                }
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                if (typeChosen == CardType.GROUP) {
                    nameStrField.requestFocus();
                } else if (typeChosen == CardType.DIVISION) {
                    divField.requestFocus();
                } else {
                    grpField.requestFocus();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private class DivisionPromptActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showDivisionPrompt();
        }
    }

    private class DivisionPromptKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                showDivisionPrompt();
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                nameStrField.requestFocus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
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

    private class PromptFocusListener implements FocusListener {
        private HTH_PromptButton promptBtn;

        public PromptFocusListener(HTH_PromptButton promptBtn) {
            this.promptBtn = promptBtn;
        }

        @Override
        public void focusGained(FocusEvent e) {
            promptBtn.setText("|");
        }

        @Override
        public void focusLost(FocusEvent e) {
            promptBtn.setText("");
        }
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
