package client.screen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import byteControl.ByteController;
import client.ClientMain;
import main.RemoteDesktopMain;

public class ScreenReceiveSocket extends Thread{
	
	public ScreenGUI gui;
	private MulticastSocket socket;
	private DatagramPacket rep;
	private byte[] repBuffer;
	private BufferedImage[][] splitedImg;
	private InetAddress addr;
	
	public static int width=0,height=0;
	public static int w_pixel=0,h_pixel=0;
	
	String serverAddr;
	int port = 54567;
	
	public ScreenReceiveSocket() {
		serverAddr = ClientMain.addr;
		port = 54567;
		try {
			addr = InetAddress.getByName("224.0.0.1");
		} catch (UnknownHostException e) {
		}
	}
	
	public void run() {
		while(!isInterrupted()) {
			try {
				if(ClientMain.connected == true) {
					getImg();
				}
			} catch(IOException e) {
			}
		}
	}
	
	public void connect() {
		try {
			socket = new MulticastSocket(port);
			socket.setSoTimeout(100);
			socket.joinGroup(addr);
		} catch(IOException e) {
			e.printStackTrace();
		}
		RemoteDesktopMain.frame.setVisible(false);
		ClientMain.connected = true;
		
		width = ClientMain.width;
		height = ClientMain.height;
		
		w_pixel = width;
		h_pixel = 300000/w_pixel;
		
		repBuffer = new byte[65000];

		gui = new ScreenGUI();
		gui.startWindow(width, height);
		
		splitedImg = new BufferedImage[(width+w_pixel-1)/w_pixel][(height+h_pixel-1)/h_pixel];
		
		gui.adjust = new int[splitedImg.length][splitedImg[0].length][2];
	}
	
	public void getImg() throws IOException {
		for(int w=0; w<splitedImg.length; w++) {
			for(int h=0; h<splitedImg[w].length; h++) {
				rep = new DatagramPacket(repBuffer, repBuffer.length);
				socket.receive(rep);
				
				repBuffer = rep.getData();
				
				byte[] tmp = new byte[4];
				System.arraycopy(repBuffer, 0, tmp, 0, 4);
				int ww = ByteController.byteArrayToInt(tmp);
				System.arraycopy(repBuffer, 4, tmp, 0, 4);
				int hh = ByteController.byteArrayToInt(tmp);
				System.arraycopy(repBuffer, 8, tmp, 0, 4);
				int len = ByteController.byteArrayToInt(tmp);
				
				tmp = new byte[len];
				System.arraycopy(repBuffer, 12, tmp, 0, len);
				
				ByteArrayInputStream bais = new ByteArrayInputStream(tmp);
				splitedImg[ww/w_pixel][hh/h_pixel] = ImageIO.read(bais);
				
				gui.paintImage(splitedImg[ww/w_pixel][hh/h_pixel], ww, hh, ww/w_pixel, hh/h_pixel);
			}
		}
	}
}
