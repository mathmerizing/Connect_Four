import java.util.Scanner;
import java.io.File;

public class Test {

  public static void main(String[] args) throws
  Exception
  {
          String fileName = args[0];
          Genome opponent = Genome.load(new File("./saved/" + fileName + ".txt"), 1, new Population(1));
          opponent.setKnowsPossibleMoves(true);
          opponent.setName("Genome");
          Board board = opponent.getBoard();

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
          players[j] = opponent;

          Main.gameLoop(board, players);
  }

}
