//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Controller implements MouseListener, KeyListener
{
	private boolean keepGoing;
	//member variables for keyboard controls
	private boolean keyLeft;
	private boolean keyRight;
	private boolean keyUp;
	private boolean keyDown;
	private boolean spaceHeld = false;

	public static final int editBX = 10;
	public static final int editBY = 10;
	public static final int editBW = 100;
	public static final int editBH = 100;

	//reference to the model object
	private Model model;
	
	public Controller(Model m)
	{
		model = m;
		keepGoing = true; 
	}

	//method that handles when the mouse is clicked whenever the user is in edit mode
	public void mousePressed(MouseEvent e)
	{
		boolean placed;
		if(model.getEditMode())
		{
			int mouseX = e.getX() + view.getRoomX();
			int mouseY = e.getY() + view.getRoomY();

			int sx = e.getX();
			int sy = e.getY();
			boolean inEditBox = (sx >= editBX && sx <= editBX + editBW
			&& sy >= editBY && sy <= editBY + editBH);

			if(inEditBox)
			{
				model.setItemNum();
				return;
			}

			int x = Math.floorDiv(mouseX, 50) * 50;
			int y = Math.floorDiv(mouseY, 60) * 60;

        	if(model.getAddMapItem())
            	placed = model.addSprite(x, y);
        	else
				model.removeSprite(x, y);
		}
	}

	//mouse actions
	public void mouseReleased(MouseEvent e) {  };
	public void mouseEntered(MouseEvent e) {  };
	public void mouseExited(MouseEvent e) {  };
	public void mouseClicked(MouseEvent e) 
	{ 
		// if (e.getY() < 100)
		// 	System.out.println("Break here");
	}

	//switch method that sets each key boolean to true if it is pressed
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT:
				keyRight = true;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = true;
				break;
			case KeyEvent.VK_UP:
				keyUp = true;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = true;
				break;
			case KeyEvent.VK_SPACE:
				if(!spaceHeld)
				{
					model.throwBoomerang();
					spaceHeld = true;
				}
				break;
		}
	}

	//switch method that sets the key boolean to false when the key is released
	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT:
				keyRight = false;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = false;
				break;
			case KeyEvent.VK_UP:
				keyUp = false;
				break;
			case KeyEvent.VK_DOWN:
				keyDown = false;
				break;
			case KeyEvent.VK_SPACE:
				spaceHeld = false;
				break;
			case KeyEvent.VK_ESCAPE:
				System.exit(0);
		}
		//if the user presses q while running the game, the program will end
		char c = Character.toLowerCase(e.getKeyChar());

		//switch case that handles the button inputs, such as moving the camera, as well as edit mode, adding objects, and removing objects
		switch(c)
		{
			case 'q':
				System.exit(0);
				break;
			case 'e':
				model.setEditMode(!model.getEditMode());
				model.addMapItemTrue();
				break;
			case 'a':
				if(model.getEditMode())
					model.addMapItemTrue();
				break;
			case 'r':
			 	if(model.getEditMode())
			 		model.addMapItemFalse();
				break;
			case 'c':
				if(model.getEditMode())
					model.clearMap();
				break;

			//Json methods for saving and loading a file
			case 's':
				Json saveObject = model.marshal();
				saveObject.save("map.json");
				System.out.println("Map has been saved.");
				break;
			case'l':
				loadMap();
				break;
		}
	}

	//loads the map
	public void loadMap()
	{
		Json loadObject = Json.load("map.json");
		model.unmarshal(loadObject);
		System.out.println("Map has been loaded.");
	}
	
	public void keyTyped(KeyEvent e)
	{    }

	//class member variable reference to the view class
	private View view;

	//calls the object that view references
	public void setView(View v)
	{
		view = v;
		loadMap();
	}

	//updates oldX and oldY to links coordiantes and moves and calls for collision detection
	public boolean update()
	{
		Link link = model.getLink();
		link.savePreviousPostion();

		if(keyRight)
		{
		  	link.moveRight();
		}	
		if(keyLeft)
		{
		  	link.moveLeft();
		}	
		if(keyDown)
		{
		  	link.moveDown();
		}
		if(keyUp)
		{
		  	link.moveUp();
		}
		//the Controller keeps track of whether or not we have quit the program and
		//returns this value to the Game engine of whether or not to continue the game loop
		return keepGoing;
	}
}
