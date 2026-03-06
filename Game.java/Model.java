//Zach Raper
//9/30/2025
//Continuation of Project 1 and 2, adding a character with animations as well as collision detection

import java.util.ArrayList;
import java.util.Iterator;

public class Model
{
    ArrayList<Sprite> sprites;
    ArrayList<Sprite> toAdd;
    ArrayList<Sprite> toRemove;
    ArrayList<Sprite> itemsICanAdd;
    protected int itemNum = 0;
    private boolean editMode = false;
    private boolean addMapItem =true;
    private Link link;

    //model constructor
    public Model()
    {
        sprites = new ArrayList<Sprite>();
        toAdd = new ArrayList<Sprite>();
        toRemove = new ArrayList<Sprite>();
        itemsICanAdd = new ArrayList<Sprite>();
        link = new Link(100, 100, 10.0);
        instanceOfItems();
        sprites.add(link);
    }

    //unmarshal constructor
    public void unmarshal(Json ob)
    {
        sprites.clear();
        sprites.add(link);

        try
        {
            Json treeList = ob.get("trees");
            for(int i = 0; i < treeList.size(); i++)
                sprites.add(new Tree(treeList.get(i)));
        }
        catch(RuntimeException ignore)
        {}

        try
        {
            Json chestList = ob.get("chests");
            for(int i = 0; i < chestList.size(); i++)
                sprites.add(new TreasureChest(chestList.get(i)));
        }
        catch(RuntimeException ignore)
        {}
    }

    //marshal constructor
    public Json marshal()
    {
        Json ob = Json.newObject();
        Json treeList = Json.newList();
        Json chestList = Json.newList();
        ob.add("trees", treeList);
        ob.add("chests", chestList);

        for(int i = 0; i < sprites.size(); i++)
        {
            if(sprites.get(i).isTree())
                treeList.add(((Tree)sprites.get(i)).marshal());
            else if(sprites.get(i).isTresure())
                chestList.add(((TreasureChest)sprites.get(i)).marshal());
        }
        return ob;
    }

    //method to get the trees from the array list
    public ArrayList<Sprite> getTree()
    {
        return sprites;
    }

    public Link getLink()
    {
        return link;
    }

    //throws boomerang
    public void throwBoomerang()
    {
        int cx = link.getLinkX() + link.getLinkW() / 2 - 9;
        int cy = link.getLinkY() + link.getLinkH() / 2 - 9;
        int dir = link.getLinkDir();
        double ls = link.getLinkSpeed();
        queueAdd(new Boomerang(cx, cy, dir, ls));
    }

    //checks for collision
    public boolean doesCollisionOccur(Sprite a, Sprite b)
    {
        if(a.x >= b.x + b.w)
            return false;
        if(a.x + a.w <= b.x)
            return false;
        if(a.y >= b.y + b.h)
            return false;
        if(a.y + a.h <= b.y)
            return false;
        return true;
    }

    //checks to make sure that an object or link is not already there
    private boolean canPlace(Sprite exists)
    {
        for(Sprite s : sprites)
        {
            if(s == exists)
                continue;
            if(doesCollisionOccur(exists, s))
                return false;
        }
        return true;
    }

    //adds sprites based on if it is a tree or a chest
    public boolean addSprite(int worldX, int worldY)
    {
        Sprite EMS = getItemIAmAdding();
        if(EMS == null)
            return false;
        
        Sprite exists = null;

        if(EMS.isTree())
        {
            int gx = Math.floorDiv(worldX, 50) * 50;
            int gy = Math.floorDiv(worldY, 60) * 60;
            exists = new Tree(gx, gy);
        }
        else if(EMS.isTresure())
        {
            TreasureChest dummy = new TreasureChest(0, 0);
            int cx = worldX - dummy.w / 2;
            int cy = worldY - dummy.h / 2;
            exists = new TreasureChest(cx, cy);
        }
        else
            return false;

        if(canPlace(exists))
        {
            queueAdd(exists);
            return true;
        }
        else
            return false;
    }

    //removes sprites based on if it is a tree or a chest
    public void removeSprite(int x, int y)
    {
        Sprite EMS = getItemIAmAdding();
        if(EMS == null)
            return;
        for(Sprite s : sprites)
        {
            if(EMS.isTree() && s.isTree() && s.amIClickingOnYou(x, y))
            {
                queueRemove(s);
                return;
            }
            if(EMS.isTresure() && s.isTresure() && s.amIClickingOnYou(x, y))
            {
                queueRemove(s);
                return;
            }
        }
    }

    //adds one instance of a tree and a chest to the array for adding items
    public void instanceOfItems()
    {
        itemsICanAdd.add(new Tree(0, 0));
        itemsICanAdd.add(new TreasureChest(0, 0));
    }

    //iterates through the trees and then sends it to handle the collision
    public void collisionLoop(int oldX, int oldY)
    {
        Iterator<Sprite> it = sprites.iterator();
        while (it.hasNext())
        {
            Sprite s = it.next();
            link.handleCollision(s);
        }
    }

    //method to add a new tree to the map with measures to prevent multiple trees being added to the same plot
    public void addTree(int x, int y)
    {
        boolean isTreeHere = false;
        for(int i = 0; i < sprites.size(); i++)
        {
            if(sprites.get(i).amIClickingOnYou(x, y))
            {
                isTreeHere = true;
                break;
            }
        }
        if(!isTreeHere)
            sprites.add(new Tree(x, y));
    }

