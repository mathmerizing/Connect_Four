# Four In A Row
*by Yerso Checya Sinti and Julian Roth* <br><br>
A Java version of the game "Four In A Row". Learning about Minimax, Alpha-beta pruning and NEAT.

## OUR FOUR IN A ROW APP:
<a href="https://play.google.com/store/apps/details?id=com.mathmerizing.fourinarow"><img src="https://github.com/mathmerizing/Four_In_A_Row/blob/master/documentation/main_menu.jpg" width="300"></a>

## REQUIREMENTS:
NORMAL USERS: <br> <br>
<b>Java 10</b> (or newer), <b>Python 3</b> with the module '<b>pygame</b>' <br>
("python3" needs to be a added to the environment variables) <br>
[How to install 'pygame': "pip install pygame"]
<br><br>
<b>---RUN THE PROGRAM---</b><br>
<i>single player:</i>
<pre> >>> javac Main.java;java Main </pre>
<i>multi player:</i>

<pre> >>> javac Main.java;java Main -multi</pre>

<br>
USERS TRYING TO PLAY AROUND WITH NEAT: <br>
Additional modules 'networkx','matplotlib','numpy' and 'scipy'.
Instructions on how to run the programs for NEAT:
https://github.com/mathmerizing/Four_In_A_Row/blob/master/src/UserGuide.txt

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

Link to an instructional Youtube Video about the Minimax algorithm: https://www.youtube.com/watch?v=l-hh51ncgDI&index=3&t=0s&list=LL1ZTjVhPHvlgFev4ryx0suQ 

## SNEAK PEAK
<img src="https://github.com/mathmerizing/Four_In_A_Row/blob/master/documentation/Four_In_A_Row_Screenshot.png" width="450">
Added a small GUI to visualize the current board state.

## NEAT
NEAT stands for Neuroevolution of Augmenting Topologies is a genetic algorithm developed by Ken Stanley in 2002.
At the beginning of training a population of 500 random neural networks are being created. At first the structure is minimal:
  - 42 neurons representing the current board state
  - 1 bias neuron
  - 7 neurons representing each column of the board
  
That's why the neural nets have 43 input neurons and 7 output neurons at first.

The neural networks play against the Minimax algorithm. Through evaluation of the networks's performances, in each epoch slightly stronger networks are being created thanks to mutatation, speciation and crossover. The structure of the networks grows with time, as can be seen in the following example: <br>
<img src="https://github.com/mathmerizing/Four_In_A_Row/blob/master/documentation/Newest_Best_Genome.png" width="900"> <br>

Unfortunately, there have been a few obstacles with creating a decent opponent with NEAT:
  - training is deterministic: bot vs. tree search (both players act the same way in a given position)
  - many hyper parameters for training: maybe I didn't choose the optimal parameters
  - the neuronal networks have a lot of paramaters that need to be trained: at first there were 43 * 7 = 301 parameters that had to be trained, the number of parameters even increased over time
  - the neural networks tended to place their tiles always in the same column, which is not a good strategy
  
My approaches to fix this:
  - at the beginning of training play hardcoded openings which give the neural nets a big advantage, such that they can learn simple patterns and strategies
  - the Minimax algorithm is only allowed to calculated one move ahead
 
The results: <br>
<img src="https://github.com/mathmerizing/Four_In_A_Row/blob/master/documentation/Statistics.png" width="900"> <br>
In this graph, the fitness score of the best and the worst neural network in each epoch is being interpolated, such that the trends in the fitness score can be seen. 
<img src="https://github.com/mathmerizing/Four_In_A_Row/blob/master/documentation/Performance.png" width="900"> <br>
In this graph, each bar represents the whole population of neural networks, the percentage of the bar colored in a certain way stands for the same percentage of the population that achieved a certain result (win / tie / loss).

In the first 50 to 60 epochs the neural networks are playing hardcoded openings. One might notice that it usually takes around 3 epochs for the majority of the population to master a new situation. Then there is a huge dip between the 50 and 100 epoch marks. This decrease in fitness is being caused by the fact, that the neural networks are not playing hardcoded openings anymore, but instead need to find a strategy to beat the Minimax algorithm. Eventually around a third of the population manages to beat the Minimax algorithm. This is the final result of the training.

I did not reach my desired goal with regards to the performance of the bots. Initially one of the networks trained with NEAT should defeat the Minimax algorithm that was looking 3 moves ahead. Apparently, this approach is not well suited for the task at hand and I probably should have implemted the algorithm of Alpha Zero (MCTS + ResNet) instead.

## MORE INFORMATION:
Please checkout the more in depth documentation on the project's to learn more about the implementation of the Minimax algorithm. [Wiki page](https://github.com/mathmerizing/Four_In_A_Row/wiki "Wiki").


<br><br><br>
Other resources that we used: https://codereview.stackexchange.com/questions/82647/evaluation-function-for-connect-four
