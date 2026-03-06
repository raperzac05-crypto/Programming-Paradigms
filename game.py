# CSCE 31903 Programming Paradigms
# Fall 2025
# Zach Raper
# 12/3/2025
# Assignment 7 starter code, translating Assignments 4 and 6
# into python as well as adding cuccos

import pygame
import time
import json
import math

from pygame.locals import*
from time import sleep


class Sprite():
    def __init__(self, x1, y1, w1, h1, image):
        self.x = x1
        self.y = y1
        self.w = w1
        self.h = h1
        self.px = x1
        self.py = y1
        self.speed = 10
        self.valid = True
        self.image = pygame.image.load(image)

    def update(self):
        return self.valid
    
    #draw method, uses location and size to scale the image correctly
    def draw(self, screen, room_x, room_y):
        LOCATION = (self.x - room_x, self.y - room_y)
        SIZE = (self.w, self.h)
        screen.blit(pygame.transform.scale(self.image, SIZE), LOCATION)
    
    def am_i_clicking_on_you(self, mouseX, mouseY):
        if mouseX >= self.x and mouseX <= self.x+self.w and mouseY >= self.y and mouseY <= self.y+self.h:
            return True
        else:
            return False
        
    #moved from the link class due to cuccos colliding with sprites very similarly
    #also useful to override it for the cuccos    
    def handle_collision(self, other):
        #right side
        if self.px + self.w <= other.x and self.x + self.w >= other.x:
            self.x = other.x - self.w

        #left side
        if self.px >= other.x + other.w and self.x <= other.x + other.w:
            self.x = other.x + other.w

        #bottom side
        if self.py + self.h <= other.y and self.y + self.h >= other.y:
            self.y = other.y - self.h

        #top side
        if self.py >= other.y + other.h and self.y + self.h >= other.y:
            self.y = other.y + other.h

    #methods to identify the exact type of sprite
    def is_link(self):
        return False
    
    def is_tree(self):
        return False
    
    def is_boomerang(self):
        return False
    
    def is_treasure(self):
        return False
    
    def is_cucco(self):
        return False

    # for the starter code, we assume that all Sprites of a certain
    # type are the same size, and thus don't need w and h saved
    # However, it would be very easy to add more attributes to be 
    # saved here!
    def marshal(self):
        return {
            "x": self.x,
            "y": self.y
        }

class Tree(Sprite):
    # variables that belong to the class, not to a specific
    # instance of the class - this is similar to Java's static variables
    TREE_WIDTH = 50
    TREE_HEIGHT = 60
    num_tree = 0
    
    # this method belongs to the class itself and does not need
    # the 'self' object
    #upon further inspection, resetting trees is not needed
    #@staticmethod
    #def reset_tree():
        #Tree.num_tree = 0
    
    # constructor with default values - one of the ways you can
    # mimic Java's overloaded constructors
    # in this example, if w and h are provided, create the fish
    # as defined. If they are not provided, create a fish at 
    # half the regular size
    def __init__(self, x, y, w=None, h=None):
        if w is None or h is None:
            super().__init__(x, y, Tree.TREE_WIDTH/2, Tree.TREE_HEIGHT/2, "images/tree.png")
        else:
            super().__init__(x, y, w, h, "images/tree.png")
        Tree.num_tree += 1

    def is_tree(self):
        return True

