import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


abstract class GameObject implements Interactable {
	
	private int x, y;
	private Rectangle boundary;
	
	public int getX() {
		return this.x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public Rectangle getBounds() {
		return this.boundary;
	}
	
	public void setBounds(Rectangle b) {
		this.boundary = b;
	}
	
	public abstract void update(double elapsedTime);
	
	public abstract void draw (Graphics g);
	
	public abstract void interact(Player p);
	
	public abstract void interact(String player);
	
	public abstract int getProgress();
	
	public abstract void setProgress(int progress);
	
	public abstract boolean isDone();
}

class Clue extends GameObject implements Interactable {

	private boolean inUse;
	private boolean done;
	private int progress;
	
	public Clue(int x, int y) {
		this.setX(x);
		this.setY(y);
		this.setBounds(new Rectangle(x, y, 32, 32));
		this.setInUse(false);
		this.progress = 0;
		this.done = false;
	}
	
	public void update(double elapsedTime) {
		if ((this.isInUse()) && (progress<100000)) {
			progress+=elapsedTime*1000;
		}
		
		if (progress==100000) {
			done = true;
		}
	}
	
	public void interact(Player p) {
		
		if (this.done) {
			return;
		}
		
		if (p.getClass().getName().equals("Rabbit")) {
			setInUse(true);
			
		} else {
			setInUse(false);
		}
	}
	
	
	public void interact(String player) {
		
		if (this.done) {
			return;
		}
		
		if (player.equals("rabbit")) {
			setInUse(true);
			
		} else {
			setInUse(false);
		}
	}
	public void draw(Graphics g) {
		g.setColor(this.done?Color.GREEN:Color.ORANGE);
		g.fillRect(this.getX(), this.getY(), 32, 32);
		
		g.setColor(Color.BLACK);
		g.drawString(""+this.progress/1000+"/100", this.getX(), this.getY()-10);
	}

	@Override
	public int getProgress() {
		return this.progress;
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public boolean isDone() {
		return this.done;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
}

class Exit extends Clue implements Interactable {

	private boolean open;
	
	public Exit(int x, int y) {
		super(x, y);
		this.open = false;
	}
	
	public void open() {
		this.open = true;
	}

	@Override
	public void draw(Graphics g) {
		if (this.isDone()) {
			return;
		}
		g.setColor(Color.CYAN);
		g.fillRect(this.getX(), this.getY(), 32, 32);
		g.drawString(""+this.getProgress()/1000+"/100", this.getX(), this.getY()-10);
	}

	@Override
	public void interact(Player p) {
		if ((this.isDone()) || (!this.open)) {
			return;
		}
		
		if (p.getClass().getName().equals("Rabbit")) {
			this.setInUse(true);
			
		} else {
			this.setInUse(false);
		}
	}

	@Override
	public void interact(String player) {
		if ((this.isDone()) || (!this.open)) {
			return;
		}
		
		if (player.equals("rabbit")) {
			this.setInUse(true);
			
		} else {
			this.setInUse(false);
		}
		
	}
	
}
