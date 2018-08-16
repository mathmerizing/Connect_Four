# Connect-Four
*by Yerso Checya Sinti and Julian Roth* <br><br>
A Java version of the game "Connect Four". Learning about Minimax and Alpha-beta pruning.

## MODES:
- single player
- multiplayer

### SINGLE PLAYER:
  The user plays against a bot. <br>
  Currently supported:
  - bot playing random (possible) moves
  - bot implementing minimax and alpha-beta pruning

### MULTIPLAYER:
  User1 plays against User2 on the same PC.
 
Currently working on a good evaluation function and a general performance optimization.

**__Ideas:__** - preprocess<sup>[1](#myfootnote1)</sup> the first eight moves and create a dictionary with moves to play in a given situation

<br><br><br><br><br>

<a name="myfootnote1">1</a>: Preprocessing with https://archive.ics.uci.edu/ml/machine-learning-databases/connect-4/ <br>
Other resources that we used: https://codereview.stackexchange.com/questions/82647/evaluation-function-for-connect-four