class Link(Sprite):
    #class varibles
    LINK_WIDTH = 50
    LINK_HEIGHT = 50

    #animation
    FRAMES_PER_DIRECTION = 11
    directions = 4
    frames = None
    
    #rupee counter now held by link, which makes sense
    rupee_count = 0

    def __init__(self, x, y, link_speed):
        super().__init__(x, y, Link.LINK_WIDTH,Link.LINK_HEIGHT, "images/link1.png")

        #instance varibles
        self.px = x
        self.py = y
        self.link_speed = link_speed
        self.frame = 0
        self.dir = 0

        #image loading, similar to JavaScript
        if Link.frames is None:
            Link.frames = []
            for i in range(Link.directions):
                #python is not a fan of doing Link.frames[i] = [], so just create a row
                row = []
                for j in range(Link.FRAMES_PER_DIRECTION):
                    index = i * Link.FRAMES_PER_DIRECTION + j + 1
                    #the f is essential to convert the {index} bit into a string
                    img = pygame.image.load(f"images/link{index}.png")
                    #append img into row and then append row into Link.frames
                    row.append(img)
                Link.frames.append(row)

    #link draw method
    def draw(self, screen, room_x, room_y):
        #sets the image to link1.png
        img = self.image

        #checks to make sure the frames exist, there is a direction, and there is a frame at the index
        if Link.frames and Link.frames[self.dir] and Link.frames[self.dir][self.frame]:
            #pulls out the candidate frame
            candidate = Link.frames[self.dir][self.frame]
        if candidate is not None:
            img = candidate
        
        LOCATION = (self.x - room_x, self.y - room_y)
        SIZE = (self.w, self.h)
        screen.blit(pygame.transform.scale(img, SIZE), LOCATION)
            

    #movement methods so link moves correctly
    def move_right(self):
        self.x += self.link_speed
        self.dir = 2
        self.next_frame()

    def move_left(self):
        self.x -= self.link_speed
        self.dir = 1
        self.next_frame()

    def move_down(self):
        self.y += self.link_speed
        self.dir = 0
        self.next_frame()
    
    def move_up(self):
        self.y -= self.link_speed
        self.dir = 3
        self.next_frame()
    
    #saves the previous position
    def save_previous_position(self):
        self.px = self.x
        self.py = self.y

    #cycles through the frames to the correct link png
    def next_frame(self):
        self.frame = (self.frame + 1) % Link.FRAMES_PER_DIRECTION

    def is_link(self):
        return True

    def update(self):
        return True
    
