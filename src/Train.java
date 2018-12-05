import java.util.ArrayList;

public class Train
{
  public static void main(String[] args) throws
            Exception
    {
      Population population = new Population(1);
      NN nn0 = population.getNetworks()[0];
      System.out.println(nn0);

      ArrayList<Pair> moveList = new ArrayList<>();
      moveList.add(new Pair(5,3)); moveList.add(new Pair(4,3)); moveList.add(new Pair(5,5));
      Board.replay("P1","P2", moveList);
    }
}
