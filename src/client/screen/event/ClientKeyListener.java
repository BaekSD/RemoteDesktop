package client.screen.event;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import client.ClientMain;

public class ClientKeyListener implements KeyListener {
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 157 && System.getProperty("os.name").toLowerCase().indexOf("win")>=0) {
			e.setKeyCode(KeyEvent.VK_WINDOWS);
		}
		ClientMain.ess.send(new Event(2, new Point(0, 0), e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == 157) {
			e.setKeyCode(KeyEvent.VK_WINDOWS);
		}
		ClientMain.ess.send(new Event(3, new Point(0, 0), e.getKeyCode()));
	}
}
