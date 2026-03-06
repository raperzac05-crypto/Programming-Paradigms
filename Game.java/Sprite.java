

import java.awt.Graphics;

public abstract class Sprite extends Object
{
    protected int x, y, w, h;

    public Sprite(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    //handles if a sprite is being clicked on
    public boolean amIClickingOnYou(int mouseX, int mouseY)
    {
        if(mouseX >= x && mouseX <= x+w &&
            mouseY >= y && mouseY <= y+h)
            return true;
        else
            return false;
    }

    //identifiers for the children classes
    public boolean isTree()          {     return false;   }
    public boolean isLink()          {     return false;   }
    public boolean isBoomerang()     {     return false;   }
    public boolean isTresure()       {     return false;   }

    //methods that all the children classes need
    public abstract void draw(Graphics g, int roomX, int roomY);
    public abstract boolean update();
    public abstract Json marshal();
}