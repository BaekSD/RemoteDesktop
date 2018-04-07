package client.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientAuthChecker {

	private static Socket socket;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	
	public static boolean check(String addr, String code, int[] wh) {
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(addr, 55666), 100);
			dos = new DataOutputStream(socket.getOutputStream());	
			dis = new DataInputStream(socket.getInputStream());
			dos.writeUTF(code);
			dos.flush();
			
			boolean ret = dis.readBoolean();
			int width = dis.readInt();
			int height = dis.readInt();
			
			wh[0] = width;
			wh[1] = height;
			
			return ret;
			
		} catch (Exception e) {
			return false;
		}
	}
}
