import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

class GameClient {

	private Socket mySocket;
	private BufferedReader input;
	private PrintWriter output;
	private GameWindow game;
	private Player player;
	private String username;
	private boolean running = true;
	private ArrayList<Player> otherPlayers = new ArrayList<>();
	private Map gameMap;

	private State gameState = State.MAIN_MENU;

	public static void main(String[] args) {
		new GameClient().go();
	}

	public void go() {
		connect("localhost", 5000);
		//send messages to server
		
		Thread listener = new Thread(new ServerListener());
		listener.start();
		game = new GameWindow(this);
		
	}

	public Socket connect (String ip, int port) {

		try {
			mySocket = new Socket(ip, port);

			InputStreamReader stream1 = new InputStreamReader(mySocket.getInputStream());
			input = new BufferedReader(stream1);
			output = new PrintWriter(mySocket.getOutputStream());

		} catch (IOException e) { //failed to connect
			System.out.println("Failed to connect to server, quiting");
			System.exit(-1);
			e.printStackTrace();
		}

		System.out.println("Connected to server");
		return mySocket;
	}

	public void setUser(String name) {
		this.username = name;
	}
	
	public void initGame(Player p) {
		player = p;
		gameMap = new Map(p);

	}
	
	public synchronized void updatePlayer(double elapsedTime) {

		
		if (player.getClass().getName().equals("Rabbit")) {
			
			Hunter hunter = null;
			
			for(Player p:otherPlayers) {
				if (p.getClass().getName().equals("Hunter")) {
					hunter = (Hunter)p;
					break;
				}
			}
			
			if (!((Rabbit)player).stillAlive(hunter)) {
				//send status to server - game over lose
				
				sendMsgToServer("<dead>");
			} else {
				player.move(gameMap, elapsedTime);
				sendMsgToServer("<m><"+player.getXPos()+"><"+player.getYPos()+">");
			}

		} else {
			player.move(gameMap, elapsedTime);
			sendMsgToServer("<m><"+player.getXPos()+"><"+player.getYPos()+">");
		}

	
	}

	public synchronized void sendMsgToServer(String msg) {
		output.println(msg);
		output.flush();
	}

	public Player getPlayer() {
		return this.player;
	}

	public Map getMap() {
		return this.gameMap;
	}
	
	public State getState() {
		return this.gameState;
	}
	
	public void changeState(State st) {
		this.gameState = st;
	}
	
	public ArrayList<Player> getOtherPlayers() {
		return this.otherPlayers;
	}
	
	class ServerListener implements Runnable {

		public synchronized void run() {
			while (running) {
				try {
					if (input.ready()) {

						String msg = input.readLine();

						ClientMessage clMsg = new ClientMessage(msg);
						
						String cmd = clMsg.message[0];
						
						
						if (cmd.equals("m")) { //self movement
							player.update(Double.parseDouble(clMsg.message[1]), Double.parseDouble(clMsg.message[2]));
							
						} else if ((cmd.equals("n")) && (gameState.equals(State.FINDING_GAME))) {
							changeState(State.MAIN_MENU);
							
						} else if ((cmd.equals("init")) && (gameState.equals(State.FINDING_GAME))) {
							System.out.println("init game:"+clMsg.message[1]);
							if (clMsg.message[1].equals("rabbit")) {
								initGame(new Rabbit(username, Integer.parseInt(clMsg.message[2]), Integer.parseInt(clMsg.message[3])));
							
							} else {
								initGame(new Hunter(username, Integer.parseInt(clMsg.message[2]), Integer.parseInt(clMsg.message[3])));
							}
							
							changeState(State.GAME);
							
						} else if (cmd.equals("np")) { //new player
							double x = Double.parseDouble(clMsg.message[3]);
							double y = Double.parseDouble(clMsg.message[4]);
							
							if (clMsg.message[1].equals("Rabbit")) {
								otherPlayers.add(new Rabbit(clMsg.message[2], x, y));
							} else {
								otherPlayers.add(new Hunter(clMsg.message[2], x, y));
							}

						} else if (cmd.equals("start")){
							changeState(State.GAME);
							
						} else if (cmd.equals("om")) { //other movement
							
							for (Player p:otherPlayers) {
								if (clMsg.message[1].equals(p.getName())) {
									p.update(Double.parseDouble(clMsg.message[2]), Double.parseDouble(clMsg.message[3]));
								}
							}
						} else if (cmd.equals("go")) { //update gameobject interactions
							gameMap.getObjects().get(Integer.parseInt(clMsg.message[1])).interact(clMsg.message[2]);
						}else if (cmd.equals("dead")) { //update client back to menu
							System.out.println("dead message" + msg);
							changeState(State.MAIN_MENU);
							
							//gameMap.getObjects().get(Integer.parseInt(clMsg.message[1])).interact(clMsg.message[2]);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				input.close();
				output.close();
				mySocket.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


	}

}
