import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

class Map { 

	private final int TILESIZE = 32;
	int map[][]; //this is loaded from a file*/
	private Tile[][] worldMap;
	private ArrayList<GameObject> objects = new ArrayList<>();
	private ArrayList<Exit> exits = new ArrayList<>();
	private ArrayList<Tile> walls = new ArrayList<>();

	public Map() {
		map = loadMapData("map.txt");
		createWorldMap();
	}

	public Map(Player p) { 

		map = loadMapData("map.txt");   
		createWorldMap();
	}

	public int[][] loadMapData(String filename) { 
		int data[][]=null;
		try {                                 

			File f = new File(filename);
			Scanner input = new Scanner(f);   
			data = new int[input.nextInt()][input.nextInt()]; // first two line are map size

			for (int j=0;j<data.length;j++){
				for (int i=0;i<data[0].length;i++) {    
					data[j][i]=input.nextInt();
				}
			}
			input.close();    

		} catch(Exception E){
			System.out.println("map data loading failed");
		}
		return data;
	}


	//Creates a 2D array of tiles from the int array
	public void createWorldMap() { 
		worldMap = new Tile[map.length][map[1].length];

		for (int j=0;j<worldMap.length;j++) {
			for (int i=0;i<worldMap[1].length;i++) {
				if (map[j][i]==0) { //floor
					worldMap[j][i] = new FloorTile(i*TILESIZE, j*TILESIZE);
					
				} else if (map[j][i]==1){ //walls
					worldMap[j][i] = new WallTile(i*TILESIZE, j*TILESIZE);
					walls.add(worldMap[j][i]);
					
				} else if (map[j][i]==2) { //clues
					worldMap[j][i] = new FloorTile (i*TILESIZE, j*TILESIZE);
					objects.add(new Clue(i*TILESIZE, j*TILESIZE));
					walls.add(worldMap[j][i]);
					
				} else if (map[j][i]==3) { //exits
					worldMap[j][i] = new FloorTile (i*TILESIZE, j*TILESIZE);
					Exit exit = new Exit(i*TILESIZE, j*TILESIZE);
					objects.add(exit);
					exits.add(exit);
					walls.add(worldMap[j][i]);
					
				}
			}
		}
	}
	
	public ArrayList<Tile> getWalls() {
		return this.walls;
	}

	public ArrayList<GameObject> getObjects() {
		return this.objects;
	}
	
	public ArrayList<Exit> getExits() {
		return this.exits;
	}
	
	public void update(double elapsedTime) {
		
		int done = 0;
		for (GameObject obj:objects) {
			obj.update(elapsedTime);
			
			if ((obj.getClass().getName().equals("Clue")) && (obj.isDone())) {
				done++;
			}
		}
		
		if (done == 5) {
			for (Exit e:exits) {
				e.open();
			}
			
		}
	}
	
	public void draw(Graphics g) {
		for (int j=0;j<1024;j++) {
			for (int i=0;i<1280;i++) {

				if ((i>=0 && i<map[0].length) && (j>=0 && j<map.length)) {
					
					worldMap[j][i].draw(g, i, j);

				}

			}
		}
		for (GameObject obj:objects) {
			obj.draw(g);
		}
	}

}