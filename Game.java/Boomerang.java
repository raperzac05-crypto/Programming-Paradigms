//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Boomerang extends Sprite
{
    private double vx, vy;

    private static BufferedImage[] frames = null;
    private static final int numFrames = 4;
    private int frame = 0;
    private int tick = 0;

    public static final int boomerangW = 18;
    public static final int boomerangH = 18;
    private boolean alive = true;

    //Boomerang constructor with lazy-loading as well as directions for when it is being thrown
    public Boomerang(int x, int y, int dir, double linkSpeed)
    {
        super(x, y, boomerangW, boomerangH);
        double speed = Math.max(1.0, linkSpeed + 2.0);

        if(frames == null)
        {
            frames = new BufferedImage[numFrames];
            for(int i = 0; i < numFrames; i++)
            {
                try
                {
                    frames[i] = View.loadImage("boomerang" + (i + 1) + ".png");
                }
                catch(Exception e)
                {
                    e.printStackTrace(System.err);
		            System.exit(1);
                }
            }
        }

        switch(dir)
        {
            //down
            case 0:
                vx = 0;
                vy = speed;
                break;
            //left
            case 1:
                vx = -speed;
                vy = 0;
                break;
            //right
            case 2:
                vx = speed;
                vy = 0;
                break;
            //up
            case 3:
                vx = 0;
                vy = -speed;
                break;
            default:
                vx = speed;
                vy = 0;
                break;
        }
        
    }
    
    @Override
    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("type", "boomerang");
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }

    @Override
    public boolean isBoomerang()
    {
        return true;
    }

    //draw methods for the Boomerang class
    @Override
    public void draw(Graphics g, int roomX, int roomY)
    {
        int sx = x - roomX;
        int sy = y - roomY;

        if(frames != null)
        {
            BufferedImage f = frames[frame % numFrames];
            if(f != null)
            {
                g.drawImage(f, sx, sy, boomerangW, boomerangH, null);
            }
        }
    }

    public void hitObsticle()   {   alive = false;  }

    @Override
    public String toString()
    {
        return "Boomerang (x,y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }

    //advance the boomerang based on ticks
    @Override
    public boolean update()
    {
        x += (int)Math.round(vx);
        y += (int)Math.round(vy);

        tick++;
        if(frames != null && frames[frame] != null && (tick % numFrames == 0))
            frame = (frame + 1) % numFrames;

        return alive;
    }
    
}