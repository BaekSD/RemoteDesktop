package client.screen.event;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import byteControl.ByteController;
import client.ClientMain;

public class EventSendSocket {
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private int port;
	private InetAddress addr;
	private long time = System.currentTimeMillis();
	private byte[] buffer;
	
	public void connect() {
		try {
			socket = new DatagramSocket();
			addr = InetAddress.getByName(ClientMain.addr);
			port = 54568;
			buffer = new byte[16];
		} catch (SocketException | UnknownHostException e) {
			System.exit(1);
		}
	}
	
	public boolean send(Event e) {
		if(e.getType() == 0 && e.getValue() == 7 && time+25 >= System.currentTimeMillis()) {
			//System.out.println("not yet");
			return false;
		}
		
		byte[] b = ByteController.intToByteArray(e.getType());
		System.arraycopy(b, 0, buffer, 0, 4);

		b = ByteController.intToByteArray(e.getX());
		System.arraycopy(b, 0, buffer, 4, 4);

		b = ByteController.intToByteArray(e.getY());
		System.arraycopy(b, 0, buffer, 8, 4);

		b = ByteController.intToByteArray(e.getValue());
		System.arraycopy(b, 0, buffer, 12, 4);
		
		packet = new DatagramPacket(buffer, buffer.length, addr, port);
		
		try {
			socket.send(packet);
		} catch (IOException e1) {
			return false;
		}
		
		time = System.currentTimeMillis();
		return true;
	}
}