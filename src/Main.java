import java.util.Scanner;


public class Main
{
private static boolean gameOver = false;

/**
 * Main method <br>
 * Choose between single- and multi player.
 *
 * @param args "-single" for singlePlayer mode and "-multi" for multiPlayer mode
 */
public static void main(String[] args) throws
Exception
{
        if (args.length != 0)
        {
                switch (args[0])
                {
                case "-single":
                        singlePlayer();
                        break;
                case "-multi":
                        multiPlayer();
                        break;
                }
        }
        else
        {
                singlePlayer();
        }
}

private static void multiPlayer() throws
Exception
{
        Board board = new Board();
        Player[] players = new Player[2];

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the first player's name: ");
        String firstPlayer = scanner.nextLine();

        System.out.println("Enter the second player's name: ");
        String secondPlayer = scanner.nextLine();

        players[0] = new Human(firstPlayer, 1, board);
        players[1] = new Human(secondPlayer, 2, board);

        gameLoop(board, players);
}


private static void singlePlayer() throws
Exception
{
        Board board = new Board();
        Player[] players = new Player[2];

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your name: ");
        String name = scanner.nextLine();

        System.out.println("Do you want to be first? (y/n)");
        String isFirst = scanner.nextLine();

        int i = 0;
        int j = 1;

        if (isFirst.toLowerCase().equals("n") || isFirst.toLowerCase().equals("no"))
        {
                i = 1;
                j = 0;
        }

        players[i] = new Human(name, -1, board);
        players[j] = new Minimax(1, board, 2);

        gameLoop(board, players);
}

private static void gameLoop(Board board, Player[] players)
{
        while (!gameOver)
        {
                for (Player player : players)
                {
                        if (!gameOver)
                        {
                                //long startTime = System.currentTimeMillis();
                                player.move(board);
                                //long endTime = System.currentTimeMillis();
                                //System.err.println("      INFO ::: " + player.getName() + "'s move took " + (0.0 + endTime -
                                //        startTime) / 1000 + " seconds.");

                                System.out.println(player.getName() + "'s move:\n" + board);

                                gameOver = checkGameOver(board, player);
                        }
                }
        }
}

private static boolean checkGameOver(Board board, Player player)
{
        if (board.isGameOver())
        {
                System.out.println(player.getName() + " W0N!");
                return true;
        }
        else if (board.getPossibleMoves().length == 0)
        {
                System.out.println("IT`S A TIE!");
                return true;
        }
        return false;
}


}
