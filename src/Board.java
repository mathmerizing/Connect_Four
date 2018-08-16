import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.IntStream;
import java.util.Collections;

public class Board
{

    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private int[][] boardState = new int[ROWS][COLUMNS];
    private Stack availableTiles = new Stack();
    private HashMap tiles = new HashMap(2);

    Board()
    {
        //set the available tiles
        availableTiles.push("X");
        availableTiles.push("O");
    }

    Board copy()
    {
        Board out = new Board();
        out.setBoardState(this);
        out.tiles = (HashMap) this.tiles.clone();
        return out;
    }


    boolean isGameOver()
    {
        //check horizontally
        for (int i = 0; i < this.ROWS; i++)
        {
            for (int j = 0; j < this.COLUMNS - 3; j++)
            {
                if (this.boardState[i][j] == this.boardState[i][j + 1] &&
                        this.boardState[i][j + 1] == this.boardState[i][j + 2] &&
                        this.boardState[i][j + 2] == this.boardState[i][j + 3] &&
                        this.boardState[i][j] != 0)
                {
                    return true;
                }
            }
        }

        //check vertically
        for (int i = 0; i < this.ROWS - 3; i++)
        {
            for (int j = 0; j < this.COLUMNS; j++)
            {
                if (this.boardState[i][j] == this.boardState[i + 1][j] &&
                        this.boardState[i + 1][j] == this.boardState[i + 2][j] &&
                        this.boardState[i + 2][j] == this.boardState[i + 3][j] &&
                        this.boardState[i][j] != 0)
                {
                    return true;
                }
            }
        }

        //check diagonally (topleft - bottomright)
        for (int i = 0; i < this.ROWS - 3; i++)
        {
            for (int j = 0; j < this.COLUMNS - 3; j++)
            {
                if (this.boardState[i][j] == this.boardState[i + 1][j + 1] &&
                        this.boardState[i + 1][j + 1] == this.boardState[i + 2][j + 2] &&
                        this.boardState[i + 2][j + 2] == this.boardState[i + 3][j + 3] &&
                        this.boardState[i][j] != 0)
                {
                    return true;
                }
            }
        }

        //check diagonally (bottomleft - topright)
        for (int i = this.ROWS - 1; i > 3; i--)
        {
            for (int j = 0; j < this.COLUMNS - 3; j++)
            {
                if (this.boardState[i][j] == this.boardState[i - 1][j + 1] &&
                        this.boardState[i - 1][j + 1] == this.boardState[i - 2][j + 2] &&
                        this.boardState[i - 2][j + 2] == this.boardState[i - 3][j + 3] &&
                        this.boardState[i][j] != 0)
                {
                    return true;
                }
            }
        }

        //GAME IS NOT OVER:
        return false;
    }

    void nextMove(int playerNum, int column) throws
            Exception
    {
        if (!IntStream.of(this.getPossibleMoves()).anyMatch(x->x == column))
        {
            throw new Exception("ILLEGAL MOVE! Column " + column + " is not a possible move.");
        }

        for (int i = ROWS - 1; i > -1; i--)
        {
            if (this.boardState[i][column] == 0)
            {
                this.boardState[i][column] = playerNum;
                break;
            }
        }
    }

    int[] getPossibleMoves()
    {
        if (isGameOver())
        {
            return new int[]{};
        }

        ArrayList<Integer> moves = new ArrayList<>();

        for (int i = 0; i < COLUMNS; i++)
        {
            if (boardState[0][i] == 0)
            {
                moves.add(i);
            }
        }

        return moves.stream().mapToInt(i->i).toArray();
    }


    int[][] getBoardState()
    {
        return this.boardState;
    }

    private void setBoardState(Board in)
    {
        for (int i = 0; i < in.ROWS; i++)
        {
            for (int j = 0; j < in.COLUMNS; j++)
            {
                this.boardState[i][j] = in.getBoardState()[i][j];
            }
        }
    }

    int getColumns()
    {
        return this.COLUMNS;
    }

    int getRows()
    {
        return this.ROWS;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();

        horizontalLine(s);

        for (int[] row : this.boardState)
        {
            s.append("|");
            for (int v : row)
            {
                if (v != 0)
                {
                    s.append(tiles.get(v));
                }
                else
                {
                    s.append(" ");
                }

                s.append("|");
            }
            s.append("\n");
            horizontalLine(s);
        }


        return s.toString();
    }

    private void horizontalLine(StringBuilder s)
    {
        s.append(String.join("", Collections.nCopies(2 * COLUMNS + 1, "-")));
        s.append("\n");
    }

    String popTile()
    {
        return (String) this.availableTiles.pop();
    }


    void setHashMap(int playerNum, String tileType)
    {
        this.tiles.put(playerNum, tileType);
    }
}