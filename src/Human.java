import java.util.Arrays;
import java.util.Scanner;

public class Human
        extends Player
{
    public Human(String name, int playerNum, Board board) throws
            Exception
    {
        super(name, playerNum, board);
    }

    @Override
    public void move(Board board)
    {
        boolean moved = false;

        while (!moved)
        {
            Scanner reader = new Scanner(System.in);
            System.out.println(this.name.toUpperCase() + ":: Enter a column number: ");
            int n = reader.nextInt();

            try
            {
                board.nextMove(this.playerNum, n);
                moved = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Possible moves are: \n" + Arrays.toString(board.getPossibleMoves()));
            }
        }
    }
}