class Cucco(Sprite):
    #class variables
    #width and height
    C_WIDTH = 30
    C_HEIGHT = 40

    #counters, directions, and coordinates
    hit_counter = 0
    num_cuccos = 0
    num_vanished = 0
    linkx = 0
    linky = 0
    RIGHT = 1
    LEFT = 0
    angry_mode = False

    #animation and frames
    total_angry_time = 40
    total_attached_time = 20
    frames_normal = None
    frames_angry = None
    FRAMES_PER_DIRECTION = 2
    directions = 2

    #cooldown added due to cuccos immediately attacking after being angry
    cooldown_frames = 500
    cooldown_timer = 0

    #useful for marshalling
    @staticmethod
    def reset_cucco():
        Cucco.num_cuccos = 0

    def __init__(self, x, y, cucco_speed=4):
        super().__init__(x, y, Cucco.C_WIDTH, Cucco.C_HEIGHT, "images/cucco1.png")

        #when a new cucco is created, increment the total amount
        Cucco.num_cuccos += 1

        #instance variables
        self.cucco_speed = cucco_speed
        self.xdir = cucco_speed
        self.ydir = cucco_speed
        self.frame = 0
        self.dir = 0
        self.tick = 0
        self.attach_timer = 0
        self.angry_timer = 0
        self.attached_to_link = False
        self.valid = True

        #same image loading as link but split between normal and angry frames
        if Cucco.frames_normal is None:
            Cucco.frames_normal = []
            for i in range(Cucco.directions):
                row = []
                for j in range(Cucco.FRAMES_PER_DIRECTION):
                    index = i * Cucco.FRAMES_PER_DIRECTION + j + 1
                    img = pygame.image.load(f"images/cucco{index}.png")
                    row.append(img)
                Cucco.frames_normal.append(row)
        if Cucco.frames_angry is None:
            Cucco.frames_angry = []
            for i in range(Cucco.directions):
                row = []
                for j in range(Cucco.FRAMES_PER_DIRECTION):
                    index = i * Cucco.FRAMES_PER_DIRECTION + j + 1
                    img = pygame.image.load(f"images/angrycucco{index}.png")
                    row.append(img)
                Cucco.frames_angry.append(row)

    #cucco draw method, same as link
    def draw(self, screen, room_x, room_y):
        #draws normal frames by default, cuccos start off in a great mood
        img = self.image

        if Cucco.frames_normal and Cucco.frames_normal[self.dir] and Cucco.frames_normal[self.dir][self.frame]:
            candidate = Cucco.frames_normal[self.dir][self.frame]
        if candidate is not None:
            img = candidate

        LOCATION = (self.x - room_x, self.y - room_y)
        SIZE = (self.w, self.h)
        screen.blit(pygame.transform.scale(img, SIZE), LOCATION)

        #draws the angry sprites when angry_mode is true
        if self.angry_mode:
            if Cucco.frames_angry and Cucco.frames_angry[self.dir] and Cucco.frames_angry[self.dir][self.frame]:
                candidate = Cucco.frames_angry[self.dir][self.frame]
            if candidate is not None:
                img = candidate

            LOCATION = (self.x - room_x, self.y - room_y)
            SIZE = (self.w, self.h)
            screen.blit(pygame.transform.scale(img, SIZE), LOCATION)

    #same concept with tracking link down, but instead going away to prevent being stuck
    def push_off_link(self, distance=40):
        dx = self.x - Cucco.linkx
        dy = self.y - Cucco.linky

        #if it's basically on top of Link, choose a default push direction
        if dx == 0 and dy == 0:
            dx = 1

        length = math.sqrt(dx*dx + dy*dy)
        #if the distance between the cucco and link < than the cucco's own size
        if length < max(self.w, self.h):
            #make sure the distance isn't 0, kept as length for consistency reasons
            if length < 0.001:
                length = 0.001
            #moves the cucco outward from link in the direction it was already going
            direction_to_go_x = dx / length
            direction_to_go_y = dy / length
            self.x = Cucco.linkx + direction_to_go_x * distance
            self.y = Cucco.linky + direction_to_go_y * distance

    #resets angry mode, as well as the hit counter and the number or cuccos
    #that disappear   
    def reset_angry_mode(self):
        Cucco.hit_counter = 0
        Cucco.num_vanished = 0
        Cucco.angry_mode = False
        
        Cucco.cooldown_timer = Cucco.cooldown_frames

        #push this cucco away from Link so they don't overlap
        self.push_off_link(distance=40)

    #method from the instructions that tracks down links 
    #postition and sends the cuccos there
    def hawk_link_down(self):
        #find Link and fly to him
        dx = Cucco.linkx - self.x
        dy = Cucco.linky - self.y
        length = math.sqrt(dx*dx + dy*dy)
        
        #if the cuccos are within 10 units, set attachment to true and start the timer
        if length < 10:
            self.attached_to_link = True
            self.attach_timer = 0

        #make sure the distance isn't 0
        if length < 0.001:
            #self.attached_to_link = True
            length = 0.001
        
        direction_to_go_x = dx / length
        direction_to_go_y = dy / length
        self.x += direction_to_go_x * self.cucco_speed
        self.y += direction_to_go_y * self.cucco_speed

    def update(self):
        #save the previous position
        self.px = self.x
        self.py = self.y

        if Cucco.cooldown_timer > 0:
            Cucco.cooldown_timer -= 1

        #checks to make sure that there is at least one cucco, or that three have vanished
        #and resets angry mode and counters if this is true
        if (Cucco.num_cuccos <= 1 or Cucco.num_vanished >= 3) and Cucco.angry_mode:
            Cucco.angry_mode = False
            Cucco.hit_counter = 0
            Cucco.num_vanished = 0

        #resets counters and speed, as well as if the cuccos are attached to link
        #after they have calmed down
        if not Cucco.angry_mode:
            self.attached_to_link = False
            self.angry_timer = 0
            self.attach_timer = 0
            self.cucco_speed = 4

        #if the cucco is attached to link, start incrementing the timer until it is
        #greater than or equal to the total time, after that the cucco is removed
        if self.attached_to_link:
            self.attach_timer += 1
            if self.attach_timer >= Cucco.total_attached_time:
                Cucco.num_vanished += 1
                Cucco.num_cuccos -= 1
                self.valid = False
            return self.valid

        #angry mode behavior, increments the timer until it hits the max time, then
        #the cuccos calm down
        if Cucco.angry_mode:
            self.cucco_speed = 12
            self.hawk_link_down()
            self.angry_timer += 1
            if self.angry_timer >= Cucco.total_angry_time:
                self.reset_angry_mode()

        #roomba chicken behavior
        else:
            self.x += self.xdir
            self.y += self.ydir

            if self.xdir > 0:
                self.dir = Cucco.RIGHT
            elif self.xdir < 0:
                self.dir = Cucco.LEFT
        
        #increments the tick for animation purposes
        self.tick += 1
        if self.tick % 10 == 0:
            self.next_frame()

        return self.valid

    def is_cucco(self):
        return True
    
    #cycles through the frames to the correct cucco png, same as link
    def next_frame(self):
        self.frame = (self.frame + 1) % Cucco.FRAMES_PER_DIRECTION
    
    #overriden collision handling, allows the cucco to change directions after colliding with an object
    def handle_collision(self, other):
        #saves previous coordinates
        if not self.angry_mode:
            old_x = self.x
            old_y = self.y

            super().handle_collision(other)

            #direction flipping
            if self.x != old_x:
                self.xdir *= -1

            if self.y != old_y:
                self.ydir *= -1

    #collison detection for when hit by link, increments the hit counter
    def hit_by_link(self, link):
        if not self.angry_mode and Cucco.cooldown_timer == 0:
            Cucco.hit_counter += 1
            print(Cucco.hit_counter)

            #angry mode activation
            if Cucco.hit_counter >= 5:
                Cucco.angry_mode = True

            self.x = self.px
            self.y = self.py

            #prevents link from phasing through the cucco
            link.x = link.px
            link.y = link.py

            #direction flipping
            self.xdir *= -1
            self.ydir *= -1

    #collision detection for when hit by a boomerang, increments the hit counter 
    def hit_by_boomerang(self, b):
        if not self.angry_mode and Cucco.cooldown_timer == 0:    
            Cucco.hit_counter += 1

            #angry mode activation
            if Cucco.hit_counter >= 5:
                Cucco.angry_mode = True

            self.x = self.px
            self.y = self.py

            #"kills" the boomerang
            b.alive = False

            #direction flipping
            self.xdir *= -1
            self.ydir *= -1


