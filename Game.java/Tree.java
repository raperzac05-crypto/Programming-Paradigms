//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Tree extends Sprite
{
    public static final int treeW = 50;
    public static final int treeH = 60;
    private static BufferedImage image =  null;

    //tree constructor with lazy-loading
    public Tree(int x, int y)
    {
        super(x, y, treeW, treeH);
        if(image == null)
            image = View.loadImage("tree.png");
    }

    public Tree(Json ob)
    {
        super((int)ob.getLong("x"), (int)ob.getLong("y"), treeW, treeH);
        if(image == null)
            image = View.loadImage("tree.png");
    }
    
    public Json marshal()
    {
        Json ob = Json.newObject();
        ob.add("x", x);
        ob.add("y", y);
        return ob;
    }

    public boolean isTree()
    {
        return true;
    }

    //draw methods for the tree class
    public void draw(Graphics g, int roomX, int roomY)
    {
        g.drawImage(image, x - roomX, y - roomY, w, h, null);
    }

    public static void drawEditModeTree(Graphics g)
    {
		g.drawImage(image, 10, 10, 75, 75, null);
    }

    // public boolean doesTreeExist(int mouseX, int mouseY)
    // {
    //     return getTreeX() == mouseX && getTreeY() == mouseY;
    // }

    @Override
    public String toString()
    {
        return "Tree (x,y) = (" + x + ", " + y + "), w = " + w + ", h = " + h;
    }

    //get and set methods for the member varibles
    public int getTreeX()
    {
        return x;
    }

    public int getTreeY()
    {
        return y;
    }

    public int getTreeW()
    {
        return w;
    }

    public int getTreeH()
    {
        return h;
    }

    public int getTreeRight()
    {
        return x + w;
    }

    public int getTreeLeft()
    {
        return x;
    }

    public int getTreeTop()
    {
        return y;
    }

    public int getTreeBottom()
    {
        return y + h;
    }

    public void setTreeX(int x)
    {
        this.x = x;
    }

    public void setTreeY(int y)
    {
        this.y = y;
    }

    @Override
    public boolean update()
    {
        return true;
    }
}