package com.hth.id_card.user_interface.decorator;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.hth.backend.iSeries;
import com.hth.backend.beans.DIVISN;
import com.hth.backend.beans.IDCARD;
import com.hth.backend.beans.IDCFLD;
import com.hth.backend.beans.IDCIMG;
import com.hth.id_card.HTH_IDC;
import com.hth.id_card.user_interface.HTH_CardScreen;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_DeleteButton;
import com.hth.id_card.user_interface.HTH_Frame;
import com.hth.id_card.user_interface.HTH_FunctionButton;
import com.hth.id_card.user_interface.HTH_Dialog;
import com.hth.id_card.user_interface.HTH_PromptButton;
import com.hth.id_card.user_interface.HTH_TextField;
import com.hth.util.Division;
import com.hth.util.GroupMaster;
import com.hth.util.IDCard;
import com.hth.util.IDHeader;
import com.hth.util.IDLogo;
import com.hth.util.IDMask;

public class ID_Decorator extends HTH_Frame implements WindowListener, Printable {
    private static final long serialVersionUID = 101L;
    private static final String DECORATOR_NAME = "Process ID Card";
    private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);
    private static final float ID_WIDTH = 287;
    private static final float ID_HEIGHT = 180;
    private static final float ID_X_OFFSET = ID_WIDTH / 2;
    private static final float ID_FRONT_Y_OFFSET = 25;
    private static final float ID_BACK_Y_OFFSET = ID_FRONT_Y_OFFSET + ID_HEIGHT;
    private static final int WIDTH = 715;
    private static final int HEIGHT = 490;
    private static final Font DEFAULT_FONT = new Font("AvantGarde", Font.PLAIN, 15);

    private HTH_CardScreen cardFramePanel;

    private boolean isCardLoaded;
    private boolean isShiftPressed;
    private boolean isFront;
    private boolean isLogoEditor;
    private boolean isAdvanced;
    private int focusedIdx;
    private int selectedLine, selectedCol;

    private IDLogo[] selLogoList;
    private IDHeader[] selHeaderList;
    private GroupMaster[] groupList, selGroupList;
    private Division[] divisionList, selDivisionList;
    private IDCard[] cardList, selCardList;
    private IDCard selectedCard;
    private Component compReqFocus;
    private JPanel controlPanel;
    private JLayeredPane contentScreen, promptScreen, cardContentPane;
    private JTextPane[] linePanes;
    private JButton[] logoBtns;
    private JTextPane focusedTextPane;
    private JCheckBox bold, italic, underline;
    private JRadioButton left, right, center, small, normal, large, xLarge;
    private JRadioButton hLargeLogo, vLargeLogo, smallLogo;
    private HTH_TextField grpField, divField, crdField;
    private HTH_TextField fromGrpField, fromDivField, fromCrdField;
    private HTH_TextField toGrpField, toDivField, toCrdField;
    private HTH_FunctionButton[] functionKeys;
    private HTH_ControlButton[] controlKeys;

    public ID_Decorator() {
        super(DECORATOR_NAME);
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
    }

    public void begin(GroupMaster[] groupList) {
        this.groupList = groupList;

        promptScreen.setBounds(0, 0, contentScreen.getSize().width, contentScreen.getSize().height);
        setSelectionScreen();
    }

    private void initComponents() {
        focusedIdx = 0;

        promptScreen = new JLayeredPane();
        promptScreen.setOpaque(true);
        promptScreen.setBackground(Color.WHITE);

        cardContentPane = new JLayeredPane();
        cardContentPane.setOpaque(true);
        cardContentPane.setBackground(Color.WHITE);

        contentScreen = new JLayeredPane();
        contentScreen.setOpaque(false);

        controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));

        linePanes = new JTextPane[18];

        logoBtns = new JButton[9];

        grpField = new HTH_TextField(10, HTH_FONT);
        divField = new HTH_TextField(3, HTH_FONT);
        crdField = new HTH_TextField(2, HTH_FONT);
        crdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startEditor();
            }
        });

        fromGrpField = new HTH_TextField(10, HTH_FONT);
        fromDivField = new HTH_TextField(3, HTH_FONT);
        fromCrdField = new HTH_TextField(2, HTH_FONT);

        toGrpField = new HTH_TextField(10, HTH_FONT);
        toDivField = new HTH_TextField(3, HTH_FONT);
        toCrdField = new HTH_TextField(2, HTH_FONT);
        toCrdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyCard();
            }
        });

        bold = new JCheckBox("Bold");
        bold.setOpaque(false);
        bold.setMnemonic(KeyEvent.VK_B);
        bold.setFont(HTH_FONT.deriveFont(14.0f));

        italic = new JCheckBox("Italic");
        italic.setOpaque(false);
        italic.setMnemonic(KeyEvent.VK_I);
        italic.setFont(HTH_FONT.deriveFont(14.0f));

        underline = new JCheckBox("Underline");
        underline.setOpaque(false);
        underline.setMnemonic(KeyEvent.VK_U);
        underline.setFont(HTH_FONT.deriveFont(14.0f));

        left = new JRadioButton("Left");
        left.setOpaque(false);
        left.setMnemonic(KeyEvent.VK_L);
        left.setFont(HTH_FONT.deriveFont(14.0f));

        center = new JRadioButton("Center");
        center.setOpaque(false);
        center.setMnemonic(KeyEvent.VK_E);
        center.setFont(HTH_FONT.deriveFont(14.0f));

        right = new JRadioButton("Right");
        right.setOpaque(false);
        right.setMnemonic(KeyEvent.VK_R);
        right.setFont(HTH_FONT.deriveFont(14.0f));

        ButtonGroup alignments = new ButtonGroup();
        alignments.add(left);
        alignments.add(center);
        alignments.add(right);

        small = new JRadioButton("Small");
        small.setOpaque(false);
        small.setFont(HTH_FONT.deriveFont(14.0f));
        small.setMnemonic(KeyEvent.VK_S);
        small.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusedTextPane.setFont(focusedTextPane.getFont().deriveFont(14.0f));
                rmvModifier("large", "xlarge");
                addModifier("small");

                focusedTextPane.requestFocus();
            }
        });

        normal = new JRadioButton("Medium");
        normal.setOpaque(false);
        normal.setMnemonic(KeyEvent.VK_M);
        normal.setFont(HTH_FONT.deriveFont(14.0f));
        normal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusedTextPane.setFont(focusedTextPane.getFont().deriveFont(15.0f));
                rmvModifier("xlarge", "large", "small");

                focusedTextPane.requestFocus();
            }
        });

        large = new JRadioButton("Large");
        large.setOpaque(false);
        large.setMnemonic(KeyEvent.VK_G);
        large.setFont(HTH_FONT.deriveFont(14.0f));
        large.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusedTextPane.setFont(focusedTextPane.getFont().deriveFont(18.0f));
                rmvModifier("small", "xlarge");
                addModifier("large");

                focusedTextPane.requestFocus();
            }
        });

        xLarge = new JRadioButton("X-Large");
        xLarge.setOpaque(false);
        xLarge.setMnemonic(KeyEvent.VK_X);
        xLarge.setFont(HTH_FONT.deriveFont(14.0f));
        xLarge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                focusedTextPane.setFont(focusedTextPane.getFont().deriveFont(20.0f));
                rmvModifier("small", "large");
                addModifier("xlarge");

                focusedTextPane.requestFocus();
            }
        });

        ButtonGroup sizes = new ButtonGroup();
        sizes.add(small);
        sizes.add(normal);
        sizes.add(large);
        sizes.add(xLarge);

        smallLogo = new JRadioButton("Do Not Span");
        smallLogo.setOpaque(false);
        smallLogo.setMnemonic(KeyEvent.VK_N);
        smallLogo.setFont(HTH_FONT.deriveFont(14.0f));
        smallLogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFront) {
                    selectedCard.setFrontLarge("");
                } else {
                    selectedCard.setBackLarge("");
                }
            }
        });

        hLargeLogo = new JRadioButton("Span Horizontally");
        hLargeLogo.setOpaque(false);
        hLargeLogo.setMnemonic(KeyEvent.VK_H);
        hLargeLogo.setFont(HTH_FONT.deriveFont(14.0f));
        hLargeLogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFront) {
                    selectedCard.setFrontLarge("H");
                } else {
                    selectedCard.setBackLarge("H");
                }
            }
        });

        vLargeLogo = new JRadioButton("Span Verically");
        vLargeLogo.setOpaque(false);
        vLargeLogo.setMnemonic(KeyEvent.VK_V);
        vLargeLogo.setFont(HTH_FONT.deriveFont(14.0f));
        vLargeLogo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFront) {
                    selectedCard.setFrontLarge("V");
                } else {
                    selectedCard.setBackLarge("V");
                }
            }
        });

        ButtonGroup logoSize = new ButtonGroup();
        logoSize.add(smallLogo);
        logoSize.add(hLargeLogo);
        logoSize.add(vLargeLogo);
    }

    private void setSelectionScreen() {
        isCardLoaded = false;
        resetContent();
        setSelectionFunctionKeys();
        setSelectionControlKeys();
        setSelectionLabels();
        setSelectionInputFields();
        revalidate();
        repaint();
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

    private void setSelectionLabels() {
        int endLineX = 200;

        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        borderPanel.setBounds(30, 100, contentScreen.getSize().width * 8 / 10, 240);
        borderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Process ID Card",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        contentScreen.add(borderPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel grpLabel = getLabel("Block/Group ID");
        grpLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 160, grpLabel.getPreferredSize().width, grpLabel.getPreferredSize().height);
        contentScreen.add(grpLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel divLabel = getLabel("Division");
        divLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 220, divLabel.getPreferredSize().width, divLabel.getPreferredSize().height);
        contentScreen.add(divLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel crdLabel = getLabel("Card Number");
        crdLabel.setBounds(endLineX - grpLabel.getPreferredSize().width, 280, crdLabel.getPreferredSize().width, crdLabel.getPreferredSize().height);
        contentScreen.add(crdLabel, JLayeredPane.DEFAULT_LAYER);
    }

    private void setSelectionInputFields() {
        int strLineX = 250;

        grpField.setBounds(strLineX, 155, grpField.getPreferredSize().width, grpField.getPreferredSize().height);
        contentScreen.add(grpField, JLayeredPane.DEFAULT_LAYER);
        grpField.requestFocus();

        HTH_PromptButton grpPromptBtn = new HTH_PromptButton();
        grpPromptBtn.setBounds(strLineX + grpField.getPreferredSize().width + 10, 160, grpPromptBtn.getPreferredSize().width, grpPromptBtn.getPreferredSize().height);
        grpPromptBtn.addActionListener(new GroupPromptActionListener(grpField, divField, crdField));
        grpPromptBtn.addKeyListener(new GroupPromptKeyListener(grpField, divField, crdField));
        grpPromptBtn.addMouseListener(new PromptMouseListener());
        grpPromptBtn.addFocusListener(new PromptFocusListener(grpPromptBtn));
        grpPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(grpPromptBtn, JLayeredPane.DEFAULT_LAYER);

        divField.setBounds(strLineX, 215, divField.getPreferredSize().width, divField.getPreferredSize().height);
        contentScreen.add(divField, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton divPromptBtn = new HTH_PromptButton();
        divPromptBtn.setBounds(strLineX + divField.getPreferredSize().width + 10, 220, divPromptBtn.getPreferredSize().width, divPromptBtn.getPreferredSize().height);
        divPromptBtn.addActionListener(new DivisionPromptActionListener(grpField, divField, crdField));
        divPromptBtn.addKeyListener(new DivisionPromptKeyListener(grpField, divField, crdField));
        divPromptBtn.addMouseListener(new PromptMouseListener());
        divPromptBtn.addFocusListener(new PromptFocusListener(divPromptBtn));
        grpPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(divPromptBtn, JLayeredPane.DEFAULT_LAYER);

        crdField.setBounds(strLineX, 275, crdField.getPreferredSize().width, crdField.getPreferredSize().height);
        contentScreen.add(crdField, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton crdPromptBtn = new HTH_PromptButton();
        crdPromptBtn.setBounds(strLineX + crdField.getPreferredSize().width + 10, 280, crdPromptBtn.getPreferredSize().width, crdPromptBtn.getPreferredSize().height);
        crdPromptBtn.addActionListener(new CardPromptActionListener(grpField, divField, crdField, true));
        crdPromptBtn.addKeyListener(new CardPromptKeyListener(grpField, divField, crdField, null, true));
        crdPromptBtn.addMouseListener(new PromptMouseListener());
        crdPromptBtn.addFocusListener(new PromptFocusListener(crdPromptBtn));
        crdPromptBtn.setFocusTraversalKeysEnabled(false);
        contentScreen.add(crdPromptBtn, JLayeredPane.DEFAULT_LAYER);

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
                startEditor();
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

    private void setSelectionFunctionKeys() {
        UIManager.getDefaults().put("ToolTipUI", "javax.swing.plaf.basic.BasicToolTipUI");

        String addKey = "Add", cpyKey = "Copy", rmvKey = "Remove";
        Action addAction, cpyAction, rmvAction;
        HTH_FunctionButton addBtn, cpyBtn, rmvBtn;

        addAction = new AbstractAction(addKey) {
            private static final long serialVersionUID = 10103L;

            @Override
            public void actionPerformed(ActionEvent e) {
                startAddProcess();
            }
        };
        cpyAction = new AbstractAction(cpyKey) {
            private static final long serialVersionUID = 10104L;

            @Override
            public void actionPerformed(ActionEvent e) {
                startCopyProcess();
            }
        };
        rmvAction = new AbstractAction(rmvKey) {
            private static final long serialVersionUID = 10105L;

            @Override
            public void actionPerformed(ActionEvent e) {
                startRemoveProcess();
            }
        };

        addBtn = new HTH_FunctionButton("Add Card");
        addBtn.setToolTipText("F6=Add Card");
        addBtn.addActionListener(addAction);
        addBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), addKey);
        addBtn.getActionMap().put(addKey, addAction);

        cpyBtn = new HTH_FunctionButton("Block/Group/Div Copy");
        cpyBtn.setToolTipText("F8=Block/Group/Div Copy");
        cpyBtn.addActionListener(cpyAction);
        cpyBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), cpyKey);
        cpyBtn.getActionMap().put(cpyKey, cpyAction);

        rmvBtn = new HTH_FunctionButton("Delete");
        rmvBtn.setToolTipText("Shift+F11=Delete");
        rmvBtn.addActionListener(rmvAction);
        rmvBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, InputEvent.SHIFT_DOWN_MASK), rmvKey);
        rmvBtn.getActionMap().put(rmvKey, rmvAction);

        functionKeys = new HTH_FunctionButton[]{addBtn, cpyBtn, rmvBtn};
        setFunctionKeys(functionKeys);
    }

    private void restoreFunctionKeys() {
        setFunctionKeys(functionKeys);
    }

    private void startEditor() {
        final String group = grpField.getText();
        final String division = divField.getText();
        final String number = crdField.getText();

        boolean isExisted = IDCARD.isCardExisted(group, division, number);
        if (isExisted) {
            showLoading();
            Thread idLoader = new Thread(new Runnable() {
                @Override
                public void run() {
                    selectedCard = IDCARD.getCard(group, division, number);
                    editCard(true);
                }
            });
            idLoader.start();
        } else {
            boolean isGrpValid = false;
            for (GroupMaster grp : groupList) {
                if (grp.getCarrier().equals(group) || grp.getID().equals(group)) {
                    isGrpValid = true;
                    break;
                }
            }

            boolean isDivValid = division.isEmpty();
            if (divisionList != null) {
                for (Division div : divisionList) {
                    if (div.getID().equals(division)) {
                        isDivValid = true;
                        break;
                    }
                }
            }

            String msg;
            if (isGrpValid && isDivValid) {
                msg = "The selected ID card not found";
            } else if (!isGrpValid) {
                msg = "Block/Group ID not found";
            } else {
                msg = "Division not found";
            }

            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
            HTH_Dialog.showMessageDialog(this, "Record Not Found", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void startAddProcess() {
        String group = grpField.getText();
        String division = divField.getText();
        String num = crdField.getText();

        if (num.isEmpty()) {
            crdField.setText("00");
            num = "00";
        }

        if (group.isEmpty()) {
            String msg = "Please select a group or type in block ID";
            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
            HTH_Dialog.showMessageDialog(this, "Group/Block ID Is Empty", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
        } else {
            boolean isExisted = IDCARD.isCardExisted(group, division, num);
            if (isExisted) {
                String msg = "ID card already exists";
                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
                HTH_Dialog.showMessageDialog(this, "Duplicate Record Found", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
            } else {
                boolean isGrpValid = false;
                for (GroupMaster grp : groupList) {
                    if (grp.getCarrier().equals(group) || grp.getID().equals(group)) {
                        isGrpValid = true;
                        break;
                    }
                }

                boolean isDivValid = division.isEmpty();
                if (divisionList != null) {
                    for (Division div : divisionList) {
                        if (div.getID().equals(division)) {
                            isDivValid = true;
                            break;
                        }
                    }
                }

                String msg;
                if (isGrpValid && isDivValid) {
                    selectedCard = new IDCard(group.toUpperCase(), division.toUpperCase(), num.toUpperCase(), "");
                    editCard(true);
                    return;
                } else if (!isGrpValid) {
                    msg = "Block/Group ID not found";
                } else {
                    msg = "Division not found";
                }

                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);
                HTH_Dialog.showMessageDialog(this, "Record Not Found", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void startCopyProcess() {
        resetContent();

        functionKeys = new HTH_FunctionButton[0];
        fromGrpField.setText("");
        fromDivField.setText("");
        fromCrdField.setText("");
        toGrpField.setText("");
        toDivField.setText("");
        toCrdField.setText("");

        showCopyPrompt();

        revalidate();
        repaint();
    }

    private void startRemoveProcess() {
        String group = grpField.getText();
        String division = divField.getText();
        String number = crdField.getText();

        boolean isExisted = IDCARD.isCardExisted(group, division, number);
        if (isExisted) {
            String msg = "Are you sure?";
            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
            int result = HTH_Dialog.showConfirmDialog(this, "Confirmation", msg, dialogSize);
            if (result == JOptionPane.YES_OPTION) {
                IDCARD.delCard(group, division, number);
                grpField.setText("");
                divField.setText("");
                crdField.setText("");
            }
        } else {
            boolean isGrpValid = false;
            for (GroupMaster grp : groupList) {
                if (grp.getCarrier().equals(group) || grp.getID().equals(group)) {
                    isGrpValid = true;
                    break;
                }
            }

            boolean isDivValid = division.isEmpty();
            if (divisionList != null) {
                for (Division div : divisionList) {
                    if (div.getID().equals(division)) {
                        isDivValid = true;
                        break;
                    }
                }
            }

            String msg;
            if (isGrpValid && isDivValid) {
                msg = "The selected ID card not found";
            } else if (!isGrpValid) {
                msg = "Block/Group ID not found";
            } else {
                msg = "Division not found";
            }

            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
            HTH_Dialog.showMessageDialog(this, "Record Not Found", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setFunctionKeys(HTH_FunctionButton[] buttons) {
        resetFunctionKeys();
        addFunctionKeys(buttons);
    }

    private void resetContent() {
        setHeaderTitle(null);
        controlPanel.removeAll();
        contentScreen.removeAll();
    }

    private JLabel getLabel(String txt) {
        JLabel label = new JLabel(txt);
        label.setFont(HTH_FONT);
        int txtW = label.getFontMetrics(HTH_FONT).stringWidth(txt);
        int txtH = label.getFontMetrics(HTH_FONT).getHeight();
        label.setPreferredSize(new Dimension(txtW, txtH));

        return label;
    }

    private JLabel getLabel(String txt, Font font) {
        JLabel label = new JLabel(txt);
        label.setFont(font);
        int txtW = label.getFontMetrics(font).stringWidth(txt);
        int txtH = label.getFontMetrics(font).getHeight();
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

    private JPanel getGhostPanel(Dimension size) {
        JPanel ghostPanel = new JPanel();
        ghostPanel.setPreferredSize(size);
        ghostPanel.setMaximumSize(size);
        ghostPanel.setMinimumSize(size);
        ghostPanel.setOpaque(false);

        return ghostPanel;
    }

    private void showCopyPrompt() {
        setHeaderTitle("Copy Card");

        JLayeredPane copyPane = new JLayeredPane();
        copyPane.setOpaque(true);
        copyPane.setBackground(Color.WHITE);
        copyPane.setBounds(0, 0, contentScreen.getSize().width, contentScreen.getSize().height);
        contentScreen.add(copyPane, JLayeredPane.DEFAULT_LAYER);

        HTH_FunctionButton exitBtn = new HTH_FunctionButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        });
        setFunctionKeys(new HTH_FunctionButton[]{exitBtn});

        setCopyPromptLabels(copyPane);
        setCopyPromptFields(copyPane);
        setCopyControlBtn();

        fromGrpField.requestFocus();
    }

    private void setCopyPromptLabels(JLayeredPane prompt) {
        JPanel fromBorderPanel = new JPanel();
        fromBorderPanel.setOpaque(false);
        fromBorderPanel.setBounds(30, 100, contentScreen.getSize().width * 45 / 100, 240);
        fromBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "From",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        prompt.add(fromBorderPanel, JLayeredPane.DEFAULT_LAYER);

        int endLineX = 200;
        JLabel fromGrpLabel = getLabel("Block/Group ID");
        fromGrpLabel.setBounds(endLineX - fromGrpLabel.getPreferredSize().width, 160, fromGrpLabel.getPreferredSize().width, fromGrpLabel.getPreferredSize().height);
        prompt.add(fromGrpLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel fromDivLabel = getLabel("Division");
        fromDivLabel.setBounds(endLineX - fromGrpLabel.getPreferredSize().width, 220, fromDivLabel.getPreferredSize().width, fromDivLabel.getPreferredSize().height);
        prompt.add(fromDivLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel fromCrdLabel = getLabel("Card Number");
        fromCrdLabel.setBounds(endLineX - fromGrpLabel.getPreferredSize().width, 280, fromCrdLabel.getPreferredSize().width, fromCrdLabel.getPreferredSize().height);
        prompt.add(fromCrdLabel, JLayeredPane.DEFAULT_LAYER);

        JPanel toBorderPanel = new JPanel();
        toBorderPanel.setOpaque(false);
        toBorderPanel.setBounds(75 + fromBorderPanel.getBounds().width, 100, contentScreen.getSize().width * 45 / 100, 240);
        toBorderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "To",
                TitledBorder.LEADING,
                TitledBorder.TOP,
                HTH_FONT,
                Color.BLACK));
        prompt.add(toBorderPanel, JLayeredPane.DEFAULT_LAYER);

        endLineX += contentScreen.getSize().width / 2;
        JLabel toGrpLabel = getLabel("Block/Group ID");
        toGrpLabel.setBounds(endLineX - toGrpLabel.getPreferredSize().width, 160, toGrpLabel.getPreferredSize().width, toGrpLabel.getPreferredSize().height);
        prompt.add(toGrpLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel toDivLabel = getLabel("Division");
        toDivLabel.setBounds(endLineX - toGrpLabel.getPreferredSize().width, 220, toDivLabel.getPreferredSize().width, toDivLabel.getPreferredSize().height);
        prompt.add(toDivLabel, JLayeredPane.DEFAULT_LAYER);

        JLabel toCrdLabel = getLabel("Card Number");
        toCrdLabel.setBounds(endLineX - toGrpLabel.getPreferredSize().width, 280, toCrdLabel.getPreferredSize().width, toCrdLabel.getPreferredSize().height);
        prompt.add(toCrdLabel, JLayeredPane.DEFAULT_LAYER);
    }

    private void setCopyPromptFields(JLayeredPane prompt) {
        int strLineX = 250;
        fromGrpField.setBounds(strLineX, 155, fromGrpField.getPreferredSize().width, fromGrpField.getPreferredSize().height);
        prompt.add(fromGrpField, JLayeredPane.DEFAULT_LAYER);

        fromDivField.setBounds(strLineX, 215, fromDivField.getPreferredSize().width, fromDivField.getPreferredSize().height);
        prompt.add(fromDivField, JLayeredPane.DEFAULT_LAYER);

        fromCrdField.setBounds(strLineX, 275, fromCrdField.getPreferredSize().width, fromCrdField.getPreferredSize().height);
        prompt.add(fromCrdField, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton fromGrpPromptBtn = new HTH_PromptButton();
        fromGrpPromptBtn.setBounds(strLineX + fromGrpField.getPreferredSize().width + 10, 160, fromGrpPromptBtn.getPreferredSize().width, fromGrpPromptBtn.getPreferredSize().height);
        fromGrpPromptBtn.addActionListener(new GroupPromptActionListener(fromGrpField, fromDivField, fromCrdField));
        fromGrpPromptBtn.addKeyListener(new GroupPromptKeyListener(fromGrpField, fromDivField, fromCrdField));
        fromGrpPromptBtn.addMouseListener(new PromptMouseListener());
        fromGrpPromptBtn.addFocusListener(new PromptFocusListener(fromGrpPromptBtn));
        fromGrpPromptBtn.setFocusTraversalKeysEnabled(false);
        prompt.add(fromGrpPromptBtn, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton fromDivPromptBtn = new HTH_PromptButton();
        fromDivPromptBtn.setBounds(strLineX + fromDivField.getPreferredSize().width + 10, 220, fromDivPromptBtn.getPreferredSize().width, fromDivPromptBtn.getPreferredSize().height);
        fromDivPromptBtn.addActionListener(new DivisionPromptActionListener(fromGrpField, fromDivField, fromCrdField));
        fromDivPromptBtn.addKeyListener(new DivisionPromptKeyListener(fromGrpField, fromDivField, fromCrdField));
        fromDivPromptBtn.addMouseListener(new PromptMouseListener());
        fromDivPromptBtn.addFocusListener(new PromptFocusListener(fromDivPromptBtn));
        fromDivPromptBtn.setFocusTraversalKeysEnabled(false);
        prompt.add(fromDivPromptBtn, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton fromCrdPromptBtn = new HTH_PromptButton();
        fromCrdPromptBtn.setBounds(strLineX + fromCrdField.getPreferredSize().width + 10, 280, fromCrdPromptBtn.getPreferredSize().width, fromCrdPromptBtn.getPreferredSize().height);
        fromCrdPromptBtn.addActionListener(new CardPromptActionListener(fromGrpField, fromDivField, fromCrdField, false));
        fromCrdPromptBtn.addKeyListener(new CardPromptKeyListener(fromGrpField, fromDivField, fromCrdField, toGrpField, false));
        fromCrdPromptBtn.addMouseListener(new PromptMouseListener());
        fromCrdPromptBtn.addFocusListener(new PromptFocusListener(fromCrdPromptBtn));
        fromCrdPromptBtn.setFocusTraversalKeysEnabled(false);
        prompt.add(fromCrdPromptBtn, JLayeredPane.DEFAULT_LAYER);

        strLineX += contentScreen.getSize().width / 2;
        toGrpField.setBounds(strLineX, 155, toGrpField.getPreferredSize().width, toGrpField.getPreferredSize().height);
        prompt.add(toGrpField, JLayeredPane.DEFAULT_LAYER);

        toDivField.setBounds(strLineX, 215, toDivField.getPreferredSize().width, toDivField.getPreferredSize().height);
        prompt.add(toDivField, JLayeredPane.DEFAULT_LAYER);

        toCrdField.setBounds(strLineX, 275, toCrdField.getPreferredSize().width, toCrdField.getPreferredSize().height);
        toCrdField.setFocusTraversalKeysEnabled(false);
        toCrdField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    fromGrpField.requestFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        prompt.add(toCrdField, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton toGrpPromptBtn = new HTH_PromptButton();
        toGrpPromptBtn.setBounds(strLineX + toGrpField.getPreferredSize().width + 10, 160, toGrpPromptBtn.getPreferredSize().width, toGrpPromptBtn.getPreferredSize().height);
        toGrpPromptBtn.addActionListener(new GroupPromptActionListener(toGrpField, toDivField, toCrdField));
        toGrpPromptBtn.addKeyListener(new GroupPromptKeyListener(toGrpField, toDivField, toCrdField));
        toGrpPromptBtn.addMouseListener(new PromptMouseListener());
        toGrpPromptBtn.addFocusListener(new PromptFocusListener(toGrpPromptBtn));
        toGrpPromptBtn.setFocusTraversalKeysEnabled(false);
        prompt.add(toGrpPromptBtn, JLayeredPane.DEFAULT_LAYER);

        HTH_PromptButton toDivPromptBtn = new HTH_PromptButton();
        toDivPromptBtn.setBounds(strLineX + toDivField.getPreferredSize().width + 10, 220, toDivPromptBtn.getPreferredSize().width, toDivPromptBtn.getPreferredSize().height);
        toDivPromptBtn.addActionListener(new DivisionPromptActionListener(toGrpField, toDivField, toCrdField));
        toDivPromptBtn.addKeyListener(new DivisionPromptKeyListener(toGrpField, toDivField, toCrdField));
        toDivPromptBtn.addMouseListener(new PromptMouseListener());
        toDivPromptBtn.addFocusListener(new PromptFocusListener(toDivPromptBtn));
        toDivPromptBtn.setFocusTraversalKeysEnabled(false);
        prompt.add(toDivPromptBtn, JLayeredPane.DEFAULT_LAYER);
    }

    private void setCopyControlBtn() {
        controlPanel.removeAll();

        String exitKey = "Exit", okKey = "Ok";
        Action exitAction, okAction;
        exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10106L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectionScreen();
            }
        };
        okAction = new AbstractAction(okKey) {
            private static final long serialVersionUID = 10107L;

            @Override
            public void actionPerformed(ActionEvent e) {
                copyCard();
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
        okBtn.setToolTipText("ENTER=OK");
        okBtn.addActionListener(okAction);
        okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
        okBtn.getActionMap().put(okKey, okAction);
        controlPanel.add(okBtn);

        controlKeys = new HTH_ControlButton[]{exitBtn, okBtn};
    }

    public void copyAll() {
        String msg = "";
        String fromGrp = fromGrpField.getText();
        String group1 = grpField.getText();
        String fromCrd = fromCrdField.getText();
        if (group1.isEmpty()) {
            if (!fromGrp.isEmpty() && fromCrd.isEmpty()) {
                msg = "Are You sure ? This will copy all card into all divisions.";
                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
                int result = HTH_Dialog.showConfirmDialog(this, "Confirmation", msg, dialogSize);
                if (result == JOptionPane.YES_OPTION) {
                    cardList = IDCARD.getCardList(fromGrp, "");
                    //boolean isExisted = IDCARD.isCardExistedWithoutCard(fromGrp);
                    if (cardList.length > 0) {

                        showLoading();
                        Thread idLoader = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                selectedCard = IDCARD.getCard(fromGrp, "", "");
                                updateDivList(fromGrpField);
                                if (divisionList != null) {
                                    for (Division div : divisionList) {
                                        for (int i = 0; i < cardList.length; i++) {
                                            selectedCard.setDiv(div.getID());
                                            selectedCard.setCardNumber(cardList[0].getCardNumber());
                                            IDCARD.savCard(selectedCard);
                                        }
                                    }
                                }
                            }
                        });
                        idLoader.start();
                    }
                }

            } else if (!fromGrp.isEmpty() && !fromCrd.isEmpty()) {
                msg = "Are You sure ? This will copy card " + fromCrd + " into all divisions.";
                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
                int result = HTH_Dialog.showConfirmDialog(this, "Confirmation", msg, dialogSize);
                if (result == JOptionPane.YES_OPTION) {
                    // cardList = IDCARD.getCardList(fromGrp, fromDivField.getText());
                    boolean isExisted = IDCARD.isCardExistedWithoutDiv(fromGrp, fromCrd);
                    if (isExisted) {
                        showLoading();
                        Thread idLoader = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                selectedCard = IDCARD.getCard(fromGrp, "", fromCrd);
                                updateDivList(fromGrpField);
                                if (divisionList != null) {
                                    for (Division div : divisionList) {
                                        selectedCard.setDiv(div.getID());
                                        //IDCARD.cpyCard(fromGrp, div.getID(), fromCrd, fromCrd, div.getID(), fromCrd);
                                        IDCARD.savCard(selectedCard);
                                    }
                                }
                                //editCard(true);
                            }
                        });
                        idLoader.start();
                    }
                }
            } else {

            }
        }
    }

    private void showGroupPrompt(final HTH_TextField grpField, final HTH_TextField divField, final HTH_TextField cardField) {
        String cpyKey = "Copy";
        HTH_FunctionButton cpyBtnAll;
        Action cpyAddAllAction;

        cpyAddAllAction = new AbstractAction(cpyKey) {
            private static final long serialVersionUID = 10104L;

            @Override
            public void actionPerformed(ActionEvent e) {
                copyAll();
            }
        };

        compReqFocus = grpField;
        hideContents();
        setPromptFunctionKeys();
        setPromptControlKeys();

        setHeaderTitle("Select Group");
        selGroupList = groupList;

        cpyBtnAll = new HTH_FunctionButton("Copy To All Div");
        cpyBtnAll.setToolTipText("F8=Copy To All Div");
        cpyBtnAll.addActionListener(cpyAddAllAction);
        cpyBtnAll.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), cpyKey);
        //cpyBtn.getActionMap().put(cpyKey, cpyAction);

        JPanel headerPanel = setGroupRowPanel(null, null, null, null);
        headerPanel.setBounds(15, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setGroupPrompt(grpField, divField, cardField));
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
                    informationPane.setViewportView(setGroupPrompt(grpField, divField, cardField));
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
                informationPane.setViewportView(setGroupPrompt(grpField, divField, cardField));
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
        functionKeys = new HTH_FunctionButton[]{cpyBtnAll};
    }

    private void showDivisionPrompt(HTH_TextField grpField, final HTH_TextField divField, final HTH_TextField cardField) {
        compReqFocus = divField;
        hideContents();
        updateDivList(grpField);
        selDivisionList = divisionList;
        setPromptFunctionKeys();
        setPromptControlKeys();
        setHeaderTitle("Select Division");

        JPanel headerPanel = setDivisionRowPanel(null, null, null);
        headerPanel.setBounds(50, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setDivisionPrompt(divField, cardField));
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
                    informationPane.setViewportView(setDivisionPrompt(divField, cardField));
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
                informationPane.setViewportView(setDivisionPrompt(divField, cardField));
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
    }

    private void showHeaderChooser() {
        selHeaderList = searchHeader("");

        setPromptFunctionKeys();
        setPromptControlKeys();
        setHeaderTitle("Select Heading");

        JPanel headerPanel = setHeaderRowPanel(null);
        headerPanel.setBounds(15, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setHeaderPrompt());
        informationPane.setBounds(15, 40 + headerPanel.getPreferredSize().height, headerPanel.getPreferredSize().width + 15, contentScreen.getSize().height * 9 / 10);
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
                    selHeaderList = searchHeader(searchField.getText().trim());
                    informationPane.setViewportView(setHeaderPrompt());
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selHeaderList = searchHeader(searchField.getText().trim());
                informationPane.setViewportView(setHeaderPrompt());
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
    }

    private JPanel setGroupPrompt(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField) {
        JPanel groupPanel = new JPanel();
        groupPanel.setBackground(Color.WHITE);

        if (selGroupList.length == 0) {
            groupPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            groupPanel.add(getLabel("No records found."));
        } else {
            groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
            for (GroupMaster group : selGroupList) {
                groupPanel.add(setGroupRowPanel(group, grpField, divField, crdField));
            }
        }

        return groupPanel;
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

    private JPanel setGroupRowPanel(final GroupMaster group, final HTH_TextField grpField, final HTH_TextField divField, final HTH_TextField crdField) {
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
                        divField.setText("");
                        crdField.setText("");

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
                    divField.setText("");
                    crdField.setText("");

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

    private GroupMaster[] searchGroup(String key) {
        if (key.isEmpty()) {
            return groupList;
        } else {
            List<GroupMaster> matchedGroup = new ArrayList<GroupMaster>();
            for (GroupMaster group : groupList) {
                boolean isPossible = group.getName().contains(key) || group.getID().contains(key);
                if (isPossible) {
                    matchedGroup.add(group);
                }
            }

            return matchedGroup.toArray(new GroupMaster[0]);
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

    private IDCard[] searchIDCard(String key) {
        if (key.isEmpty()) {
            return cardList;
        } else {
            List<IDCard> matchedCard = new ArrayList<IDCard>();
            for (IDCard card : cardList) {
                boolean isPossible = card.getCardNumber().contains(key) || card.getDescription().contains(key);
                if (isPossible) {
                    matchedCard.add(card);
                }
            }

            return matchedCard.toArray(new IDCard[0]);
        }
    }

    private IDLogo[] searchLogo(String key) {
        if (key.isEmpty()) {
            return IDCIMG.getLogoList();
        } else {
            List<IDLogo> matchedLogo = new ArrayList<IDLogo>();
            for (IDLogo logo : IDCIMG.getLogoList()) {
                boolean isPossible = logo.getName().contains(key) || logo.getDescription().contains(key);
                if (isPossible) {
                    matchedLogo.add(logo);
                }
            }

            return matchedLogo.toArray(new IDLogo[0]);
        }
    }

    private IDHeader[] searchHeader(String key) {
        if (key.isEmpty()) {
            return IDCFLD.getIDHeaderList();
        } else {
            String lowKey = key.toLowerCase();

            List<IDHeader> matchedHeader = new ArrayList<IDHeader>();
            for (IDHeader header : IDCFLD.getIDHeaderList()) {
                String lowDesc = header.getDesc().toLowerCase();
                String lowHeader = header.getHeader().toLowerCase();
                boolean isPossible = lowDesc.contains(lowKey) || lowHeader.contains(lowKey);
                if (isPossible) {
                    matchedHeader.add(header);
                }
            }

            return matchedHeader.toArray(new IDHeader[0]);
        }
    }

    private void updateDivList(HTH_TextField grpField) {
        String selectedGroup = grpField.getText().trim();

        if (selectedGroup.isEmpty()) {
            divisionList = new Division[0];
        } else {
            divisionList = DIVISN.getDivList(selectedGroup);
        }
    }

    private JPanel setDivisionRowPanel(final Division div, final HTH_TextField divField, final HTH_TextField crdField) {
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
                        crdField.setText("");

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
                    crdField.setText("");

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

    private JPanel setDivisionPrompt(HTH_TextField divField, HTH_TextField crdField) {
        JPanel divPanel = new JPanel();
        divPanel.setBackground(Color.WHITE);

        if (selDivisionList.length == 0) {
            divPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            divPanel.add(getLabel("No records found."));
        } else {
            divPanel.setLayout(new BoxLayout(divPanel, BoxLayout.Y_AXIS));

            for (Division div : selDivisionList) {
                divPanel.add(setDivisionRowPanel(div, divField, crdField));
            }
        }

        return divPanel;
    }

    private void updateCardList(HTH_TextField grpField, HTH_TextField divField) {
        String selectedGroup = grpField.getText().trim();
        String selectedDiv = divField.getText().trim();

        if (selectedGroup.isEmpty()) {
            cardList = new IDCard[0];
        } else {
            cardList = IDCARD.getCardList(selectedGroup, selectedDiv);
        }
    }

    private JPanel setHeaderRowPanel(IDHeader header) {
        final JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(true);
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        rowPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        Font font = HTH_FONT.deriveFont(16.0f);
        final JLabel idField = getLabel(6, font);
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JLabel txtField = getLabel(25, font);
        txtField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JLabel descField = getLabel(40, font);
        descField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        int rowW = idField.getPreferredSize().width + txtField.getPreferredSize().width + descField.getPreferredSize().width;
        int rowH = idField.getPreferredSize().height;
        rowPanel.setPreferredSize(new Dimension(rowW, rowH));
        rowPanel.setMinimumSize(new Dimension(rowW, rowH));
        rowPanel.setMaximumSize(new Dimension(rowW, rowH));


        if (header == null) {
            idField.setText("ID");
            idField.setForeground(Color.BLACK);

            txtField.setText("Heading");
            txtField.setForeground(Color.BLACK);

            descField.setText("Description");
            descField.setForeground(Color.BLACK);
        } else {
            rowPanel.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        int selStr = focusedTextPane.getSelectionStart();
                        int selEnd = focusedTextPane.getSelectionEnd();

                        String txt = focusedTextPane.getText();
                        String strTxt = txt.substring(0, selStr);
                        String var = "@@" + idField.getText() + "@@";
                        String endTxt = "";
                        if (selEnd != txt.length()) {
                            endTxt = txt.substring(selEnd);
                        }
                        focusedTextPane.setText(strTxt + var + endTxt);

                        removePromptScreen();
                        focusedTextPane.requestFocus();
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
                    int selStr = focusedTextPane.getSelectionStart();
                    int selEnd = focusedTextPane.getSelectionEnd();

                    String txt = focusedTextPane.getText();
                    String strTxt = txt.substring(0, selStr);
                    String var = "@@" + idField.getText() + "@@";
                    String endTxt = "";
                    if (selEnd != txt.length()) {
                        endTxt = txt.substring(selEnd);
                    }
                    focusedTextPane.setText(strTxt + var + endTxt);

                    removePromptScreen();
                    focusedTextPane.requestFocus();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });

            idField.setText(header.getID());
            idField.setForeground(new Color(0, 0, 150));

            txtField.setText(header.getHeader());
            txtField.setForeground(new Color(0, 0, 150));

            descField.setText(header.getDesc());
            descField.setForeground(new Color(0, 0, 150));
        }

        rowPanel.add(idField);
        rowPanel.add(txtField);
        rowPanel.add(descField);

        return rowPanel;
    }

    private JPanel setHeaderPrompt() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);

        if (selHeaderList.length == 0) {
            headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            headerPanel.add(getLabel("No records found."));
        } else {
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

            for (IDHeader header : selHeaderList) {
                headerPanel.add(setHeaderRowPanel(header));
            }
        }

        return headerPanel;
    }

    private void showLoading() {
        contentScreen.removeAll();
        setFunctionKeys(new HTH_FunctionButton[0]);

        final JLabel loadingLabel = getLabel("Loading ID images...");
        loadingLabel.setBounds(40, 40, loadingLabel.getPreferredSize().width, loadingLabel.getPreferredSize().height);
        contentScreen.add(loadingLabel, JLayeredPane.DEFAULT_LAYER);

        revalidate();
        repaint();
    }

    private void previewCard() {
        setHeaderTitle("Preview ID Card");
        while (!selectedCard.isReady()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        showID(true);
    }

    private void editCard(boolean isStart) {
        contentScreen.removeAll();

        setHeaderTitle("Edit ID Card");
        if (isStart) {
            String title = "Enter description of the card";

            JLabel descLabel = getLabel("Description:", HTH_FONT.deriveFont(16.0f));

            HTH_TextField descField = new HTH_TextField(60, HTH_FONT.deriveFont(14.0f));
            descField.setText(selectedCard.getDescription());
            descField.setSelectionStart(0);
            descField.setSelectionEnd(descField.getText().length());

            JPanel dialogPanel = new JPanel();
            dialogPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 3, 0));
            dialogPanel.setOpaque(false);
            dialogPanel.add(descLabel);
            dialogPanel.add(descField);

            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 80 / 100, contentScreen.getSize().height * 3 / 10);
            int option = HTH_Dialog.showInputDialog(this, title, dialogPanel, dialogSize);
            if (option == JOptionPane.YES_OPTION) {
                selectedCard.setDescription(descField.getText().trim());
                isCardLoaded = true;
            } else {
                setSelectionScreen();
                return;
            }
        }

        isFront = true;
        isAdvanced = false;

        if (isLogoEditor) {
            editLogos();
        } else {
            editTexts();
        }
    }

    private void confirmExit() {
        UIManager.put("Button.defaultButtonFollowsFocus", true);
        String msg = "Save the changes?";
        Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
        int option = HTH_Dialog.showConfirmDialog(this, "Confirm Save", msg, dialogSize);
        if (option == JOptionPane.YES_OPTION) {
            saveCard();
            exitProgram();
        } else if (option == JOptionPane.NO_OPTION) {
            exitProgram();
        }
    }

    private void editTexts() {
        contentScreen.removeAll();

        String previewKey = "Preview";
        Action previewAction = new AbstractAction(previewKey) {
            private static final long serialVersionUID = 10110L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                }
                previewCard();
            }
        };
        HTH_FunctionButton previewBtn = new HTH_FunctionButton("Preview");
        previewBtn.setToolTipText("F5=Preview");
        previewBtn.addActionListener(previewAction);
        previewBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), previewKey);
        previewBtn.getActionMap().put(previewKey, previewAction);

        String turnKey = "Turn";
        Action turnAction = new AbstractAction(turnKey) {
            private static final long serialVersionUID = 10111L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                }
                isFront = !isFront;
                editTexts();
            }
        };
        HTH_FunctionButton turnBtn = isFront ? new HTH_FunctionButton("Card Back") : new HTH_FunctionButton("Card Front");
        turnBtn.setToolTipText("F9=" + turnBtn.getText());
        turnBtn.addActionListener(turnAction);
        turnBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), turnKey);
        turnBtn.getActionMap().put(turnKey, turnAction);

        String logoKey = "Logos";
        Action logoAction = new AbstractAction(logoKey) {
            private static final long serialVersionUID = 10112L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                }
                editLogos();
            }
        };
        HTH_FunctionButton editLogoBtn = new HTH_FunctionButton("Logos");
        editLogoBtn.setToolTipText("F4=Logos");
        editLogoBtn.addActionListener(logoAction);
        editLogoBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), logoKey);
        editLogoBtn.getActionMap().put(logoKey, logoAction);

        String saveKey = "Save";
        Action saveAction = new AbstractAction(saveKey) {
            private static final long serialVersionUID = 10113L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                }
                saveCard();
            }
        };
        HTH_FunctionButton saveBtn = new HTH_FunctionButton("Save");
        saveBtn.setToolTipText("F6=Save");
        saveBtn.addActionListener(saveAction);
        saveBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), saveKey);
        saveBtn.getActionMap().put(saveKey, saveAction);

        String headKey = "Headers";
        Action headAction = new AbstractAction(headKey) {
            private static final long serialVersionUID = 10114L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                    showHeaderChooser();
                }
            }
        };
        HTH_FunctionButton headBtn = new HTH_FunctionButton("Headers");
        headBtn.setToolTipText("F7=Headers");
        headBtn.addActionListener(headAction);
        headBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), headKey);
        headBtn.getActionMap().put(headKey, headAction);

        String advKey = "Advanced";
        Action advAction = new AbstractAction(advKey) {
            private static final long serialVersionUID = 10115L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                }
                isAdvanced = !isAdvanced;
                editTexts();
            }
        };
        HTH_FunctionButton advBtn = isAdvanced ? new HTH_FunctionButton("Edit Texts") : new HTH_FunctionButton("Format Headers");
        advBtn.setToolTipText("F8=" + advBtn.getText());
        advBtn.addActionListener(advAction);
        advBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), advKey);
        advBtn.getActionMap().put(advKey, advAction);

        String rmvKey = "Delete Card";
        Action rmvAction = new AbstractAction(rmvKey) {
            private static final long serialVersionUID = 10116L;

            @Override
            public void actionPerformed(ActionEvent e) {
                startRemoveProcess();
            }
        };
        HTH_FunctionButton rmvBtn = new HTH_FunctionButton("Delete");
        rmvBtn.setToolTipText("Shift+F11=Delete");
        rmvBtn.addActionListener(rmvAction);
        rmvBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, InputEvent.SHIFT_DOWN_MASK), rmvKey);
        rmvBtn.getActionMap().put(rmvKey, rmvAction);
        functionKeys = new HTH_FunctionButton[]{previewBtn, turnBtn, editLogoBtn, saveBtn, headBtn, advBtn, rmvBtn};
        setFunctionKeys(functionKeys);

        controlPanel.removeAll();

        String exitKey = "Exit";
        Action exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10117L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isAdvanced) {
                    updateTxt();
                }
                confirmExit();
            }
        };
        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);
        controlKeys = new HTH_ControlButton[]{exitBtn};

        showTxtModifiers();
        showTxtEditor();

        revalidate();
        repaint();

        linePanes[0].requestFocus();
        isLogoEditor = false;
    }

    private void editLogos() {
        contentScreen.removeAll();

        String previewKey = "Preview";
        Action previewAction = new AbstractAction(previewKey) {
            private static final long serialVersionUID = 10118L;

            @Override
            public void actionPerformed(ActionEvent e) {
                previewCard();
            }
        };
        HTH_FunctionButton previewBtn = new HTH_FunctionButton("Preview");
        previewBtn.setToolTipText("F5=Preview");
        previewBtn.addActionListener(previewAction);
        previewBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), previewKey);
        previewBtn.getActionMap().put(previewKey, previewAction);

        String turnKey = "Turn";
        Action turnAction = new AbstractAction(turnKey) {
            private static final long serialVersionUID = 10119L;

            @Override
            public void actionPerformed(ActionEvent e) {
                isFront = !isFront;
                editLogos();
            }
        };
        HTH_FunctionButton turnBtn = isFront ? new HTH_FunctionButton("Card Back") : new HTH_FunctionButton("Card Front");
        turnBtn.setToolTipText("F9=" + turnBtn.getText());
        turnBtn.addActionListener(turnAction);
        turnBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), turnKey);
        turnBtn.getActionMap().put(turnKey, turnAction);

        String txtKey = "Texts";
        Action txtAction = new AbstractAction(txtKey) {
            private static final long serialVersionUID = 10120L;

            @Override
            public void actionPerformed(ActionEvent e) {
                isAdvanced = false;
                editTexts();
            }
        };
        HTH_FunctionButton editTxtBtn = new HTH_FunctionButton("Texts");
        editTxtBtn.setToolTipText("F4=Texts");
        editTxtBtn.addActionListener(txtAction);
        editTxtBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), txtKey);
        editTxtBtn.getActionMap().put(txtKey, txtAction);

        String saveKey = "Save";
        Action saveAction = new AbstractAction(saveKey) {
            private static final long serialVersionUID = 10121L;

            @Override
            public void actionPerformed(ActionEvent e) {
                saveCard();
            }
        };
        HTH_FunctionButton saveBtn = new HTH_FunctionButton("Save");
        saveBtn.setToolTipText("F6=Save");
        saveBtn.addActionListener(saveAction);
        saveBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), saveKey);
        saveBtn.getActionMap().put(saveKey, saveAction);

        functionKeys = new HTH_FunctionButton[]{previewBtn, turnBtn, editTxtBtn, saveBtn};
        setFunctionKeys(functionKeys);

        controlPanel.removeAll();

        String exitKey = "Exit";
        Action exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10122L;

            @Override
            public void actionPerformed(ActionEvent e) {
                confirmExit();
            }
        };
        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);

        controlPanel.add(exitBtn);
        controlKeys = new HTH_ControlButton[]{exitBtn};

        showLogoModifiers();
        showLogoEditor();

        revalidate();
        repaint();

        logoBtns[focusedIdx].requestFocus();
        isLogoEditor = true;
    }

    private void updateTxt() {
        String[] lines = new String[18];

        for (int idx = 0; idx < lines.length; idx++) {
            JTextPane pane = linePanes[idx];
            lines[idx] = pane.getText();
        }

        if (isFront) {
            selectedCard.setFrontTexts(lines);
        } else {
            selectedCard.setBackTexts(lines);
        }
    }

    private void showTxtModifiers() {
        JPanel modPanel = new JPanel();
        modPanel.setOpaque(false);
        modPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        modPanel.setBounds(2, 2, contentScreen.getSize().width - 4, contentScreen.getSize().height * 15 / 100);
        contentScreen.add(modPanel, JLayeredPane.DEFAULT_LAYER);

        ActionListener[] listeners;
        if (isAdvanced) {
            JPanel fontPanel = new JPanel();
            fontPanel.setOpaque(false);
            fontPanel.setLayout(new GridLayout(0, 1));
            fontPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Font", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, HTH_FONT.deriveFont(12.0f), Color.BLACK));
            fontPanel.setPreferredSize(new Dimension(contentScreen.getSize().width * 15 / 100, modPanel.getBounds().height));

            listeners = bold.getActionListeners();
            if (listeners.length == 1) {
                bold.removeActionListener(listeners[0]);
            }
            bold.addActionListener(new BoldAdvancedActionListener());
            fontPanel.add(bold);

            listeners = italic.getActionListeners();
            if (listeners.length == 1) {
                italic.removeActionListener(listeners[0]);
            }
            italic.addActionListener(new ItalicAdvancedActionListener());
            fontPanel.add(italic);

            listeners = underline.getActionListeners();
            if (listeners.length == 1) {
                underline.removeActionListener(listeners[0]);
            }
            underline.addActionListener(new UnderlineAdvancedActionListener());
            fontPanel.add(underline);
            modPanel.add(fontPanel);
            //TODO: INCOMPLETE....Needed color picker.
        } else {
            JPanel alignPanel = new JPanel();
            alignPanel.setOpaque(false);
            alignPanel.setLayout(new GridLayout(0, 1));
            alignPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Alignment", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, HTH_FONT.deriveFont(12.0f), Color.BLACK));
            alignPanel.setPreferredSize(new Dimension(contentScreen.getSize().width * 15 / 100, modPanel.getBounds().height));

            listeners = left.getActionListeners();
            if (listeners.length == 1) {
                left.removeActionListener(listeners[0]);
            }
            left.addActionListener(new LeftActionListener());
            alignPanel.add(left);

            listeners = center.getActionListeners();
            if (listeners.length == 1) {
                center.removeActionListener(listeners[0]);
            }
            center.addActionListener(new CenterActionListener());
            alignPanel.add(center);

            listeners = right.getActionListeners();
            if (listeners.length == 1) {
                right.removeActionListener(listeners[0]);
            }
            right.addActionListener(new RightActionListener());
            alignPanel.add(right);
            modPanel.add(alignPanel);

            JPanel fontPanel = new JPanel();
            fontPanel.setOpaque(false);
            fontPanel.setLayout(new GridLayout(0, 1));
            fontPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Font", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, HTH_FONT.deriveFont(12.0f), Color.BLACK));
            fontPanel.setPreferredSize(new Dimension(contentScreen.getSize().width * 15 / 100, modPanel.getBounds().height));

            listeners = bold.getActionListeners();
            if (listeners.length == 1) {
                bold.removeActionListener(listeners[0]);
            }
            bold.addActionListener(new BoldActionListener());
            fontPanel.add(bold);

            listeners = italic.getActionListeners();
            if (listeners.length == 1) {
                italic.removeActionListener(listeners[0]);
            }
            italic.addActionListener(new ItalicActionListener());
            fontPanel.add(italic);

            listeners = underline.getActionListeners();
            if (listeners.length == 1) {
                underline.removeActionListener(listeners[0]);
            }
            underline.addActionListener(new UnderlineActionListener());
            fontPanel.add(underline);
            modPanel.add(fontPanel);

            JPanel sizePanel = new JPanel();
            sizePanel.setOpaque(false);
            sizePanel.setLayout(new GridLayout(0, 1));
            sizePanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Size", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, HTH_FONT.deriveFont(12.0f), Color.BLACK));
            sizePanel.setPreferredSize(new Dimension(contentScreen.getSize().width * 15 / 100, modPanel.getBounds().height));
            sizePanel.add(small);
            sizePanel.add(normal);
            sizePanel.add(large);
            sizePanel.add(xLarge);
            modPanel.add(sizePanel);
            //TODO: INCOMPLETE....Needed color picker.
        }
    }

    private void showTxtEditor() {
        JPanel txtPanel = new JPanel();
        txtPanel.setLayout(new GridLayout(18, 0, 0, 0));
        txtPanel.setBackground(Color.WHITE);
        txtPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        txtPanel.setBounds(2, 120, contentScreen.getSize().width - 4, contentScreen.getSize().height * 8 / 10);
        contentScreen.add(txtPanel, JLayeredPane.DEFAULT_LAYER);

        String[] lines = isFront ? selectedCard.getFrontTexts() : selectedCard.getBackTexts();
        String[] mods = isFront ? selectedCard.getFrontTextModifiers() : selectedCard.getBackTextModifiers();

        if (isAdvanced) {
            for (int idx = 0; idx < lines.length; idx++) {
                String text = lines[idx];
                String styleList = mods[idx];

                if (text.contains("@@")) {
                    text = IDCARD.mergeVar(text);
                }

                JPanel linePanel = new JPanel();
                linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
                linePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 18));
                linePanel.setOpaque(false);

                if (text.contains("||")) {
                    String[] texts = text.split("\\|\\|");
                    for (int col = 0; col < texts.length; col++) {
                        String txt = texts[col];
                        JTextPane txtPane = getTextPane(txt, styleList, idx + 1, col + 1);
                        txtPane.setPreferredSize(new Dimension(txtPanel.getBounds().width / texts.length, txtPanel.getBounds().height / 18));
                        txtPane.setMinimumSize(new Dimension(txtPanel.getBounds().width / texts.length, txtPanel.getBounds().height / 18));
                        txtPane.setMaximumSize(new Dimension(txtPanel.getBounds().width / texts.length, txtPanel.getBounds().height / 18));
                        linePanel.add(txtPane);
                    }
                } else {
                    JTextPane txtPane = getTextPane(text, styleList, idx + 1, 1);
                    txtPane.setPreferredSize(new Dimension(txtPanel.getBounds().width, txtPanel.getBounds().height / 18));
                    txtPane.setMinimumSize(new Dimension(txtPanel.getBounds().width, txtPanel.getBounds().height / 18));
                    txtPane.setMaximumSize(new Dimension(txtPanel.getBounds().width, txtPanel.getBounds().height / 18));
                    linePanel.add(txtPane);
                }

                txtPanel.add(linePanel);
            }
            applyMask(txtPanel);
        } else {
            for (int idx = 0; idx < lines.length; idx++) {
                String text = lines[idx];
                String styleList = mods[idx];

                JTextPane txtPane = getTextPane(text, styleList, idx + 1, 1);
                txtPane.setPreferredSize(new Dimension(txtPanel.getBounds().width, txtPanel.getBounds().height / 18));
                txtPane.setMinimumSize(new Dimension(txtPanel.getBounds().width, txtPanel.getBounds().height / 18));
                txtPane.setMaximumSize(new Dimension(txtPanel.getBounds().width, txtPanel.getBounds().height / 18));
                txtPanel.add(txtPane);
                linePanes[idx] = txtPane;
            }
        }
    }

    private JTextPane getTextPane(String text, String styleList, final int line, final int col) {
        final JTextPane txtPane = new JTextPane();
        txtPane.setFont(DEFAULT_FONT);
        txtPane.setEditable(!isAdvanced);
        txtPane.setOpaque(false);
        txtPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 2, 0, 2)));
        txtPane.setForeground(Color.BLACK);
        txtPane.getDocument().addDocumentListener(new HTH_DocumentListener(txtPane, 150));
        txtPane.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                focusedTextPane = txtPane;
                if (!isAdvanced) {
                    updateTxtModifiers();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        if (isAdvanced) {
            txtPane.addCaretListener(new CaretListener() {
                @Override
                public void caretUpdate(CaretEvent e) {
                    selectedCol = col;
                    selectedLine = line;
                    updateMasks();
                }
            });
        }

        StyledDocument doc = txtPane.getStyledDocument();
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_LEFT);
        doc.setParagraphAttributes(0, text.length(), alignment, false);

        if (!styleList.isEmpty()) {
            String[] keys = styleList.split(",");
            String value;

            for (String key : keys) {
                value = key.trim();
                switch (value) {
                    case "center":
                        StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_CENTER);
                        doc.setParagraphAttributes(0, text.length(), alignment, false);
                        break;
                    case "left":
                        StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_LEFT);
                        doc.setParagraphAttributes(0, text.length(), alignment, false);
                        break;
                    case "right":
                        StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_RIGHT);
                        doc.setParagraphAttributes(0, text.length(), alignment, false);
                        break;
                    case "b":
                        txtPane.setFont(txtPane.getFont().deriveFont(Font.BOLD));
                        break;
                    case "i":
                        txtPane.setFont(txtPane.getFont().deriveFont(Font.ITALIC));
                        break;
                    case "u":
                        SimpleAttributeSet attrs = new SimpleAttributeSet();
                        StyleConstants.setUnderline(attrs, true);
                        doc.setParagraphAttributes(0, text.length(), attrs, false);
                        break;
                    case "WHITE":
                        txtPane.setForeground(new Color(255, 255, 255));
                        break;
                    case "RED":
                        txtPane.setForeground(new Color(255, 0, 0));
                        break;
                    case "GREEN":
                        txtPane.setForeground(new Color(0, 255, 0));
                        break;
                    case "BLUE":
                        txtPane.setForeground(new Color(0, 0, 255));
                        break;
                    case "small":
                        txtPane.setFont(txtPane.getFont().deriveFont(14.0f));
                        break;
                    case "large":
                        txtPane.setFont(txtPane.getFont().deriveFont(18.0f));
                        break;
                    case "xlarge":
                        txtPane.setFont(txtPane.getFont().deriveFont(20.0f));
                        break;
                    default:
                        if (value.contains("#") && (value.length() == 4 || value.length() == 7)) {
                            String colorHex = value;

                            if (value.length() == 4) {
                                colorHex = "#" + value.charAt(1) + value.charAt(1) + value.charAt(2) + value.charAt(2) + value.charAt(3) + value.charAt(3);
                            }

                            txtPane.setForeground(new Color(
                                    Integer.valueOf(colorHex.substring(1, 3), 16),
                                    Integer.valueOf(colorHex.substring(3, 5), 16),
                                    Integer.valueOf(colorHex.substring(5, 7), 16)));
                        }
                }
            }
        }

        try {
            doc.insertString(0, text, alignment);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return txtPane;
    }

    private void applyMask(JPanel textPanel) {
        Component[] lineList = textPanel.getComponents();

        JPanel linePanel;
        JTextPane textField;
        int lineIdx, col, start, length;
        String[] modList;

        IDMask[] maskList = isFront ? selectedCard.getFrontMask() : selectedCard.getBackMask();

        for (IDMask mask : maskList) {
            lineIdx = mask.getLine() - 1;
            col = mask.getCol();
            start = mask.getStartIdx();
            length = mask.getEndIdx() - start + 1;
            modList = mask.getModifierList();

            if (col == -1) {
                linePanel = (JPanel) lineList[lineIdx + 1];
                String colorHex = modList[0].replaceFirst("BG#", "");
                Color bgColor = new Color(Integer.valueOf(colorHex.substring(0, 2), 16), Integer.valueOf(colorHex.substring(2, 4), 16), Integer.valueOf(colorHex.substring(4), 16));
                linePanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 0, 0, 0),
                        BorderFactory.createMatteBorder(2, 0, 0, 0, bgColor)));
            } else {
                try {
                    linePanel = (JPanel) lineList[lineIdx];
                    textField = (JTextPane) linePanel.getComponent(col - 1);

                    StyledDocument doc = textField.getStyledDocument();
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    for (String mod : modList) {
                        if (mod.equals("b")) {
                            StyleConstants.setBold(attrs, true);
                        } else if (mod.equals("i")) {
                            StyleConstants.setItalic(attrs, true);
                        } else if (mod.equals("u")) {
                            StyleConstants.setUnderline(attrs, true);
                        } else if (mod.startsWith("#") && mod.length() == 7) {
                            String colorHex = mod.replaceFirst("#", "");
                            Color color = new Color(Integer.valueOf(colorHex.substring(0, 2), 16), Integer.valueOf(colorHex.substring(2, 4), 16), Integer.valueOf(colorHex.substring(4), 16));
                            StyleConstants.setForeground(attrs, color);
                        }
                    }
                    doc.setCharacterAttributes(start, length, attrs, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateMasks() {
        bold.setSelected(false);
        ;
        italic.setSelected(false);
        underline.setSelected(false);

        IDMask[] masks = isFront ? selectedCard.getFrontMask() : selectedCard.getBackMask();
        int startIdx = focusedTextPane.getSelectionStart();
        int endIdx = focusedTextPane.getSelectionEnd() - 1;

        for (IDMask mask : masks) {
            if (mask.getLine() == selectedLine && mask.getCol() == selectedCol
                    && mask.getStartIdx() <= startIdx && mask.getEndIdx() >= endIdx) {
                String[] formatters = mask.getModifierList();
                for (String format : formatters) {
                    switch (format) {
                        case "b":
                            bold.setSelected(true);
                            break;
                        case "i":
                            italic.setSelected(true);
                            break;
                        case "u":
                            underline.setSelected(true);
                            break;
                        default:
                            if (format.startsWith("#") && format.length() == 7) {

                            }
                    }
                }
            }
        }
    }

    private void updateTxtModifiers() {
        normal.setSelected(true);
        center.setSelected(false);
        left.setSelected(true);
        right.setSelected(false);
        bold.setSelected(false);
        ;
        italic.setSelected(false);
        underline.setSelected(false);
        small.setSelected(false);
        large.setSelected(false);
        xLarge.setSelected(false);

        for (int idx = 0; idx < linePanes.length; idx++) {
            JTextPane pane = linePanes[idx];
            if (pane == focusedTextPane) {
                String[] modifiers = isFront ? selectedCard.getFrontTextModifiers() : selectedCard.getBackTextModifiers();
                String mod = modifiers[idx];
                String[] parts = mod.split(",");
                for (String part : parts) {
                    String value = part.trim();
                    switch (value) {
                        case "center":
                            center.setSelected(true);
                            left.setSelected(false);
                            right.setSelected(false);
                            break;
                        case "left":
                            left.setSelected(true);
                            center.setSelected(false);
                            right.setSelected(false);
                            break;
                        case "right":
                            right.setSelected(true);
                            left.setSelected(false);
                            center.setSelected(false);
                            break;
                        case "b":
                            bold.setSelected(true);
                            break;
                        case "i":
                            italic.setSelected(true);
                            break;
                        case "u":
                            underline.setSelected(true);
                            break;
                        case "WHITE":
                            break;
                        case "RED":
                            break;
                        case "GREEN":
                            break;
                        case "BLUE":
                            break;
                        case "small":
                            small.setSelected(true);
                            normal.setSelected(false);
                            large.setSelected(false);
                            xLarge.setSelected(false);
                            break;
                        case "large":
                            large.setSelected(true);
                            small.setSelected(false);
                            normal.setSelected(false);
                            xLarge.setSelected(false);
                            break;
                        case "xlarge":
                            xLarge.setSelected(true);
                            large.setSelected(false);
                            small.setSelected(false);
                            normal.setSelected(false);
                            break;
                        default:
                            if (value.contains("#") && (value.length() == 4 || value.length() == 7)) {

                            }
                    }
                }
            }
        }
    }

    private void showCardPrompt(HTH_TextField grpField, HTH_TextField divField, final HTH_TextField cardField, final boolean isEditing) {
        compReqFocus = cardField;

        hideContents();
        updateCardList(grpField, divField);
        selCardList = cardList;
        setPromptFunctionKeys();
        setPromptControlKeys();
        setHeaderTitle("Select ID Card");

        JPanel headerPanel = setCardRowPanel(null, null, isEditing);
        headerPanel.setBounds(50, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setCardPrompt(cardField, isEditing));
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
                    selCardList = searchIDCard(searchField.getText().trim());
                    informationPane.setViewportView(setCardPrompt(cardField, isEditing));
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selCardList = searchIDCard(searchField.getText().trim());
                informationPane.setViewportView(setCardPrompt(cardField, isEditing));
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
    }

    private JPanel setCardRowPanel(final IDCard card, final HTH_TextField crdField, final boolean isEditing) {
        final JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(true);
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        rowPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        Font font = HTH_FONT.deriveFont(17.0f);
        JLabel numField = getLabel(4, font);
        numField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JLabel descField = getLabel(60, font);
        descField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        int rowW = numField.getPreferredSize().width + descField.getPreferredSize().width;
        int rowH = numField.getPreferredSize().height;
        rowPanel.setPreferredSize(new Dimension(rowW, rowH));
        rowPanel.setMinimumSize(new Dimension(rowW, rowH));
        rowPanel.setMaximumSize(new Dimension(rowW, rowH));


        if (card == null) {
            numField.setText("Card");
            numField.setForeground(Color.BLACK);

            descField.setText("Description");
            descField.setForeground(Color.BLACK);
        } else {
            rowPanel.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        crdField.setText(card.getCardNumber());
                        crdField.requestFocus();
                        removePromptScreen();

                        if (isEditing) {
                            startEditor();
                        } else {
                            crdField.requestFocus();
                        }
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
                    crdField.setText(card.getCardNumber());
                    crdField.requestFocus();
                    removePromptScreen();

                    if (isEditing) {
                        startEditor();
                    } else {
                        crdField.requestFocus();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });

            numField.setText(card.getCardNumber());
            numField.setForeground(new Color(0, 0, 150));

            descField.setText(card.getDescription());
            descField.setForeground(new Color(0, 0, 150));
        }

        rowPanel.add(numField);
        rowPanel.add(descField);

        return rowPanel;
    }

    private JPanel setCardPrompt(HTH_TextField crdField, boolean isEditing) {
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);

        if (selCardList.length == 0) {
            cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            cardPanel.add(getLabel("No records found."));
        } else {
            cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

            for (IDCard card : selCardList) {
                cardPanel.add(setCardRowPanel(card, crdField, isEditing));
            }
        }

        return cardPanel;
    }

    private void showLogoModifiers() {
        JPanel modPanel = new JPanel();
        modPanel.setOpaque(false);
        modPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
        modPanel.setBounds(2, 2, contentScreen.getSize().width - 4, contentScreen.getSize().height * 15 / 100);
        contentScreen.add(modPanel, JLayeredPane.PALETTE_LAYER);

        JPanel sizePanel = new JPanel();
        sizePanel.setOpaque(false);
        sizePanel.setLayout(new GridLayout(0, 1));
        sizePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Size", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, HTH_FONT.deriveFont(12.0f), Color.BLACK));
        sizePanel.setPreferredSize(new Dimension(contentScreen.getSize().width * 15 / 100, modPanel.getBounds().height));
        sizePanel.add(smallLogo);
        sizePanel.add(hLargeLogo);
        sizePanel.add(vLargeLogo);
        modPanel.add(sizePanel);

        String isLargeFlag = isFront ? selectedCard.getFrontLarge() : selectedCard.getBackLarge();

        if (isLargeFlag.isEmpty()) {
            smallLogo.setSelected(true);
        } else if (isLargeFlag.equals("H")) {
            hLargeLogo.setSelected(true);
        } else if (isLargeFlag.equals("V")) {
            vLargeLogo.setSelected(true);
        }
    }

    private void showLogoEditor() {
        while (!selectedCard.isReady()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new GridLayout(3, 3, 0, 0));
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        logoPanel.setBounds(2, 120, contentScreen.getSize().width - 4, contentScreen.getSize().height * 8 / 10);
        contentScreen.add(logoPanel, JLayeredPane.DEFAULT_LAYER);

        Image[] logoList = isFront ? selectedCard.getFrontLogo() : selectedCard.getBackLogo();
        for (int pos = 0; pos < logoList.length; pos++) {
            final int position = pos;
            final JButton imgBtn = new JButton();
            imgBtn.setContentAreaFilled(false);
            imgBtn.setFocusPainted(false);
            imgBtn.setRolloverEnabled(true);
            imgBtn.setFont(HTH_FONT);
            imgBtn.setBorder(null);
            imgBtn.setOpaque(false);
            imgBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    focusedIdx = position;
                    showLogoChooser();
                }
            });
            imgBtn.addMouseListener(new MouseListener() {
                private Border border = null;

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    border = imgBtn.getBorder();
                    imgBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    imgBtn.setBorder(border);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });
            imgBtn.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        showLogoChooser();
                    } else if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        String[] vars = isFront ? selectedCard.getFrontLogoVars() : selectedCard.getBackLogoVars();
                        vars[position] = "";
                        Image[] logos = isFront ? selectedCard.getFrontLogo() : selectedCard.getBackLogo();
                        logos[position] = null;

                        if (isFront) {
                            selectedCard.setFrontLogo(logos);
                            selectedCard.setFrontLogoVars(vars);
                        } else {
                            selectedCard.setBackLogo(logos);
                            selectedCard.setBackLogoVars(vars);
                        }
                        editLogos();
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyTyped(KeyEvent e) {
                }

            });
            imgBtn.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    focusedIdx = position;
                    imgBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    imgBtn.setBorder(null);
                }
            });

            if (logoList[pos] == null) {
                imgBtn.setText("Click to add logo");
                imgBtn.setHorizontalAlignment(JButton.CENTER);
                imgBtn.setVerticalAlignment(JButton.CENTER);
            } else {
                BufferedImage logo = toBufferedImage(logoList[pos]);
                int orgWidth = logoList[pos].getWidth(null);
                int orgHeight = logoList[pos].getHeight(null);
                int newWidth = orgWidth;
                int newHeight = orgHeight;

                if (orgWidth != WIDTH / 3) {
                    newWidth = WIDTH / 3;
                    newHeight = (newWidth * orgHeight) / orgWidth;
                }

                if (newHeight > HEIGHT / 3) {
                    newHeight = HEIGHT / 3;
                    newWidth = (newHeight * orgWidth) / orgHeight;
                }

                Image logoScaled = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                imgBtn.setIcon(new ImageIcon(logoScaled));

                if (pos % 3 == 0) {
                    imgBtn.setHorizontalAlignment(JButton.LEFT);
                } else if (pos % 3 == 1) {
                    imgBtn.setHorizontalAlignment(JButton.CENTER);
                } else if (pos % 3 == 2) {
                    imgBtn.setHorizontalAlignment(JButton.RIGHT);
                }

                imgBtn.setLayout(new FlowLayout(FlowLayout.TRAILING));
                HTH_DeleteButton delBtn = new HTH_DeleteButton();
                delBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String[] vars = isFront ? selectedCard.getFrontLogoVars() : selectedCard.getBackLogoVars();
                        vars[position] = "";
                        Image[] logos = isFront ? selectedCard.getFrontLogo() : selectedCard.getBackLogo();
                        logos[position] = null;

                        if (isFront) {
                            selectedCard.setFrontLogo(logos);
                            selectedCard.setFrontLogoVars(vars);
                        } else {
                            selectedCard.setBackLogo(logos);
                            selectedCard.setBackLogoVars(vars);
                        }
                        editLogos();
                    }
                });
                imgBtn.add(delBtn);
            }

            logoPanel.add(imgBtn);
            logoBtns[pos] = imgBtn;
        }
    }

    private void showLogoChooser() {
        controlPanel.removeAll();

        String exitKey = "Exit";
        Action exitAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10123L;

            @Override
            public void actionPerformed(ActionEvent e) {
                removePromptScreen();
                editLogos();
            }
        };
        HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
        exitBtn.setToolTipText("F3=Exit");
        exitBtn.addActionListener(exitAction);
        exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
        exitBtn.getActionMap().put(exitKey, exitAction);
        controlPanel.add(exitBtn);

        String uploadKey = "Upload";
        Action uploadAction = new AbstractAction(uploadKey) {
            private static final long serialVersionUID = 10124L;

            @Override
            public void actionPerformed(ActionEvent e) {
                uploadLogo();
            }
        };
        HTH_FunctionButton uploadBtn = new HTH_FunctionButton("Upload");
        uploadBtn.setToolTipText("F4=Upload");
        uploadBtn.addActionListener(uploadAction);
        uploadBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), uploadKey);
        uploadBtn.getActionMap().put(uploadKey, uploadAction);


        String exitFuncKey = "ExitFunction";
        Action exitFuncAction;
        exitFuncAction = new AbstractAction(exitKey) {
            private static final long serialVersionUID = 10125L;

            @Override
            public void actionPerformed(ActionEvent e) {
                removePromptScreen();
            }
        };
        HTH_FunctionButton exitFuncBtn = new HTH_FunctionButton("Exit");
        exitFuncBtn.setToolTipText("F3=Exit");
        exitFuncBtn.addActionListener(exitFuncAction);
        exitFuncBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitFuncKey);
        exitFuncBtn.getActionMap().put(exitFuncKey, exitFuncAction);
        setFunctionKeys(new HTH_FunctionButton[]{uploadBtn, exitFuncBtn});

        setHeaderTitle("Select ID Logo");
        showLogoList();
    }

    private void showLogoList() {
        selLogoList = IDCIMG.getLogoList();

        JPanel headerPanel = setLogoRowPanel(null);
        headerPanel.setBounds(20, 40, headerPanel.getPreferredSize().width, headerPanel.getPreferredSize().height);
        promptScreen.add(headerPanel, JLayeredPane.MODAL_LAYER);

        final JScrollPane informationPane = new JScrollPane(setLogoListPrompt());
        informationPane.setBounds(20, 40 + headerPanel.getPreferredSize().height, headerPanel.getPreferredSize().width + 15, contentScreen.getSize().height * 9 / 10);
        informationPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, informationPane.getBounds().height));
        informationPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        promptScreen.add(informationPane, JLayeredPane.MODAL_LAYER);

        JLabel searchLabel = getLabel("Search");
        searchLabel.setBounds(25, 10, searchLabel.getPreferredSize().width, searchLabel.getPreferredSize().height);
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
                    selLogoList = searchLogo(searchField.getText().trim());
                    informationPane.setViewportView(setLogoListPrompt());
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selLogoList = searchLogo(searchField.getText().trim());
                informationPane.setViewportView(setLogoListPrompt());
            }
        });
        promptScreen.add(searchField, JLayeredPane.MODAL_LAYER);

        contentScreen.add(promptScreen, JLayeredPane.MODAL_LAYER);

        revalidate();
        repaint();

        searchField.requestFocus();
    }

    private void refreshLogoPrompt() {
        IDCIMG.refreshList();
        promptScreen.removeAll();
        contentScreen.remove(promptScreen);
        showLogoList();
    }

    private JPanel setLogoListPrompt() {
        JPanel listPanel = new JPanel();
        listPanel.setBackground(Color.WHITE);

        if (selLogoList.length == 0) {
            listPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            listPanel.add(getLabel("No records found."));
        } else {
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

            for (IDLogo logo : selLogoList) {
                listPanel.add(setLogoRowPanel(logo));
            }
        }

        return listPanel;
    }

    private JPanel setLogoRowPanel(final IDLogo logo) {
        final JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(true);
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        rowPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);

        Font font = HTH_FONT.deriveFont(17.0f);
        JLabel nameField = getLabel(15, font);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        JLabel descField = getLabel(52, font);
        descField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        int rowW = nameField.getPreferredSize().width + descField.getPreferredSize().width;
        int rowH = nameField.getPreferredSize().height;
        rowPanel.setPreferredSize(new Dimension(rowW, rowH));
        rowPanel.setMinimumSize(new Dimension(rowW, rowH));
        rowPanel.setMaximumSize(new Dimension(rowW, rowH));

        if (logo == null) {
            nameField.setText("Name");
            nameField.setForeground(Color.BLACK);

            descField.setText("Description");
            descField.setForeground(Color.BLACK);
        } else {
            rowPanel.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String[] logoVars = isFront ? selectedCard.getFrontLogoVars() : selectedCard.getBackLogoVars();
                        logoVars[focusedIdx] = logo.getName();

                        Image[] logos = isFront ? selectedCard.getFrontLogo() : selectedCard.getBackLogo();
                        logos[focusedIdx] = iSeries.downloadImages(new String[]{"/MobileApp/" + HTH_IDC.member + "/Logos/" + logo.getName().replace("&%", "") + ".PNG"})[0];

                        if (isFront) {
                            selectedCard.setFrontLogoVars(logoVars);
                            selectedCard.setFrontLogo(logos);
                        } else {
                            selectedCard.setBackLogoVars(logoVars);
                            selectedCard.setBackLogo(logos);
                        }
                        removePromptScreen();
                        editLogos();
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
                    String[] logoVars = isFront ? selectedCard.getFrontLogoVars() : selectedCard.getBackLogoVars();
                    logoVars[focusedIdx] = logo.getName();

                    Image[] logos = isFront ? selectedCard.getFrontLogo() : selectedCard.getBackLogo();
                    logos[focusedIdx] = iSeries.downloadImages(new String[]{"/MobileApp/" + HTH_IDC.member + "/Logos/" + logo.getName().replace("&%", "") + ".PNG"})[0];

                    if (isFront) {
                        selectedCard.setFrontLogoVars(logoVars);
                        selectedCard.setFrontLogo(logos);
                    } else {
                        selectedCard.setBackLogoVars(logoVars);
                        selectedCard.setBackLogo(logos);
                    }
                    removePromptScreen();
                    editLogos();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }
            });

            nameField.setText(logo.getName());
            nameField.setForeground(new Color(0, 0, 150));

            descField.setText(logo.getDescription());
            descField.setForeground(new Color(0, 0, 150));
        }

        rowPanel.add(nameField);
        rowPanel.add(descField);

        return rowPanel;
    }

    private void uploadLogo() {
        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Image", "png"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File input = fileChooser.getSelectedFile();

            if (input.length() <= (50 * 1024)) {
                JLabel descLabel = getLabel("Description:", HTH_FONT.deriveFont(14.0f));
                HTH_TextField descField = new HTH_TextField(60, HTH_FONT.deriveFont(14.0f));

                JPanel descPanel = new JPanel();
                descPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
                descPanel.setOpaque(false);
                descPanel.add(descLabel);
                descPanel.add(descField);

                JLabel nameLabel = getLabel("Name:", HTH_FONT.deriveFont(16.0f));
                nameLabel.setPreferredSize(descLabel.getPreferredSize());
                nameLabel.setMaximumSize(descLabel.getPreferredSize());
                nameLabel.setMinimumSize(descLabel.getPreferredSize());
                HTH_TextField nameField = new HTH_TextField(13, HTH_FONT.deriveFont(14.0f));

                JPanel namePanel = new JPanel();
                namePanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
                namePanel.setOpaque(false);
                namePanel.add(nameLabel);
                namePanel.add(nameField);

                JPanel dialogPanel = new JPanel();
                dialogPanel.setOpaque(false);
                dialogPanel.setLayout(new GridLayout(2, 1, 0, 5));
                dialogPanel.add(namePanel);
                dialogPanel.add(descPanel);

                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 80 / 100, contentScreen.getSize().height * 3 / 10);
                int result = HTH_Dialog.showInputDialog(this, "Enter Name And Description", dialogPanel, dialogSize);
                if (result == JOptionPane.YES_OPTION) {
                    String name = nameField.getText().replaceAll("&%", "");
                    String desc = descField.getText();
                    IDCIMG.uploadFile(input, name, desc);
                    refreshLogoPrompt();
                }
            } else {
                String errMsg = "The image size is too large.\n"
                        + "Limited Size: 50KB";
                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
                HTH_Dialog.showMessageDialog(this, "Error on uploading logos", errMsg, dialogSize, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showID(final boolean isFront) {
        contentScreen.removeAll();

        String editKey = "Edit";
        Action editAction = new AbstractAction(editKey) {
            private static final long serialVersionUID = 10126L;

            @Override
            public void actionPerformed(ActionEvent e) {
                editCard(false);
            }
        };
        HTH_FunctionButton editBtn = new HTH_FunctionButton("Edit");
        editBtn.setToolTipText("F6=Edit");
        editBtn.addActionListener(editAction);
        editBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), editKey);
        editBtn.getActionMap().put(editKey, editAction);

        String turnKey = "Turn";
        Action turnAction = new AbstractAction(turnKey) {
            private static final long serialVersionUID = 10127L;

            @Override
            public void actionPerformed(ActionEvent e) {
                showID(!isFront);
            }
        };
        HTH_FunctionButton turnBtn = isFront ? new HTH_FunctionButton("Card Back") : new HTH_FunctionButton(" Card Front");
        turnBtn.setToolTipText("F9=" + turnBtn.getText());
        turnBtn.addActionListener(turnAction);
        turnBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), turnKey);
        turnBtn.getActionMap().put(turnKey, turnAction);

        String pdfKey = "PDF";
        Action pdfAction = new AbstractAction(pdfKey) {
            private static final long serialVersionUID = 10128L;

            @Override
            public void actionPerformed(ActionEvent e) {
                savePDF();
            }
        };
        HTH_FunctionButton pdfBtn = new HTH_FunctionButton("Save As PDF");
        pdfBtn.setToolTipText("F7=Save As PDF");
        pdfBtn.addActionListener(pdfAction);
        pdfBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), pdfKey);
        pdfBtn.getActionMap().put(pdfKey, pdfAction);

        String printKey = "Print";
        Action printAction = new AbstractAction(printKey) {
            private static final long serialVersionUID = 10129L;

            @Override
            public void actionPerformed(ActionEvent e) {
                printCard();
            }
        };
        HTH_FunctionButton printBtn = new HTH_FunctionButton("Print Sample");
        printBtn.setToolTipText("F5=Print Sample");
        printBtn.addActionListener(printAction);
        printBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), printKey);
        printBtn.getActionMap().put(printKey, printAction);

        functionKeys = new HTH_FunctionButton[]{editBtn, turnBtn, pdfBtn, printBtn};
        setFunctionKeys(functionKeys);

        cardFramePanel = new HTH_CardScreen(selectedCard, isFront);
        cardFramePanel.setBounds(0, 0, contentScreen.getSize().width, contentScreen.getSize().height);
        JLayeredPane cardContentPanel = cardFramePanel.getCardContentPanel();
        cardContentPanel.setBounds(getCardBound());

        cardFramePanel.add(setCardBorder(), JLayeredPane.DEFAULT_LAYER);
        cardFramePanel.add(cardContentPanel, JLayeredPane.PALETTE_LAYER);

        contentScreen.add(cardFramePanel, JLayeredPane.DEFAULT_LAYER);
        revalidate();
        repaint();
    }

    private void addModifier(String... modsToAdd) {
        for (int idx = 0; idx < linePanes.length; idx++) {
            if (linePanes[idx] == focusedTextPane) {
                String[] modifiers = isFront ? selectedCard.getFrontTextModifiers() : selectedCard.getBackTextModifiers();
                String modifier = modifiers[idx];
                String[] mods = modifier.split(",");
                for (String modToAdd : modsToAdd) {
                    for (String mod : mods) {
                        if (mod.equals(modToAdd)) {
                            modToAdd = null;
                            break;
                        }
                    }
                    if (modToAdd != null) {
                        modifier += "," + modToAdd;
                    }
                }
                if (modifier.startsWith(",")) {
                    modifiers[idx] = modifier.substring(1);
                } else {
                    modifiers[idx] = modifier;
                }

                if (isFront) {
                    selectedCard.setFrontTextModifiers(modifiers);
                } else {
                    selectedCard.setBackTextModifiers(modifiers);
                }
                return;
            }
        }
    }

    private void rmvModifier(String... modsToRmv) {
        for (int idx = 0; idx < linePanes.length; idx++) {
            if (linePanes[idx] == focusedTextPane) {
                String[] modifiers = isFront ? selectedCard.getFrontTextModifiers() : selectedCard.getBackTextModifiers();
                String[] mods = modifiers[idx].split(",");
                String newMod = "";
                for (String mod : mods) {
                    if (!mod.isEmpty()) {
                        for (String modToRmv : modsToRmv) {
                            if (mod.equals(modToRmv)) {
                                mod = null;
                                break;
                            }
                        }
                        if (mod != null) {
                            newMod += mod + ",";
                        }
                    }
                }

                if (newMod.endsWith(",")) {
                    modifiers[idx] = newMod.substring(0, newMod.length() - 1);
                } else {
                    modifiers[idx] = newMod;
                }

                if (isFront) {
                    selectedCard.setFrontTextModifiers(modifiers);
                } else {
                    selectedCard.setBackTextModifiers(modifiers);
                }
                return;
            }
        }
    }

    private void addMask(String mod) {
        List<IDMask> newMasks = new ArrayList<IDMask>();
        List<IDMask> appliedMasks = new ArrayList<IDMask>();

        IDMask[] oldMasks = isFront ? selectedCard.getFrontMask() : selectedCard.getBackMask();

        int selectedStr = focusedTextPane.getSelectionStart();
        int selectedEnd = focusedTextPane.getSelectionEnd() - 1;

        for (IDMask mask : oldMasks) {
            if (mask.getLine() == selectedLine && mask.getCol() == selectedCol) {
                if (mask.getStartIdx() > selectedEnd || mask.getEndIdx() < selectedStr
                        || (mask.getStartIdx() > selectedStr && mask.getEndIdx() < selectedEnd)
                        || (mask.getStartIdx() < selectedStr && mask.getEndIdx() > selectedEnd)) {
                    newMasks.add(mask);
                } else {
                    appliedMasks.add(mask);
                }
            } else {
                newMasks.add(mask);
            }
        }

        boolean isExtraNeeded = true;
        for (IDMask mask : appliedMasks) {
            if (mask.getStartIdx() == selectedStr && mask.getEndIdx() == selectedEnd) {
                mask.setModifiers(mask.getModifiers() + "," + mod);
                newMasks.add(mask);
                isExtraNeeded = false;
            } else if (mask.getStartIdx() < selectedStr && mask.getEndIdx() < selectedEnd) {
                IDMask leftMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), mask.getStartIdx(), selectedStr - 1, mask.getModifiers());
                IDMask rightMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), mask.getEndIdx() + 1, selectedEnd, mod);
                mask.setStartIdx(selectedStr);
                mask.setModifiers(mask.getModifiers() + "," + mod);
                newMasks.add(leftMask);
                newMasks.add(mask);
                newMasks.add(rightMask);
            } else if (mask.getStartIdx() < selectedStr && mask.getEndIdx() == selectedEnd) {
                IDMask leftMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), mask.getStartIdx(), selectedStr - 1, mask.getModifiers());
                mask.setStartIdx(selectedStr);
                mask.setModifiers(mask.getModifiers() + "," + mod);
                newMasks.add(leftMask);
                newMasks.add(mask);
            } else if (mask.getStartIdx() > selectedStr && mask.getEndIdx() > selectedEnd) {
                IDMask leftMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), selectedStr, mask.getStartIdx() - 1, mod);
                IDMask rightMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), selectedEnd + 1, mask.getEndIdx(), mask.getModifiers());
                mask.setEndIdx(selectedEnd);
                mask.setModifiers(mask.getModifiers() + "," + mod);
                newMasks.add(leftMask);
                newMasks.add(mask);
                newMasks.add(rightMask);
            } else if (mask.getStartIdx() == selectedStr && mask.getEndIdx() > selectedEnd) {
                IDMask rightMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), selectedEnd + 1, mask.getEndIdx(), mask.getModifiers());
                mask.setEndIdx(selectedEnd);
                mask.setModifiers(mask.getModifiers() + "," + mod);
                newMasks.add(mask);
                newMasks.add(rightMask);
            }
        }

        if (isExtraNeeded) {
            String face = isFront ? "F" : "B";
            IDMask extra = new IDMask(face, selectedLine, selectedCol, selectedStr, selectedEnd, mod);
            newMasks.add(extra);
        }

        if (isFront) {
            selectedCard.setFrontMask(newMasks.toArray(new IDMask[0]));
        } else {
            selectedCard.setBackMask(newMasks.toArray(new IDMask[0]));
        }
    }

    private void rmvMask(String mod) {
        List<IDMask> newMasks = new ArrayList<IDMask>();
        List<IDMask> appliedMasks = new ArrayList<IDMask>();

        IDMask[] oldMasks = isFront ? selectedCard.getFrontMask() : selectedCard.getBackMask();

        int selectedStr = focusedTextPane.getSelectionStart();
        int selectedEnd = focusedTextPane.getSelectionEnd() - 1;
        for (IDMask mask : oldMasks) {
            if (mask.getLine() == selectedLine && mask.getCol() == selectedCol) {
                if (mask.getStartIdx() > selectedEnd || mask.getEndIdx() < selectedStr) {
                    newMasks.add(mask);
                } else {
                    appliedMasks.add(mask);
                }
            } else {
                newMasks.add(mask);
            }
        }

        for (IDMask mask : appliedMasks) {
            if (mask.getStartIdx() >= selectedStr && mask.getEndIdx() <= selectedEnd) {
                mask.setModifiers(mask.getModifiers().replaceAll(mod + ",?", "").replaceAll(",$", ""));
                if (!mask.getModifiers().isEmpty()) {
                    newMasks.add(mask);
                }
            } else if (mask.getStartIdx() < selectedStr && mask.getEndIdx() > selectedEnd) {
                IDMask leftMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), mask.getStartIdx(), selectedStr - 1, mask.getModifiers());
                IDMask rightMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), selectedEnd + 1, mask.getEndIdx(), mask.getModifiers());
                mask.setStartIdx(selectedStr);
                mask.setEndIdx(selectedEnd);
                mask.setModifiers(mask.getModifiers().replaceAll(mod + ",?", "").replaceAll(",$", ""));
                newMasks.add(leftMask);
                newMasks.add(rightMask);
                if (!mask.getModifiers().isEmpty()) {
                    newMasks.add(mask);
                }
            } else if (mask.getStartIdx() >= selectedStr && mask.getEndIdx() > selectedEnd) {
                IDMask rightMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), selectedEnd + 1, mask.getEndIdx(), mask.getModifiers());
                mask.setEndIdx(selectedEnd);
                mask.setModifiers(mask.getModifiers().replaceAll(mod + ",?", "").replaceAll(",$", ""));
                newMasks.add(rightMask);
                if (!mask.getModifiers().isEmpty()) {
                    newMasks.add(mask);
                }
            } else if (mask.getStartIdx() < selectedStr && mask.getEndIdx() <= selectedEnd) {
                IDMask leftMask = new IDMask(mask.getFace(), mask.getLine(), mask.getCol(), mask.getStartIdx(), selectedStr - 1, mask.getModifiers());
                mask.setStartIdx(selectedStr);
                mask.setModifiers(mask.getModifiers().replaceAll(mod + ",?", "").replaceAll(",$", ""));
                newMasks.add(leftMask);
                if (!mask.getModifiers().isEmpty()) {
                    newMasks.add(mask);
                }
            }
        }

        if (isFront) {
            selectedCard.setFrontMask(newMasks.toArray(new IDMask[0]));
        } else {
            selectedCard.setBackMask(newMasks.toArray(new IDMask[0]));
        }
    }

    private void saveCard() {
        IDCARD.savCard(selectedCard);
    }

    private void copyCard() {
        String fromGrp = fromGrpField.getText().trim();
        String fromDiv = fromDivField.getText().trim();
        String fromCrd = fromCrdField.getText().trim();

        String toGrp = toGrpField.getText().trim();
        String toDiv = toDivField.getText().trim();
        String toCrd = toCrdField.getText().trim();

        if (toCrd.isEmpty()) {
            toCrdField.setText("00");
            toCrd = "00";
        }

        if (fromGrp.isEmpty() || fromCrd.isEmpty() || toGrp.isEmpty()) {
            String msg = "Block/Group ID and From card number cannot be empty.";
            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
            HTH_Dialog.showMessageDialog(this, "Mandatory Fields Are Empty", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
        } else {
            boolean isGrpValid, isDivValid;
            String msg;
            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 40 / 100, contentScreen.getSize().height * 2 / 10);

            isGrpValid = false;
            for (GroupMaster grp : groupList) {
                if (grp.getCarrier().equals(fromGrp) || grp.getID().equals(fromGrp)) {
                    isGrpValid = true;
                    break;
                }
            }

            isDivValid = fromDiv.isEmpty();
            updateDivList(fromGrpField);
            if (divisionList != null) {
                for (Division div : divisionList) {
                    if (div.getID().equals(fromDiv)) {
                        isDivValid = true;
                        break;
                    }
                }
            }

            if (isGrpValid && isDivValid) {
                // From card is valid....
                isGrpValid = false;
                for (GroupMaster grp : groupList) {
                    if (grp.getCarrier().equals(toGrp) || grp.getID().equals(toGrp)) {
                        isGrpValid = true;
                        break;
                    }
                }

                isDivValid = toDiv.isEmpty();
                updateDivList(toGrpField);
                if (divisionList != null) {
                    for (Division div : divisionList) {
                        if (div.getID().equals(toDiv)) {
                            isDivValid = true;
                            break;
                        }
                    }
                }

                if (isGrpValid && isDivValid) {
                    // To Card is valid....
                    boolean isFromCardExisted = IDCARD.isCardExisted(fromGrp, fromDiv, fromCrd);
                    boolean isToCardExisted = IDCARD.isCardExisted(toGrp, toDiv, toCrd);

                    if (isFromCardExisted && !isToCardExisted) {
                        IDCARD.cpyCard(fromGrp, fromDiv, fromCrd, toGrp, toDiv, toCrd);
                        setSelectionScreen();
                        return;
                    } else if (!isFromCardExisted) {
                        msg = "The selected From card not found";
                    } else {
                        msg = "To card exists. Are you sure to replace?";
                        int option = HTH_Dialog.showConfirmDialog(this, "Replace Card", msg, dialogSize);
                        if (option == JOptionPane.YES_OPTION) {
                            IDCARD.delCard(toGrp, toDiv, toCrd);
                            IDCARD.cpyCard(fromGrp, fromDiv, fromCrd, toGrp, toDiv, toCrd);
                            setSelectionScreen();
                        }
                        return;
                    }
                } else if (!isGrpValid) {
                    msg = "To Block/Group ID not found";
                } else {
                    msg = "To Division not found";
                }
            } else if (!isGrpValid) {
                msg = "From Block/Group ID not found";
            } else {
                msg = "From Division not found";
            }
            HTH_Dialog.showMessageDialog(this, "Record Not Found", msg, dialogSize, JOptionPane.WARNING_MESSAGE);
        }
    }

    private void savePDF() {
        File front, back, output;

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Document", "pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            output = fileChooser.getSelectedFile();

            if (!output.getName().endsWith(".pdf") && !output.getName().endsWith(".PDF")) {
                output = new File(output.getParent(), output.getName() + ".pdf");
            }

            PDDocument doc = new PDDocument();
            PDPage page;
            try {
                page = new PDPage();

                showID(true);
                front = generateIDImg("Front");

                showID(false);
                back = generateIDImg("Back");

                float midHeight, midWidth;
                midHeight = page.getArtBox().getHeight() / 2; // Middle point of height of the page.
                midWidth = page.getArtBox().getWidth() / 2; // Middle point of width of the page.

                PDPageContentStream contents = new PDPageContentStream(doc, page); // Stream to write contents on page.
                PDImageXObject ftImg = PDImageXObject.createFromFile(front.getAbsolutePath(), doc);
                contents.drawImage(ftImg, midWidth - ID_X_OFFSET, midHeight + ID_FRONT_Y_OFFSET, ID_WIDTH, ID_HEIGHT);

                PDImageXObject bkImg = PDImageXObject.createFromFile(back.getAbsolutePath(), doc);
                contents.drawImage(bkImg, midWidth - ID_X_OFFSET, midHeight - ID_BACK_Y_OFFSET, ID_WIDTH, ID_HEIGHT);

                contents.close(); // Release the stream.
                doc.addPage(page);

                front.delete();
                back.delete();

                doc.save(output);
                doc.close();
                Desktop.getDesktop().open(output);
            } catch (FileNotFoundException fnfe) {
                showID(true);

                String errMsg = "A file name cannot contain any of the following characters:\n"
                        + ":\"<>|";
                Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
                HTH_Dialog.showMessageDialog(this, "Error on saving ID cards", errMsg, dialogSize, JOptionPane.ERROR_MESSAGE);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printCard() {
        PrinterJob job = PrinterJob.getPrinterJob();

        PageFormat pf = job.defaultPage();
        Paper p = pf.getPaper();

        p.setImageableArea(3, 3, p.getWidth() - 6, p.getHeight() - 6);
        pf.setPaper(p);

        PageFormat page = job.validatePage(pf);
        job.setPrintable(this, page);

        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException pe) {
                pe.printStackTrace();
            }
        }
    }

    private void exitProgram() {
        System.exit(0);
    }

    private File generateIDImg(String name) throws Exception {
        File file = File.createTempFile(name, ".png");

        JLayeredPane cardContentPanel = cardFramePanel.getCardContentPanel();
        BufferedImage cardImg = new Robot().createScreenCapture(cardContentPanel.getBounds());
        Graphics2D cardGraphics = cardImg.createGraphics();
        cardContentPanel.paint(cardGraphics);

        BufferedImage img = new BufferedImage(WIDTH + 10, HEIGHT + 10, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D imgGraphics = img.createGraphics();
        imgGraphics.setColor(Color.WHITE);
        imgGraphics.fillRect(0, 0, WIDTH + 10, HEIGHT + 10); // Fill the color of the card.

        imgGraphics.setColor(Color.BLACK);
        imgGraphics.setStroke(new BasicStroke(1));
        imgGraphics.drawRoundRect(5, 5, WIDTH + 4, HEIGHT + 4, 10, 10);
        imgGraphics.drawImage(cardImg, 7, 7, WIDTH, HEIGHT, null);
        ImageIO.write(img, "png", file);

        return file;
    }

    private Rectangle getCardBound() {
        int contentWidth = contentScreen.getSize().width;
        int x = (contentWidth / 2) - (WIDTH / 2);
        int y = 120;

        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    private JPanel setCardBorder() {
        Rectangle cardBound = getCardBound();
        int padding = 3;

        JPanel borderPanel = new JPanel();
        borderPanel.setOpaque(false);
        borderPanel.setBounds(cardBound.x - padding, cardBound.y - padding, cardBound.width + (2 * padding), cardBound.height + (2 * padding));
        borderPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

        return borderPanel;
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

    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphic = bImage.createGraphics();
        graphic.drawImage(img, 0, 0, null);
        graphic.dispose();

        return bImage;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        // We have 2 pages for each id, and 'page' is zero-based.
        if (page >= 2) {
            showID(false);

            String msg = "Printer is printing the sample card.";
            Dimension dialogSize = new Dimension(contentScreen.getSize().width * 50 / 100, contentScreen.getSize().height * 2 / 10);
            HTH_Dialog.showMessageDialog(this, "Printing ID card", msg, dialogSize, JOptionPane.INFORMATION_MESSAGE);
            return NO_SUCH_PAGE;
        }

        boolean isFront;
        if (page % 2 == 0) {
            isFront = true;
        } else {
            isFront = false;
        }

        showID(isFront);
        isFront = !isFront;
        cardFramePanel.applyCover();

        JLayeredPane cardContentPanel = cardFramePanel.getCardContentPanel();
        Dimension dim;
        double cHeight, cWidth, pHeight, pWidth, pXStart, pYStart, xRatio, yRatio;
        dim = cardContentPanel.getSize();
        cHeight = dim.getHeight();
        cWidth = dim.getWidth();
        pHeight = pf.getImageableHeight();
        pWidth = pf.getImageableWidth();
        pXStart = pf.getImageableX();
        pYStart = pf.getImageableY();
        xRatio = pWidth / cWidth;
        yRatio = pHeight / cHeight;
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pXStart, pYStart);
        g2d.scale(xRatio, yRatio);
        cardContentPanel.printAll(g2d);
        g2d.dispose();
        cardContentPanel.revalidate();

        // Tell the caller that this page is part of the printed document
        return PAGE_EXISTS;
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (isCardLoaded) {
            if (!isAdvanced) {
                updateTxt();
            }
            confirmExit();
        } else {
            exitProgram();
        }
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    private class HTH_DocumentListener implements DocumentListener {
        private String value;
        private JTextPane field;
        private int limit;

        public HTH_DocumentListener(JTextPane field, int limit) {
            this.field = field;
            this.limit = limit;
            this.value = "";
        }

        @Override
        public void changedUpdate(DocumentEvent arg0) {
        }

        @Override
        public void insertUpdate(DocumentEvent arg0) {
            validateTxt();
        }

        @Override
        public void removeUpdate(DocumentEvent arg0) {
        }

        private void validateTxt() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (field.getText().length() > limit) {
                        int cursorPos = field.getCaretPosition() - 1;
                        field.setText(value);
                        field.setCaretPosition(cursorPos);
                    } else {
                        value = field.getText();
                    }
                }
            });
        }
    }

    public class GroupPromptActionListener implements ActionListener {
        private HTH_TextField grpField, divField, crdField;

        private GroupPromptActionListener(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField) {
            this.grpField = grpField;
            this.divField = divField;
            this.crdField = crdField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showGroupPrompt(grpField, divField, crdField);
        }
    }

    private class GroupPromptKeyListener implements KeyListener {
        private HTH_TextField grpField, divField, crdField;

        public GroupPromptKeyListener(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField) {
            this.grpField = grpField;
            this.divField = divField;
            this.crdField = crdField;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                showGroupPrompt(grpField, divField, crdField);
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                divField.requestFocus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private class DivisionPromptActionListener implements ActionListener {
        private HTH_TextField grpField, divField, crdField;

        public DivisionPromptActionListener(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField) {
            this.grpField = grpField;
            this.divField = divField;
            this.crdField = crdField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showDivisionPrompt(grpField, divField, crdField);
        }
    }

    private class DivisionPromptKeyListener implements KeyListener {
        private HTH_TextField grpField, divField, crdField;

        public DivisionPromptKeyListener(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField) {
            this.grpField = grpField;
            this.divField = divField;
            this.crdField = crdField;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                showDivisionPrompt(grpField, divField, crdField);
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                crdField.requestFocus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private class CardPromptActionListener implements ActionListener {
        private HTH_TextField grpField, divField, crdField;
        private boolean isEditing;

        public CardPromptActionListener(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField, boolean isEditing) {
            this.grpField = grpField;
            this.divField = divField;
            this.crdField = crdField;
            this.isEditing = isEditing;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showCardPrompt(grpField, divField, crdField, isEditing);
        }
    }

    private class CardPromptKeyListener implements KeyListener {
        private HTH_TextField grpField, divField, crdField, nextField;
        private boolean isEditing;

        public CardPromptKeyListener(HTH_TextField grpField, HTH_TextField divField, HTH_TextField crdField, HTH_TextField nextField, boolean isEditing) {
            this.grpField = grpField;
            this.divField = divField;
            this.crdField = crdField;
            this.nextField = nextField;
            this.isEditing = isEditing;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                showCardPrompt(grpField, divField, crdField, isEditing);
            } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                if (nextField == null) {
                    grpField.requestFocus();
                } else {
                    nextField.requestFocus();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
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

    private class BoldActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Font f = focusedTextPane.getFont();

            if (bold.isSelected()) {
                focusedTextPane.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
                addModifier("b");
            } else {
                focusedTextPane.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
                rmvModifier("b");
            }
            focusedTextPane.requestFocus();
        }
    }

    private class BoldAdvancedActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int charLen = focusedTextPane.getSelectionEnd() - focusedTextPane.getSelectionStart();

            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            if (bold.isSelected()) {
                StyleConstants.setBold(attrSet, true);
                addMask("b");
            } else {
                StyleConstants.setBold(attrSet, false);
                rmvMask("b");
            }
            doc.setCharacterAttributes(focusedTextPane.getSelectionStart(), charLen, attrSet, false);
            focusedTextPane.requestFocus();
        }
    }

    private class ItalicActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Font f = focusedTextPane.getFont();

            if (italic.isSelected()) {
                focusedTextPane.setFont(f.deriveFont(f.getStyle() | Font.ITALIC));
                addModifier("i");
            } else {
                focusedTextPane.setFont(f.deriveFont(f.getStyle() & ~Font.ITALIC));
                rmvModifier("i");
            }

            focusedTextPane.requestFocus();
        }
    }

    private class ItalicAdvancedActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int charLen = focusedTextPane.getSelectionEnd() - focusedTextPane.getSelectionStart();

            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            if (italic.isSelected()) {
                StyleConstants.setItalic(attrSet, true);
                addMask("i");
            } else {
                StyleConstants.setItalic(attrSet, false);
                rmvMask("i");
            }
            doc.setCharacterAttributes(focusedTextPane.getSelectionStart(), charLen, attrSet, false);

            focusedTextPane.requestFocus();
        }
    }

    private class UnderlineActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            if (underline.isSelected()) {
                StyleConstants.setUnderline(attrSet, true);
                addModifier("u");
            } else {
                StyleConstants.setUnderline(attrSet, false);
                rmvModifier("u");
            }
            doc.setParagraphAttributes(0, focusedTextPane.getText().length(), attrSet, false);

            focusedTextPane.requestFocus();
        }
    }

    private class UnderlineAdvancedActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int charLen = focusedTextPane.getSelectionEnd() - focusedTextPane.getSelectionStart();

            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet attrSet = new SimpleAttributeSet();
            if (underline.isSelected()) {
                StyleConstants.setUnderline(attrSet, true);
                addMask("u");
            } else {
                StyleConstants.setUnderline(attrSet, false);
                rmvMask("u");
            }
            doc.setCharacterAttributes(focusedTextPane.getSelectionStart(), charLen, attrSet, false);

            focusedTextPane.requestFocus();
        }
    }

    private class LeftActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet alignment = new SimpleAttributeSet();
            StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_LEFT);
            doc.setParagraphAttributes(0, focusedTextPane.getText().length(), alignment, false);
            rmvModifier("center", "right");
            addModifier("left");

            focusedTextPane.requestFocus();
        }
    }

    private class CenterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet alignment = new SimpleAttributeSet();
            StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_CENTER);
            doc.setParagraphAttributes(0, focusedTextPane.getText().length(), alignment, false);
            rmvModifier("right", "left");
            addModifier("center");

            focusedTextPane.requestFocus();
        }
    }

    private class RightActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StyledDocument doc = focusedTextPane.getStyledDocument();
            SimpleAttributeSet alignment = new SimpleAttributeSet();
            StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_RIGHT);
            doc.setParagraphAttributes(0, focusedTextPane.getText().length(), alignment, false);
            rmvModifier("left", "center");
            addModifier("right");

            focusedTextPane.requestFocus();
        }
    }
}
