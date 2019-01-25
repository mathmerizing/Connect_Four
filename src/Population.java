import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Population {
  private int size;
  private Genome[] genomes;
  private int cumulativeFitness = 0;
  private int globalInnovationNumber = 0;
  private int epoch = 0;
  private List<Connection> connectionHistory = new ArrayList<>();
  private int globalNodeCount = 0;
  private Player opponent = new Minimax(1,new Board(),4);
  private boolean moreThanOneWinner = false;

  private final int TIE_SCORE = 176; // fitness of a genome that ties, references: Genome.calculateFitness()
  private final int OPPONENT_UPDATE_INTERVAL = 5;
  private final boolean ALLOW_GENOME_OPPONENT = false;

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

    if (saveBest) { best.setName(magicNumber + "/Genome_" + magicNumber + "_" + epoch); System.out.println(best.getName()); best.save(); best.saveGraph(); saveStats(magicNumber); }
    this.updateOpponent(best);

    List<Species> speciesList = new ArrayList<>();
    for (Genome g : this.genomes)
    {
      speciesList = Species.updateSpecies(speciesList,g);
    }

    System.out.println("Number of Species: " + speciesList.size());
    /*
    for (Species s : speciesList)
    {
      System.out.println("Species" + s.getSpeciesNum() + ": ");
      for (Genome g : s.getGenomeList())
      {
        System.out.println(g.getName());
      }
    }
    */

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

  public void play() throws Exception
  {
    for (Genome genome : this.genomes)
    {
      //System.out.println("OPPONENT IS " + ((!(this.opponent instanceof Minimax)) ? "NOT ": "" ) +"OF TYPE MINIMAX.");
      // clone the opponent
      Player opponentCopy = new Human("bla",2,new Board()); // ONLY TEMPORARILY
      if (this.opponent instanceof Minimax) {
         opponentCopy = new Minimax(1,genome.getBoard(),((Minimax)this.opponent).getDepth());
         //System.out.println("created Minimax copy");
      } else if (this.opponent instanceof Genome) {
        opponentCopy = new Genome(((Genome)this.opponent),genome.getBoard(),1);
        ((Genome)opponentCopy).setKnowsPossibleMoves(true);
        opponentCopy.setName("Genome_OPPONENT");
      }

      Player[] players = new Player[2];
      if (new Random().nextBoolean())
      {
        players[0] = opponentCopy;
        players[1] = genome;
      } else {
        players[0] = genome;
        players[1] = opponentCopy;
      }
      //System.out.println("\nRED: " + players[0].getName() + "\t YELLOW: " + players[1].getName());
      while (genome.getBoard().getPossibleMoves().length != 0)
      {
        if (genome.getIllegalMove()) { /*System.out.println("ILLEGAL MOVE!");*/break; }
        for (Player p : players)
        {
          //Player p moves
          p.move(genome.getBoard());

          //genome.getBoard().showBoard(); // VISUALIZATION !!!

          //did the genome make an illegalMove
          if (genome.getIllegalMove()) { break; }
          //if the game is not over minimax should move
          if (genome.getBoard().getPossibleMoves().length == 0)
          {
            break;
          }
        }
      }
      /*
      if (!genome.getIllegalMove())
      {
        System.out.print("WINNER: ");
        int winner = genome.getBoard().getWinner();
        for (Player p : players)
        {
          if (p.getPlayerNum() == winner) { System.out.println(p.getName() + "\n"); }
        }
      }
      */
      genome.calculateFitness();
      // showing all games in which the genome wins against the opponent
      if (genome.getFitness() >= 200 && moreThanOneWinner == false)
      {
        moreThanOneWinner = true;
        Board.replay(players[0].getName(), players[1].getName() ,genome.getBoard().getMoveList());
        System.exit(0);
      }
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

  public int updateGlobalNodeCount(int num)
  {
    if (num > this.globalNodeCount) { this.globalNodeCount = num; }
    return this.globalNodeCount;
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

    System.out.println("OPPONENT: " + this.opponent.getName());
    System.out.println(" > PERFORMANCE < ");
    System.out.println("\t WIN:  " + winPerc + " %");
    System.out.println("\t TIE:  " + tiePerc + " %");
    System.out.println("\t LOSS: " + lossPerc + " %");
  }

  public void saveStats(long magicNumber)
  {
    double[] fitnessValues = new double[this.size];
    List<Genome> sortedGenomes = this.sortedByFitness();
    for (int i = 0; i < this.size; i++) { fitnessValues[i] = sortedGenomes.get(i).getFitness(); }

    double maxFit = calculateMax(fitnessValues);
    double upperQFit = calculateUpperQuartile(fitnessValues);
    double medianFit = calculateMedian(fitnessValues);
    double lowerQFit = calculateLowerQuartile(fitnessValues);
    double minFit = calculateMin(fitnessValues);

    String fileName = "./saved/" + magicNumber + "/stats.txt";
    File f = new File(fileName);

    StringBuilder s = new StringBuilder();

    try {
      if (f.createNewFile()) {
        //File is created
        //do nothing
      } else {
        //File already exists
        s.append("\n\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }


    s.append(maxFit + "\n" + upperQFit + "\n" + medianFit + "\n" + lowerQFit + "\n" + minFit);

    try {
      FileWriter writer = new FileWriter(f,true);
      writer.write(s.toString());
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private double calculateMin(double[] data) {
    return data[data.length - 1];
  }

  private double calculateMax(double[] data) {
    return data[0];
  }

  public double calculateMedian(double[] data) {
        int j = (int) Math.floor( (data.length - 1.0) / 2.0 );
        return data[j];
  }

  private double calculateLowerQuartile(double[] data) {
        int j = (int) Math.floor( (3.0 * data.length - 1.0) / 4.0 );
        return data[j];
  }

  private double calculateUpperQuartile(double[] data) {
        int j = (int) Math.floor( (data.length - 1.0) / 4.0 );
        return data[j];
  }

  private void updateOpponent(Genome bestGenome) throws Exception
  {
    if ((this.epoch % this.OPPONENT_UPDATE_INTERVAL) == 0 && this.ALLOW_GENOME_OPPONENT)
    {
      if (this.opponent instanceof Minimax) {
        double[] fitnessValues = new double[this.size];
        List<Genome> sortedGenomes = this.sortedByFitness();
        for (int i = 0; i < this.size; i++) { fitnessValues[i] = sortedGenomes.get(i).getFitness(); }

        double maxFit = calculateMax(fitnessValues);
        if (maxFit >= this.TIE_SCORE)
        {
          this.opponent = new Genome(bestGenome);
          System.out.println(" ----- NEW OPPONENT ----- ");
        }
      } else {
        // opponent is already a genome
        this.opponent = new Genome(bestGenome);
        System.out.println(" ----- NEW OPPONENT ----- ");
      }
    }
  }



}
