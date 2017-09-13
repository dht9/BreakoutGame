###Breakout Game, Computer Science 308

####High-Level Design Goals

One of my design goals was to create a separate class from every unique object in the game (i.e. bouncer, paddle, brick, status display, splash screen). The reason for this was to break the project down into organized, purposeful parts for efficient development. 

A goal that I did not quite reach was to divide the class that extended JavaFX Application (GameEngine.java) into smaller classes that each had a unique role, rather than have one enormous "main" class that tied everything together. I am still not quite sure how I could achieve that, although I think I made a decent attempt by creating the `HeadsUpDisplay`, `StartMenu`, and `Team` classes. This made debugging less difficult than before but still more difficult than if I manage to break up GameEngine.java into even more classes.

####How to Add New Features

To add a new feature for an object such as the bouncer, paddle, or brick, the respective classes need to be modified. If it is a new attribute of that object, the constructor needs to be modified to compensate for that new addition. Depending on what that new feature should do, methods should be defined in the classes, and the class GameEngine.java should be updated to call those methods on the object instances whenever needed. 

For example, for the paddle ability feature, I added the following code (plus more but not shown):

```java
enum PaddleAbility {
		EXTENDED, STICKY, EDGEWARP, NORMAL;
	}
```

For each of the types of paddle abilities, I created a method that enabled those abilities and modified GameEngine.java to call those methods based on the current paddle ability in the application:

```java
// Detect paddle, screen edge collision.
private void checkPaddleOutOfBounds() {
	if (myPaddle1.hasAbility(PaddleAbility.EDGEWARP)) {
		myPaddle1.edgeWarp();
		myPaddle2.edgeWarp();
	}
	...
}
```
For features not related to these specific objects, then the user must create methods and/or classes that define and implement those features. For this project, GameEngine.java integrates all of the features into the Application so methods can be created there as well as in a separate class that is then referenced by GameEngine.java. For example, to add the status display feature, I created HeadsUpDisplay.java and added the following code to `createGameScene` in GameEngine.java:

```java
HeadsUpDisplay hud = new HeadsUpDisplay();
	hud.createHUDLabel(" Level ", level, "left");
	hud.createHUDLabel("Team Lives Remaining: ", teamLives, "right");
	hud.createHUDLabel(" Paddle Ability: ", paddleAbility, "bottom");
```

As you can see, adding a feature requires implementation in GameEngine.java and is better to create a new class for that feature rather than adding more definitions and methods in the already large GameEngine.java class.


####Design Choice Justifications

The design choice that I highly favor involves creating a HeadsUpDisplay object and StartMenu object in their own separate classes, rather than jumbling there definitions and implementations into GameEngine.java. The pros of these changes are better organization and partitioning of project goals into their own classes and better readability of the high-level structure of the project. The cons are not having all of the UI components in one class, which was what the initial design consisted of, but the trade-off highly favors the pros. Further analysis can be found in the comments in HeadsUpDisplay.java.

Another major design choice involved handling most collision detection GameEngine.java or Bouncer.java. In the JavaFX Application, ImageView and shape intersect was checked in each frame in GameEngine.java. If the Bouncer intersected with a paddle, GameEngine.java would call methods on the bouncer to handle that collision. Why is this collision handling defined in Bouncer.java and not Paddle.java or GameEngine.java? This is because the bouncer itself is effected by that collision. In other words, fields of the bouncer need to be modified when that collision occurs. 

For example, in Bouncer.java the following method is called when the bouncer collides with the top/bottom of a paddle.

```java
private void calcBounceVelocity(Paddle myPaddle) {
	double distFromCenter = myView.getX() + myView.getFitWidth() / 2 - (myPaddle.getX() + myPaddle.getWidth() / 2);
	double normalizedDistFromCenter = distFromCenter / (myPaddle.getWidth() / 2);
	double bounceAngle = normalizedDistFromCenter * MAX_BOUNCE_ANGLE;
	double bouncerVx = BOUNCER_SPEED * Math.sin(Math.toRadians(bounceAngle));
	double bouncerVy = BOUNCER_SPEED * Math.cos(Math.toRadians(bounceAngle));

	if (isStartingAtPaddle1())
		myVelocity = new Point2D(bouncerVx, -bouncerVy);
	else
		myVelocity = new Point2D(bouncerVx, bouncerVy);
}
```
The paddle itself is not effected by the collision, only the bouncer is (more specifically, its velocity). This is why Bouncer.java has a method to treat this event and update the bouncer's velocity. The reason this method is not located in GameEngine.java is because it would defeat the purpose of having a Bouncer class in the first place. The Bouncer class should not merely be a "data holder". It should be able to handle the bouncer data itself whenever it is told to do so. The same could be said when a paddle intersects the screen edge and when the bouncer hits a brick. Methods need to be called on those instances and the data needs to be manipulated by the instances' classes rather than be the "main" class. 

####Assumptions/Decisions

One major assumption made in the design of this project is that the text file containing the brick level layout was formatted in a 28x21 grid of numbers from 0-10 (excluding 4) or else the program may crash. The reason behind this specific dimension is that this grid satisfies the combination of the screen width and height with the defined brick width and height. This was found through trial and error, so there must be a more efficient way of determining the size of the text file grid and loading in the brick layouts into the JavaFX application without having an issue of the text file given. Possibly the brick height and width could be algorithmically determined given the size of the text file is always "correct"? This approach could eliminate the assumption that a text file will always be a 28x21 grid. In addition, it is assumed that the text file contains the number of rows and columns in the first line (see Level1.txt). If these numbers are wrong, the program may crash.

