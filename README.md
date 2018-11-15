# Four In A Row
*by Yerso Checya Sinti and Julian Roth* <br><br>
A Java version of the game "Connect Four". Learning about Minimax and Alpha-beta pruning.

## OUR FOUR IN A ROW APP:
<a href=""><img src="" width="300"></a>

## MODES:
- single player
- multi player

### SINGLE PLAYER:
  The user plays against a bot. <br>
  Currently supported:
  - bot playing random (possible) moves
  - bot implementing minimax and alpha-beta pruning (recommended depth: 8 [plies](https://en.wikipedia.org/wiki/Ply_(game_theory)))

### MULTI PLAYER:
  User1 plays against User2 on the same PC.
 
 ## USER INPUT:
 Follow the instructions.<br>
 When asked to enter your move, please enter an integer between 0 and 6. <br>
 0 is the column on the far left and 6 is the column on the far right.
 
## GUIDE TO MINIMAX AND ALPHA-BETA PRUNING:

[![https://www.youtube.com/watch?v=l-hh51ncgDI&index=3&t=0s&list=LL1ZTjVhPHvlgFev4ryx0suQ%2F0%2F](http://i3.ytimg.com/vi/l-hh51ncgDI/hqdefault.jpg)](https://www.youtube.com/watch?v=l-hh51ncgDI&index=3&t=0s&list=LL1ZTjVhPHvlgFev4ryx0suQ%2F0%2F) <br>
Click on the picture or the link: https://www.youtube.com/watch?v=l-hh51ncgDI&index=3&t=0s&list=LL1ZTjVhPHvlgFev4ryx0suQ 
<br>

## IDEAS FOR OPTIMIZATION:
Implement [
Iterative deepening depth-first search](https://en.wikipedia.org/wiki/Iterative_deepening_depth-first_search) which keeps the time per move fixed and adjusts the depth of the minimax algorithm accordingly. <br>
Create a opening move database (preprocess the computationally intensive first few moves).
<br>

## MORE INFORMATION:
Please checkout the more in depth documentation on the project's [Wiki page](https://github.com/mathmerizing/Connect_Four/wiki "Wiki").


<br><br><br>
Other resources that we used: https://codereview.stackexchange.com/questions/82647/evaluation-function-for-connect-four
