package main;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class RemoteDesktopMain {
	
	public static JFrame frame;
	private static RemoteDesktopGUI gui;
	
	public static void main(String[] args) {
		frame = new JFrame("Remote Desktop");
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				gui.disconnect();
				e.getWindow().dispose();
				System.exit(0);
			}
		});
		frame.setResizable(false);
		frame.setSize(500, 350);
		frame.setLocationRelativeTo(null);
		//frame.setLocation(30, 40);
		
		//(new FTGUI()).setVisible(true);
		gui = new RemoteDesktopGUI();
	}
	
	public static void toServerMenu() {
		gui.toServerMenu();
	}
}
