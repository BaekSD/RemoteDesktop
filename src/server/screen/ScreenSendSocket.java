package server.screen;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.imageio.ImageIO;

import byteControl.ByteController;
import server.ServerMain;

public class ScreenSendSocket extends Thread {
	//private DatagramSocket socket;
	private MulticastSocket socket;
	private DatagramPacket rep;
	private byte[] repBuffer;
	private InetAddress addr;
	
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	private long t, send_t;
	
	public boolean connected = false;
	
	private int port;
	
	public ScreenSendSocket(int port) {
		this.port = port;
	}
	
	public void run() {
		startServer();
		send_t = t = System.currentTimeMillis();
		while(!isInterrupted()) {
			if(send_t+30 < System.currentTimeMillis()) {
				sendImage();
			}
		}
	}
	
	public void startServer() {
		try {
			socket = new MulticastSocket();
			socket.setTimeToLive(100);
			addr = InetAddress.getByName("224.0.0.1");
			repBuffer = new byte[65000];
			rep = new DatagramPacket(repBuffer, repBuffer.length);
		} catch (IOException e) {
		}
	}
	
	public void disconnect() {
		ServerMain.count--;
		connected = false;
		if(socket != null) {
			socket.close();
		}
	}
	
	public void sendImage() {
		try {
			send_t = System.currentTimeMillis();
			for(int ww=0; ww<ImageControl.splitedImg.length; ww++) {
					for(int hh=0; hh<ImageControl.splitedImg[ww].length; hh++) {
						if(ImageControl.isChanged[ww][hh] || t < System.currentTimeMillis()-1000) {
							baos = new ByteArrayOutputStream();
							ImageIO.write(ImageControl.splitedImg[ww][hh], "jpeg", baos);
							
							byte[] tmp = baos.toByteArray();
							
							repBuffer = new byte[tmp.length+12];
							
							if(repBuffer.length > 65000) {
								BufferedImage origin = ImageControl.splitedImg[ww][hh];
								BufferedImage img1 = origin.getSubimage(0, 0, origin.getWidth(), origin.getHeight()/2);
								BufferedImage img2 = origin.getSubimage(0, origin.getHeight()/2, origin.getWidth(), origin.getHeight()-img1.getHeight());
								
								baos = new ByteArrayOutputStream();
								ImageIO.write(img1, "jpeg", baos);
								
								tmp = baos.toByteArray();
								
								repBuffer = new byte[tmp.length+12];
								
								if(repBuffer.length <= 65000) {
									System.arraycopy(ByteController.intToByteArray(ImageControl.wh[ww][hh][0]), 0, repBuffer, 0, 4);
									System.arraycopy(ByteController.intToByteArray(ImageControl.wh[ww][hh][1]), 0, repBuffer, 4, 4);
									System.arraycopy(ByteController.intToByteArray(tmp.length), 0, repBuffer, 8, 4);
									System.arraycopy(tmp, 0, repBuffer, 12, tmp.length);
				
									rep = new DatagramPacket(repBuffer, repBuffer.length, addr, port);
									socket.send(rep);
								}
								
								baos = new ByteArrayOutputStream();
								ImageIO.write(img2, "jpeg", baos);
								
								tmp = baos.toByteArray();
								
								repBuffer = new byte[tmp.length+12];
								
								if(repBuffer.length <= 65000) {
									System.arraycopy(ByteController.intToByteArray(ImageControl.wh[ww][hh][0]), 0, repBuffer, 0, 4);
									System.arraycopy(ByteController.intToByteArray(ImageControl.wh[ww][hh][1]+img1.getHeight()), 0, repBuffer, 4, 4);
									System.arraycopy(ByteController.intToByteArray(tmp.length), 0, repBuffer, 8, 4);
									System.arraycopy(tmp, 0, repBuffer, 12, tmp.length);
				
									rep = new DatagramPacket(repBuffer, repBuffer.length, addr, port);
									socket.send(rep);
								}
							} else {
								System.arraycopy(ByteController.intToByteArray(ImageControl.wh[ww][hh][0]), 0, repBuffer, 0, 4);
								System.arraycopy(ByteController.intToByteArray(ImageControl.wh[ww][hh][1]), 0, repBuffer, 4, 4);
								System.arraycopy(ByteController.intToByteArray(tmp.length), 0, repBuffer, 8, 4);
								System.arraycopy(tmp, 0, repBuffer, 12, tmp.length);
			
								rep = new DatagramPacket(repBuffer, repBuffer.length, addr, port);
								socket.send(rep);
							}
						}
					}
			}
			if(t < System.currentTimeMillis()-1100) {
				t = System.currentTimeMillis();
			}
		} catch (Exception e) {
		}
	}
}
