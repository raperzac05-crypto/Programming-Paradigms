//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class TreasureChest extends Sprite
{
    //variables that determine the if it is in the chest state or the rupee state
    private enum State {CHEST, RUPEE}
    private State state = State.CHEST;

    public static final int w = 25;
    public static final int h = 30;

    private static BufferedImage chestImage = null;
    private static BufferedImage rupeeImage;

    private static final int invFrames = 5;
    private static final int lifeFrames = 40;
    private int rupeeFrames = 0;

    private boolean alive = true;

    //constructors
    public TreasureChest(int x, int y)
    {
        super(x, y, w, h);
        lazyLoad();
    }

    public TreasureChest(Json ob)
    {
        super((int)ob.getLong("x"), (int)ob.getLong("y"), w, h);
        lazyLoad();
        String s = null;
        try
        {
            s = ob.getString("state");
        }
        catch(RuntimeException ignore)
        {}

        if("rupee".equals(s))
        {
            state = State.RUPEE;
            rupeeFrames = 0;
        }
        else
            state = State.CHEST;
    }

    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("type", "treasure");
        ob.add("x", x);
        ob.add("y", y);
        ob.add("state", state == State.RUPEE ? "rupee" : "chest");
        return ob;
    }

    //loads both images
    private static void lazyLoad()
    {
        if(chestImage == null)
            chestImage = View.loadImage("treasurechest.png");
        if(rupeeImage == null)
            rupeeImage = View.loadImage("rupee.png");
    }

    @Override
    public boolean isTresure()
    {
        return true;
    }

    @Override
    public void draw(Graphics g, int roomX, int roomY)
    {
        BufferedImage image = (state == State.CHEST) ? chestImage : rupeeImage;
        g.drawImage(image, x - roomX, y - roomY, w, h, null);
    }

    //stays as a chest unless the state changes, the rupee will despawn after 40 frames
    @Override
    public boolean update()
    {
        if(state == State.RUPEE)
        {
            rupeeFrames++;
            if(rupeeFrames >= lifeFrames)
                alive = false;
        }
        return alive;
    }

    //handles collision with link
    public void collideWithLink(Link link)
    {
        if(state == State.CHEST)
        {
            link.handleCollision(this);
            state = State.RUPEE;
            rupeeFrames = 0;
        }
        else
        {
            if(rupeeFrames >= invFrames)
                alive = false;
        }
    }

    //handles collision with boomerang
    public void collideWithBoomerang(Boomerang b)
    {
        if(state == State.CHEST)
        {
            b.hitObsticle();
            state = State.RUPEE;
            rupeeFrames = 0;
        }
        else
        {
            if(rupeeFrames >= invFrames)
            {
                alive = false;
                b.hitObsticle();
            }
        }
    }
}