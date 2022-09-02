package com.hth.id_card.user_interface.printer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.hth.id_card.user_interface.HTH_CardScreen;
import com.hth.id_card.user_interface.HTH_ControlButton;
import com.hth.id_card.user_interface.HTH_Frame;
import com.hth.id_card.user_interface.HTH_FunctionButton;
import com.hth.util.IDCard;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * HTH ID printer window.
 * <br><br>
 * This window contains all the functionalities in HTH ID printer program. Modify this class for Printer portion of ID Process.
 * 
 * @author dickfoong
 */
public class ID_Printer extends HTH_Frame implements WindowListener, Printable {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 102L;

	/**
	 * Program title shown on application window.
	 */
	//----7
//	private static final String PRINTER_NAME = "ID Printer";
	public static String PRINTER_NAME = "ID Printer";

	/**
	 * ID card width on PDF page.
	 */
	private static final float ID_WIDTH = 287;

	/**
	 * ID card height on PDF page.
	 */
	private static final float ID_HEIGHT = 180;

	/**
	 * ID card X-location offset on PDF page.
	 */
	private static final float ID_X_OFFSET = ID_WIDTH / 2;

	/**
	 * Front of ID card Y-location offset on PDF page.
	 */
	private static final float ID_FRONT_Y_OFFSET = 25;

	/**
	 * Back of ID card Y-location offset on PDF page.
	 */
	private static final float ID_BACK_Y_OFFSET = ID_FRONT_Y_OFFSET + ID_HEIGHT;

	/**
	 * ID card width showed on monitor.
	 */
	private static final int WIDTH = 715;

	/**
	 * ID card height showed on monitor.
	 */
	private static final int HEIGHT = 490;

	/**
	 * Default font for all texts.
	 */
	private static final Font HTH_FONT = new Font("Arial", Font.PLAIN, 18);

	/**
	 * Card panel.
	 */
	private HTH_CardScreen cardFramePanel;

	/**
	 * All parent panels used in this printer screen.
	 */
	private JPanel contentPanel, controlPanel;

	/**
	 * All information labels shown in this printer screen.
	 */
	private JLabel loadingLabel, cardCountLabel;

	/**
	 * Function keys in this printer screen.
	 */
	private HTH_FunctionButton prevBtn, nextBtn, turnBtn, pdfBtn, printBtn, download;

	/**
	 * Boolean indicates the face of the ID card.
	 */
	private boolean isFront;

	/**
	 * Curring index of the ID card.
	 */
	private int currIdx;

	/**
	 * Array of cards to be printed.
	 */
	private IDCard[] cardList;

