package com.hth.id_card.user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class HTH_DateField extends JTextField {
	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 10L;

	private int limit;

	public HTH_DateField(int limit, Font font) {
		super();

		this.limit = limit;

		setBackground(Color.WHITE);
		setForeground(new Color(0, 0, 150));
		setHorizontalAlignment(LEFT);
		setFont(font);
		setBorder(BorderFactory.createCompoundBorder(getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		addMouseListener(new HTH_TextFieldMouseListener(this));
		getDocument().addDocumentListener(new HTH_DocumentListener(this));
		addKeyListener(new HTH_TextFieldKeyListener(this));

		Dimension size = getTextSize();
		setPreferredSize(size);
		setMaximumSize(size);
		setMinimumSize(size);
	}

	private Dimension getTextSize() {
		String txt = "";
		for (int c = 0; c <= limit; c++) {
			txt += "M";
		}

		int width =  getFontMetrics(getFont()).stringWidth(txt);
		int height = getFontMetrics(getFont()).getHeight() + 10;

		return new Dimension(width, height);
	}

	private class HTH_DocumentListener implements DocumentListener {
		private String value;
		private HTH_DateField field;

		public HTH_DocumentListener(HTH_DateField field) {
			this.field = field;
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
					String fieldValue = field.getText().replaceAll("/", "");
					if (field.getText().length() > limit) {
						int cursorPos = field.getCaretPosition() - 1;
						field.setText(value);
						field.setCaretPosition(cursorPos);
					} else if (fieldValue.length() == limit - 2) {
						String newValue = fieldValue.substring(0,2) + "/" + fieldValue.substring(2,4) + "/" + fieldValue.substring(4);
						if (!value.replaceAll("/", "").equals(fieldValue)) {
							field.setText(newValue);
							field.setCaretPosition(newValue.length());
						}
						value = newValue;
					} else {
						if (!value.equals(field.getText())) {
							int cursorPos = field.getCaretPosition();
							value = field.getText();
							field.setText(value);
							field.setCaretPosition(cursorPos);
						}
					}
				}
			});
		}
	}

	private class HTH_TextFieldMouseListener implements MouseListener {
		private HTH_DateField field;

		public HTH_TextFieldMouseListener(HTH_DateField field) {
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

	private class HTH_TextFieldKeyListener implements KeyListener {
		HTH_DateField field;

		private HTH_TextFieldKeyListener(HTH_DateField field) {
			this.field = field;
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if ((e.getKeyChar() >= '0' && e.getKeyChar() <= '9')
					|| e.getKeyCode() == KeyEvent.VK_DELETE
					|| e.getKeyCode() == KeyEvent.VK_BACK_SPACE
					|| e.getKeyCode() == KeyEvent.VK_DOWN
					|| e.getKeyCode() == KeyEvent.VK_UP
					|| e.getKeyCode() == KeyEvent.VK_LEFT
					|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
				field.setEditable(true);
			} else {
				field.setEditable(false);

			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
}
