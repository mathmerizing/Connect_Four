import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Train
{
public static void main(String[] args) throws
Exception
{
        Population population = new Population(100);
        NN nn0 = population.getNetworks()[0];
        System.out.println(nn0);
        //System.out.println(new Matrix(nn0.getBoard().getBoardState()));

        //ArrayList<Pair> moveList = new ArrayList<>();
        //moveList.add(new Pair(5,3)); moveList.add(new Pair(4,3)); moveList.add(new Pair(5,5));
        //Board.replay("P1","P2", moveList);

        //population.playMinimax(2);
        //population.calculateFitness();
        //System.out.println(population.getFitnesses());
        //System.out.println(population.getCumFitness());
        //System.out.println(population.weightedPick());
        //population.naturalSelection(0.01);

        for (int i = 0; i < 10; i++)
        {
          if (i > 0)
          {
            population.naturalSelection(0.005);
          }
          for (int j = 0; j < 5; j++)
          {
            //System.out.println("Round " + (j+1));
            if (j > 0) { population.archiveBoard(); }
            population.playMinimax(4, true);
            population.calculateFitness();
          }
          if ((i+1)%10 == 0) {
            NN best = population.getBest();
            String fileName = "./saved/" + System.currentTimeMillis() + "_" + best.getName() + "_" + (i+1) + ".json";
            File f = new File(fileName);
            f.createNewFile();
            best.save(f);
            NN loaded = NN.load(f);
            System.out.println("Best: \n" + best);
            System.out.println("Loaded: \n" + loaded);
          }
          System.out.println(population.getBest().getFitness());
          if (i == 999)
          {
            Board.replay(population.getBest().getName(), "MINIMAX", population.getBest().getBoard().getMoveList());
          }
        }

        //System.out.println(new Matrix(nn0.getBoard().getBoardState()));
        //System.out.println(nn0.getBoard().getBoardStateLinear());
}
}