	/**
	 * Constructor to create ID printer screen with loading screen set up.
	 */
	public ID_Printer() {
		super(PRINTER_NAME);
		addWindowListener(this);

		initComponents();
		setFunctionKeys();

		contentPanel.setBackground(Color.WHITE);
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		controlPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		controlPanel.setOpaque(false);

		String exitKey = "Exit";
		Action exitAction = new AbstractAction(exitKey) {
			private static final long serialVersionUID = 10110L;
			@Override
			public void actionPerformed(ActionEvent e) {
				exitProgram();
			}
		};
		HTH_ControlButton exitBtn = new HTH_ControlButton("Exit");
		exitBtn.setToolTipText("F3=Exit");
		exitBtn.addActionListener(exitAction);
		exitBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), exitKey);
		exitBtn.getActionMap().put(exitKey, exitAction);
		controlPanel.add(exitBtn);

		add(contentPanel, BorderLayout.CENTER);

		setEnable(false);
		loadingLabel.setFont(HTH_FONT);
		loadingLabel.setHorizontalAlignment(JLabel.CENTER);
		loadingLabel.setVerticalAlignment(JLabel.CENTER);
		contentPanel.add(loadingLabel, BorderLayout.CENTER);

		setVisible(true);
	}

	/**
	 * Initialize all the components in the programs.
	 */
	private void initComponents() {
		contentPanel = new JPanel();
		controlPanel = new JPanel();
		loadingLabel = new JLabel("ID card(s) loading... Please wait.");
		cardCountLabel = new JLabel("Card: ");
		turnBtn = new HTH_FunctionButton("Back");
		pdfBtn = new HTH_FunctionButton("Save all ID cards");
		printBtn = new HTH_FunctionButton("Print all");
		prevBtn = new HTH_FunctionButton("Previous ID card");
		nextBtn = new HTH_FunctionButton("Next ID card");
		download = new HTH_FunctionButton("Download");
	}

	/**
	 * Set the function keys of the programs.
	 */
	private void setFunctionKeys() {
		UIManager.getDefaults().put("ToolTipUI", "javax.swing.plaf.basic.BasicToolTipUI");

		String turnKey = "Turn";
		Action turnAction = new AbstractAction(turnKey) {
			private static final long serialVersionUID = 10111L;
			@Override
			public void actionPerformed(ActionEvent e) {
				showID();
			}
		};
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
		printBtn.setToolTipText("F5=Print Sample");
		printBtn.addActionListener(printAction);
		printBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), printKey);
		printBtn.getActionMap().put(printKey, printAction);

		String prevKey = "Prev";
		Action prevAction = new AbstractAction(prevKey) {
			private static final long serialVersionUID = 10130L;
			@Override
			public void actionPerformed(ActionEvent e) {
				showPrev();
			}
		};
		prevBtn.setToolTipText("Shift + F7=Previous ID Card");
		prevBtn.addActionListener(prevAction);
		prevBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.SHIFT_DOWN_MASK), prevKey);
		prevBtn.getActionMap().put(prevKey, prevAction);

		String nextKey = "Prev";
		Action nextAction = new AbstractAction(nextKey) {
			private static final long serialVersionUID = 10131L;
			@Override
			public void actionPerformed(ActionEvent e) {
				showNext();
			}
		};
		nextBtn.setToolTipText("Shift + F8=Next ID Card");
		nextBtn.addActionListener(nextAction);
		nextBtn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.SHIFT_DOWN_MASK), nextKey);
		nextBtn.getActionMap().put(nextKey, nextAction);

		String downloadKey = "Download";
		Action downloadCSV = new AbstractAction(downloadKey) {
			private static final long serialVersionUID = 10131L;
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.exit(0);
				//IDCard id = new IDCard()
				try {
					downloadFun();
					DataSingleton.destroy();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
				;
			}
		};
		download.setToolTipText("Download csv");
		download.addActionListener(downloadCSV);
		//download.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.SHIFT_DOWN_MASK), nextKey);
		//download.getActionMap().put(nextKey, nextAction);

		addFunctionKeys(turnBtn, pdfBtn, printBtn, download);
	}

	/**
	 * Exit the program.
	 */
	private void exitProgram() {
		System.exit(0);
	}

	/**
	 * Begin printer session with given list of cards.
	 * @param cardList
	 */
	public void begin(IDCard[] cardList) {
		this.cardList = cardList;
		currIdx = 0;

		if (cardList.length > 1) {
			resetFunctionKeys();
			addFunctionKeys(prevBtn, nextBtn, turnBtn, pdfBtn, printBtn, download);
		}

		isFront = true;

		showID();
	}

	/**
	 * Draw and show the current ID.
	 */
	private void showID() {
		if (cardFramePanel != null) {
			contentPanel.remove(cardFramePanel); 
		}

		if (cardList.length == 0) {
			setEnable(false);
			loadingLabel.setText("There is no ID cards in the queue.");
			contentPanel.add(loadingLabel, BorderLayout.CENTER);
			revalidate();
			repaint();
		} else {
			cardCountLabel.setText("Card: " + (currIdx + 1) + " / " + cardList.length);
			cardCountLabel.setFont(HTH_FONT);

			while (!cardList[currIdx].isReady()) {
				try {
					setEnable(false);
					contentPanel.add(loadingLabel, BorderLayout.CENTER);
					revalidate();
					repaint();

					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			contentPanel.remove(loadingLabel);
			setEnable(true);

			if (isFront) {
				turnBtn.setText("Back");
			} else {
				turnBtn.setText("Front");
			}
			turnBtn.setToolTipText("F9=" + turnBtn.getText());

			cardFramePanel = new HTH_CardScreen(cardList[currIdx], isFront);
			JLayeredPane cardContentPanel = cardFramePanel.getCardContentPanel();
			cardContentPanel.setBounds(getCardBound());

			cardFramePanel.add(setCardBorder(), JLayeredPane.DEFAULT_LAYER);
			cardFramePanel.add(cardContentPanel, JLayeredPane.PALETTE_LAYER);

			contentPanel.add(cardFramePanel, BorderLayout.CENTER);
			JPanel cardCountPanel = new JPanel();
			cardCountPanel.setOpaque(false);
			cardCountPanel.setLayout(new BorderLayout());
			contentPanel.add(cardCountPanel, BorderLayout.NORTH);

			cardCountLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 10));
			cardCountPanel.add(cardCountLabel, BorderLayout.EAST);

			revalidate();
			repaint();

			isFront = !isFront;
		}
	}

	/**
	 * Switch on/off on all function keys.
	 * 
	 * @param isEnable
	 *        on/off of the function keys.
	 */
	private void setEnable(boolean isEnable) {
		if (cardList != null && cardList.length > 1) {
			if (currIdx == 0) {
				prevBtn.setEnabled(false);
			} else {
				prevBtn.setEnabled(isEnable);
			}

			if (currIdx == cardList.length - 1) {
				nextBtn.setEnabled(false);
			} else {
				nextBtn.setEnabled(isEnable);
			}
		}

		turnBtn.setEnabled(isEnable);
		pdfBtn.setEnabled(isEnable);
		printBtn.setEnabled(isEnable);
		download.setEnabled(isEnable);
	}

	/**
	 * Show next ID card.
	 */
	private void showNext() {
		currIdx++;
		isFront = true;
		showID();
	}

	/**
	 * Show previous ID card.
	 */
	private void showPrev() {
		currIdx--;
		isFront = true;
		showID();
	}

	/**
	 * Save ID cards as PDF document with 1 page per card.
	 */
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
				currIdx = 0;
				isFront = true;

				while (currIdx < cardList.length) {
					page = new PDPage();

					showID();
					front = generateIDImg("Front");

					showID();
					back = generateIDImg("Back");

					float midHeight, midWidth;
					midHeight = page.getArtBox().getHeight() / 2; // Middle height of the page.
					midWidth = page.getArtBox().getWidth() / 2; // Middle width of the page.

					PDPageContentStream contents = new PDPageContentStream(doc, page); // Stream to write contents on page.
					PDImageXObject ftImg = PDImageXObject.createFromFile(front.getAbsolutePath(), doc);
					contents.drawImage(ftImg, midWidth - ID_X_OFFSET, midHeight + ID_FRONT_Y_OFFSET, ID_WIDTH, ID_HEIGHT);

					PDImageXObject bkImg = PDImageXObject.createFromFile(back.getAbsolutePath(), doc);
					contents.drawImage(bkImg, midWidth - ID_X_OFFSET, midHeight - ID_BACK_Y_OFFSET, ID_WIDTH, ID_HEIGHT);

					contents.close(); // Release the stream.
					doc.addPage(page);

					front.delete();
					back.delete();

					currIdx++;
				}

				doc.save(output);
				doc.close();
				Desktop.getDesktop().open(output);
			} catch (FileNotFoundException fnfe) {
				currIdx = 0; // Reset to last page.
				showID();

				String errMsg = "A file name can't contain any of the following characters:\n:\"<>|";
				JOptionPane.showMessageDialog(this, errMsg, "Error on saving ID cards", JOptionPane.ERROR_MESSAGE);
				return;
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			currIdx = cardList.length - 1; // Reset to last page.
		}
	}

	/**
	 * Submit the cards to the printer.
	 */
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
			} catch (PrinterException ex) {
				/* The job did not successfully complete */
			}
		}	
	}

	/**
	 * Generate temporary file for ID card images.
	 * 
	 * @param name
	 *        Name of the file.
	 * @return
	 *        File instance of the output temporary file.
	 *        
	 * @throws Exception
	 *        Exception thrown if file is not created.
	 */
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

	/**
	 * Prepare printer image to print onto physical papers/cards.
	 */
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		// We have 2 pages for each id, and 'page' is zero-based.
		if ((currIdx = page / 2) >= cardList.length) {
			currIdx = cardList.length - 1;
			showID();

			String msg = "Printer is printing the ID cards. You may close the current window.";
			JOptionPane.showMessageDialog(this, msg, "Printing ID cards", JOptionPane.INFORMATION_MESSAGE);

			return NO_SUCH_PAGE;
		}

		if (page % 2 == 0) {
			isFront = true;
		} else {
			isFront = false;
		}

		showID();
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
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pXStart, pYStart);
		g2d.scale(xRatio, yRatio);
		cardContentPanel.printAll(g2d);
		g2d.dispose();
		cardContentPanel.revalidate();

		// Tell the caller that this page is part of the printed document
		return PAGE_EXISTS;
	}

	/**
	 * Set the borders surround the ID card.
	 * 
	 * @return
	 *        Empty panel with borders surrounding.
	 */
	private JPanel setCardBorder() {
		Rectangle cardBound = getCardBound();
		int padding = 3;

		JPanel borderPanel = new JPanel();
		borderPanel.setOpaque(false);
		borderPanel.setBounds(cardBound.x - padding, cardBound.y - padding, cardBound.width + (2 * padding), cardBound.height + (2 * padding));
		borderPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));

		return borderPanel;
	}

	/**
	 * Get the card boundaries.
	 * 
	 * @return
	 *        Rectangle boundaries.
	 */
	private Rectangle getCardBound() {
		int contentWidth = contentPanel.getSize().width;
		int contentHeight = contentPanel.getSize().height - controlPanel.getSize().height;
		int x = (contentWidth / 2) - (WIDTH / 2);
		int y = (contentHeight / 2) - (HEIGHT / 2);

		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		exitProgram();
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

	public static void downloadFun() throws IOException, FileNotFoundException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet spreadsheet = workbook.createSheet(" IDCARD REPORT ");
		XSSFRow row;
		Map<String, Object[]> studentData = new TreeMap<String, Object[]>();
		//studentData.put("1",new Object[]{"IDCARD REPORT"});
		studentData.put("1",new Object[] { "User", "Group", "EmpID", "FirstName", "LastName", "card Number", "Number Of Cards", "Coverage", "Effective Data", "Date", "Time"});

		List<String[]> listData = DataSingleton.singleton().getInsureList();
		for(int i=0; i<listData.size(); i++){
			int num = 2+i;
			Object[] data = new String[11];
			Object[] all =new String[listData.get(i).length];
			all = listData.get(i);
			data[0]="";       //User
			data[1]=all[95];  //Group
			data[2]=all[96];      //empid
			data[3]=all[97]; //first
			data[4]=all[98]; //last
			data[5]=all[99]; //card number
			data[6]="";      //no of card
			data[7]=all[100]; //coverage
			data[8]="";      //effective data
			data[9]=all[101]; //date
			data[10]=all[102];//time
			studentData.put(String.valueOf(num), data);
		}

		//studentData.put("4", new Object[] { "MAC", "129", "54322", "Prince", "verma", "7564654", "1", " ", " ","02012022","1100" });
		Set<String> keyid = studentData.keySet();
		System.out.println(keyid);

		int rowid = 0;
		for (String key : keyid) {

			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = studentData.get(key);
			int cellid = 0;

			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String)obj);
			}
		}

		File file = null;
		JFrame parentFrame = new JFrame();

		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter restrict = new FileNameExtensionFilter("xlsx files", ".xlsx");
		fileChooser.addChoosableFileFilter(restrict);
		fileChooser.setDialogTitle("Select a Location");
		int userSelection = fileChooser.showSaveDialog(parentFrame);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			file = new File(fileToSave.getAbsolutePath()+".xlsx");
		}

		System.out.println(":'." + file.getAbsolutePath());
		String path = file.getAbsolutePath();

		FileOutputStream out = new FileOutputStream(new File(path));

		workbook.write(out);
		out.close();


	}

}
