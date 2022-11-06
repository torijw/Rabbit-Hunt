import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

class GameWindow extends JFrame { 
	
	public GameWindow(GameClient client) { 
		
		setTitle("Rabbit Hunt");
		setSize(1280,1024); 
		setResizable(false);  
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		getContentPane().add(new GamePanel(client));
		pack();
		setVisible(true);
	}

	static class GamePanel extends JPanel implements KeyListener, ActionListener {
		
		FrameRate frameRate;
		Clock clock;
		GameClient client;
		private String username;
		Player[] players = new Player[4];
		private HashMap<State, Screen> screens = new HashMap<>();

		public GamePanel(GameClient client) { 
			setPreferredSize(new Dimension(1280, 1024));
			addKeyListener(this);
			setFocusable(true);
			requestFocusInWindow();
			
			frameRate = new FrameRate();
			clock=new Clock();
			this.client = client;
			
			screens.put(State.MAIN_MENU, new StartScreen(this));
			screens.put(State.ROLE_SELECT, new SelectionScreen(this));
			screens.put(State.RABBIT_LOSE, new RabbitLoseScreen(this));
			screens.put(State.HUNTER_WIN, new HunterWinScreen(this));

		}

		public void paintComponent(Graphics g) { 
			super.paintComponent(g); 
			
			switch (client.getState()) {
			case MAIN_MENU:
				
				StartScreen menu = (StartScreen)screens.get(State.MAIN_MENU);
				
				menu.render(g);
				for (JButton b:menu.getButtons()) {
					this.add(b);
					b.setVisible(true);
				}
				break;
				
			case ROLE_SELECT:
				
				SelectionScreen selection = (SelectionScreen)screens.get(State.ROLE_SELECT);
				
				selection.render(g);
				for (JButton b:selection.getButtons()) {
					this.add(b);
					b.setVisible(true);
				}
				
				//select roles: rabbit or hunter
				//enter username
				//branches off to GAME
				break;
					
			case GAME:
				
				//game running
				//update the content
				clock.update();
				frameRate.update();
				
				//draw the screen
				client.updatePlayer(clock.getElapsedTime());
				client.getMap().update(clock.getElapsedTime());
				client.getMap().draw(g);
				client.getPlayer().draw(g);
				
				for (Player p:client.getOtherPlayers()) {
					p.draw(g);
				}
				
				frameRate.draw(g,10,10);
				
				break;
			case CONTROLS:
				
				//displays controls
				//back to main menu
				
				break;
			case CREDIT:
				
				//displays credits
				break;
			case FINDING_GAME:
				
				//waiting screen;
				break;

			case RABBIT_LOSE:
				
				RabbitLoseScreen rabbitLose = (RabbitLoseScreen)screens.get(State.RABBIT_LOSE);
				
				rabbitLose.render(g);
				this.add(rabbitLose.getExit());
				rabbitLose.getExit().setVisible(true);

				break;
				
			case HUNTER_WIN:
				
				HunterWinScreen hunterWin = (HunterWinScreen)screens.get(State.HUNTER_WIN);
				
				hunterWin.render(g);
				this.add(hunterWin.getExit());
				hunterWin.getExit().setVisible(true);
				
				break;
			default:
				break;
				
			}

			//request a repaint
			repaint();
		}
		
		public void keyPressed(KeyEvent e) {
			
			if (client.getState() == State.GAME) {
				
				switch(e.getKeyCode()) { 
				case KeyEvent.VK_DOWN:
					client.getPlayer().setYVel(client.getPlayer().getSpeed());
					break;

				case KeyEvent.VK_UP:
					client.getPlayer().setYVel(-client.getPlayer().getSpeed());
					break;
					
				case KeyEvent.VK_LEFT:
					client.getPlayer().setXVel(-client.getPlayer().getSpeed());
					break;
					
				case KeyEvent.VK_RIGHT:
					client.getPlayer().setXVel(client.getPlayer().getSpeed());
					break;
					
				case KeyEvent.VK_Z: 
					
					Rectangle bounds = new Rectangle ((int)client.getPlayer().getXPos()-16, (int) client.getPlayer().getYPos()-16, 64, 64);
					
					for (GameObject obj:client.getMap().getObjects()) {
						//check which object is in the vicinity
						if (bounds.intersects(obj.getBounds())) {
							client.sendMsgToServer("<go><"+client.getMap().getObjects().indexOf(obj)+">");
							obj.interact(client.getPlayer());
						}
						//send object status change to server
					}
					break;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
			if (client.getState()==State.GAME) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					client.getPlayer().setYVel(0);
					break;

				case KeyEvent.VK_UP:
					client.getPlayer().setYVel(0);
					break;
					
				case KeyEvent.VK_LEFT:
					client.getPlayer().setXVel(0);
					break;
					
				case KeyEvent.VK_RIGHT:
					client.getPlayer().setXVel(0);
					break;
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			switch (client.getState()) {
			case MAIN_MENU:

				if (e.getSource() == ((StartScreen)screens.get(State.MAIN_MENU)).getStartButton()) {
					client.changeState(State.ROLE_SELECT); //should be ROLESELECT
					this.removeAll();
					this.add(((SelectionScreen)screens.get(State.ROLE_SELECT)).getUserField());
					
				} else if (e.getSource() == ((StartScreen)screens.get(State.MAIN_MENU)).getControlsButton()) {
					client.changeState(State.CONTROLS);
					
				} else if (e.getSource() == ((StartScreen)screens.get(State.MAIN_MENU)).getCreditsButton()) {
					client.changeState(State.CREDIT);
				} 
				break;
				
			case ROLE_SELECT:
				
				if (e.getSource() == ((SelectionScreen)screens.get(State.ROLE_SELECT)).getRabbitButton()) {
					
					JTextField userField = ((SelectionScreen)screens.get(State.ROLE_SELECT)).getUserField();
					//String s = userField.getText();
					username = userField.getText();
					client.setUser(username);
					userField.setText("");
					
					this.requestFocus();
					
					client.sendMsgToServer("<rabbit><"+username+">"); 
					//client.initGame(new Rabbit(s, 512, 500));
					
					this.removeAll();
					client.changeState(State.FINDING_GAME);
					
				} else if (e.getSource() == ((SelectionScreen)screens.get(State.ROLE_SELECT)).getHunterButton()) {
					
					JTextField userField = ((SelectionScreen)screens.get(State.ROLE_SELECT)).getUserField();
					username = userField.getText();
					client.setUser(username);
					userField.setText("");
					
					this.requestFocus();
					
					client.sendMsgToServer("<hunter><"+username+">");
					
					this.removeAll();
					client.changeState(State.FINDING_GAME);
					
					
				} else if (e.getSource() == ((SelectionScreen)screens.get(State.ROLE_SELECT)).getBackButton()) {
					this.removeAll();
					
					client.changeState(State.MAIN_MENU);
					
				}
				break;
			case CONTROLS:
				break;
			case CREDIT:
				break;
			case FINDING_GAME:
				
				break;
			case GAME:
				break;

			case RABBIT_LOSE:
				
				if (e.getSource() == ((RabbitLoseScreen)screens.get(State.RABBIT_LOSE)).getExit()) {
					System.exit(-1);
					
				}
				break;
			case HUNTER_WIN:
				
				if (e.getSource() == ((HunterWinScreen)screens.get(State.HUNTER_WIN)).getExit()) {
					System.exit(-1);
					
				}
				break;
			default:
				break;
				
			}
		}

	}

}