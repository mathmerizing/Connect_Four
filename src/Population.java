import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Population {
private int size;
private NN[] networks;
private List<Integer> fitnesses = new ArrayList<>();
private int cumFitness = 0;
private int epoch = 1;

public Population(int size) throws Exception {
        this.size = size;
        this.networks = new NN[this.size];
        for (int i = 0; i < this.size; i++) { this.networks[i] = new NN(-1,new Board()); }
}

public NN[] getNetworks()
{
        return this.networks;
}

public void calculateFitness()
{
  for (NN nn : this.networks)
  {
    nn.increaseFitness(nn.getBoard().calculateScore());
  }
  List<NN> nets = Arrays.asList(this.networks);
  Collections.sort(nets, (a,b) -> b.getFitness() - a.getFitness());
  nets.forEach(n -> fitnesses.add(n.getFitness()));
  for (Integer num : this.fitnesses) { this.cumFitness += num; }
}

public void playMinimax(int depth, boolean start) throws Exception
{
  for (NN nn : this.networks)
  {
    Minimax enemy = new Minimax(1,nn.getBoard(),4);
    if (!start) { enemy.move(nn.getBoard()); }
    while (nn.getBoard().getPossibleMoves().length != 0)
    {
        nn.move(nn.getBoard());
        if (nn.getBoard().getPossibleMoves().length != 0)
        {
            enemy.move(nn.getBoard());
        }
    }
  }
}

public void archiveBoard()
{
  for (NN nn : this.networks)
  {
    nn.archiveBoard();
  }
}

public List<Integer> getFitnesses()
{
  return this.fitnesses;
}

public int getCumFitness()
{
    return this.cumFitness;
}

public NN weightedPick()
{
  double probability = new Random().nextDouble();
  //System.out.println(probability);
  int pos = 0;
  while (probability > 0)
  {
    if (pos == this.size) { break; }
    probability -= (((double)this.networks[pos].getFitness())/this.cumFitness);
    pos++;
    //System.out.println(probability);
  }
  pos--;
  //System.out.println(pos);
  return this.networks[pos];
}

public void naturalSelection(double mutation) throws Exception
{
  Population nextGen = new Population(this.size);
  nextGen.networks[0] = this.getBest().mutatedCopy(0);
  for (int i = 1; i < this.size; i++)
  {
    nextGen.networks[i] = this.weightedPick().mutatedCopy(mutation);
  }
  this.networks = nextGen.networks;
  this.fitnesses = nextGen.fitnesses;
  this.cumFitness = nextGen.cumFitness;
  this.epoch++;
}

public NN getBest()
{
  return this.networks[0];
}
}
