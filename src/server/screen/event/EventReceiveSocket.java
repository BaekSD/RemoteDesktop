package server.screen.event;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import byteControl.ByteController;
import server.ServerMain;

public class EventReceiveSocket extends Thread {
	/*
	private LinkedList<Socket> socket = new LinkedList<>();
	private ServerSocket serverSocket;
	*/
	private DatagramSocket socket;
	private DatagramPacket packet;
	private byte[] buffer;
	
	public EventReceiveSocket(int port) {
		try {
			socket = new DatagramSocket(port);
			buffer = new byte[16];
			packet = new DatagramPacket(buffer, buffer.length);
		} catch (SocketException e) {
			
		}
	}
	
	public void run() {
		while(!this.isInterrupted()) {
			receiveEvent();
		}
	}
	
	public void close() {
		this.interrupt();
		if(socket != null) {
			socket.close();
		}
	}
	
	public void receiveEvent() {
		try {
			socket.receive(packet);
			
			buffer = packet.getData();
			
			byte[] b = new byte[4];
			
			System.arraycopy(buffer, 0, b, 0, 4);
			int type = ByteController.byteArrayToInt(b);

			System.arraycopy(buffer, 4, b, 0, 4);
			int x = ByteController.byteArrayToInt(b);

			System.arraycopy(buffer, 8, b, 0, 4);
			int y = ByteController.byteArrayToInt(b);

			System.arraycopy(buffer, 12, b, 0, 4);
			int value = ByteController.byteArrayToInt(b);
			
			switch(type) {
			case 0:
				ServerMain.robot.mouseMove(x, y);
				if(value == 0) {
					ServerMain.robot.mousePress(InputEvent.BUTTON1_MASK);
					ServerMain.robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if(value == 1) {	//press left
					ServerMain.robot.mousePress(InputEvent.BUTTON1_MASK);
				} else if(value == 2) {	//press mid
					ServerMain.robot.mousePress(InputEvent.BUTTON2_MASK);
				} else if(value == 3) { //press right
					ServerMain.robot.mousePress(InputEvent.BUTTON3_MASK);
				} else if(value == 4) {	//release left
					ServerMain.robot.mouseRelease(InputEvent.BUTTON1_MASK);
				} else if(value == 5) {	//release mid
					ServerMain.robot.mouseRelease(InputEvent.BUTTON2_MASK);
				} else if(value == 6) {	//release right
					ServerMain.robot.mouseRelease(InputEvent.BUTTON3_MASK);
				} else if(value == 7) {	//just move
					
				}
				break;
			case 1:
				ServerMain.robot.mouseWheel(value);
				break;
			case 2:
				if(value == 157 && System.getProperty("os.name").toLowerCase().indexOf("win")>=0) {
					value = KeyEvent.VK_WINDOWS;
				} else if(value == KeyEvent.VK_WINDOWS && System.getProperty("os.name").toLowerCase().indexOf("mac")>=0) {
					value = 157;
				}
				ServerMain.robot.keyPress(value);
				break;
			case 3:
				if(value == 157 && System.getProperty("os.name").toLowerCase().indexOf("win")>=0) {
					value = KeyEvent.VK_WINDOWS;
				} else if(value == KeyEvent.VK_WINDOWS && System.getProperty("os.name").toLowerCase().indexOf("mac")>=0) {
					value = 157;
				}
				ServerMain.robot.keyRelease(value);
				break;
			}
			
		} catch(IOException e) {
			
		} catch(IllegalArgumentException e) {
			
		} catch(Exception e) {
			
		}
	}
}