class Boomerang(Sprite):
    #class varibles
    num_frames = 4
    frames = None
    B_WIDTH = 18
    B_HEIGHT = 18

    def __init__(self, start_x, start_y, dir, link_speed, image):
        super().__init__(start_x, start_y, Boomerang.B_WIDTH, Boomerang.B_HEIGHT, image)

        #sets speed to be slightly faster than link
        speed = max(1.0, link_speed + 2.0)

        #instance varibles
        self.vx = 0
        self.vy = 0
        self.frame = 0
        self.tick = 0
        self.alive = True

        #same loading concept as with link and cucco
        if not Boomerang.frames:
            #creates an array to animate the boomerang
            Boomerang.frames = []
            for i in range(Boomerang.num_frames):
                #loads correct png based on the array created
                img = pygame.image.load(f"images/boomerang{i + 1}.png")
                Boomerang.frames.append(img)

        #match is the python equivilant of a switch statement
        #determines the direction the boomerang moves
        match dir:
            #down
            case 0:
                self.vx = 0
                self.vy = speed
            #left
            case 1:
                self.vx = -speed
                self.vy = 0
            #right
            case 2:
                self.vx = speed
                self.vy = 0
            #up
            case 3:
                self.vx = 0
                self.vy = -speed
            #default sets to right
            case _:
                self.vx = speed
                self.vy = 0

    def is_boomerang(self):
        return True
    
    def hit_obsticle(self):
        self.alive = False
    
    #draws the boomerang
    def draw(self, screen, room_x, room_y):
        sx = self.x - room_x
        sy = self.y - room_y

        if Boomerang.frames:
            img = Boomerang.frames[self.frame % Boomerang.num_frames]
            
            LOCATION = (sx, sy)
            SIZE = (self.w, self.h)

            screen.blit(pygame.transform.scale(img, SIZE), LOCATION)

    #advances the boomerang based on ticks
    def update(self):
        self.x += round(self.vx)
        self.y += round(self.vy)

        self.tick += 1

        #actual update part coming later
        if Boomerang.frames and Boomerang.frames[self.frame] and self.tick % Boomerang.num_frames == 0:
            self.frame = (self.frame + 1) % Boomerang.num_frames

        return self.alive

