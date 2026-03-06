//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Link extends Sprite
{
    //variables for the link class
    private int px;
    private int py;
    public static final int linkW = 50;
    public static final int linkH = 50;
    private int linkFrame;
    private int linkDir;
    private int directions = 4;
    private int framesPerDirection = 11;
    private double linkSpeed;
    private static BufferedImage image[][] = null;

    //link class constructor with lazy-loading
    public Link(int x, int y, double linkSpeed)
    {
        super(x, y, linkW, linkH);
        px = x;
        py = y;
        this.linkSpeed = linkSpeed;
        if(image == null)
        {
        
            image = new BufferedImage[directions][framesPerDirection];

		    for(int i = 0; i < directions; i++)
			    for(int j = 0; j < framesPerDirection; j++)
			    {
                    try
                    {
				        int index = i * framesPerDirection + j + 1;
				        image[i][j] = View.loadImage("link"+ index +".png");			    
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace(System.err);
			            System.exit(1);
                    }
                }
        }
    }

    @Override
    public boolean isLink()
    {
        return true;
    }

    @Override
    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("type", "link");
        ob.add("x", x);
        ob.add("y", y);
        ob.add("speed", linkSpeed);
        return ob;
    }

    //method to draw link
    public void draw(Graphics g, int roomX, int roomY)
    {
        int dir = getLinkDir();
        int frame = getLinkFrame();
        g.drawImage(image[dir][frame], getLinkX() - roomX, getLinkY() - roomY, w, h, null);
    }

    //toString method, used for debugging
    @Override
    public String toString()
    {
        return "Link (x,y) = (" + x + ", " + y + "), w = " + linkW + ", h = " + linkH;
    }

    //method that calls the collision detection method and handles it based on direction and previous x and y coordinates
    public void handleCollision(Sprite t) 
    {
            //right side
            if(px + linkW <= t.x && x + linkW >= t.x)
                x = t.x - linkW;

            //left side
            else if(px >= t.x + t.w && x <= t.x + t.w)
                x = t.x + t.w;

            //bottom side
            if(py + linkH <= t.y && y + linkH >= t.y) 
                y = t.y - linkH;

            //top side
            else if(py >= t.y + t.h && y <= t.y + t.h)
                y = t.y + t.h;
    }

    public void savePreviousPostion()
    {
        px = x;
        py = y;
    }

    //methods called in controller to move link
    public void moveRight()
    {
        //savePreviousPostion();
        setLinkX(getLinkX() + (int)getLinkSpeed());
	    setLinkDir(2);
	    nextFrame();
    }

    public void moveLeft()
    {
        //savePreviousPostion();
        setLinkX(getLinkX() - (int)getLinkSpeed());
	    setLinkDir(1);
        nextFrame();
    }

    public void moveDown()
    {
        //savePreviousPostion();
        setLinkY(getLinkY() + (int)getLinkSpeed());
	    setLinkDir(0);
	    nextFrame();    
    }

    public void moveUp()
    {
        //savePreviousPostion();
        setLinkY(getLinkY() - (int)getLinkSpeed());
	    setLinkDir(3);
	    nextFrame();
    }

    //getters and setters for the link object
    public int getLinkX()
    {
        return x;
    }

    public int getLinkY()
    {
        return y;
    }

    public int getLinkW()
    {
        return linkW;
    }

    public int getLinkH()
    {
        return linkH;
    }

    public int getLinkFrame() 
    {
        return linkFrame;
    }

    public int getLinkDir() 
    {
        return linkDir;
    }

    public double getLinkSpeed()
    {
        return linkSpeed;
    }

    public int getLinkRight()
    {
        return x + linkW - 1;
    }

    public int getLinkLeft()
    {
        return x + 1;
    }

    public int getLinkTop()
    {
        return y + 1;
    }

    public int getLinkBottom()
    {
        return y + linkH - 1;
    }

    public void setLinkX(int x)
    {
        this.x = x;
    }

    public void setLinkY(int y)
    {
        this.y = y;
    }

    public void setLinkSpeed(double linkSpeed)
    {
        this.linkSpeed = linkSpeed;
    }

    public void setLinkDir(int linkDir) 
    {
        this.linkDir = linkDir;
    }

    //method that sets the frame to the corresponding link image
    public void nextFrame()
    {
        linkFrame = (linkFrame + 1) % 11;
    }

    //update function
    @Override
    public boolean update()
    {
        return true;
    }
}