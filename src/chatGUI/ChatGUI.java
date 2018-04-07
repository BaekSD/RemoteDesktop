package chatGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import client.ClientMain;
import server.ServerMain;

public class ChatGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JScrollPane text_s, input_s;
	private JTextArea text, input;
	private JButton button;
	private Container container;
	private boolean isServer;

	public ChatGUI(boolean isServer) {
		super("Chatting");
		this.isServer = isServer;
		init();
	}
	
	public void init() {
		//this.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(5,5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setBackground(new Color(0,0,0,128));
		
		this.setContentPane(panel);
		this.setSize(350, 480);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		text = new JTextArea();
		text.setLineWrap(true);
		text.setEditable(false);
		text_s = new JScrollPane(text);
		text_s.setSize(300, 440);
		text_s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		input = new JTextArea();
		input.setLineWrap(true);
		input.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					sendText(input.getText().substring(0, input.getText().length()-1));
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		input_s = new JScrollPane(input);
		input_s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("img/send.png"));
		//ImageIcon icon = new ImageIcon("img/send.png");
		icon = new ImageIcon(icon.getImage().getScaledInstance(40*icon.getIconWidth()/icon.getIconHeight(), 40, Image.SCALE_SMOOTH));
		button = new JButton(icon);
		button.setSize(icon.getIconWidth(), icon.getIconHeight());
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendText(input.getText().substring(0, input.getText().length()-1));
			}
		});
		
		input_s.setSize(this.getWidth()-button.getWidth(), button.getHeight());
		
		container = new Container();
		container.setLayout(new BorderLayout(5,5));
		container.setSize(300, button.getHeight());
		container.add(input_s, "Center");
		container.add(button, "East");
		
		this.add(text_s, "Center");
		this.add(container, "South");
	}
	
	public void addText(String name, String t, String str) {
		if(!text.getText().equals("")) {
			text.append("\n");
		}
		
		String add = t + " " + name + ": " + str;
		
		text.append(add);
		
		text.setCaretPosition(text.getDocument().getLength());
	}
	
	public void sendText(String str) {
		if(!str.equals("")) {
			if(isServer) {
				ServerMain.sendMsg(str);
			} else {
				ClientMain.ccs.sendMsg(str);
			}
		}
		input.setText("");
	}
}
