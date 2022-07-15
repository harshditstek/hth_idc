package com.hth.images;

import java.net.URL;

public class HTH_Image {
	public static URL getImageURL(String imgName) {
		//------1
		//URL imgPath = HTH_Image.class.getResource(imgName);
		//imgName = "/src/main/java/com/hth/images/hth_block.png" ;
		//imgName = "/Users/abosala7/myWork/IBM/utitlities/Hi-TechHealth-projects/HTH_IDC/src/main/java/com/hth/images/hth_block.png";
		//imgName ="/com/hth/images/hth_block.png";
		URL imgPath = HTH_Image.class.getClassLoader().getResource(imgName);
		//URL imgPath = HTH_Image.class.getResource(imgName);
//		System.out.println("----1."+imgName);
//
//		System.out.println("----2."+imgPath);
		return imgPath;
	}
}
