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

        ArrayList<Pair> moveList = new ArrayList<>();
        moveList.add(new Pair(5,3)); moveList.add(new Pair(4,3)); moveList.add(new Pair(5,5));
        Board.replay("P1","P2", moveList);

        population.playMinimax(2);
        population.calculateFitness();
        List<NN> nets = Arrays.asList(population.getNetworks());
        Collections.sort(nets, (a,b) -> b.getFitness() - a.getFitness());
        nets.forEach(n -> System.out.println(n.getFitness()));

        //System.out.println(new Matrix(nn0.getBoard().getBoardState()));
        //System.out.println(nn0.getBoard().getBoardStateLinear());
}
}
