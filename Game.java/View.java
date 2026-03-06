//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class View extends JPanel
{
	//member variables for the view class
	private final Model model;

	private int currRoomX = 0;
	private int currRoomY = 0;

	
	//method that creates the panel for the game, as well as adds the background color
	public void paintComponent(Graphics g)
	{
		updateRoomView();

		g.setColor(new Color (69, 108, 65));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		int roomX = getRoomX();
		int roomY = getRoomY();

		for(Sprite s : model.getTree())
			s.draw(g, roomX, roomY);

		
		//draws the tree image if in edit mode, and if addMapItem is falses a red box is drawn instead
		if(model.getEditMode())
		{
			g.setColor(new Color(74, 103, 65));
			g.fillRect(0, 0, 100, 100);
			

			if(!model.getAddMapItem())
			{
				g.setColor(new Color(255, 0, 0));
				g.fillRect(0, 0, 100, 100);
				
			}

			Sprite EMS = model.getItemIAmAdding();
			if(EMS != null)
			{
				int previewX = (100 - EMS.w) / 2;
				int previewY = (100 - EMS.h) / 2;

				int fakeRoomX = EMS.x - previewX;
				int fakeRoomY = EMS.y - previewY;

				EMS.draw(g, fakeRoomX, fakeRoomY);
			}
		}
	}

	public static BufferedImage loadImage(String filename)
	{
		try
		{
			return ImageIO.read(new File("images/" + filename));
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
			return null;
		}
	}

	//getters for currRoomX and currRoomY
	public int getRoomX()
	{
		return currRoomX;
	}

	public int getRoomY()
	{
		return currRoomY;
	}

	//view class constuctor, takes in a controller and model as parameters
	public View(Controller c, Model m)
	{	
		model = m;

		c.setView(this);
	}

	//updates the room view down the middle of the link sprite
	private void updateRoomView()
	{
	 	int roomXMultiplier = Math.floorDiv(model.getLink().getLinkX() + Link.linkW / 2, Game.windowWidth);
	 	currRoomX = roomXMultiplier * Game.windowWidth;

		int roomYMultiplier = Math.floorDiv(model.getLink().getLinkY() + Link.linkH / 2, Game.windowHeight);
		currRoomY = roomYMultiplier * Game.windowHeight;
	}
}
