import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Random;

public class Population {
  private int size;
  private Genome[] genomes;
  private int cumulativeFitness = 0;

  public Population(int size) throws Exception
  {
    this.size = size;
    this.genomes = new Genome[size];
    for (int i = 0; i < size; i++)
    {
      this.genomes[i] = new Genome(-1,new Board());
    }
  }

  public void play() throws Exception { play(4); }

  public void play(int depth) throws Exception
  {
    for (Genome genome : this.genomes)
    {
      Minimax minimax = new Minimax(1,genome.getBoard(),depth);
      if ((new Random()).nextBoolean()) { minimax.move(genome.getBoard()); }
      while (genome.getBoard().getPossibleMoves().length != 0)
      {
        //genome moves
        genome.move(genome.getBoard());
        //did the genome make an illegalMove
        if (genome.getIllegalMove()) { break; }
        //if the game is not over minimax should move
        if (genome.getBoard().getPossibleMoves().length != 0)
        {
          minimax.move(genome.getBoard());
        }
      }
      genome.calculateFitness();
    }
  }

  public List<Genome> sortedByFitness()
  {
    List<Genome> sortedGenomes = Arrays.asList(this.genomes);
    Collections.sort(sortedGenomes, (a,b) -> b.getFitness() - a.getFitness());
    return sortedGenomes;

  }


  public int getSize() { return this.size; }
  public void setSize(int size) { this.size = size; }

  public Genome[] getGenomes() { return this.genomes; }

  public int getCumulativeFitness() { return this.cumulativeFitness; }
  public void resetCumulativeFitness() { this.cumulativeFitness = 0; }

  public void calculateCumulativeFitness()
  {
    if (this.cumulativeFitness != 0) { return; }
    Arrays.asList(this.genomes).forEach(g -> this.cumulativeFitness += g.getFitness());
  }

  public double getAverageFitness() { return this.cumulativeFitness/(0.0 + this.size); }

}
