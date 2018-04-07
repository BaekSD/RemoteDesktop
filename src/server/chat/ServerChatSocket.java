package server.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import chatGUI.ChatGUI;
import main.RemoteDesktopMain;
import server.ServerMain;

public class ServerChatSocket extends Thread {
	private LinkedList<Socket> socket = new LinkedList<>();
	private ServerSocket serverSocket;
	private ChatGUI gui;
	private int count;
	
	public ServerChatSocket(ServerSocket socket) {
		this.serverSocket = socket;
		count = 0;
		try {
			serverSocket.setSoTimeout(100);
		} catch (SocketException e) {
		}
		gui = new ChatGUI(true);
	}
	
	public void run() {
		DataInputStream dis;
		String name, str;
		while(!isInterrupted()) {
			for(int i=0; i<socket.size(); i++) {
				try {
					dis = new DataInputStream(socket.get(i).getInputStream());
					name = dis.readUTF();
					str = dis.readUTF();
					if(!gui.isVisible()) {
						gui.setVisible(true);
					}
					sendToClients(name, str);
				} catch (IOException e) {
				}
				
				try {
					DataOutputStream dos = new DataOutputStream(socket.get(i).getOutputStream());
					dos.writeInt(-1);
					dos.flush();
				} catch (IOException e) {
					disconnect(i);
				}
			}
		}
	}
	
	public void connect() {
		Socket s;
		try {
			s = serverSocket.accept();
			s.setSoTimeout(100);
			socket.add(s);
			count++;
			
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(count);
			dos.flush();
		} catch(IOException e) {
		}
	}
	
	public void sendToClients(String name, String str) {
		DataOutputStream dos;
		
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		String t = "["+dayTime.format(new Date(time))+"]";
		
		gui.addText(name, t, str);
		
		for(int i=0; i<socket.size(); i++) {
			if(socket.get(i) == null) continue;
			try {
				dos = new DataOutputStream(socket.get(i).getOutputStream());
				dos.writeInt(0);
				dos.writeUTF(name);
				dos.writeUTF(t);
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e) {
				try {
					socket.get(i).close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	public void showChatGUI() {
		if(gui == null) {
			JOptionPane.showMessageDialog(RemoteDesktopMain.frame, "Chatting System Error", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			if(!gui.isVisible()) {
				gui.setVisible(true);
			}
		}
	}
	
	public void disconnect() {
		if(gui.isVisible()) {
			gui.setVisible(false);
		}
		
		for(int i=0; i<socket.size(); i++) {
			Socket s = socket.get(i);
			try {
				if(s != null) {
					s.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	public void disconnect(int i) {
		if(socket.size() <= i) {
			return;
		}
		Socket s = socket.get(i);
		if(s != null) {
			try {
				s.close();
				socket.remove(i);
			} catch (IOException e) {
			}
		}
		
		ServerMain.garbageCollect(i);
	}
}