class TreasureChest(Sprite):
    #class varibles
    T_WIDTH = 25
    T_HEIGHT = 30

    chest_image = None
    rupee_image = None

    I_FRAMES = 5
    LIFE_FRAMES = 40

    def __init__(self, x, y):
        super().__init__(x, y, TreasureChest.T_WIDTH, TreasureChest.T_HEIGHT, "images/treasurechest.png")

        #instance varibles
        self.state = "CHEST"
        self.rupee_frames = 0
        self.alive = True

        if TreasureChest.chest_image is None:
            TreasureChest.chest_image = pygame.image.load("images/treasurechest.png")
        if TreasureChest.rupee_image is None:
            TreasureChest.rupee_image = pygame.image.load("images/rupee.png")
        
    def is_treasure(self):
        return True

    #draw method     
    def draw(self, screen, room_x, room_y):
        img = TreasureChest.chest_image if self.state == "CHEST" else TreasureChest.rupee_image

        LOCATION = (self.x - room_x, self.y - room_y)
        SIZE = (self.w, self.h)

        screen.blit(pygame.transform.scale(img, SIZE), LOCATION) 
    
    #remains alive unless the state gets changed to rupee
    def update(self):
        if self.state == "RUPEE":
            self.rupee_frames += 1
            if self.rupee_frames >= TreasureChest.LIFE_FRAMES:
                self.alive = False
        return self.alive

   #handles collision with link 
    def collide_with_link(self, link):
        if self.state == "CHEST":
            link.handle_collision(self)
            self.state = "RUPEE"
            self.rupee_frames = 0
        else:
            if self.rupee_frames >= TreasureChest.I_FRAMES:
                self.alive = False
                Link.rupee_count += 1

    #handles collision with boomerang
    def collide_with_boomerang(self, b):
        if self.state == "CHEST":
            b.alive = False
            self.state = "RUPEE"
            self.rupee_frames = 0
        else:
            if self.rupee_frames >= TreasureChest.I_FRAMES:
                self.alive = False
                b.alive = False
                Link.rupee_count += 1


