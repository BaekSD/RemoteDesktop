package main.clientGUI;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import client.ClientMain;
import client.auth.ClientAuthChecker;
import main.RemoteDesktopGUI;
import main.RemoteDesktopMain;

public class ClientLoginGUI implements ActionListener {
	
	private RemoteDesktopGUI prev;
	private Container container;
	
	private JLabel addressLabel, codeLabel;
	private JTextField addressField, codeField;
	private JButton connect, toPrev;
	
	private boolean trying = false;
	
	private ConnectionChecker cc = new ConnectionChecker();
	private Timer jobScheduler;
	
	public class ConnectionChecker extends TimerTask {
		public void run() {
			int[] wh = new int[2];
			if(!ClientAuthChecker.check(addressField.getText(), codeField.getText(), wh)) {
				cancelTimer();
				//container.setVisible(false);
				error_msg();
				trying = false;
				return;
			} else {
				cancelTimer();
				container.setVisible(false);
				RemoteDesktopMain.frame.setVisible(false);
				ClientMain cm = new ClientMain(addressField.getText(), wh[0], wh[1]);
				cm.startClient();
				addressField.setText("");
				codeField.setText("");
				trying = false;
			}
		}		
	}
	
	public ClientLoginGUI(RemoteDesktopGUI prev) {
		this.prev = prev;
		container = new Container();
		//cem = new ConnectionErrorMessage(this);
		init();
	}
	
	public void setTimer() {
		jobScheduler = new Timer(true);
		cc = new ConnectionChecker();
		jobScheduler.scheduleAtFixedRate(cc, 0, 100);
	}
	
	public void cancelTimer() {
		if(jobScheduler != null) {
			jobScheduler.cancel();
		}
		if(cc != null) {
			cc.cancel();
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
		
		addressLabel = new JLabel("Server Address");
		addressLabel.setSize(200, 50);
		addressLabel.setLocation(45, 60);
		addressLabel.setFont(font);
		addressLabel.setHorizontalAlignment(JLabel.CENTER);
		RemoteDesktopMain.frame.add(addressLabel);
		
		addressField = new JTextField();
		addressField.setFont(font);
		addressField.setSize(200, 50);
		addressField.setLocation(255, 60);
		RemoteDesktopMain.frame.add(addressField);
		
		codeLabel = new JLabel("Auth Code");
		codeLabel.setSize(200, 50);
		codeLabel.setLocation(45, 130);
		codeLabel.setFont(font);
		codeLabel.setHorizontalAlignment(JLabel.CENTER);
		RemoteDesktopMain.frame.add(codeLabel);
		
		codeField = new JTextField();
		codeField.setFont(font);
		codeField.setSize(200, 50);
		codeField.setLocation(255, 130);
		RemoteDesktopMain.frame.add(codeField);
		
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/back.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		toPrev = new JButton(icon);
		toPrev.setSize(200, 50);
		toPrev.setLocation(255, 210);
		toPrev.addActionListener(this);
		RemoteDesktopMain.frame.add(toPrev);
		
		ImageIcon icon2 = new ImageIcon(getClass().getClassLoader().getResource("img/connect.png"));
		icon2 = new ImageIcon(icon2.getImage().getScaledInstance(40*icon2.getIconWidth()/icon2.getIconHeight(), 40, Image.SCALE_SMOOTH));
		connect = new JButton(icon2);
		connect.setSize(200, 50);
		connect.setLocation(45, 210);
		connect.addActionListener(this);
		RemoteDesktopMain.frame.add(connect);
		
		toMain();
	}
	
	public void resume() {
		RemoteDesktopMain.frame.setContentPane(container);
		RemoteDesktopMain.frame.setVisible(true);
		container.setVisible(true);
	}
	
	public void toMain() {
		prev.resume();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == toPrev) {
			container.setVisible(false);
			addressField.setText("");
			codeField.setText("");
			toMain();
		} else if(e.getSource() == connect) {
			if(trying) {
				return;
			}
			trying = true;
			
			setTimer();
		}
	}
	
	public void error_msg() {
		JOptionPane.showMessageDialog(RemoteDesktopMain.frame, "Connection Fail", "Connection Fail", JOptionPane.ERROR_MESSAGE);
	}
}
