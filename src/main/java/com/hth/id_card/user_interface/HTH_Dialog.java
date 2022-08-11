package com.hth.id_card.user_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

public class HTH_Dialog extends JDialog {
	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 9L;
	private static final Color BG_BORDER = new Color(82, 144, 202);
	private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 14);

	private static HTH_Dialog dialog = null;
	
	private static String msg;
	private Dimension size;
//	private int type;
	private int option;
	private JRadioButton yes;
	private JRadioButton no;

	public static void showMessageDialog(HTH_Frame frame, String title, String msg, Dimension size, int type) {
		dialog = new HTH_Dialog(frame, title, msg, size, type);
		
		dialog.setContentPane(getMessageDialog());
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	//----added
	public static void showMessageDialogWithNoCards(HTH_Frame frame, String title, String msg, Dimension size, int type) {
		dialog = new HTH_Dialog(frame, title, msg, size, type);

		dialog.setContentPane(getMessageDialogThenClose());
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
	
	public static int showConfirmDialog(HTH_Frame frame, String title, String msg, Dimension size) {
		dialog = new HTH_Dialog(frame, title, msg, size, JOptionPane.QUESTION_MESSAGE);
		
		dialog.setContentPane(getConfirmDialog());
		dialog.setLocationRelativeTo(frame);
		dialog.option = JOptionPane.YES_OPTION;
		dialog.yes.setSelected(true);
		dialog.yes.requestFocus();
		dialog.setVisible(true);

		return dialog.option;
	}
	
	public static int showInputDialog(HTH_Frame frame, String title, JPanel inputPanel, Dimension size) {
		dialog = new HTH_Dialog(frame, title, "", size, JOptionPane.PLAIN_MESSAGE);
		
		dialog.setContentPane(getInputDialog(inputPanel));
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
		
		return dialog.option;
	}
	
	private HTH_Dialog() {
		
	}
	private HTH_Dialog(HTH_Frame frame, String title, String msg, Dimension size, int type) {
		super(frame, title, true);
		this.msg = msg;
		this.size = size;
//		this.type = type;
		
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
		setUndecorated(true);
	}
	
	private static JPanel getMessageDialog() {
		String okKey = "OK";
		Action okAction = new AbstractAction(okKey) {
			private static final long serialVersionUID = 91L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		};
		HTH_ControlButton okBtn = new HTH_ControlButton("OK");
		okBtn.setToolTipText("ENTER=OK");
		okBtn.addActionListener(okAction);
		okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
		okBtn.getActionMap().put(okKey, okAction);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);
		JPanel msgPanel = null;
		if(msg.equals("Data Inserted Successfully")){
			msgPanel = getMsgPanel(Color.GREEN);
		}else{
			msgPanel = getMsgPanel(Color.RED);
		}

		contentPanel.add(msgPanel, BorderLayout.CENTER);
		
		JPanel btnPanel = getControlPanel(okBtn);
		contentPanel.add(btnPanel, BorderLayout.SOUTH);
		
		JPanel dialogPanel = getDialogBorder();
		dialogPanel.add(contentPanel, BorderLayout.CENTER);
		return dialogPanel;
	}

	//---added
	private static JPanel getMessageDialogThenClose() {
		String okKey = "OK";
		Action okAction = new AbstractAction(okKey) {
			private static final long serialVersionUID = 91L;
			@Override
			public void actionPerformed(ActionEvent arg0) {

				dialog.dispose();
				System.exit(0);
			}
		};
		HTH_ControlButton okBtn = new HTH_ControlButton("OK");
		okBtn.setToolTipText("ENTER=OK");
		okBtn.addActionListener(okAction);
		okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
		okBtn.getActionMap().put(okKey, okAction);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);

		JPanel msgPanel = getMsgPanel(Color.RED);
		contentPanel.add(msgPanel, BorderLayout.CENTER);

		JPanel btnPanel = getControlPanel(okBtn);
		contentPanel.add(btnPanel, BorderLayout.SOUTH);

		JPanel dialogPanel = getDialogBorder();
		dialogPanel.add(contentPanel, BorderLayout.CENTER);
		return dialogPanel;
	}


	private static JPanel getConfirmDialog() {
		String exitKey = "Exit";
		Action exitAction = new AbstractAction(exitKey) {
			private static final long serialVersionUID = 92L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.option = JOptionPane.CANCEL_OPTION;
				dialog.dispose();
			}
		};
		HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
		exitBtn.setToolTipText("F3=Exit");
		exitBtn.addActionListener(exitAction);
		exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
		exitBtn.getActionMap().put(exitKey, exitAction);

		String okKey = "OK";
		Action okAction = new AbstractAction(okKey) {
			private static final long serialVersionUID = 93L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		};
		HTH_ControlButton okBtn = new HTH_ControlButton("OK");
		okBtn.setToolTipText("ENTER=OK");
		okBtn.addActionListener(okAction);
		okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
		okBtn.getActionMap().put(okKey, okAction);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setOpaque(false);
		contentPanel.add(mainPanel, BorderLayout.CENTER);
		
		JPanel msgPanel = getMsgPanel(Color.BLACK);
		mainPanel.add(msgPanel);
		
		JPanel optionPanel = getOptPanel();
		mainPanel.add(optionPanel);
		
		JPanel btnPanel = getControlPanel(exitBtn, okBtn);
		contentPanel.add(btnPanel, BorderLayout.SOUTH);

		JPanel dialogPanel = getDialogBorder();
		dialogPanel.add(contentPanel, BorderLayout.CENTER);
		return dialogPanel;
	}
	
	private static JPanel getInputDialog(JPanel inputPanel) {
		String exitKey = "Exit";
		Action exitAction = new AbstractAction(exitKey) {
			private static final long serialVersionUID = 92L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.option = JOptionPane.CANCEL_OPTION;
				dialog.dispose();
			}
		};
		HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
		exitBtn.setToolTipText("F3=Exit");
		exitBtn.addActionListener(exitAction);
		exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
		exitBtn.getActionMap().put(exitKey, exitAction);

		String okKey = "OK";
		Action okAction = new AbstractAction(okKey) {
			private static final long serialVersionUID = 93L;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.option = JOptionPane.YES_OPTION;
				dialog.dispose();
			}
		};
		HTH_ControlButton okBtn = new HTH_ControlButton("OK");
		okBtn.setToolTipText("ENTER=OK");
		okBtn.addActionListener(okAction);
		okBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), okKey);
		okBtn.getActionMap().put(okKey, okAction);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setOpaque(false);
		mainPanel.add(inputPanel);
		contentPanel.add(mainPanel, BorderLayout.CENTER);
		
		JPanel btnPanel = getControlPanel(exitBtn, okBtn);
		contentPanel.add(btnPanel, BorderLayout.SOUTH);

		JPanel dialogPanel = getDialogBorder();
		dialogPanel.add(contentPanel, BorderLayout.CENTER);
		return dialogPanel;
	}
	
	private static JPanel getDialogBorder() {
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BorderLayout());
		dialogPanel.setBackground(Color.WHITE);
		dialogPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, BG_BORDER));
		
		JPanel topBorderPanel = new JPanel();
		topBorderPanel.setLayout(new BorderLayout());
		topBorderPanel.setBackground(BG_BORDER);
		topBorderPanel.setPreferredSize(new Dimension(dialog.size.width, 25));
		topBorderPanel.setMaximumSize(new Dimension(dialog.size.width, 25));
		topBorderPanel.setMinimumSize(new Dimension(dialog.size.width, 25));
		dialogPanel.add(topBorderPanel, BorderLayout.NORTH);
		
		JLabel titleLabel = new JLabel(dialog.getTitle());
		titleLabel.setFont(HTH_FONT);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		topBorderPanel.add(titleLabel, BorderLayout.WEST);
		
		JPanel botBorderPanel = new JPanel();
		botBorderPanel.setBackground(BG_BORDER);
		botBorderPanel.setPreferredSize(new Dimension(dialog.size.width, 25));
		botBorderPanel.setMaximumSize(new Dimension(dialog.size.width, 25));
		botBorderPanel.setMinimumSize(new Dimension(dialog.size.width, 25));
		dialogPanel.add(botBorderPanel, BorderLayout.SOUTH);
		
		return dialogPanel;
	}
	
	private static JPanel getMsgPanel(Color foreground) {
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new GridBagLayout());
		msgPanel.setOpaque(false);
		
