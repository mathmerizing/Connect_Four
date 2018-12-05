import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Stack;
import java.util.stream.IntStream;
import java.util.Collections;

class Board
{
    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private final int EMPTY_COLOR = 0;
    private int[][] boardState = new int[ROWS][COLUMNS];
    private Stack<String> availableTiles = new Stack<>();
    private HashMap<Integer,String> tiles = new HashMap<>(2);
    private int moves = 0;
    List<Pair> moveList = new ArrayList<>();

    Board()
    {
        //set the available tiles
        availableTiles.push("X");
        availableTiles.push("O");
    }

    @SuppressWarnings("unchecked")
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
                        this.boardState[i][j] != this.getEmptyColor())
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
                        this.boardState[i][j] != this.getEmptyColor())
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
                        this.boardState[i][j] != this.getEmptyColor())
                {
                    return true;
                }
            }
        }

        //check diagonally (bottomleft - topright)
        for (int i = this.ROWS - 1; i >= 3; i--)
        {
            for (int j = 0; j < this.COLUMNS - 3; j++)
            {
                if (this.boardState[i][j] == this.boardState[i - 1][j + 1] &&
                        this.boardState[i - 1][j + 1] == this.boardState[i - 2][j + 2] &&
                        this.boardState[i - 2][j + 2] == this.boardState[i - 3][j + 3] &&
                        this.boardState[i][j] != this.getEmptyColor())
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
            if (this.boardState[i][column] == this.getEmptyColor())
            {
                this.boardState[i][column] = playerNum;
                this.moveList.add(new Pair(i,column));
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

    int[] getBoardStateLinear()
    {
      int[] out = new int[42];
      for (int i = 0;i < this.ROWS; i++) {
        for (int j = 0; j < this.COLUMNS; j++) {
          out[i*this.COLUMNS+j] = this.boardState[i][j];
        }
      }
      return out;
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
        return this.availableTiles.pop();
    }


    void setHashMap(int playerNum, String tileType)
    {
        this.tiles.put(playerNum, tileType);
    }

    int getEmptyColor()
    {
        return this.EMPTY_COLOR;
    }

    int getMoves() { return this.moves; }

    public static void replay(String player1, String player2, ArrayList<Pair> moveList) throws Exception
    {
      Board b = new Board();
      b.setHashMap(1,"X");
      b.setHashMap(-1,"O");
      while (!moveList.isEmpty())
      {
        System.out.println(player1 + "'s move:");
        b.nextMove(1,moveList.remove(0).getSecond());
        System.out.println(b);

        if (!moveList.isEmpty())
        {
          System.out.println(player2 + "'s move:");
          b.nextMove(-1,moveList.remove(0).getSecond());
          System.out.println(b);
        }
      }
    }
}
