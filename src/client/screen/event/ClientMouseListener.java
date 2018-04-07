package client.screen.event;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import client.ClientMain;
import client.screen.ScreenGUI;

public class ClientMouseListener implements MouseMotionListener, MouseListener, MouseWheelListener {
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int realX, realY;
		int clkX = e.getX(), clkY = (int)(e.getY() - ClientMain.srs.gui.getPreferredSize().getHeight());

		realX = (int)(clkX * ScreenGUI.real_w / ScreenGUI.modify_w);
		realY = (int)(clkY * ScreenGUI.real_h / ScreenGUI.modify_h);
		
		//realX = ClientSocketMain.width*clkX/ClientMain.cw.getWidth();
		//realY = (ClientSocketMain.height - (int)ClientMain.cw.getPreferredSize().getHeight())*clkY/ClientMain.cw.getHeight();

		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			ClientMain.ess.send(new Event(0, new Point(realX, realY), 1));
			break;
		case MouseEvent.BUTTON2:
			ClientMain.ess.send(new Event(0, new Point(realX, realY), 2));
			break;
		case MouseEvent.BUTTON3:
			ClientMain.ess.send(new Event(0, new Point(realX, realY), 3));
			break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int realX, realY;
		int clkX = e.getX(), clkY = (int)(e.getY() - ClientMain.srs.gui.getPreferredSize().getHeight());

		realX = (int)(clkX * ScreenGUI.real_w / ScreenGUI.modify_w);
		realY = (int)(clkY * ScreenGUI.real_h / ScreenGUI.modify_h);
		
		//realX = ClientSocketMain.width*clkX/ClientMain.cw.getWidth();
		//realY = (ClientSocketMain.height - (int)ClientMain.cw.getPreferredSize().getHeight())*clkY/ClientMain.cw.getHeight();
		
		switch(e.getButton()) {
		case MouseEvent.BUTTON1:
			ClientMain.ess.send(new Event(0, new Point(realX, realY), 4));
			break;
		case MouseEvent.BUTTON2:
			ClientMain.ess.send(new Event(0, new Point(realX, realY), 5));
			break;
		case MouseEvent.BUTTON3:
			ClientMain.ess.send(new Event(0, new Point(realX, realY), 6));
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int realX, realY;
		int clkX = e.getX(), clkY = (int)(e.getY() - ClientMain.srs.gui.getPreferredSize().getHeight());

		realX = (int)(clkX * ScreenGUI.real_w / ScreenGUI.modify_w);
		realY = (int)(clkY * ScreenGUI.real_h / ScreenGUI.modify_h);
		
		//realX = ClientSocketMain.width*clkX/ClientMain.cw.getWidth();
		//realY = (ClientSocketMain.height - (int)ClientMain.cw.getPreferredSize().getHeight())*clkY/ClientMain.cw.getHeight();
		
		ClientMain.ess.send(new Event(0, new Point(realX, realY), 7));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int realX, realY;
		int clkX = e.getX(), clkY = (int)(e.getY() - ClientMain.srs.gui.getPreferredSize().getHeight());

		realX = (int)(clkX * ScreenGUI.real_w / ScreenGUI.modify_w);
		realY = (int)(clkY * ScreenGUI.real_h / ScreenGUI.modify_h);
		
		//realX = ClientSocketMain.width*clkX/ClientMain.cw.getWidth();
		//realY = (ClientSocketMain.height - (int)ClientMain.cw.getPreferredSize().getHeight())*clkY/ClientMain.cw.getHeight();
		
		ClientMain.ess.send(new Event(0, new Point(realX, realY), 7));
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		ClientMain.ess.send(new Event(1, new Point(0,0), e.getWheelRotation()));
	}
}
