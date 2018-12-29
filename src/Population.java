import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Random;

public class Population {
  private int size;
  private Genome[] genomes;
  private int cumulativeFitness = 0;
  private int globalInnovationNumber = 0;
  private int epoch = 0;

  public Population(int size) throws Exception
  {
    this.size = size;
    this.genomes = new Genome[size];
    for (int i = 0; i < size; i++)
    {
      this.genomes[i] = new Genome(-1, this);
    }
  }

  public void nextEpoch(boolean saveBest, long magicNumber) throws Exception
  {
    this.epoch++;
    System.out.println("--- EPOCH " + this.epoch + ": ---");
    this.play();
    this.calculateCumulativeFitness();
    double averageFitness = this.getAverageFitness();
    Genome best = this.sortedByFitness().get(0);

    System.out.println("Highest Fitness: " + best.getFitness());
    System.out.println("Average Fitness: " + averageFitness);

    if (saveBest) { best.setName("Genome_" + magicNumber + "_" + epoch); best.save(); best.saveGraph(); }

    List<Species> speciesList = new ArrayList<>();
    for (Genome g : this.genomes)
    {
      speciesList = Species.updateSpecies(speciesList,g);
    }

    System.out.println("Number of Species: " + speciesList.size());

    List<Genome> newGenomes = new ArrayList<>();
    for (Species s : speciesList)
    {
      s.reproduce(averageFitness);
      newGenomes.addAll(s.getGenomeList());
    }

    if (newGenomes.size() < this.size)
    {
      int difference = this.size - newGenomes.size();
      for (int i = 0; i < difference; i++)
      {
        Genome g = new Genome(newGenomes.get(0));
        g.mutate();
        newGenomes.add(g);
      }
    }

    this.genomes = newGenomes.subList(0,this.size).toArray(new Genome[this.size]);

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

  public int updateGlobalInnovation(int num)
  {
    if (num > this.globalInnovationNumber) { this.globalInnovationNumber = num; }
    return this.globalInnovationNumber;
  }

}
