import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

//This class represents a single tile on the map
class Tile { 
	
	int ID;
	int xPosMap, yPosMap;
	final int width = 32;
	Color colour;

	public Tile(Color c, int x, int y, int id) { 
		colour=c;
		xPosMap=x;
		yPosMap=y;  
		ID = id;
	}

	public void draw (Graphics g, int xScreen, int yScreen) { 
		g.setColor(colour);
		g.fillRect(xScreen*width, yScreen*width, width, width);  //-1 to see edges
	}
	
	//check intersecting rectangles for collisions
	public Rectangle getBounds() {
		return new Rectangle(xPosMap, yPosMap, width, width);
	}

}

class FloorTile extends Tile {
	
	public FloorTile(int x, int y) {
		super(Color.WHITE, x, y, 1);
	}
}

class WallTile extends Tile {
	
	public WallTile(int x, int y) {
		super(Color.DARK_GRAY, x, y, 2);
	}
}

class ObjectTile extends Tile {
	
	public ObjectTile(int x, int y) {
		super (Color.BLACK, x, y, 3);
	}
}



