//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.Toolkit;
import javax.swing.JFrame;

public class Game extends JFrame
{
	//member variables for the game class
	private boolean keepGoing;
	private Model model;
	private View view;
	private Controller controller;

	public final static int windowWidth = 700;
	public final static int windowHeight = 500;
	
	//constructor for the game class
	public Game()
	{
		model = new Model();
		controller = new Controller(model);
		view = new View(controller, model);
		this.setTitle("A4 - Polymorphism");
		this.setSize(windowWidth, windowHeight);
		this.setFocusable(true);
		this.getContentPane().add(view);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.addKeyListener(controller);
		keepGoing = true;
		view.addMouseListener(controller);
	}

	//method to run the game
	public void run()
	{
		//do-while loop to keep the game running
		do
		{
			keepGoing = controller.update();
			model.update();
			//this will indirectly call View.paintComponent
			view.repaint();
			//updates the screen
			Toolkit.getDefaultToolkit().sync();

			//go to sleep for 50 ms
			try
			{
				Thread.sleep(50);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		while(keepGoing);
	}

	//main method, starts the game
	public static void main(String[] args)
	{
		Game g = new Game();
		g.run();
	}
}