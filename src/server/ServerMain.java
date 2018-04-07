package server;

import java.awt.Robot;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;

import server.chat.ServerChatSocket;
import server.ft.ServerFTSocket;
import server.screen.ImageControl;
import server.screen.ScreenSendSocket;
import server.screen.event.EventReceiveSocket;

public class ServerMain {

	public static int count = 0;
	public static Robot robot;
	//private static LinkedList<ScreenSendSocket> sss = new LinkedList<>();
	ScreenSendSocket sss = null;
	private static LinkedList<ServerFTSocket> fts = new LinkedList<>();
	private static ServerSocket screenSocket, eventSocket, chatSocket, ftcSocket, ftSocket;
	private static EventReceiveSocket ers;
	private static ServerChatSocket scs;
	private ImageControl ic;
	
	public ServerMain() {
		ServerMain.count = 0;

		try {
			//screenSocket = new ServerSocket(54567);
			chatSocket = new ServerSocket(54569);
			ftcSocket = new ServerSocket(54570);
			ftSocket = new ServerSocket(54571);
		} catch (IOException e) {
		}
		
		ic = new ImageControl();
		
		(new Thread() {
			public void run() {
				while(true) {
					ic.screenCapture();
					ic.spliteImage();
					ic.compareImage();
					try {
						sleep(30);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
		
		startServer();
	}
	
	public void startServer() {
		sss = new ScreenSendSocket(54567);
		sss.start();
		scs = new ServerChatSocket(chatSocket);
		scs.start();
		ers = new EventReceiveSocket(54568);
		ers.start();
	}

	public void connectServer() {
		/*
		ScreenSendSocket newSSS = new ScreenSendSocket(screenSocket);
		sss.add(newSSS);
		newSSS.start();
		*/
		ServerFTSocket newFTS = new ServerFTSocket(ftcSocket, ftSocket);
		fts.add(newFTS);
		newFTS.start();
		scs.connect();
	}
	
	public void disconnect() {
		if(sss != null) {
			sss.interrupt();
			sss.disconnect();
		}
		if(scs != null) {
			scs.interrupt();
			scs.disconnect();
		}
		if(ers != null) {
			ers.close();
		}
		
		try {
			if(screenSocket != null) {
				screenSocket.close();
			}
			if(eventSocket != null) {
				eventSocket.close();
			}
			if(chatSocket != null) {
				chatSocket.close();
			}
		} catch (IOException e) {
		}
	}
	
	public static void sendMsg(String str) {
		scs.sendToClients("Server", str);
	}
	
	public void showChatGUI() {
		scs.showChatGUI();
	}
	
	
	public static void garbageCollect(int i) {
		count--;
		fts.get(i).interrupt();
		fts.get(i).disconnect();
		fts.remove(i);
	}
	
}
