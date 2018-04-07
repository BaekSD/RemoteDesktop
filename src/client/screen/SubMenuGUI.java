package client.screen;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import client.ClientMain;
import client.chat.ClientChatSocket;
import client.ft.ClientFTSocket;

public class SubMenuGUI extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Container container;
	
	private JButton chat, ft, exit;
	
	public SubMenuGUI() {
		super();
	}
	
	public SubMenuGUI(Frame owner, boolean modal) {
		super(owner, modal);
	}
	
	public SubMenuGUI(Dialog owner, boolean modal) {
		super(owner, modal);
	}
	
	public void init(int width, int height) {
		this.container = new Container();
		
		this.setContentPane(container);
		this.setUndecorated(true);
		this.setSize(width/18, width*3/18);
		this.setBackground(new Color(0,0,0,200));
		this.setResizable(false);
		this.setLocationRelativeTo(ClientMain.srs.gui.tmp);
		
		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/chat.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(this.getWidth()*7/10, (this.getWidth()*7/10)*icon.getIconHeight()/icon.getIconWidth(), Image.SCALE_SMOOTH));
		chat = new JButton(icon);
		chat.setSize(this.getWidth(),this.getWidth());
		chat.setLocation(0, 0);
		chat.addActionListener(this);
		this.add(chat);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/ft.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(this.getWidth()*7/10, (this.getWidth()*7/10)*icon.getIconHeight()/icon.getIconWidth(), Image.SCALE_SMOOTH));
		ft = new JButton(icon);
		ft.setSize(this.getWidth(),this.getWidth());
		ft.setLocation(0, this.getWidth());
		ft.addActionListener(this);
		this.add(ft);
		
		icon = new ImageIcon(getClass().getClassLoader().getResource("img/exit.png"));
		icon = new ImageIcon(icon.getImage().getScaledInstance(this.getWidth()*7/10, (this.getWidth()*7/10)*icon.getIconHeight()/icon.getIconWidth(), Image.SCALE_SMOOTH));
		exit = new JButton(icon);
		exit.setSize(this.getWidth(),this.getWidth());
		exit.setLocation(0, this.getWidth()*2);
		exit.addActionListener(this);
		this.add(exit);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(chat)) {
			if(ClientChatSocket.gui != null) {
				ClientChatSocket.gui.setVisible(true);
			}
		} else if(e.getSource().equals(ft)) {
			if(ClientFTSocket.gui != null) {
				ClientFTSocket.gui.setVisible(true);
			}
		} else if(e.getSource().equals(exit)) {
			System.exit(0);
		}
	}
}
