import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class Minimax
        extends Bot
{
    private int depth;

    public Minimax(int playerNum, Board board, int depth) throws
            Exception
    {
        super(playerNum, board);
        this.depth = depth;
    }

    @Override
    public void move(Board board)
    {
        try
        {
            board.nextMove(this.playerNum, this.minimaxMove(board, this.depth,Integer.MIN_VALUE,Integer.MAX_VALUE,
                                                            true));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int minimaxMove(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) throws
            Exception
    {
        int maxEval = Integer.MIN_VALUE;
        int[] evaluatedMoves = new int[board.getPossibleMoves().length];
        System.out.println("possible moves: " + evaluatedMoves.length);
        for (int i = 0; i < board.getPossibleMoves().length; i++) {
            Board boardCopy = board.copy();
            boardCopy.nextMove(1, board.getPossibleMoves()[i]);
            evaluatedMoves[i] = minimax(boardCopy, depth - 1, alpha, beta, !maximizingPlayer);
        }
        int pos = board.getPossibleMoves()[0];
        int biggestValue = Integer.MIN_VALUE;
        for (int i = 0; i < board.getPossibleMoves().length; i++) {
            if (evaluatedMoves[i] > biggestValue) {
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
                alpha = Math.max(alpha,eval);
                if (beta <= alpha) {
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
                int eval = minimax(boardCopy, depth - 1, alpha, beta,true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

	  @Deprecated
    private int oldEvaluate(Board board, boolean maximizingPlayer)
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

        if (board.getPossibleMoves().length == 0)
        {
            return 0;
        }

        int[][] vals = {{3, 4, 5, 7, 5, 4, 3}, // coefficients for board values
                {4, 6, 8, 10, 8, 6, 4},
                {5, 8, 11, 13, 11, 8, 5},
                {5, 8, 11, 13, 11, 8, 5},
                {4, 6, 8, 10, 8, 6, 4},
                {3, 4, 5, 7, 5, 4, 3}};

        int sum = 0;

        for (int i = 0; i < board.getRows(); i++)
        {
            for (int j = 0; j < board.getColumns(); j++)
            {
                sum += vals[i][j] * board.getBoardState()[i][j];
            }
        }

        return sum;
    }


    private int[][] bS = {{3, 4, 5, 7, 5, 4, 3}, // coefficients for board values
            {4, 6, 8, 10, 8, 6, 4},
            {5, 8, 11, 13, 11, 8, 5},
            {5, 8, 11, 13, 11, 8, 5},
            {4, 6, 8, 10, 8, 6, 4},
            {3, 4, 5, 7, 5, 4, 3}};
    private int[] cS = {0, 10, 100}; // coefficients for x in a row

    private int evaluate(Board board, boolean maximizingPlayer) {
        if (board.isGameOver()) {
            if (maximizingPlayer) {
                return Integer.MIN_VALUE;
            }
            else {
                return Integer.MAX_VALUE;
            }
        }
        else if (board.getPossibleMoves().length == 0) {
            return 0;
        }
        int total = 0;
        total += boardStrength(board);
        total += connectedStrength(board);
        return total;
    }

    private int boardStrength(Board board) {
        int total = 0;
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) {
                total += board.getBoardState()[row][col] * bS[row][col];
            }
        }
        return total;
    }

    private int connectedStrength(Board board) {
        int total = 0;
        int color;
        IntegerPair a;
        IntegerPair b;
        int[][] s = {{1,1,0,-1},{0,1,1,1}}; //up/down, up-right/down-left, right/left, down-right/up-left
        IntegerPair last;
        boolean[][] visited = new boolean[board.getRows()][board.getColumns()];
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getColumns(); col++) { // loop through each tile position possible
                if (!visited[row][col]) {
                    last = new IntegerPair(row, col);
                    color = board.getBoardState()[row][col];
                    if (color != 0) {
                        for (int i = 0; i < 4; ++ i) { // loop through four directions
                            a = check(last, s[0][i], s[1][i], visited, board); // get lengths
                            b = check(last, -s[0][i], -s[1][i], visited, board);
                            if (a.second() + b.second() >= 3) { // if possible to create a four with this chain
                                total += color * cS[a.first() + b.first()]; // add to heuristic
                            }
                        }
                    }
                    visited[row][col] = true;
                }
            }
        }
        return total;
    }

    private IntegerPair check(IntegerPair last, int d1, int d2, boolean[][] visited, Board board) { // returns the # in a row
        //in row direction d1 and col direction d2 and also if the next one is free
        int len = 1, player = board.getBoardState()[last.first()][last.second()];
        while (last.first() + len * d1 >= 0
                && last.second() + len * d2 >= 0
                && last.first() + len * d1 <= board.getRows() - 1
                && last.second() + len * d2 <= board.getColumns() - 1 // while inbounds
                && board.getBoardState()[last.first() + len * d1][last.second() + len * d2] == player) { // and while the tiles are the same color as the player's
            visited[last.first() + len * d1][last.second() + len * d2] = true;
            len += 1;
        }
        int same = len;
        while (last.first() + len * d1 >= 0
                && last.second() + len * d2 >= 0
                && last.first() + len * d1 <= board.getRows() - 1
                && last.second() + len * d2 <= board.getColumns() - 1 // again, while inbounds
                && board.getBoardState()[last.first() + len * d1][last.second() + len * d2] == 0) { // while the tiles are not the enemy's
            visited[last.first() + len * d1][last.second() + len * d2] = true;
            len += 1;
        }
        return new IntegerPair(same-1, len-1);
    }

    @Deprecated
    private int yersoEvaluate(Board board, boolean maximizingPlayer) {
        return scoreCounter(board, maximizingPlayer) - scoreCounter(board, !maximizingPlayer);
    }

    @Deprecated
    private int scoreCounter(Board board, boolean maximizingPlayer) {
        //horizontal score
        int currentPlayer = 1;
        if (maximizingPlayer) {
            currentPlayer = -1;
        }
        int max = 0;
        int counter;
        int j;
        for (int i = 0; i < board.getRows(); i++) {
            counter = 0;
            j = 0;
            if (board.getColumns() - j - 1 > max) {
                while (board.getBoardState()[i][j] == currentPlayer) {
                    counter++;
                    j++;
                }
            }
            max = Math.max(max, counter);
        }
        //vertical score
        for (int i = 0; i < board.getColumns(); i++) {
            counter = 0;
            j = 0;
            if (board.getRows() - i - 1 > max) {
                while (board.getBoardState()[j][i] == currentPlayer) {
                    counter++;
                    j++;
                }
            }
            max = Math.max(max, counter);
        }
        //diagonal (\) score
        counter = 0;
        int i = 0;
        j = 0;
        if (board.getColumns() - j - 1 > max) {
            if (board.getRows() - i - 1 > max) {
                while (board.getBoardState()[i][j] == currentPlayer) {
                    counter++;
                    i++;
                    j++;
                }
            }
        }
        max = Math.max(max, counter);
        //diagonal (/) score
        counter = 0;
        i = 0;
        j = board.getColumns() - 1;
        if (j > max) {
            if (board.getRows() - i - 1 > max) {
                while (board.getBoardState()[i][j] == currentPlayer) {
                    counter++;
                    i++;
                    j--;
                }
            }
        }
        max = Math.max(max, counter);
        return max;
    }
}
