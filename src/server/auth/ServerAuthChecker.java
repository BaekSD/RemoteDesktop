package server.auth;

import java.awt.Toolkit;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.ServerMain;

public class ServerAuthChecker {
	
	private static Socket socket;
	private static ServerSocket serverSocket;
	
	private static DataInputStream dis;
	private static DataOutputStream dos;
	
	private static String code;
	
	//public static boolean connected = false;
	
	public static boolean check() {
		try {
			if(serverSocket == null || serverSocket.isClosed()) {
				serverSocket = new ServerSocket(55666);
				serverSocket.setSoTimeout(1000);
			}
			
			socket = serverSocket.accept();
			
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			String read = dis.readUTF();
			read = read.toUpperCase();
			boolean bool = read.equals(code);
			
			if(ServerMain.count >= 5) {
				//bool = false;
			}
			
			dos.writeBoolean(bool);
			dos.writeInt(Toolkit.getDefaultToolkit().getScreenSize().width);
			dos.writeInt(Toolkit.getDefaultToolkit().getScreenSize().height);
			dos.flush();
			
			socket.close();
			serverSocket.close();
			
			return bool;
			
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void setCode(String code) {
		ServerAuthChecker.code = code;
	}
}
