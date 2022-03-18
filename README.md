# Maze

### Main Menu:
![Main Menu](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/main.PNG)
- Press 'n' to start playing a new game, 'r' to replay a saved game, 'l' to load a saved game, 's' to go to the settings, 'q' to exit.
- Mouse operations supported.

### Enter Seed:
![seed](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/seed.PNG)
- Seed is a number that determines how the maze would generate; different seed numbers would generate distinct mazes.
- Press 's' to finish entering the seed and start the game.

*An alternative: Press 'r' to skip manually entering the seed number and use a randomly generated seed.*

### The Start:
![start](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/start.PNG)
- At the start of a game, the player has a health of 256, vision of 2, obscured vision of 1, and is in the default White appearance.

### Collect Visions:
![vision](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/vision.PNG)
- Collecting a Vision (the sun symbol) would increment the player's vision by 1, allowing the character to see more.
- Collecting a certain amount of visions would change the appearance of the character, such as its color and trace.
- A special sound would be played upon the success of collecting a Vision.

### Trigger a Lightning:
![lightning](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/l0.PNG)
![lightning](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/l1.PNG)
- Triggering a lightning would enable the player to temporarily view the entire maze.
- A lightning would appear on a random place in the maze after every 5 collections of Vision.
- A special sound would be played upon the success of triggering a lightning.

### Use a Portal:
![portal](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/portal0.PNG)
![portal](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/portal1.PNG)
- Stepping on a portal would immediately transfer the character to another portal.
- All portals except the one being stepped upon have a chance to be the destination.
- There would be 3 to 6 portals randomly placed in the maze at the start of each game.
- The minimum distance between any two portals is 10.
- A special sound would be played upon the success of teleportation.

### Special Abilities:
##### Damage the surrounding walls
![damage](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/d0.PNG)
*Press ":" then "d" or simply mouse-click the yellow-colored "(D)amage"*
![damage](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/d1.PNG)
- The use of this ability would cost 16 health.
- If there is no wall around the player, the operation would be ignored.
- A special sound would be played upon the success of casting this ability.

##### Teleport to a random place
![teleport](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/t0.PNG)
*Press ":" then "t" or simply mouse-click the yellow-colored "(T)eleport"*
![teleport](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/t1.PNG)
- The use of this ability would cost 10 health.
- A special sound would be played upon the success of casting this ability.

##### Summon a lightning
![teleport](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/t0.PNG)
*Press ":" then "t" or simply mouse-click the yellow-colored "(T)eleport"*
![teleport](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/t1.PNG)
- The use of this ability would cost 10 health and 1 vision.

### Next Level:
![the door](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/final.PNG)
- Upon collecting enough visions, a door would appear on the maze, and the player can view the entire maze from now on.
- Getting to the door would take the player to the next level: reset the character, gain 64 health, and be put in a different maze.

![next level](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/nextLevel.PNG)

### Game Over:
![game over](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/fail0.PNG)
- If the player cannot make to the next level before Health reaches 0, the game would end and a score would be displayed.

![game over](https://github.com/alexxuyaowen/maze/blob/master/Maze/Demo/fail1.PNG)

### Other Features:
- The white text on the upper left corner indicates what the mouse is hovering over.
  - For example, if the mouse is hovering over the avatar, the text would be "player"; hovering over the wall, it would be "wall"; hovering over a accessible place, it would be "floor"; hovering over an inaccessible place or a place beyond the vision range, it would be "nothing"
- Mouse-click on an accessible place would take the character to there along the shortest path.
- Press ":" then "q" to quickly save and exit the game.
- Press ":" then "s" to save the game without leaving the game.
- Press ":" then "n" to go the main menu (without saving).
- Press ":" then "f" to turn on/off fullscreen (see the entire maze at no cost); this option is for the developers and not the players.
- Press "f" then "r/y/w/g/b" to manually change the appearance of the character to be "red/yellow/white/green/blue"; this option is just for fun.