    //method that comes through the trees on the map to remove the correct tree
    public void removeTree(int x, int y)
    {
        for(int i = 0; i < sprites.size(); i++)
        {
            if(sprites.get(i).amIClickingOnYou(x, y))
                sprites.remove(i);
        }
    }

    //method that calls the built in Arraylist method to clear the entire map
    public void clearMap()
    {
       sprites.clear();
       sprites.add(link);
    }

    //get methods for edit mode and add map item
    public boolean getEditMode()
    {
        return editMode;
    }

    public boolean getAddMapItem()
    {
        return addMapItem;
    }

    public int getItemNum()
    {
        return itemNum;
    }

    public Sprite getItemIAmAdding()
    {
        if(itemsICanAdd.isEmpty())
            return null;
        return itemsICanAdd.get(itemNum);
    }

    //set methods for edit mode and add map item

    public void setItemNum()
    {
        if(itemsICanAdd.isEmpty())
            return;
        itemNum = (itemNum + 1) % itemsICanAdd.size();
    }

    public void setEditMode(boolean mode)
    {
        this.editMode = mode;
    }

    public void setAddMapItem(boolean addMapItem)
    {
        this.addMapItem = addMapItem;
    }

    //methods that sets addMapItem to true or false based on if the user wants to add or remove objects
    public void addMapItemTrue()
    {
        this.addMapItem = true;
    }

    public void addMapItemFalse()
    {
        this.addMapItem = false;
    }

    //method to add sprites in the edit box based on if its a tree or chest
    public void addEditModeSprite(int x, int y)
    {
        Sprite EMS = getItemIAmAdding();
        if(EMS == null)
            return;
        
        int ex = Math.floorDiv(x, 50) * 50;
        int ey = Math.floorDiv(y, 60) * 50;

        if(EMS.isTree())
        {
            for(Sprite s : sprites)
                if(s.isTree() && s.amIClickingOnYou(ex, ey))
                    return;
            sprites.add(new Tree(ex, ey));
        }
        else if(EMS.isTresure())
        {
            for(Sprite s : sprites)
                if(s.isTresure() && s.amIClickingOnYou(ex, ey))
                    return;
            sprites.add(new TreasureChest(ex, ey));
        }
    }

    //method to remove sprites in the edit box based on if its a tree or a chest
    public void removeEditModeSprite(int x, int y)
    {
        Sprite EMS = getItemIAmAdding();
        if(EMS == null)
            return;
        
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite s = sprites.get(i);

            if(EMS.isTree() && s.isTree() && s.amIClickingOnYou(x, y))
            {
                sprites.remove(i);
                return;
            }
            if(EMS.isTresure() && s.isTresure() && s.amIClickingOnYou(x, y))
            {
                sprites.remove(i);
                return;
            }
        }
    }

    //queues for adding and removing sprites to prevent concurrency errors
    private void queueAdd(Sprite s)
    {
        toAdd.add(s);
    }

    private void queueRemove(Sprite s)
    {
        toRemove.add(s);
    }

    //handles how different sprites interact with eachother, as well as whether to add and/or remove them
    public void update() 
    {
        //creates a snapshot for the inner loop
        final ArrayList<Sprite> snapshot = new ArrayList<>(sprites);

        //outer loop, handles removing
        for (Iterator<Sprite> it1 = sprites.iterator(); it1.hasNext();) 
        {
            Sprite s1 = it1.next();

            if (toRemove.contains(s1)) 
            { 
                it1.remove(); 
                continue; 
            }

            boolean alive = s1.update();
            if (!alive) 
            { 
                it1.remove(); 
                continue; 
            }

            boolean killCurrent = false;

        // inner loop over snapshot only
            for (Sprite s2 : snapshot) 
            {
                if (s1 == s2) 
                    continue;
                if (toRemove.contains(s2)) 
                    continue;
                if (!doesCollisionOccur(s1, s2)) 
                    continue;

            //link vs tree
                if (s1.isLink() && s2.isTree()) 
                    ((Link)s1).handleCollision(s2);
                
                else if (s2.isLink() && s1.isTree()) 
                    ((Link)s2).handleCollision(s1);
                

            //link vs treasure
                else if (s1.isLink() && s2.isTresure()) 
                {
                    ((TreasureChest)s2).collideWithLink(link);
                
                } 
                else if (s2.isLink() && s1.isTresure()) 
                {
                    ((TreasureChest)s1).collideWithLink(link);
                }

            //boomerang vs tree
                else if (s1.isBoomerang() && (s2.isTree())) 
                {
                    ((Boomerang)s1).hitObsticle();
                    killCurrent = true; 
                } 
                else if (s2.isBoomerang() && (s1.isTree())) 
                {
                    ((Boomerang)s2).hitObsticle();
                    queueRemove(s2);   
                }

            //boomerang vs treasure
                else if(s1.isBoomerang() && s2.isTresure())
                {
                    ((TreasureChest)s2).collideWithBoomerang((Boomerang)s1);
                }
                else if(s2.isBoomerang() && s1.isTresure())
                {
                    ((TreasureChest)s1).collideWithBoomerang((Boomerang)s2);
                }

                if (killCurrent) 
                    break; 
            }

            if (killCurrent) 
            {
                it1.remove();
                continue;
            }
    }

    //prevents concurrency errors by clearing the add and remove queues
    if (!toRemove.isEmpty()) 
    {
        sprites.removeAll(toRemove);
        toRemove.clear();
    }
    if (!toAdd.isEmpty()) 
    {
        sprites.addAll(toAdd);
        toAdd.clear();
    }
}

}    