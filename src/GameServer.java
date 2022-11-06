import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class GameServer {

	private ServerSocket serverSocket;
	private boolean running = true;
	private ArrayList<ClientHandler> clients = new ArrayList<>();
	private Queue<ClientHandler> rabbits = new LinkedList<>();
	private Queue<ClientHandler> hunters = new LinkedList<>();
	private int[][] rabbitSpawns = {{40, 40, 40, 40}, {40, 40, 40, 40}};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new GameServer().run();
	}
	
	public void run() {
		System.out.println("Waiting for a client connection..");

		Socket client = null;

		try {
			serverSocket = new ServerSocket(5000);
			
			while (running) {

				client = serverSocket.accept(); 
				System.out.println("Client connected");

				ClientHandler newClient = new ClientHandler(client);
				clients.add(newClient);
				
				Thread t = new Thread(newClient); //create a thread for the new client and pass in the socket
				t.start(); 
			}

		} catch (Exception e) { 
			
			System.out.println("Error accepting connection");
			//close all and quit
			try {
				client.close();
			}catch (Exception e1) { 
				System.out.println("Failed to close socket");
			}
			System.exit(-1);
		}
	}

	class ClientHandler implements Runnable {

		private Socket client;
		private BufferedReader input;
		private PrintWriter output;
		private boolean running;
		private boolean signedIn;
		private boolean waiting;
		private Player player;

		public ClientHandler (Socket s) {
			this.client = s;

			try {
				this.output = new PrintWriter(client.getOutputStream());
				InputStreamReader stream = new InputStreamReader(client.getInputStream());
				this.input = new BufferedReader(stream);

			} catch (IOException e) {
				e.printStackTrace();
			}

			signedIn = false;
			running = false;
			waiting = true;
		}
		
		@Override
		public void run() {

			while (!signedIn) {
				try {
					if (input.ready()) {
						
						//gets username and role of player
						String msg = input.readLine();
						
						ClientMessage clMsg = new ClientMessage(msg);
						String username = clMsg.message[1];
						
						
						if (clMsg.message[0].equals("rabbit")) {
							
							if (rabbits.size() == 4) {
								this.sendMsg("<n>"); //cannot get in; spots filled
								
							} else {
								int x = rabbitSpawns[0][rabbits.size()];
								int y = rabbitSpawns[1][rabbits.size()];
								player = new Rabbit(username, x, y);
								rabbits.add(this);
								this.sendMsg("<init><rabbit><"+x+"><"+y+">");
								this.sendMsg("<start>");
								
								signedIn = true;
								running = true;
							}
							
						} else {
							
							if (hunters.size() == 1) {
								this.sendMsg("<n>");
								
							} else {
								player = new Hunter(username, 512, 400);
								hunters.add(this);
								this.sendMsg("<init><hunter><512><400>");
								this.sendMsg("<start>");
								
								signedIn = true;
								running = true;
							}

						}
					}
					
				} catch (IOException e) {
					System.out.println("Failed to recieve message from:");
					e.printStackTrace();
				}
			}
			
			for (ClientHandler cl:clients) {
				if (!cl.equals(this)) {
					cl.addPlayer(this.player);
					this.addPlayer(cl.player);
				}
			}

			
			while (running) {
				try {
					if (input.ready()) { //read messages
						
						String msg = input.readLine();
					
						ClientMessage clMsg = new ClientMessage(msg);
						String cmd = clMsg.message[0];
						
						if (cmd.equals("m")) {
							player.update(Double.parseDouble(clMsg.message[1]), Double.parseDouble(clMsg.message[2]));
				
							//send to all other clients in same game
							for (ClientHandler cl:clients) {
								if (cl.equals(this)) {
									cl.sendMsg(msg);
									 
								} else {
									cl.sendMsg("<om><"+this.player.getName()+"><"+this.player.getXPos()+"><"+this.player.getYPos()+">");
								}
							}
						} else if (cmd.equals("go")) { //game object interactions
							
							for (ClientHandler cl:clients) {
								cl.sendMsg(msg+"<"+player.getClass().getName()+">");
							}
						}else if (cmd.equals("dead")) { //game object interactions
							signedIn=false;
							for (ClientHandler cl:clients) {
								cl.sendMsg(msg+"<"+player.getClass().getName()+">");
							}
						}
					}
					
				} catch (IOException e) {
					System.out.println("Failed to recieve message from:");
					e.printStackTrace();
				}
				

			}

		}
		
		public void sendMsg(String s) {
			output.println(s);
			output.flush();
		}
			
		public void addPlayer(Player p) {
			sendMsg("<np><"+p.getClass().getName()+"><"+p.getName()+"><"+p.getXPos()+"><"+p.getYPos()+">");
		
		}
	}
}


