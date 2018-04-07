package server.screen;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import server.ServerMain;

public class ImageControl {
	
	public static int w_pixel = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static int h_pixel = 300000/w_pixel;
	
	public static double scale = 1.0;
	
	public static BufferedImage img, img2;
	public static BufferedImage[][] splitedImg, splitedImg2;
	public static int[][][] wh;
	public static boolean isChanged[][];
	
	public ImageControl() {
		img = null;
		splitedImg = null;
		try {
			ServerMain.robot = new Robot();
		} catch (AWTException e) {
		}
	}
	
	public void spliteImage() {
		int height = img.getHeight();
		int width = img.getWidth();

		splitedImg = new BufferedImage[(width+w_pixel-1)/w_pixel][(height+h_pixel-1)/h_pixel];
		wh = new int[splitedImg.length][splitedImg[0].length][4];
		
		for(int i=0; i<splitedImg.length; i++) {
			for(int j=0; j<splitedImg[i].length; j++) {
				int w = w_pixel;
				int h = h_pixel;
				if((i+1)*w_pixel > width) {
					w = width - i*w_pixel;
				}
				if((j+1)*h_pixel > height) {
					h = height - j*h_pixel;
				}
				splitedImg[i][j] = img.getSubimage(i*w_pixel, j*h_pixel, w, h);
				wh[i][j][0] = i*w_pixel;
				wh[i][j][1] = j*h_pixel;
			}
		}
	}
	
	public void compareImage() {
		
		if(isChanged == null || img2 == null) {
			isChanged = new boolean[(img.getWidth()+w_pixel-1)/w_pixel][(img.getHeight()+h_pixel-1)/h_pixel];
			
			for(int i=0; i<isChanged.length; i++) {
				for(int j=0; j<isChanged[i].length; j++) {
					isChanged[i][j] = true;
				}
			}
			return;
		}
		
		for(int i=0; i<isChanged.length; i++) {
			for(int j=0; j<isChanged[i].length; j++) {
				isChanged[i][j] = false;
			}
		}
		
		/*
		for(int i=0; i<img.getWidth(); i++) {
			for(int j=0; j<img.getHeight(); j++) {
				if(img.getRGB(i, j) != img2.getRGB(i, j)) {
					isChanged[i/pixel][j/pixel] = true;
				}
			}
		}
		*/
		
		for(int i=0; i<isChanged.length; i++) {
			for(int j=0; j<isChanged[i].length; j++) {
				for(int w=w_pixel*i; w<splitedImg[i][j].getWidth()+w_pixel*i; w++) {
					for(int h=h_pixel*j; h<splitedImg[i][j].getHeight()+h_pixel*j; h++) {
						if(img.getRGB(w, h) != img2.getRGB(w, h)) {
							isChanged[i][j] = true;
							break;
						}
					}
					if(isChanged[i][j]) {
						break;
					}
				}
			}
		}
	}
	
	public void screenCapture() {
		//Image tmp;
		img2 = img;
		img = ServerMain.robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		/*
		tmp = img.getScaledInstance((int)(img.getWidth()*scale), (int)(img.getHeight()*scale), BufferedImage.SCALE_SMOOTH);
		
		img = new BufferedImage(tmp.getWidth(null), tmp.getHeight(null), img.getType());
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		*/
		/*
		Image tmp = img.getScaledInstance(img.getWidth(), img.getHeight(), Image.SCALE_SMOOTH);
		img = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		
		Graphics2D g2d = img.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		*/
	}
	
	public void spliteTest() {
		try {
			img = ImageIO.read(new File("/Users/baek/Desktop/test/capture1.png"));
		} catch(IOException e) {
			
		}
		
		int height = img.getHeight();
		int width = img.getWidth();
		
		splitedImg = new BufferedImage[(width+w_pixel-1)/w_pixel][(height+h_pixel-1)/h_pixel];
		
		for(int i=0; i<splitedImg.length; i++) {
			for(int j=0; j<splitedImg[i].length; j++) {
				int w = w_pixel;
				int h = h_pixel;
				if((i+1)*w_pixel > width) {
					w = width - i*w_pixel;
				}
				if((j+1)*h_pixel > height) {
					h = height - j*h_pixel;
				}
				splitedImg[i][j] = img.getSubimage(i*w_pixel, j*h_pixel, w, h);
				try {
					ImageIO.write(splitedImg[i][j], "png", new File("/Users/baek/Desktop/test/splite/split"+i+"_"+j+".png"));
				} catch(IOException e) {
					
				}
			}
		}
	}
	
	public void compareTest() {
		try {
			img = ImageIO.read(new File("/Users/baek/Desktop/test/capture1.png"));
			img2 = ImageIO.read(new File("/Users/baek/Desktop/test/capture2.png"));
		} catch(IOException e) {
			
		}
		
		BufferedImage compared = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		
		for(int i=0; i<compared.getWidth(); i++) {
			for(int j=0; j<compared.getHeight(); j++) {
				if(img.getRGB(i, j) == img2.getRGB(i, j)) {
					compared.setRGB(i, j, 0);
				} else {
					compared.setRGB(i, j, img.getRGB(i, j));
				}
			}
		}
		
		try {
			ImageIO.write(compared, "png", new File("/Users/baek/Desktop/test/compared.png"));
		} catch(IOException e) {
			
		}
	}
}
