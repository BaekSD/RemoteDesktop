package client;

import client.chat.ClientChatSocket;
import client.ft.ClientFTSocket;
import client.screen.ScreenReceiveSocket;
import client.screen.event.EventSendSocket;

public class ClientMain {
	public static String addr = "10.211.55.28";
	public static ScreenReceiveSocket srs;
	public static ClientChatSocket ccs;
	public static EventSendSocket ess;
	public static ClientFTSocket fts;
	public static boolean connect_error = false;
	public static boolean connected = false;
	public static int width, height;
	
	
	public ClientMain(String addr, int w, int h) {
		ClientMain.addr = addr;
		width = w;
		height = h;
	}
	
	public void startClient() {
		srs = new ScreenReceiveSocket();
		ccs = new ClientChatSocket();
		ccs.start();
		fts = new ClientFTSocket();
		fts.start();
		srs.connect();
		srs.start();
		connected = true;
	}
}
