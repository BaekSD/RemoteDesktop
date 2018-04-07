package client.screen.event;

import java.awt.Point;

public class Event {
	int type;
	Point p;
	int value;
	
	public Event(int type, Point p, int value) {
		this.type = type;
		this.p = p;
		this.value = value;
	}
	
	public Event(int type, int x, int y, int value) {
		this.type = type;
		p = new Point(x,y);
		this.value = value;
	}
	
	public int getType() {
		return type;
	}
	
	public Point getPoint() {
		return p;
	}
	
	public int getX() {
		return p.x;
	}
	
	public int getY() {
		return p.y;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setPoint(Point p) {
		this.p = p;
	}
	
	public void setLocation(int x, int y) {
		this.p.setLocation(x, y);
	}
	
	public void setX(int x) {
		this.p.setLocation(x, this.p.y);
	}
	
	public void setY(int y) {
		this.p.setLocation(this.p.x, y);
	}
	
	public void setValue(int v) {
		this.value = v;
	}
}
