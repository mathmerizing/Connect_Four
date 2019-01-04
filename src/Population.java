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
  private List<Connection> connectionHistory = new ArrayList<>();

  private final int TIE_SCORE = 176; // fitness of a genome that ties, references: Genome.calculateFitness()

  public Population(int size) throws Exception
  {
    this.size = size;
    this.genomes = new Genome[size];
    for (int i = 0; i < size; i++)
    {
      this.genomes[i] = new Genome(-1, this);
    }
  }

// fill an entire population with one genome
  public Population(Genome genome, int size) throws Exception
  {
    this.size = size;
    this.genomes = new Genome[size];
    genome.setPopulation(this);
    for (int i = 0; i < size; i++)
    {
      this.genomes[i] = new Genome(genome);
      this.genomes[i].setName("Genome_Clone_" + i);
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
    this.sortedByFitness().forEach(g -> System.out.println(g.getName()  + ": " + g.getFitness()));

    System.out.println("Highest Fitness: " + best.getFitness());
    System.out.println("Average Fitness: " + averageFitness);
    this.printPerformance();

    if (saveBest) { best.setName(magicNumber + "/Genome_" + magicNumber + "_" + epoch); System.out.println(best.getName()); best.save(); best.saveGraph(); }

    List<Species> speciesList = new ArrayList<>();
    for (Genome g : this.genomes)
    {
      speciesList = Species.updateSpecies(speciesList,g);
    }

    System.out.println("Number of Species: " + speciesList.size());
    for (Species s : speciesList)
    {
      System.out.println("Species" + s.getSpeciesNum() + ": ");
      for (Genome g : s.getGenomeList())
      {
        System.out.println(g.getName());
      }
    }

    /*
    System.out.println("BEFORE REPRODUCTION: ");
    speciesList.forEach(s -> s.getGenomeList().forEach(g -> System.out.println(g.getName() + ": " + g.getConnectionGenes().size() + " connections")));
    */

    List<Genome> newGenomes = new ArrayList<>();
    for (Species s : speciesList)
    {
      s.reproduce(averageFitness);
      newGenomes.addAll(s.getGenomeList());
    }

    /*
    System.out.println("AFTER REPRODUCTION: ");
    speciesList.forEach(s -> s.getGenomeList().forEach(g -> System.out.println(g.getName() + ": " + g.getConnectionGenes().size() + " connections")));
    */

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
    this.resetCumulativeFitness();

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

  public double getAverageFitness() { return this.cumulativeFitness / (this.size + 0.0); }

  public int updateGlobalInnovation(int num)
  {
    if (num > this.globalInnovationNumber) { this.globalInnovationNumber = num; }
    return this.globalInnovationNumber;
  }

  public void addConnection(Connection connection)
  {
    int connectionInnov = connection.getInnovationNumber();
    for (Connection c : this.connectionHistory)
    {
      if (c.getInnovationNumber() == connectionInnov) { return; }
    }
    this.connectionHistory.add(connection);
  }

  public int getConnectionInnovation(int fromNumber, int toNumber)
  {
    int innov = 0;
    for (Connection c : this.connectionHistory)
    {
      if (c.getFromNode().getNumber() == fromNumber && c.getToNode().getNumber() == toNumber)
      {
        innov = c.getInnovationNumber();
        break;
      }
    }
    return innov;
  }

  public List<Double> getPerformance()
  {
    // ouput order: WinPerc , TiePerc , LossPerc
    List<Double> out = new ArrayList<>();
    int winCount = 0;
    int tieCount = 0;
    int lossCount = 0;
    for (Genome g : this.genomes)
    {
      if (g.getFitness() > this.TIE_SCORE) {
        winCount++;
      } else if (g.getFitness() < this.TIE_SCORE) {
        lossCount++;
      } else {
        tieCount++;
      }
    }
    System.out.println("lossCount: " + lossCount);
    out.add(Math.round((10000.0 * winCount)/this.size)/100.0);
    out.add(Math.round((10000.0 * tieCount)/this.size)/100.0);
    out.add(Math.round((10000.0 * lossCount)/this.size)/100.0);
    return out;
  }

  public void printPerformance()
  {
    List<Double> performance = this.getPerformance();
    double winPerc = performance.get(0).doubleValue();
    double tiePerc = performance.get(1).doubleValue();
    double lossPerc = performance.get(2).doubleValue();

    System.out.println(" > PERFORMANCE < ");
    System.out.println("\t WIN:  " + winPerc + " %");
    System.out.println("\t TIE:  " + tiePerc + " %");
    System.out.println("\t LOSS: " + lossPerc + " %");
  }



}
