package client.ft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import client.ClientMain;

public class ClientFTSocket extends Thread {
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;
	private FileOutputStream fos;
	private FileInputStream fis;
	
	public static FTGUI gui;
	
	public void run() {
		connect();
	}
	
	public void connect() {
		String addr = ClientMain.addr;
		int port = 54570;
		
		try {
			socket = new Socket(addr, port);
			
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			
			gui = new FTGUI(this);
		} catch(IOException e) {
			System.exit(1);
		}
	}
	
	public Object[][] serverPath(String path) {
		Object[][] ret;
		try {
			dos.writeInt(0);
			dos.flush();
			
			if(path == null) {
				path = "";
			}
			
			dos.writeUTF(path);
			dos.flush();
			
			int size = dis.readInt();
			if(size < 0) {
				return null;
			}
			
			ret = new Object[size][4];
			
			for(int i=0; i<size; i++) {
				ret[i][0] = dis.readUTF();
				ret[i][1] = dis.readLong();
				ret[i][2] = dis.readUTF();
				ret[i][3] = dis.readLong();
			}
		} catch (IOException e) {
			return null;
		}
		return ret;
	}
	
	public boolean serverAdd(String dir) {
		try {
			dos.writeInt(1);
			dos.flush();
			
			dos.writeUTF(dir);
			dos.flush();
			
			return dis.readBoolean();
		} catch(IOException e) {
			return false;
		}
	}
	
	public boolean serverDelete(String f) {
		try {
			dos.writeInt(2);
			dos.flush();
			
			dos.writeUTF(f);
			dos.flush();
			
			return dis.readBoolean();
		} catch(IOException e) {
			return false;
		}
	}
	
	public String[] serverRoot() {
		String[] ret = null;
		try {
			dos.writeInt(3);
			dos.flush();
			
			int len = dis.readInt();
			if(len <= 0) {
				return null;
			}
			
			ret = new String[len];
			for(int i=0; i<len; i++) {
				ret[i] = dis.readUTF();
			}
		} catch(IOException e) {
			return null;
		}
		
		return ret;
	}
	
	public void serverUp() {
		try {
			dos.writeInt(4);
			dos.flush();
		} catch(IOException e) {
			
		}
	}
	
	public boolean isDirectory(String path) {
		try {
			dos.writeInt(5);
			dos.flush();
			
			dos.writeUTF(path);
			dos.flush();
			
			return dis.readBoolean();
		} catch(IOException e) {
			return false;
		}
	}
	
	public String getParent(String path) {
		try {
			dos.writeInt(6);
			dos.flush();
			
			dos.writeUTF(path);
			dos.flush();
			String str = dis.readUTF();
			return str;
		} catch (IOException e) {
			return "";
		}	
	}
	
	public String getCorrectedPath(String path) {
		try {
			dos.writeInt(7);
			dos.flush();
			
			dos.writeUTF(path);
			dos.flush();
			
			String str = dis.readUTF();
			return str;
		} catch(IOException e) {
			return "";
		}
	}
	
	public void send(String path, String c_path, String s_path) {
		File f = new File(c_path+"/"+path);
		try {
			long total = 0;
			dos.writeInt(8);
			dos.flush();

			Socket f_socket = new Socket(ClientMain.addr, 54571);
			OutputStream os = f_socket.getOutputStream();
			
			dos.writeUTF(s_path+"/"+path);
			dos.writeLong(f.length());
			dos.flush();
			
			String corrected = dis.readUTF();

			gui.addLog("Sending file ("+f.getPath()+") to Server ("+corrected+") started.");
			gui.setProgressLabelText("Sending file to \""+corrected+"\"");
			
			fis = new FileInputStream(f);
			
			byte[] buffer = new byte[4096];
			int readBytes;
			
			while(!gui.thread.isInterrupted() && total < f.length()) {
				readBytes = fis.read(buffer);
				if(readBytes <= 0) {
					break;
				}
				os.write(buffer, 0, readBytes);
				os.flush();
				total += readBytes;
				gui.progressAdd(readBytes);
			}
			
			os.close();
			f_socket.close();
			fis.close();

			if(!gui.thread.isInterrupted()) {
				gui.addLog("Sending file ("+f.getPath()+") to Server ("+corrected+") ended.");
			} else {
				gui.addLog("Stop to Sending file ("+f.getPath()+") to Server ("+corrected+")");
			}
		} catch (IOException e) {
		}
	}
	
	public void receive(String path, String c_path, String s_path) {
		File f = new File(c_path+"/"+path);
		try {
			long total = 0;
			dos.writeInt(9);
			dos.flush();
			
			Socket f_socket = new Socket(ClientMain.addr, 54571);
			f_socket.setSoTimeout(500);
			InputStream is = f_socket.getInputStream();
			long time = System.currentTimeMillis();
			
			dos.writeUTF(s_path+"/"+path);
			dos.flush();
			
			long len = dis.readLong();

			String corrected = dis.readUTF();
			gui.addLog("Receiving file ("+f.getPath()+") from Server ("+corrected+") started.");
			gui.setProgressLabelText("Receiving file to \""+f.getPath()+"\"");
			
			fos = new FileOutputStream(f);
			
			byte[] buffer = new byte[4096];
			int readBytes;
			
			while(!gui.thread.isInterrupted() && total < len) {
				try {
					if(is.available() > 0) {
						if(total + 4096 >= len) {
							readBytes = is.read(buffer, 0, (int)(len-total));
						} else {
							readBytes = is.read(buffer);
						}
						
						if(readBytes > 0) {
							fos.write(buffer, 0, readBytes);
							total += readBytes;
							gui.progressAdd(readBytes);
							time = System.currentTimeMillis();
						}
					} else {
						if(System.currentTimeMillis() - time > 500) {
							fos.flush();
							fos.close();
							f.delete();
							break;
						}
					}
				} catch(IOException e) {
					if(System.currentTimeMillis() - time > 500) {
						fos.flush();
						fos.close();
						f.delete();
						break;
					}
				}
			}
			
			is.close();
			f_socket.close();
			
			fos.flush();
			fos.close();

			if(!gui.thread.isInterrupted()) {
				gui.addLog("Receiving file ("+f.getPath()+") from Server ("+corrected+") ended.");
			} else {
				gui.addLog("Stop to Receiving file ("+f.getPath()+") from Server ("+corrected+")");
			}
		} catch(IOException e) {
			
		}
	}
	
	public boolean isExist(String path) {
		try {
			dos.writeInt(10);
			dos.flush();
			
			dos.writeUTF(path);
			dos.flush();
			
			return dis.readBoolean();
		} catch(IOException e) {
			return false;
		}
	}
	
	public long totalSize(String path) {
		try {
			dos.writeInt(11);
			dos.flush();
			
			dos.writeUTF(path);
			dos.flush();
			
			return dis.readLong();
		} catch (IOException e) {
			return 0;
		}
	}
}
