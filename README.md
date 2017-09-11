### Breakout Game, Computer Science 308

### General Info

 * Name of project designer: David Tran
 * Date started: August 30, 2017
 * Date finished: September 10, 2017
 * Estimated number of hours worked on project: 35

### Documentation

 * The code from "gameFont.css" was retrieved from https://stackoverflow.com/questions/33336542/javafx-custom-fonts.
 * The Fleftex\_M.tff file was downloaded from https://www.dafontfree.net/find.php?q=FLEFTEX\_M.TTF.
 * The GameEngine.java class was inspired by Robert Duvall at https://coursework.cs.duke.edu/CompSci308\_2017Fall/lab\_bounce/blob/master/src/ExampleBounce.java. The methods are specified in the class file.
 * The Bouncer.java class was also inspired by Robert Duvall at: https://coursework.cs.duke.edu/CompSci308_2017Fall/lab_bounce/blob/master/src/Bouncer.java and by https://gamedev.stackexchange.com/questions/4253/in-pong-how-do-you-calculate-the-balls-direction-when-it-bounces-off-the-paddl.
 * The Paddle.java class was inspired by https://www.ntu.edu.sg/home/ehchua/programming/java/JavaEnum.html.
 * The Brick.java class was also inspired by https://www.ntu.edu.sg/home/ehchua/programming/java/JavaEnum.html.

### Project Files
 * File used to start the project: GameEngine.java
 * File used to test the project: n/a
 * Resource files required by the project are Fleftex\_M.tff and gameFont.css (within the game\_dht9 package). These files support the font of the text in the starting scene and in the HUD. Please note that the path to the gameFont.css must not have a space in it (for example, the computer user cannot be "David Tran" but instead "DavidTran"). If this problem does occur, the game should not crash. Only the font of the text in the game will be effected.

### Bugs
 * When the paddle has the "Edge Warp" ability, it will get stuck in the screen edge when the player stops moving the paddle after a portion of it has already passed the screen edge. The player will have to move the paddle in the opposite direction to move the paddle.
 * The bouncer may collide physically incorrectly with a brick when it hits multiple bricks at a time or hit the very edge/corner of a brick.

### Gameplay Design Flaws (discussed more in Sprint 3)
 * The game does not let the top paddle start the bouncer.
 * The game does not allow the user to pause or go back to the start menu.
 * When the game-winning screen shows, the HUD shows Level 4.
 * The "extend" cheat key does not extend the paddle when the paddle has the "Extended" ability. You can still toggle the length from normal to extended, however.
 * You must restart the program in order to re-play the game after winning.

### Impressions
This game does it job of allowing players to play a variant of Breakout with abilities and power-ups that may enhance their experience. The text font and colors are aesthetically pleasing and the HUD makes keeping track of the game more convenient for the players. However, this game has its bugs and design flaws as noted above, which may hurt the players' experiences. This game does allow a lot of room for improvement for my design skills and planning.
