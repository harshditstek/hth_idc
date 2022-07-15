package com.hth.id_card.user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.hth.images.HTH_Image;

public class HTH_PromptButton extends JButton {

	/**
	 * Serial Version ID.
	 */
	private static final long serialVersionUID = 5L;

	public HTH_PromptButton() {
		super();
		setContentAreaFilled(false);
	    setFont(new Font("Arial",  Font.PLAIN, 15));
		setPreferredSize(new Dimension(21, 21));
		setMaximumSize(new Dimension(21, 21));
		setMinimumSize(new Dimension(21, 21));
	    setForeground(Color.BLACK);
		setBorder(null);
		setRolloverEnabled(true);
		setFocusPainted(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		try {
			BufferedImage bgImg = ImageIO.read(HTH_Image.getImageURL("more.png"));
			g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.paintComponent(g);
	}
}