//		JLabel msgIcon = new JLabel();
//		msgIcon.setIcon(getIcon());
//		msgIcon.setHorizontalAlignment(JLabel.CENTER);
//		msgIcon.setVerticalAlignment(JLabel.CENTER);
//		msgPanel.add(msgIcon);
		
		JTextPane msgTextField = new JTextPane();
		msgTextField.setText(dialog.msg);
		msgTextField.setFont(HTH_FONT.deriveFont(16.0f));
		msgTextField.setDisabledTextColor(foreground);
		msgTextField.setEditable(false);
		msgTextField.setEnabled(false);
		msgTextField.setBackground(Color.WHITE);
		msgTextField.setBorder(null);
		msgPanel.add(msgTextField);
		
		return msgPanel;
	}
	
	private static JPanel getOptPanel() {
		JRadioButton yes = new JRadioButton("Yes");
		yes.setContentAreaFilled(false);
		yes.setFocusPainted(false);
		yes.setOpaque(false);
		yes.setFont(HTH_FONT);
		yes.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_TAB) {
					dialog.no.requestFocus();
					dialog.no.setSelected(true);
					dialog.yes.setSelected(false);
					dialog.option = JOptionPane.NO_OPTION;
				}
			}

			@Override
			public void keyReleased(KeyEvent evt) {
			}

			@Override
			public void keyTyped(KeyEvent evt) {
			}
		});
		yes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dialog.option = JOptionPane.YES_OPTION;
			}
		});
		
		JRadioButton no = new JRadioButton("No");
		no.setContentAreaFilled(false);
		no.setFocusPainted(false);
		no.setOpaque(false);
		no.setFont(HTH_FONT);
		no.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_TAB) {
					dialog.yes.requestFocus();
					dialog.yes.setSelected(true);
					dialog.no.setSelected(false);
					dialog.option = JOptionPane.YES_OPTION;
				}
			}

			@Override
			public void keyReleased(KeyEvent evt) {
			}

			@Override
			public void keyTyped(KeyEvent evt) {
			}
		});
		no.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dialog.option = JOptionPane.NO_OPTION;
			}
		});
		
		ButtonGroup options = new ButtonGroup();
		options.add(yes);
		options.add(no);
		dialog.yes = yes;
		dialog.no = no;
		
		JPanel optionPanel = new JPanel();
		optionPanel.setOpaque(false);
		optionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		optionPanel.add(yes);
		optionPanel.add(no);
		
		return optionPanel;
	}
	
	private static JPanel getControlPanel(HTH_ControlButton... buttons) {
		JPanel btnPanel = new JPanel();
		btnPanel.setOpaque(false);
		btnPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 3, 3));

		btnPanel.add(buttons[0]);
		for (int idx = 0; idx < buttons.length; idx++) {
			JPanel ghostPanel = new JPanel();
			ghostPanel.setPreferredSize(new Dimension(5, 25));
			ghostPanel.setMaximumSize(new Dimension(5, 25));
			ghostPanel.setMinimumSize(new Dimension(5, 25));
			ghostPanel.setOpaque(false);
			btnPanel.add(ghostPanel);

			btnPanel.add(buttons[idx]);
		}
		
		return btnPanel;
	}
	
//	private static Icon getIcon() {
//		switch (dialog.type) {
//		case JOptionPane.ERROR_MESSAGE:
//			return UIManager.getIcon("OptionPane.errorIcon");
//		case JOptionPane.WARNING_MESSAGE:
//			return UIManager.getIcon("OptionPane.warningIcon");
//		case JOptionPane.INFORMATION_MESSAGE:
//			return UIManager.getIcon("OptionPane.informationIcon");
//		case JOptionPane.QUESTION_MESSAGE:
//			 return UIManager.getIcon("OptionPane.questionIcon");
//		case JOptionPane.PLAIN_MESSAGE:
//		default:
//			return null;
//		}
//	}

}
