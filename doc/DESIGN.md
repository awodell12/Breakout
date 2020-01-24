##Final Design of Breakout Game

### Developed by Austin Odell 
#### Based on ExampleBounce code from Prof. Duvall

### Design Goal: Make it simple to expand
A large part of the design goal (that was achieved to various levels of success)
was to make the game adaptable to handle new features. The types of features I wanted
to be able to handle were new types of Bricks, types of Power-Ups, and level designs.
I'd say this was done pretty well for the level design, moderately well for Bricks, and poorly done for the Power Ups.

###High-Level Design
The design of the class was mostly focused around the Breakout class. This extended the 
Application JavaFX class and handled almost all game-play events. It was assisted by three classes:
Brick, Input Handler, and PowerUp. This class was responsible for the set-up of the stage and also created 
the timeline for the animation. This class created and set the starting and ending game splash screens. It handled the creation of the levels based off a 2D array
it received from the InputHandler class. It then handled the step of the scene and updating all of the Nodes
in the scene. This included handling collisions of the ball with bricks, the sides of the stage, and the paddle. 
Additional collisions between power-ups and the paddle were also done in this class. 
It also controlled the effects of power-ups and cheat keys. So from a high-level this class basically ran, updated, and
displayed the whole game. 

The Input Handler takes in a text file with a desired level design and translates it into a 2D array of the brick layout. 
This array is then passed back to the Breakout class which reads it to create new Brick objects at the specified locatoin
and with the specified type. 

The Brick class extended Rectangle and really only added the encapsulation of one extra variable, being the bricks health.
The health is set via the constructor and then there is a method to decrease the health and also to 
get the current Health. From Rectangle bricks were static objects in the playing field that the ball could
bounce off of (collide) and would go invisible when their health hit 0. 

The PowerUp class extended the Circle class and only added a random assignment of a power-up type to the
circle upon its construction. This was equally likely to be any of the types of power-ups set 
by NUM_POWERS. Using Circles intersect method the game was able to tell when a power-up intersected the paddle
and was thus earned by the player. The main Breakout class handled the effect of the power-up and also the removal
of the effect after a set length of time.  

###Assumptions
Input Text File: I assumed that all input files would adhere to the format of brick types separated by spaces 
and new rows of bricks indicated by moving to the next line. I also assumed the max dimensions of the game 
and thus the text file to be 20 rows and 13 columns. Also I assumed the characters representing bricks would all be integers
0-9. 

Bricks: I assumed all collisions between bricks and the ball would have the same physics. This limits adding 
bricks that could change how the ball bounces off of it. 

Ball: I assumed it was only possible to have one ball and this would make it hard to add
more balls via cheat keys or power-ups. Everything was done on a single ImageView rather than on a 
more generalized Ball class. 

###Adding New Features
Features from plan I didnt get to add include: moving bricks, laser-shooting paddle, sticky-paddle, and multi-ball power ups. 
Correspondingly I didnt include the planned cheat keys of instant laser activation and instant extra ball. 

Adding Levels: This would be pretty simple all I would need is to create new text file with the brick 
layout and then change the MAX_LEVEL constant(This assumes all of the desired bricks are already implemented). 

Adding New Bricks: This would be of medium difficulty. If the brick had the same collision physics. All I would then need
is to decide on is the health of the brick. If the collisions were different then I'd have to add a new if-statement
to my collision method in the Breakout class to implement the new effects on the ball. Lastly if the brick were to have
its own movement I would have to add a method in Step that changed the location of these moving bricks for every step of the scene.
 
Adding More Balls: With the current design this would not be easy. To add a second ball I could just create a second ImageView
Instance and then duplicate all of the code for the original ball. Not only would this be poor design it would then limit 
the number of balls to 2 (or however many times I duplicated the code). Instead a better was to do this would be to create a
Ball Class that could handle updating its own position especially if it collided with another node it bounced off of. 

Sticky Paddle would be adding a whole new feature because currently the paddle and bar are centered and immovable
when they are stuck together(like at the start of a level or the reset cheat key). I would have to like override the 
normal movement of the ball and instead attach it to the movement of the paddle. I would also need to decide how the 
ball would release because using my current strategy from level starts of it going where you click seems like it would 
make this power-up pretty "cheesy". 

Lasers: I would need to create a new power-up for lasers in the same way that I did the rest of the power-ups. This wouldn't
be too bad as it would just entail a new power-up type and then an activate lasers method allowing the up-key to produce 
new Circle objects. Then I would need to include collision checks for bricks and lasers into my checkCollisions method 
(currently checks if a ball hits the bricks) or create a separate method for checking brick-laser collisions and giving score.
Lastly I would need to add a new method to step to update the positions of the lasers with every scene step to make them 
seem like they are moving at a certain speed. 