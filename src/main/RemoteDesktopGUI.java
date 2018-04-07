package main;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import main.clientGUI.ClientLoginGUI;
import server.ServerGUI;

public class RemoteDesktopGUI implements ActionListener {
	
	private Container container;
	private JLabel logo;
	private JButton serverBtn;
	private JButton clientBtn;
	private ServerGUI server;
	private ClientLoginGUI client;
	
	public RemoteDesktopGUI() {
		if(RemoteDesktopMain.frame != null) {
			container = RemoteDesktopMain.frame.getContentPane();
		}
		server = new ServerGUI(this);
		client = new ClientLoginGUI(this);
		init();
	}
	
	public void init() {
		container.setLayout(null);
		
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/logo.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(165*icon.getIconWidth()/icon.getIconHeight(), 165, Image.SCALE_SMOOTH));
		logo = new JLabel(icon);
		logo.setSize(RemoteDesktopMain.frame.getSize().width, 165);
		logo.setLocation(0, 20);
		logo.setHorizontalAlignment(JLabel.CENTER);
		
		RemoteDesktopMain.frame.add(logo);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/server.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		serverBtn = new JButton(icon);
		serverBtn.setSize(300, 50);
		serverBtn.setLocation(100, 200);
		serverBtn.addActionListener(this);
		
		RemoteDesktopMain.frame.add(serverBtn);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/client.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		clientBtn = new JButton(icon);
		clientBtn.setSize(300, 50);
		clientBtn.setLocation(100, 255);
		clientBtn.addActionListener(this);
		
		RemoteDesktopMain.frame.add(clientBtn);
		
		RemoteDesktopMain.frame.setVisible(true);
	}
	
	public void resume() {
		RemoteDesktopMain.frame.setContentPane(container);
		container.setVisible(true);
	}
	
	public void disconnect() {
		server.disconnect();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		container.setVisible(false);
		if(e.getSource() == serverBtn) {
			server.resume();
		} else if(e.getSource() == clientBtn) {
			client.resume();
		}
	}
	
	public void toServerMenu() {
		server = new ServerGUI(this);
		server.resume();
	}
}