class Model():
    #file name and class varible
    filename = "map.json"
    item_num = 0
    
    #creates arrays for sprites, add queue, remove queue, and items that can be added
    def __init__(self):
        self.sprites = []
        self.to_add = []
        self.to_remove = []
        self.items_i_can_add = []
        self.link = Link(100, 100, link_speed=10)
        self.sprites.append(self.link)
        self.instance_of_items()

        self.load_map()

    #loads map
    def load_map(self):
        # reset the fish count if we're loading (or reloading)
        # the map
        Cucco.reset_cucco()

        self.sprites = [self.link]
        
        # example of reading through the map.json file
        # and loading fishes and the turtle's location
        # open the json map and pull out the individual lists of sprite objects
        with open(Model.filename) as file:
            data = json.load(file)
            #get the lists . as "fishes" and "butterflies" from the map.json file

            #get link data out - these are individual
            #attributes, not a list
            linkx = data.get("linkx", self.link.x)
            linky = data.get("linky", self.link.y)
            self.link.x = linkx
            self.link.y = linky
            self.link.save_previous_position()

            #creates trees, chests, and cuccos based on attribiutes
            trees = data.get("trees", [])
            for entry in trees:
                x = entry["x"]
                y = entry["y"]

                self.sprites.append(Tree(x, y, Tree.TREE_WIDTH, Tree.TREE_HEIGHT))

            chests = data.get("chests", [])
            for entry in chests:
                x = entry["x"]
                y = entry["y"]
                self.sprites.append(TreasureChest(x, y))

            cuccos = data.get("cuccos", [])
            for entry in cuccos:
                x = entry["x"]
                y = entry["y"]
                self.sprites.append(Cucco(x, y, cucco_speed=4))

        file.close()
        
        #create turtle using saved attributes
        #self.turtle = Turtle(turtlex, turtley)
        #self.sprites.append(self.turtle)

        #for each entry inside the fishes list, pull the key:value pair out and create 
        #a new Fish object with (x,y,w,h)
        #for entry in fishes:
        #    self.sprites.append(Fish(entry["x"], entry["y"], Fish.FISH_WIDTH, Fish.FISH_HEIGHT))
    
    #saves the map
    def save_map(self):
        # create lists for each type of sprite you want to save
        trees = []
        chests = []
        cuccos = []

        # go through all of the sprites, saving them into the 
        # appropriate lists
        for s in self.sprites:
            if s.is_tree():
                trees.append(s.marshal())
            elif s.is_treasure():
                chests.append(s.marshal())
            elif s.is_cucco():
                cuccos.append(s.marshal())

        # create the dictionary of sprites, split by what types
        # they are - tress, chest and cuccos are lists, while 
        # linkx and linky are singular attributes
        map_to_save = {
            "trees" : trees,
            "chests": chests,
            "cuccos": cuccos,
            "linkx" : self.link.x,
            "linky" : self.link.y
        }

        # Save to file
        with open(Model.filename, "w") as f:
            json.dump(map_to_save, f)

    #checks to see if a sprite can be placed
    def can_place(self, exists):
        for s in self.sprites:
            if s is exists:
                continue
            if self.does_collision_occur(s, exists):
                return False
        return True

    #creates a single instance of addable items and appends them to the items_i_can_add array  
    def instance_of_items(self):
        self.items_i_can_add.append(Tree (0, 0, Tree.TREE_WIDTH, Tree.TREE_HEIGHT))
        self.items_i_can_add.append(TreasureChest(0, 0,))
        self.items_i_can_add.append(Cucco(0, 0))

    #returns the item that goes in the edit mode box
    def get_item_i_am_adding(self):
        if not self.items_i_can_add:
            return None
        return self.items_i_can_add[self.item_num]

    #method to add sprites to the map based on what item it is
    def add_sprite(self, world_x, world_y):
        ITA = self.get_item_i_am_adding()
        if ITA is None:
            return False
        
        exists = None

        if ITA.is_tree():
            gx = (world_x // 50) * 50
            gy = (world_y // 60) * 60
            exists = Tree(gx, gy, Tree.TREE_WIDTH, Tree.TREE_HEIGHT)
        
        elif ITA.is_treasure():
            dummy = TreasureChest(0, 0)
            cx = world_x - dummy.w / 2
            cy = world_y - dummy.h / 2
            exists = TreasureChest(cx, cy)

        elif ITA.is_cucco():
            exists = Cucco(world_x, world_y)
        
        else:
            return False
        
        if self.can_place(exists):
            self.queue_add(exists)
            return True
        else:
            return False
        
    #method that adds a sprite to the edit mode box based on the item it is    
    def add_edit_mode_sprite(self):
        EMS = self.get_item_i_am_adding()
        if EMS is None:
            return
        
        ex = (self.x // 50) * 50
        ey = (self.y // 60) * 50

        if EMS.is_tree():
            for s in self.sprites:
                if s.is_tree() and s.am_i_clicking_on_you(ex, ey):
                    return
            self.sprites.append(Tree(ex, ey, Tree.TREE_WIDTH, Tree.TREE_HEIGHT))
        
        elif EMS.is_treasure():
            for s in self.sprites:
                if s.is_treasure() and s.am_i_clicking_on_you(ex, ey):
                    return
            self.sprites.append(TreasureChest(ex, ey))

        elif EMS.is_cucco():
            for s in self.sprites:
                if s.is_cucco() and s.am_i_clicking_on_you(ex, ey):
                    return
            self.sprites.append(Cucco(ex, ey))

    #sets the item number
    def set_item_num(self):
        if not self.items_i_can_add:
            return
        self.item_num = (self.item_num + 1) % len(self.items_i_can_add)

    def update(self):
        #creates a snapshot list for the inner loop
        snapshot = list(self.sprites)

        #outer loop, handles removing sprites
        for s1 in snapshot:
            if s1 in self.to_remove:
                continue
            if not s1.update():
                self.queue_remove(s1)
                continue

            kill_current = False

            #inner loop over snapshot only
            for s2 in snapshot:
                if s1 is s2:
                    continue
                if s2 in self.to_remove:
                    continue
                if not self.does_collision_occur(s1, s2):
                    continue

                #link vs tree
                if s1.is_link() and s2.is_tree():
                    s1.handle_collision(s2)
                elif s2.is_link() and s1.is_tree():
                    s2.handle_collision(s1)

                #link vs treasure
                elif s1.is_link() and s2.is_treasure():
                    s2.collide_with_link(self.link)

                #link vs cucco
                elif s1.is_link() and s2.is_cucco():
                    s2.hit_by_link(self.link)
                elif s2.is_link() and s1.is_cucco():
                    s1.hit_by_link(self.link)
                
                #cucco vs tree and/or treasure, since they act the same
                elif s1.is_cucco() and (s2.is_tree() or s2.is_treasure()):
                    s1.handle_collision(s2)
                elif s2.is_cucco() and (s1.is_tree() or s1.is_treasure()):
                    s2.handle_collision(s1)

                #boomerang vs tree
                elif s1.is_boomerang() and s2.is_tree():
                    s1.hit_obsticle()
                    kill_current = True
                    break
                elif s2.is_boomerang() and s1.is_tree():
                    s2.hit_obsticle()
                    self.queue_remove(s2)

                #boomerang vs treasure
                elif s1.is_boomerang() and s2.is_treasure():
                    s2.collide_with_boomerang(s1)
                elif s2.is_boomerang() and s1.is_treasure():
                    s1.collide_with_boomerang(s1)
                
                #boomerang vs cucco, only one due to having two messing with the hit counter
                elif s1.is_boomerang() and s2.is_cucco():
                    s2.hit_by_boomerang(s1)
            
            #removes sprites that flag kill_current
            if kill_current:
                self.queue_remove(s1)
        
        #concurrency error prevention
        #clears the to_remove array
        if self.to_remove:
            #iterates through all sprites, fliters out sprites in to_remove, and builds a new list
            self.sprites = [s for s in self.sprites if s not in self.to_remove]
            self.to_remove = []

        #clears the to add array
        if self.to_add:
            #takes every sprite from to_add and adds them into the sprites list all at once
            self.sprites.extend(self.to_add)
            self.to_add = []

    #clears map
    def clear_map(self):
        self.sprites.clear()
        self.sprites.append(self.link)
        # calling a static method - notice the lack of 'self'
        Cucco.reset_cucco()

    #throws boomerang
    def throw_boomerang(self):
        cx = self.link.x + self.link.w // 2 - 9
        cy = self.link.y + self.link.h // 2 - 9
        dir = self.link.dir
        ls = self.link.link_speed
        b = Boomerang(cx, cy, dir, ls, "images/boomerang1.png")

        self.queue_add(b)

    #collision checking
    def does_collision_occur(self, a, b):
        if a.x >= b.x + b.w:
            return False
        if a.x + a.w <= b.x:
            return False
        if a.y >= b.y + b.h:
            return False
        if a.y + a.h <= b.y:
            return False
        return True

    #queues 
    def queue_add(self, s):
        self.to_add.append(s)

    def queue_remove(self, s):
        self.to_remove.append(s)


    # pos was passed as the mouse position tuple - pos[0] is x, 
    # pos[1] is y
    #def add_tree(self, pos):
    #    self.sprites.append(Tree(pos[0], pos[1]))

class View():
    #class varibles
    GAME_WINDOW_WIDTH = 800
    GAME_WINDOW_HEIGHT = 600
    CURR_ROOM_X = 0
    CURR_ROOM_Y = 0

    def __init__(self, model):
        SCREEN_SIZE = (self.GAME_WINDOW_WIDTH,self.GAME_WINDOW_HEIGHT)
        self.screen = pygame.display.set_mode(SCREEN_SIZE, 32)
        self.model = model

    #update room view, use the j or js way, doesn't really matter
    def update_room_view(self):
        center_x = self.model.link.x + self.model.link.w / 2
        center_y = self.model.link.y + self.model.link.h / 2

        room_x_multiplier = center_x // self.GAME_WINDOW_WIDTH
        room_y_multiplier = center_y // self.GAME_WINDOW_HEIGHT

        self.CURR_ROOM_X = room_x_multiplier * self.GAME_WINDOW_WIDTH
        self.CURR_ROOM_Y = room_y_multiplier * self.GAME_WINDOW_HEIGHT


    def update(self):
        self.update_room_view()
        GREEN_COLOR = (74, 103, 65)
        # change background color if the user is in edit_mode
        if Controller.edit_mode:
            self.screen.fill([146, 203, 146]) #light green
        else:
            self.screen.fill([72, 152, 72]) #dark forest green

        # draw sprites to the screen
        for sprite in self.model.sprites:
           sprite.draw(self.screen, self.CURR_ROOM_X, self.CURR_ROOM_Y)

        #draws the edit mode box
        if Controller.edit_mode:
            pygame.draw.rect(self.screen, GREEN_COLOR, pygame.Rect(0, 0, 100, 100))

            EMS = self.model.get_item_i_am_adding()
            if EMS is not None:
                preview_x = (100 - EMS.w) // 2
                preview_y = (100 - EMS.h) // 2

                fake_room_x = EMS.x - preview_x
                fake_room_y = EMS.y - preview_y

                EMS.draw(self.screen, fake_room_x, fake_room_y)
        # add text to the screen
        # Default font, size 32
        font = pygame.font.SysFont(None, 32)   
        text_string = "Link has collected " + str(Link.rupee_count) + " rupees!"
        RED_COLOR = (255, 0, 0)
        text_surface = font.render(text_string, True, RED_COLOR)
        TEXT_LOCATION = (250, 10)
        self.screen.blit(text_surface, TEXT_LOCATION)
        
        # update display screen
        pygame.display.flip()

class Controller():
    #class varibles
    edit_mode = False
    space_held = False

    edit_bx = 10
    edit_by = 10
    edit_bw = 100
    edit_bh = 100
    
    def __init__(self, model, view):
        self.model = model
        self.view = view
        self.keep_going = True

    def update(self):
        for event in pygame.event.get():
            if event.type == QUIT:
                self.keep_going = False
            elif event.type == KEYDOWN:
                if event.key == K_ESCAPE or event.key == K_q:
                    self.keep_going = False
            #handles the clicking aspect of adding sprites and switching the edit box sprite
            elif event.type == pygame.MOUSEBUTTONUP:
                if Controller.edit_mode:
                    sx, sy = event.pos

                    mouse_x = sx + self.view.CURR_ROOM_X
                    mouse_y = sy + self.view.CURR_ROOM_Y

                    in_edit_box = (sx >= self.edit_bx and sx <= self.edit_bx + self.edit_bw
                                   and sy >= self.edit_by and sy <= self.edit_by + self.edit_bh)
                    if in_edit_box:
                        self.model.set_item_num()
                        return
                    
                    x = (mouse_x // 50) * 50
                    y = (mouse_y // 60) * 60

                    if self.model.get_item_i_am_adding():
                        self.model.add_sprite(x, y)

            elif event.type == pygame.KEYUP: #this is keyReleased!
                if event.key == K_c:
                    self.model.clear_map()
                    print("Map cleared and game reset")
                if event.key == K_e:
                    Controller.edit_mode = not Controller.edit_mode
                if event.key == K_l:
                    self.model.load_map()
                    print("Map loaded")
                if event.key == K_s:
                    self.model.save_map()
                    print("Map saved")
                if event.key == K_SPACE:
                    self.space_held = False
        keys = pygame.key.get_pressed()
        # turtle's movement function changed to be closer related
        # to Link
        link = self.model.link
        link.save_previous_position()
        if keys[K_LEFT]:
            self.model.link.move_left()
        if keys[K_RIGHT]:
            self.model.link.move_right()
        if keys[K_UP]:
            self.model.link.move_up()
        if keys[K_DOWN]:
            self.model.link.move_down()
        if keys[K_SPACE]:
            if not self.space_held:
                self.model.throw_boomerang()
                self.space_held = True

        Cucco.linkx = self.model.link.x
        Cucco.linky = self.model.link.y
            

print("Use the arrow keys to move. Press Esc to quit.")
pygame.init()
pygame.font.init()
m = Model()
v = View(m)
c = Controller(m, v)
clock = pygame.time.Clock()
while c.keep_going:
    c.update()
    m.update()
    v.update()
    #sleep(0.04)
    #went with a pygame clock so that my game could run in 30 fps
    #looks a lot more polished imo
    clock.tick(30)
print("Goodbye!")