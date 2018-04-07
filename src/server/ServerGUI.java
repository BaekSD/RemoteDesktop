package server;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import main.RemoteDesktopGUI;
import main.RemoteDesktopMain;
import server.auth.ServerAuthChecker;

public class ServerGUI implements ActionListener {
	
	private RemoteDesktopGUI prev;
	private Container container;
	private JLabel address, clntCnt, code, time;
	private JButton toPrev, chat;
	private ConnectCheckTimer timer;
	private String authCode;
	private Timer jobScheduler;
	private ServerMain server;
	
	public class ConnectCheckTimer extends TimerTask {
		int count = 0;
		public void run() {
			if(count == 0) {
				count = 180;
				Random random = new Random(System.currentTimeMillis());
				authCode = "";
				for(int i=0; i<6; i++) {
					if(random.nextFloat() > 0.5) {
						authCode += random.nextInt(10);
					} else {
						int ran = random.nextInt(26);
						char c = (char)(((int)'A')+ran);
						authCode += c;
					}
				}
				code.setText("Auth Code : "+authCode);
				ServerAuthChecker.setCode(authCode);
			} else count--;
			time.setText("Expire Count : "+count/60+"m "+count%60+"s");
			clntCnt.setText("Connected Client : "+ServerMain.count);

			if(ServerAuthChecker.check()) {
				ServerMain.count++;
				server.connectServer();
			}
		}
	}
	
	public ServerGUI(RemoteDesktopGUI prev) {
		this.prev = prev;
		container = new Container();
		init();
	}
	
	public void setTimer() {
		jobScheduler = new Timer(true);
		timer = new ConnectCheckTimer();
		jobScheduler.scheduleAtFixedRate(timer, 0, 1000);
	}
	
	public void cancelTimer() {
		if(jobScheduler != null) {
			jobScheduler.cancel();
		}
		if(timer != null) {
			timer.cancel();
		}
	}
	
	public void init() {
		RemoteDesktopMain.frame.setContentPane(container);
		
		InputStream is = RemoteDesktopGUI.class.getResourceAsStream("NanumBarunpenB.ttf");
		Font font = new Font("바탕", Font.BOLD, 25);
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, is);
		} catch (FontFormatException | IOException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		
		font = font.deriveFont(25);
		
		address = new JLabel("Fail to get Address");
		try {
			address.setText("Adress : "+InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			return;
		}
		address.setSize(300, 50);
		address.setLocation(100, 30);
		address.setFont(font);
		address.setHorizontalAlignment(JLabel.CENTER);
		RemoteDesktopMain.frame.add(address);
		
		clntCnt = new JLabel("Connected client : 0");
		clntCnt.setSize(300, 50);
		clntCnt.setLocation(100, 75);
		clntCnt.setFont(font);
		clntCnt.setHorizontalAlignment(JLabel.CENTER);
		RemoteDesktopMain.frame.add(clntCnt);
		
		code = new JLabel("");
		code.setSize(300, 50);
		code.setLocation(100, 120);
		code.setFont(font);
		code.setHorizontalAlignment(JLabel.CENTER);
		RemoteDesktopMain.frame.add(code);
		
		time = new JLabel("");
		time.setSize(300,50);
		time.setLocation(100, 165);
		time.setFont(font);
		time.setHorizontalAlignment(JLabel.CENTER);
		RemoteDesktopMain.frame.add(time);
		
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/back.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		toPrev = new JButton(icon);
		toPrev.setSize(140, 50);
		toPrev.setLocation(260, 230);
		toPrev.addActionListener(this);
		RemoteDesktopMain.frame.add(toPrev);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/chat_str.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		chat = new JButton(icon);
		chat.setSize(140, 50);
		chat.setLocation(100, 230);
		chat.addActionListener(this);
		RemoteDesktopMain.frame.add(chat);
		
		toMain();
	}
	
	public void resume() {
		RemoteDesktopMain.frame.setContentPane(container);

		address = new JLabel("Fail to get Address");
		try {
			address.setText("Adress : "+InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			return;
		}
		
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/back.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		toPrev.setIcon(icon);
		
		container.setVisible(true);
		setTimer();
		
		server = new ServerMain();
	}
	
	public void toMain() {
		prev.resume();
	}
	
	public void disconnect() {
		if(server != null) {
			server.disconnect();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == toPrev) {
			container.setVisible(false);
			cancelTimer();
			disconnect();
			toMain();
		} else if(e.getSource() == chat) {
			server.showChatGUI();
		}
	}
}
