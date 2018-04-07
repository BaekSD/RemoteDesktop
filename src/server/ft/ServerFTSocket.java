package server.ft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFTSocket extends Thread {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private FileInputStream fis;
	private FileOutputStream fos;
	
	private ServerSocket f_serverSocket;
	private Socket f_socket;
	
	public ServerFTSocket(ServerSocket serverSocket, ServerSocket f_serverSocket) {
		this.serverSocket = serverSocket;
		this.f_serverSocket = f_serverSocket;
	}
	
	public void run() {
		connect();
		while(!isInterrupted()) {
			receiveReq();
		}
	}
	
	public void connect() {
		try {
			socket = serverSocket.accept();
			//socket.setSoTimeout(100);
			f_serverSocket.setSoTimeout(500);
			
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
		}
	}
	
	public void disconnect() {
		try {
			if(dis != null) {
				dis.close();
			}
			if(dos != null) {
				dos.close();
			}
			if(socket != null) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}
	
	public void receiveReq() {
		int type = -1;
		try {
			type = dis.readInt();
		} catch (IOException e) {
			return;
		}
		
		switch(type) {
		case -1:
			return;
		case 0: case 4:
			list();
			break;
		case 1:
			add();
			break;
		case 2:
			delete();
			break;
		case 3:
			listRoots();
			break;
		case 5:
			isDirectory();
			break;
		case 6:
			getParent();
			break;
		case 7:
			getCorrectedPath();
			break;
		case 8:
			receive();
			break;
		case 9:
			send();
			break;
		case 10:
			isExist();
			break;
		case 11:
			totalSize();
			break;
		default:
			return;
		}
	}
	
	public void list() {
		String path = null;
		File f;
		File[] files;
		try {
			path = dis.readUTF();
		} catch(IOException e) {
			try {
				dos.writeInt(-1);
				dos.flush();
				return;
			} catch (IOException e1) {
			}
		}
		
		if(path == null || path.equals("")) {
			files = File.listRoots();
		} else {
			f = new File(path);
			files = f.listFiles();
		}
		
		try {
			if(files == null) {
				dos.writeInt(-1);
				dos.flush();
				return;
			} else {
				dos.writeInt(files.length);
				dos.flush();
			}
		} catch (IOException e1) {
		}
		
		for(int i=0; i<files.length; i++) {
			try {
				if(files[i].getName().equals("")) {
					dos.writeUTF(files[i].getPath());
				} else {
					dos.writeUTF(files[i].getName());
				}
				dos.writeLong(files[i].length());
				if(files[i].isDirectory()) {
					dos.writeUTF("Directory");
				} else if(files[i].isFile()) {
					dos.writeUTF("File");
				} else {
					dos.writeUTF("");
				}
				dos.writeLong(files[i].lastModified());
				dos.flush();
			} catch (IOException e) {
			}
			
		}
	}
	
	public void listRoots() {
		File root[] = File.listRoots();
		
		String str[] = new String[root.length];
		
		for(int i=0; i<root.length; i++) {
			str[i] = root[i].getAbsolutePath();
		}
		
		try {
			dos.writeInt(str.length);
			for(int i=0; i<str.length; i++) {
				dos.writeUTF(str[i]);
			}
			dos.flush();
		} catch(IOException e) {
			try {
				dos.writeUTF("");
				dos.flush();
			} catch (IOException e1) {
			}
		}
	}
	
	public void add() {
		String dir = null;
		File f = null;
		try {
			dir = dis.readUTF();
			f = new File(dir);
			if(f.exists()) {
				dos.writeBoolean(false);
				dos.flush();
			} else {
				f.mkdirs();
				dos.writeBoolean(true);
				dos.flush();
			}
		} catch(IOException e) {
			try {
				dos.writeBoolean(false);
				dos.flush();
			} catch (IOException e1) {
			}
		}
	}
	
	public void delete() {
		String str = null;
		
		try {
			str = dis.readUTF();
			dos.writeBoolean(deleteRecursive(str));
		} catch(IOException e) {
			try {
				dos.writeBoolean(false);
				dos.flush();
			} catch (IOException e1) {
			}
		}
	}
	
	public boolean deleteRecursive(String str) {
		File f = new File(str);
		
		if(f.isDirectory()) {
			String[] child = f.list();
			
			for(int i=0; i<child.length; i++) {
				deleteRecursive(str+"/"+child[i]);
			}
		}
		return f.delete();
	}
	
	public void isDirectory() {
		File f = null;
		
		try {
			String str = dis.readUTF();
			f = new File(str);
			
			dos.writeBoolean(f.isDirectory());
			dos.flush();
		} catch(IOException e) {
			try {
				dos.writeBoolean(false);
				dos.flush();
			} catch (IOException e1) {
			}
		}
	}
	
	public void getParent() {
		File f = null;
		try {
			String str = dis.readUTF();
			f = new File(str);
			
			if(f.getParent() == null) {
				dos.writeUTF("");
			} else {
				dos.writeUTF(f.getParent());
			}
			dos.flush();
		} catch (IOException e) {
			try {
				dos.writeUTF("");
				dos.flush();
			} catch (IOException e1) {
			}
		}
	}
	
	public void getCorrectedPath() {
		File f = null;
		try {
			String str = dis.readUTF();
			f = new File(str);
			
			if(f.getPath() == null) {
				dos.writeUTF("");
			} else {
				dos.writeUTF(f.getPath());
			}
			dos.flush();
		} catch (IOException e) {
			try {
				dos.writeUTF("");
				dos.flush();
			} catch (IOException e1) {
			}
		}
	}
	
	public void receive() {
		try {
			f_socket = f_serverSocket.accept();
			f_socket.setSoTimeout(500);
			InputStream is = f_socket.getInputStream();
			long time = System.currentTimeMillis();
			
			long total=0;
			String path = dis.readUTF();
			long len = dis.readLong();
			File f = new File(path);
			if(f.exists()) {
				f.delete();
			}
			
			dos.writeUTF(f.getPath());
			dos.flush();
			
			fos = new FileOutputStream(f);
			
			byte[] buffer = new byte[4096];
			int readBytes;
			
			while(total < len) {
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
		} catch (IOException e) {
		}
	}
	
	public void send() {
		File f;
		try {
			f_socket = f_serverSocket.accept();
			f_socket.setSoTimeout(500);
			OutputStream os = f_socket.getOutputStream();
			
			long total = 0;
			String path = dis.readUTF();
			f = new File(path);

			dos.writeLong(f.length());
			dos.writeUTF(f.getPath());
			dos.flush();
			
			fis = new FileInputStream(f);
			
			byte[] buffer = new byte[4096];
			int readBytes;
			
			while(total < f.length()) {
				try {
					readBytes = fis.read(buffer);
					if(readBytes <= 0) {
						break;
					}
					os.write(buffer, 0, readBytes);
					os.flush();
					total += readBytes;
					
				} catch(IOException e) {
					break;
				}
			}
			
			os.close();
			f_socket.close();
			dos.flush();
			fis.close();
		} catch(IOException e) {
			
		}
	}
	
	public void isExist() {
		File f = null;
		try {
			String str = dis.readUTF();
			f = new File(str);
			
			dos.writeBoolean(f.exists());
			dos.flush();
		} catch (IOException e) {
		}
	}
	
	public void totalSize() {
		long size = 0;
		File f = null;
		File[] files = null;
		try {
			String str = dis.readUTF();
			f = new File(str);
			
			if(f.isDirectory()) {
				files = f.listFiles();
				for(int i=0; i<files.length; i++) {
					size += sizeRecursive(files[i].getPath());
				}
				dos.writeLong(size);
			} else {
				dos.writeLong(f.length());
			}
			dos.flush();
		} catch (IOException e) {
		}
	}
	
	public long sizeRecursive(String path) {
		File f = new File(path);
		File[] files = null;
		long size = 0;
		
		if(f.isDirectory()) {
			files = f.listFiles();
			for(int i=0; i<files.length; i++) {
				size += sizeRecursive(files[i].getPath());
			}
			return size;
		} else {
			return f.length();
		}
	}
}
