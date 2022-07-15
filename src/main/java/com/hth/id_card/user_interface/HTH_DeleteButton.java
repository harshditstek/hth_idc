package com.hth.id_card.user_interface;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.hth.images.HTH_Image;

public class HTH_DeleteButton extends JButton {
	  /**
	   * Serial Version ID.
	   */
	  private static final long serialVersionUID = 8L;

	  public HTH_DeleteButton() {
	    super();
	    setContentAreaFilled(false);
	    setPreferredSize(new Dimension(25, 25));
	    setMaximumSize(new Dimension(25, 25));
	    setMinimumSize(new Dimension(25, 25));
	    setBorder(null);
	    setRolloverEnabled(true);
	    setFocusPainted(false);
	  }

	  @Override
	  protected void paintComponent(Graphics g) {
	    try {
	      BufferedImage bgImg = null;

	      if (getModel().isPressed()) {
	        bgImg = ImageIO.read(HTH_Image.getImageURL("DeleteHover.png"));
	        setCursor(new Cursor(Cursor.WAIT_CURSOR));
	      } else if (getModel().isRollover()) {
	        bgImg = ImageIO.read(HTH_Image.getImageURL("DeleteHover.png"));
	        setCursor(new Cursor(Cursor.HAND_CURSOR));
	      } else {
	        bgImg = ImageIO.read(HTH_Image.getImageURL("Delete.png"));
	        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	      }

	      g.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	    super.paintComponent(g);
	  }
}
