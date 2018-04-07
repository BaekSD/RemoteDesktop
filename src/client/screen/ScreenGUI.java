package client.screen;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import client.ClientMain;
import client.screen.event.ClientKeyListener;
import client.screen.event.ClientMouseListener;
import client.screen.event.EventSendSocket;

public class ScreenGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public static double real_w = 0, real_h = 0;
	public static double modify_w = 0, modify_h = 0;
	private SubMenuGUI menu;
	public JComponent tmp;
	
	public int[][][] adjust;
	
	public ScreenGUI() {
		super("Remote Desktop Client");
	}
	
	public void startWindow(int width, int height) {
		real_w = width;
		real_h = height;
		
		if((int)(real_w/real_h) ==
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())) {
			// same ratio
			// if server is smaller than client -> maximum 80% size or smaller
			// else, 80% size
			if(real_w >= Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
				modify_w = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8;
				modify_h = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8;
			} else {
				if(real_w > Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8) {
					modify_w = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8;
					modify_h = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8;
				} else {
					modify_w = real_w;
					modify_h = real_h;
				}
			}
			
		} else if((int)(real_w/real_h) >
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/Toolkit.getDefaultToolkit().getScreenSize().getHeight())) {
			// server's width/height radio is bigger
			
			if(real_w >= Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
				modify_w = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8;
				modify_h = real_h * (modify_w/real_w);
			} else {
				if(real_w > Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8) {
					modify_w = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.8;
					modify_h = real_h * (modify_w/real_w);
				} else {
					modify_w = real_w;
					modify_h = real_h;
				}
			}
			
		} else {
			
			if(real_h >= Toolkit.getDefaultToolkit().getScreenSize().getHeight()) {
				modify_h = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8;
				modify_w = real_w * (modify_h/real_h);
			} else {
				if(real_h > Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8) {
					modify_h = Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.8;
					modify_w = real_w * (modify_h/real_h);
				} else {
					modify_w = real_w;
					modify_h = real_h;
				}
			}
			
		}
		
		this.setFocusTraversalKeysEnabled(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize((int)modify_w, (int)modify_h);
		//this.setLocation(30, 40);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		
		ClientMouseListener cml = new ClientMouseListener();
		
		this.addMouseListener(cml);
		this.addMouseMotionListener(cml);
		this.addMouseWheelListener(cml);
		this.addKeyListener(new ClientKeyListener());
		
		this.setVisible(true);
		this.setSize((int)modify_w, (int)(modify_h+this.getPreferredSize().getHeight()));
		
		ClientMain.ess = new EventSendSocket();
		ClientMain.ess.connect();
		
		tmp = new JComponent() {
			private static final long serialVersionUID = 1L;
		};
		tmp.setSize(1, 1);
		tmp.setLocation(this.getWidth(), 0);
		this.add(tmp);
		
		addSubMenu();
	}
	
	public void addSubMenu() {
		menu = new SubMenuGUI(this, false);
		
		menu.init(this.getWidth(), this.getHeight());
		menu.setLocation(this.getLocation().x+this.getWidth(), this.getLocation().y+(int)this.getPreferredSize().height);
		menu.setVisible(true);
	}
	
	public void paintImage(BufferedImage img, int x, int y, int xx, int yy) {
		BufferedImage newImg = new BufferedImage((int)(img.getWidth()*modify_w/real_w), (int)(img.getHeight()*modify_h/real_h), img.getType());
		Graphics2D g = newImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(img, 0, 0, (int)(img.getWidth()*modify_w/real_w), (int)(img.getHeight()*modify_h/real_h), null);
		g.dispose();
		
		this.getGraphics().drawImage(newImg, (int)(x*modify_w/real_w), (int)((y*modify_h/real_h)+this.getPreferredSize().getHeight()), newImg.getWidth(), newImg.getHeight()+1, this);
	}
}
