import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Minimax
        extends Bot
{
private int depth;
private int maxLeafNodes;
private int oldPossibleMoves;
//private int leafCount = 0;

public Minimax(int playerNum, Board board, int depth) throws
Exception
{
        super(playerNum, board);
        this.depth = depth;
        this.maxLeafNodes = (int)Math.pow(7,depth);
        this.oldPossibleMoves = 7;
}

@Override
public void move(Board board)
{
        try
        {
                //leafCount = 0;
                board.nextMove(this.playerNum, this.minimaxMove(board));
                //System.err.println("      Number of leafs: " + leafCount);
        }
        catch (Exception e)
        {
                e.printStackTrace();
        }
}

private int minimaxMove(Board board) throws
Exception
{
          //special move (random.choice[2,3,4]), if this.depth == 2 (1 move ahead == 2 plies) AND board.moves <= 3
          //System.out.println("DEPTH= " + this.depth + " BOARD MOVES= " + board.getMoves());
          if (this.depth == 2 && board.getMoves() <= 3) {
              int[] choices = new int[]{2,3,4};
              return choices[new Random().nextInt(choices.length)];
          }

          // temporarily disabled random moves !!!
          /*
          if (this.depth == 2) {
              if (new Random().nextInt(4) == 0) {
                  return board.getPossibleMoves()[new Random().nextInt(board.getPossibleMoves().length)];
              }
          } else if (this.depth == 4) {
              if (new Random().nextInt(9) == 0) {
                  return board.getPossibleMoves()[new Random().nextInt(board.getPossibleMoves().length)];
              }
          }
          */


        if (board.getPossibleMoves().length < this.oldPossibleMoves) {
                this.oldPossibleMoves = board.getPossibleMoves().length;
                this.depth = Math.max(this.depth,(int)Math.floor((Math.log(this.maxLeafNodes))/(Math.log(this.oldPossibleMoves))));
        }

        List<Thread> threads = new ArrayList<Thread>();

        int[] evaluatedMoves = new int[board.getPossibleMoves().length];
        //System.err.println("      possible moves: " + evaluatedMoves.length);
        for (int i = 0; i < board.getPossibleMoves().length; i++)
        {
                final Board boardCopy = board.copy();
                boardCopy.nextMove(1, board.getPossibleMoves()[i]);

                final int finalI = i;

                threads.add(new Thread(()->{
                                try {
                                        evaluatedMoves[finalI] = minimax(boardCopy, this.depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }));
        }

        for (Thread t : threads) {
                t.start();
        }

        for (Thread t : threads) {
                t.join();
        }

        int pos = board.getPossibleMoves()[0];
        int biggestValue = Integer.MIN_VALUE;
        for (int i = 0; i < board.getPossibleMoves().length; i++)
        {
                if (evaluatedMoves[i] > biggestValue)
                {
                        pos = board.getPossibleMoves()[i];
                        biggestValue = evaluatedMoves[i];
                }
        }
        return pos;
}

private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) throws
Exception
{
        if (depth == 0 || board.getPossibleMoves().length == 0)
        {
                //leafCount++;
                return evaluate(board, maximizingPlayer);
        }

        if (maximizingPlayer)
        {
                int maxEval = Integer.MIN_VALUE;
                for (int move : board.getPossibleMoves())
                {
                        Board boardCopy = board.copy();
                        boardCopy.nextMove(1, move);
                        int eval = minimax(boardCopy, depth - 1, alpha, beta, false);
                        maxEval = Math.max(maxEval, eval);
                        alpha = Math.max(alpha, eval);
                        if (beta <= alpha)
                        {
                                break;
                        }
                }
                return maxEval;
        }
        else
        {
                int minEval = Integer.MAX_VALUE;
                for (int move : board.getPossibleMoves())
                {
                        Board boardCopy = board.copy();
                        boardCopy.nextMove(-1, move);
                        int eval = minimax(boardCopy, depth - 1, alpha, beta, true);
                        minEval = Math.min(minEval, eval);
                        beta = Math.min(beta, eval);
                        if (beta <= alpha)
                        {
                                break;
                        }
                }
                return minEval;
        }
}

private final int[][] boardCoefficients = {{3, 4, 5, 7, 5, 4, 3},     // coefficients for board values
                                           {4, 6, 8, 10, 8, 6, 4},
                                           {5, 8, 11, 13, 11, 8, 5},
                                           {5, 8, 11, 13, 11, 8, 5},
                                           {4, 6, 8, 10, 8, 6, 4},
                                           {3, 4, 5, 7, 5, 4, 3}};

private final int[] xInRowCoefficients = {0, 10, 100};     // coefficients for x in a row

private int evaluate(Board board, boolean maximizingPlayer)
{
        if (board.isGameOver())
        {
                if (maximizingPlayer)
                {
                        return Integer.MIN_VALUE;
                }
                else
                {
                        return Integer.MAX_VALUE;
                }
        }
        else if (board.getPossibleMoves().length == 0)
        {
                return 0;
        }
        int total = 0;
        total += boardStrength(board);
        total += connectedStrength(board);
        return total;
}

private int boardStrength(Board board)
{
        int total = 0;
        for (int row = 0; row < board.getRows(); row++)
        {
                for (int col = 0; col < board.getColumns(); col++)
                {
                        total += board.getBoardState()[row][col] * boardCoefficients[row][col];
                }
        }
        return total;
}

private boolean isValidColorPosition(Board board, int row, int col, int color)
{
        return row >= 0
               && col >= 0
               && row < board.getRows()
               && col < board.getColumns()
               && board.getBoardState()[row][col] == color;
}

private int getLength(Board board, Pair position, Pair directions, int currentColor, boolean[][]
                      visited, int count)
{
        int row = position.getFirst() + count * directions.getFirst();
        int col = position.getSecond() + count * directions.getSecond();
        while (isValidColorPosition(board, row, col, currentColor))
        {
                visited[row][col] = true;
                count += 1;
                row += directions.getFirst();
                col += directions.getSecond();
        }
        return count;
}



private Pair getLengthPair(Board board, Pair last, Pair direction, int playerColor,
                                  boolean[][] visited)
{

        Pair position = new Pair(last.getFirst() + direction.getFirst(),
                                               last.getSecond() + direction.getSecond());

        int playerLength = getLength(board, position, direction, playerColor, visited, 0);

        int possibleLength = getLength(board, position, direction, board.getEmptyColor(), visited, playerLength);

        return new Pair(playerLength, possibleLength);
}

private int getStrength(Board board, Pair last, int color, boolean[][] visited, int[][] directions)
{

        int strength = 0;

        for (int i = 0; i < 4; ++i)
        {
                Pair direction = new Pair(directions[0][i], directions[1][i]);
                Pair a = getLengthPair(board, last, direction, color, visited);

                direction = new Pair(-directions[0][i], -directions[1][i]);
                Pair b = getLengthPair(board, last, direction, color, visited);

                if (a.getSecond() + b.getSecond() >= 3)
                {
                        strength += color * xInRowCoefficients[a.getFirst() + b.getFirst()];
                }
        }
        return strength;
}

private int connectedStrength(Board board)
{
        int total = 0;
        int[][] s = {{1, 1, 0, -1}, {0, 1, 1, 1}};
        boolean[][] visited = new boolean[board.getRows()][board.getColumns()];
        for (int row = 0; row < board.getRows(); ++row)
        {
                for (int col = 0; col < board.getColumns(); ++col)
                {
                        if (visited[row][col])
                        {
                                continue;
                        }
                        int color = board.getBoardState()[row][col];
                        if (color != board.getEmptyColor())
                        {
                                total += getStrength(board, new Pair(row, col), color, visited, s);
                        }
                        visited[row][col] = true;
                }
        }
        return total;
}


}
