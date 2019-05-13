game
====

This project implements the game of Breakout.

Name: Sam Chan, Jaiveer Katariya

### Timeline

Start Date: 1/28/19

Finish Date: 2/11/19

Hours Spent: 25-30 hours per person (on just the complete portion)

### Resources Used

Stackoverflow, JavaFX documentation, Tutorialspoint

### Notes about Testing
To run tests, simply press the following three keys in this order ONCE THE LEVEL HAS LOADED: ,./
The test file used for running tests is specified at the top of the PlayLevel class, where the tester
can specify between several types of events: a corner bounce test (to check if a ball bounces off a corner correctly), 
a destroy brick test (to make sure a brick gets destroyed when the ball hits it), a lose life test (to make sure that
the user loses a life when the ball goes out of bounds on the bottom), a sizePower test (to make sure the paddle 
appropriately doubles in size when the paddle hits the powerup), and a brickTupe test (to make sure that the bricks
appropriately transitioned from one brick type to the correct type when it was hit, i.e., grey to green and green to 
blue). 

One can specify which test they want to run on the first line of the test config files, and the next lines are the ball's
initial x position, the ball's initial y position, the ball's initial x velocity, the ball's initial y velocity, and
lastly, the file from which to load the bricks. There is already at least one text file for each kind of test in the
data folder, but if the user wishes to make their own, they can do so by following the aforementioned format, placing
the test file in the data folder, and specifying the test file in the code for the PlayLevel class. 

Note about the corner test - the user can specify the x and y coordinates of the ball, but in order for
the ball to accurately hit the corner and bounce back correctly, the ball must be positioned along either
central diagonal axis - otherwise, the ball will not hit the corner and the test will incorrectly return a success.
Moreover, to ensure that nothing stands in the balls way, if bricks are loaded into the corner bounce test, the ball
will not interact with them and instead just "go through" them.

Note about the size power test - In the size power test, the test will never end unless the user controls the paddle to
catch a powerup. The user must use the arrow keys as they would in the game when each brick is broken, and catch a size
power up appropriately. Once the power is caught, the tester will check to see if the paddle correctly grew twice in
width, and if it did, it will show the test passed screen.

Note about "level specific test" - In our game, the way that we chose to make our game harder each level was to increase
the ball's x and y velocities by 50 for each level after level 1. However, given the way that we made our tests, where
they were independent of this factor since the user specifies the ball's starting x and y velocities in the test
configuration files. So, in order to make sure this was working correctly/in order to be able to test it, we have a 
boolean at the top of the PlayLevel class called LEVEL_TEST. If LEVEL_TEST is true, the game will output to the console
what the ball's initial speed in level 1 was, and how that should change relative to the current level that the user is on.
This way, even though we could tell just by playing the game, we could determine for sure if the ball was being 
reinitialized at a higher speed for each new level after level 1. The user can remove this output from the console by
setting the LEVEL_TEST boolean to false. 

### Running the Program

Main class: MainScreen.Java

Data files needed:
Files in /data directory: ball.gif, brick1.gif, paddle.gif, sizepower.gif, test.txt (blocks file), testFile1.txt

Key/Mouse inputs:

- Any key/mouse input to start the game
- Left/Right arrow buttons to move paddle
- A/D to move second "defender" paddle

Cheat keys:

- L for gain Life
- R for reset position
- ,./ for test cases (ALL THREE IN THAT ORDER)
- Click for pausing the ball, but you can still move the paddle
- Type sam to automatically win, or jvr to automatically lose (this was Sam's idea)
- Pressing a number will automatically load that level (1, 2, or 3)

Known Bugs:
The single known bug in our program takes place upon the initialization of each level. If, in the first second or so,
the user rapidly presses the arrow keys to move the paddle, for some reason, the loadGame() function seems to get called
repeatedly, and the ball resets to its initial position and velocity. The game works perfectly fine after that first
second or so, and we remain unsure as to why this happens. 

Extra credit:
For extra credit, we added a second paddle to make it a two player game, or simply make it a more difficult single
player game. The second paddle is intended to act as a "defender of the bricks," which the second player can control 
using the A and D keys. Intentionally, the ball will go through the defender on the way down, but will deflect back
towards the paddle if hit on the bottom.  

### Notes
If there are any questions at all about how to use our program/run our test cases, please do not hesitate to reach
either one of us at jk386@duke.edu (Jaiveer Katariya) or spc28@duke.edu (Sam Chan).

### Impressions
We enjoyed this project, because it let us be really creative with how we implemented different parts of the game.  It was also fun to see features coming to life right in front of us, but testing them was an interesting experience, as we'd continually uncover glitches we never knew were even possible, which prompted us to spend a lot of time to take care of every edge case we could think of.
