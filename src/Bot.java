import java.util.Random;

public class Bot
        extends Player
{
    public Bot(int playerNum, Board board) throws
            Exception
    {
        super("The Bot", playerNum, board);
    }

    @Override
    public void move(Board board)
    {
        try
        {
            board.nextMove(this.playerNum, this.randomMove(board));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private int randomMove(Board board)
    {
        Random rand = new Random();
        int[] moves = board.getPossibleMoves();
        int pos = rand.nextInt(moves.length);

        return moves[pos];
    }

}
