package client.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

import chatGUI.ChatGUI;
import client.ClientMain;

public class ClientChatSocket extends Thread {
	private DataOutputStream dos;
	private DataInputStream dis;
	private Socket socket;
	private String name;
	public static ChatGUI gui;
	
	public void run() {
		connect();
		while(!isInterrupted()) {
			rcvMsg();
		}
	}
	
	public void connect() {
		try {
			socket = new Socket(ClientMain.addr, 54569);
			//socket.setSoTimeout(10);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			gui = new ChatGUI(false);

			name = "Client"+dis.readInt();
			gui.setTitle(name);
		} catch(IOException e) {
			System.exit(1);
		}
	}
	
	public void rcvMsg() {
		String name, t, str;
		try {
			if(dis.readInt()  == 0) {
				name = dis.readUTF();
				t = dis.readUTF();
				str = dis.readUTF();
				if(name.equals(this.name)) {
					name = "You";
				}
				gui.addText(name, t, str);
				if(!gui.isVisible()) {
					gui.setVisible(true);
				}
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(ClientMain.srs.gui, "disconnected", "disconnected", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
	}
	
	public void sendMsg(String str) {
		try {
			dos.writeUTF(name);
			dos.writeUTF(str);
			dos.flush();
		} catch (IOException e) {
		}
	}
}
