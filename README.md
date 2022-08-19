## Introduction
It is a text-based tower defense game built using Java. I include some ASCII images and sound effects to add more fun to the game. The players can also choose the difficulty of the game (normal mode or hard mode).


## Brief Explanation of the Game
“Till the End” is a defence and attack game between the tower and the dragon. The player is the mayor of Kuala Lumpur to protect the city from dragon invasion. In the game, dragon is designed to attack the city by giving damage to city’s wall. During the invasion, the city is protected by wall and tower that can attack the dragon. The dragon will fly away after attacking the wall for 10 times. Player can claim tax from citizens to upgrade the tower and the wall. In the game, the events that occur in different seasons will trigger different emotions of citizens. Plus, the towers and walls could be upgraded too. Besides that, player will also have a chance to get gold to upgrade the features in the game. These will indirectly affect the game. The player will win the game if the dragon is being successfully killed. (the health of dragon reduces to 0). Otherwise, the player loses the game if the dragon succeeds in breaking the wall (the health of wall reduces to 0).


## Basic Requirements of the Porject
The project requires us to create a game with the content of attacking or protecting the city from invasion, in this case, which is the dragon. We have to create a Java abstract class named “SameBehaviour” with relevant data fields and abstract methods declared. At the beginning of the game, a level 1 dragon will attack the city. After attacking 10 times, the dragon will fly away. The game will continue from Year 1, Spring to next season with retaining wall’s health. The event will happen and tax will be collected from citizens. Player can use the gold to take different actions to protect the city such as upgrading wall and tower. The dragon will attack the city every season. We assign the condition in a loop. It will terminate until the player reach win condition or lose condition.


## Extra Features
1.  Hard mode
    We create a game mode which is harder than usual by increasing the difficulty of the game. For example, we reduce the amount of tax that can be collected from citizens. We also increase the dragon’s stats.
2.  Diversity
    In hard mode of the game, we add some new events that will bring different effects to enhance the diversity of the game.
3.  Save and Load System
    We create a system that allows user to save the current stage. User can continue the game at the saved stage without starting the game all over again.
4.  Sound effects
    We add sound effects at the start of the game, in the middle when tower and dragon are attacking each other to make the game more interesting. There are also sound effects added when the player wins or loses the game.


## Approach Taken to Solve
At first, we divided into few groups to complete the basic coding of each class. Then, we connected every class to make it work. In working on the extra features, we created another class and edit some method function for Hard mode, Diversity and Save and Load System. For the sound effects, we downloaded JLayer library from Internet to read the mp3 file and play it as sound effect. Other than that, we also added ascii artwork in our code to make our project more interesting and it showed our creativity. During the process, we discuss together when we faced problems. Besides that, we did a lot of searching on Internet on designing our coding to make the game work. In order to add in extra features in our game, we also ask for helps from some experts to modify our game.
