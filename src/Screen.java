import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;

class Screen {
	private BufferedImage bgImage = null;

	public void render(Graphics g) {
		g.drawImage(this.bgImage, 0, 0, null);
	}

	public void setBG(BufferedImage img) {
		this.bgImage = img;
	}

}

class StartScreen extends Screen {

	private final JButton start = new JButton(new ImageIcon("startButton.jpg"));
	private final JButton controls = new JButton(new ImageIcon("controlsButton.jpg"));
	private final JButton credits = new JButton(new ImageIcon("creditsButton.jpg"));

	public StartScreen(ActionListener al) {

		try {
			this.setBG(ImageIO.read(new File("startBG.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
	
		start.setBounds(480, 480, 320, 100);
		controls.setBounds(480, 630, 320, 100);
		credits.setBounds(480, 780, 320, 100);

		start.addActionListener(al);
		controls.addActionListener(al);
		credits.addActionListener(al);
	}

	public ArrayList<JButton> getButtons() {
		ArrayList<JButton> buttons = new ArrayList<>();
		
		buttons.add(this.start);
		buttons.add(this.controls);
		buttons.add(this.credits);
		
		return buttons;
	}
	
	public JButton getStartButton() {
		return this.start;
	}
	
	public JButton getControlsButton() {
		return this.controls;
	}
	
	public JButton getCreditsButton() {
		return this.credits;
	}
	
}

class SelectionScreen extends Screen {

	private final JTextField userField = new JTextField("ENTER USERNAME");
	private final JButton back = new JButton("back");
	private final JButton rabbit = new JButton(new ImageIcon("RabbitSelect.jpg"));
	private final JButton hunter = new JButton(new ImageIcon("hunterSelect.jpg"));
	
	public SelectionScreen (ActionListener al) {
		
		try {
			this.setBG(ImageIO.read(new File("SelectionBG.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		userField.setBounds(540, 300, 200, 50);
		
		back.setBounds(0, 0, 100, 50);
		rabbit.setBounds(160, 400, 400, 490);
		hunter.setBounds(720, 400, 400, 490);

		back.addActionListener(al);
		rabbit.addActionListener(al);
		hunter.addActionListener(al);
	}
	
	public JTextField getUserField() {
		return this.userField;
	}
	
	public JButton getBackButton() {
		return this.back;
	}
	
	public JButton getRabbitButton() {
		return this.rabbit;
	}
	
	public JButton getHunterButton() {
		return this.hunter;
	}
	
	public ArrayList<JButton> getButtons() {
		ArrayList<JButton> buttons = new ArrayList<>();
		
		buttons.add(this.back);
		buttons.add(this.rabbit);
		buttons.add(this.hunter);
		
		return buttons;
	}
}

class FindingScreen extends Screen {
	
	public FindingScreen () {
		
		try {
			this.setBG(ImageIO.read(new File("FindingBG.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}

class HunterWinScreen extends Screen {
	
	private JButton exit = new JButton("exit");
	
	public HunterWinScreen (ActionListener al) {
		
		try {
			this.setBG(ImageIO.read(new File("hunterWin.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		exit.setBounds(0, 0, 100, 50);
		exit.addActionListener(al);

	}
	
	public JButton getExit() {
		return this.exit;
	}
}

class RabbitLoseScreen extends Screen {
	
	private JButton exit = new JButton("exit");
	
	public RabbitLoseScreen (ActionListener al) {
		
		try {
			this.setBG(ImageIO.read(new File("rabbitLose.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		exit.setBounds(0, 0, 100, 50);
		exit.addActionListener(al);

	}
	public JButton getExit() {
		return this.exit;
	}
}

class ControlScreen extends Screen {
	
	private final JButton back = new JButton("back");
	
	public ControlScreen (ActionListener al) {
		
		try {
			this.setBG(ImageIO.read(new File("ControlBG.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public JButton getBackButton() {
		return this.back;
	}
}

class CreditsScreen extends Screen {
	
	public CreditsScreen () {
		
		try {
			this.setBG(ImageIO.read(new File("CreditsBG.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
}