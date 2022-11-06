import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

//abstract
abstract class Player {

	private String username;;
	private int UID;
	private double xPos, yPos;
	private int speed;
	private double xVel, yVel;
	private Rectangle boundary;
	
	public String getName() {
		return this.username;
	}
	
	public void setName(String name) {
		this.username = name;
	}

	public void setBounds(Rectangle bounds) {
		this.boundary = bounds;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(int s) {
		this.speed = s;
	}
	
	public double getXPos() {
		return this.xPos;
	}
	
	public void setXPos(double x) {
		this.xPos = x;
	}
	
	public double getYPos() {
		return this.yPos;
	}
	
	public void setYPos(double y) {
		this.yPos = y;
	}

	public double getXVel() {
		return this.xVel;
	}
	
	public void setXVel(double v) {
		this.xVel = v;
	}
	
	public double getYVel() {
		return this.yVel;
	}
	
	public void setYVel(double v) {
		this.yVel = v;
	}
	
	public Rectangle getBounds() {
		return this.boundary;
	}

	//public abstract void loadSprites();

	public void update(double x, double y) {
		this.xPos = x;
		this.yPos = y;
		boundary.x = (int) x;
		boundary.y = (int) y;
	}

	public void move(Map gameMap, double elapsedTime) {
		boolean xMoved = false;
		boolean yMoved = false;
		
		double x = xPos + (xVel*elapsedTime*100);
		double y = yPos + (yVel*elapsedTime*100);
	
		//screen boundaries
		if ((x>=0) && (x+boundary.width<=1280)) {
			xMoved = true;
		}
				
		if ((y>=0) && (y+boundary.height<=1024)) {
			yMoved = true;
		}
		
		//wall collision
		ArrayList<Tile> walls = gameMap.getWalls();
		Rectangle projectedX = new Rectangle((int)x,(int) yPos, 32, 32);
		Rectangle projectedY = new Rectangle((int)xPos,(int) y, 32, 32);
		
		for (Tile t:walls) {
			
			int tileX = t.xPosMap;
			int tileY = t.yPosMap;
			
			//y blocked
			if ((projectedY.intersectsLine(tileX, tileY, tileX+32, tileY)) 
					|| (projectedY.intersectsLine(tileX, tileY+32, tileX+32, tileY+32))) {
				yMoved = false;
			}
			
			//x blocked
			if ((projectedX.intersectsLine(tileX, tileY, tileX, tileY+32)) 
					|| (projectedX.intersectsLine(tileX+32, tileY, tileX+32, tileY+32))) {
				xMoved = false;
			}
		}
		
		//update x and y if moved
		if (xMoved) {
			this.xPos = x;
			this.boundary.x = (int) xPos;
		}
		
		if (yMoved) {
			this.yPos = y;
			this.boundary.y = (int) yPos;
		}
	}
	
	public abstract void draw(Graphics g);
}

class Rabbit extends Player {
	
	private boolean alive;
	private boolean inGame;

	public Rabbit(String name, double x, double y) {
		this.setName(name);
	
		this.setSpeed(1);
		
		//randomize x and y positions
		this.setXPos(x);
		this.setYPos(y);
		
		this.setBounds(new Rectangle((int)x,(int) y, 32, 32));
		this.alive = true;
		this.inGame = true;
	}
	
	public void draw(Graphics g) {
		
		if ((this.inGame) && (this.alive)) {
			g.setColor(Color.BLUE);
			g.drawRect((int)this.getXPos(), (int)this.getYPos(), 32, 32);
			
			g.setColor(Color.BLACK);
		    FontMetrics fm   = g.getFontMetrics(g.getFont());
		    Rectangle2D rect = fm.getStringBounds(this.getName(), g);
		    int textWidth  = (int)(rect.getWidth());
		    int textX = (int)this.getXPos() + (32 - textWidth)/2;
			
			g.drawString(this.getName(), textX, (int)this.getYPos()-10);
		}
	}

	public void move(Map gameMap, double elapsedTime) {
		boolean xMoved = false;
		boolean yMoved = false;
		
		double x = this.getXPos() + (this.getXVel()*elapsedTime*100);
		double y = this.getYPos() + (this.getYVel()*elapsedTime*100);
	
		//screen boundaries
		if ((x>=0) && (x+this.getBounds().width<=1280)) {
			xMoved = true;
		}
				
		if ((y>=0) && (y+this.getBounds().height<=1024)) {
			yMoved = true;
		}
		
		//wall collision
		ArrayList<Tile> walls = gameMap.getWalls();
		Rectangle projectedX = new Rectangle((int)x,(int) this.getYPos(), 32, 32);
		Rectangle projectedY = new Rectangle((int)this.getXPos(),(int) y, 32, 32);
		
		for (Tile t:walls) {
			
			int tileX = t.xPosMap;
			int tileY = t.yPosMap;
			
			//y blocked
			if ((projectedY.intersectsLine(tileX, tileY, tileX+32, tileY)) 
					|| (projectedY.intersectsLine(tileX, tileY+32, tileX+32, tileY+32))) {
				yMoved = false;
			}
			
			//x blocked
			if ((projectedX.intersectsLine(tileX, tileY, tileX, tileY+32)) 
					|| (projectedX.intersectsLine(tileX+32, tileY, tileX+32, tileY+32))) {
				xMoved = false;
			}
		}
		
		//check if exit gates
		for (Exit e:gameMap.getExits()) {
			if ((e.isDone()) && (projectedX.intersects(e.getBounds()))) {
				this.inGame = false;
			}
		}
		
		//update x and y if moved
		if (xMoved) {
			this.setXPos(x);
			this.getBounds().x = (int)x;
		}
		
		if (yMoved) {
			this.setYPos(y);
			this.getBounds().y = (int) y;
		}
	}
	
	public boolean stillAlive(Hunter h) {
		if (h==null) {
			return true;
		}
		
		if (this.getBounds().intersects(h.getBounds())) {
			this.alive = false;
		}
		return this.alive;
	}
}

class Hunter extends Player {

	public Hunter(String name, double x, double y) {
		this.setName(name);

		this.setSpeed(2);
		
		//randomize x and y positions
		this.setXPos(x);
		this.setYPos(y);
		
		this.setBounds(new Rectangle((int)x, (int) y, 32, 32));
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.RED);
		g.drawRect((int)this.getXPos(), (int)this.getYPos(), 32, 32);
		
		g.setColor(Color.BLACK);
	    FontMetrics fm   = g.getFontMetrics(g.getFont());
	    Rectangle2D rect = fm.getStringBounds(this.getName(), g);
	    int textWidth  = (int)(rect.getWidth());
	    int textX = (int)this.getXPos() + (32 - textWidth)/2;
		
		g.drawString(this.getName(), textX, (int)this.getYPos()-10);
	}
	
}
