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
  private Player opponent = new Minimax(1,new Board(),2);
  private boolean moreThanOneWinner = false;

  //hardcoded moves for both opponents
  private List<List<Integer>> startingMoves = new ArrayList<>();
  private List<List<Integer>> notStartingMoves = new ArrayList<>();

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
    this.hardcodeMoves();
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
    //this.sortedByFitness().forEach(g -> System.out.println(g.getName()  + ": " + g.getFitness()));

    System.out.println("Highest Fitness: " + best.getFitness());
    System.out.println("Average Fitness: " + averageFitness);
    this.printPerformance(magicNumber);

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
    boolean starts = whoStarts();
    List<Integer> firstMoves = new ArrayList<>();
    int wincount = 0;

    System.out.println("startingMoves.size(): " + startingMoves.size());
    System.out.println("notStartingMoves.size(): " + notStartingMoves.size());

    if (starts) {
      if (!startingMoves.isEmpty()) { firstMoves.addAll(startingMoves.remove(0)); }
    } else {
      if (!notStartingMoves.isEmpty()) { firstMoves.addAll(notStartingMoves.remove(0)); }
    }

    for (Genome genome : this.genomes)
    {
      List<Integer> hardcodedMoves = new ArrayList<>(firstMoves);

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

      if (!starts)
      {
        players[0] = opponentCopy;
        players[1] = genome;
      } else {
        players[0] = genome;
        players[1] = opponentCopy;
      }
      while (!hardcodedMoves.isEmpty()) {
        players[0].move(genome.getBoard(),hardcodedMoves.remove(0).intValue());
        players[1].move(genome.getBoard(),hardcodedMoves.remove(0).intValue());
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

      if (genome.getFitness() >= 200) { wincount++; }

      // showing all games in which the genome wins against the opponent
      if (genome.getFitness() >= 200 && moreThanOneWinner == false)
      {
        moreThanOneWinner = true;
        Board.replay(players[0].getName(), players[1].getName() ,genome.getBoard().getMoveList());
        //System.exit(0);
      }
    }

    if (wincount < 0.6 * this.size ) {
      System.out.println("first moves: " + firstMoves + " ; starts: " + starts);
      if (starts) {
        startingMoves.add(0,firstMoves);
      } else {
        notStartingMoves.add(0,firstMoves);
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

  public void printPerformance(long magicNumber)
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

    savePerformance(magicNumber,winPerc,tiePerc,lossPerc);
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

  public void savePerformance(long magicNumber,double winPerc,double tiePerc,double lossPerc) {
    String fileName = "./saved/" + magicNumber + "/performance.txt";
    String[] path = fileName.split("/");
    new File(String.join("/",Arrays.copyOf(path, path.length-1))).mkdirs();
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
      System.out.println(f.getName() + f.exists());
    }

    s.append(winPerc + "\n" + tiePerc + "\n" + lossPerc);

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

  private void hardcodeMoves() {

     // genome is first
     startingMoves.add(Arrays.asList(new Integer[] {3,3,2,2,4,4}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,3,2,2,4,4})));
     startingMoves.add(Arrays.asList(new Integer[] {3,4,3,4,3,4}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,4,3,4,3,4})));
     startingMoves.add(Arrays.asList(new Integer[] {3,3,2,2,4,4,3,5}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,3,2,2,4,4,3,5})));
     startingMoves.add(Arrays.asList(new Integer[] {2,3,3,4,5,4,4,5,3,5}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {2,3,3,4,5,4,4,5,3,5})));
     startingMoves.add(Arrays.asList(new Integer[] {3,3,4,4}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,3,4,4})));
     startingMoves.add(Arrays.asList(new Integer[] {3,3,3,3,2,5}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,3,3,3,2,5})));
     startingMoves.add(Arrays.asList(new Integer[] {3,3,3,3,2,2}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,3,3,3,2,2})));
     startingMoves.add(Arrays.asList(new Integer[] {3,1,3,3,4,6,4,4}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,1,3,3,4,6,4,4})));
     startingMoves.add(Arrays.asList(new Integer[] {4,3,3,4,3,2,2,0,1,1}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {4,3,3,4,3,2,2,0,1,1})));
     startingMoves.add(Arrays.asList(new Integer[] {3,0,3,1,2,1,4,5,4,6}));
     startingMoves.add(inverse(Arrays.asList(new Integer[] {3,0,3,1,2,1,4,5,4,6})));

     // genome is second
     notStartingMoves.add(Arrays.asList(new Integer[] {6,3,3,4,4,2}));
     notStartingMoves.add(inverse(Arrays.asList(new Integer[] {6,3,3,4,4,2})));
     notStartingMoves.add(Arrays.asList(new Integer[] {6,3,3,4,4,2,6,6}));
     notStartingMoves.add(inverse(Arrays.asList(new Integer[] {6,3,3,4,4,2,6,6})));
     notStartingMoves.add(Arrays.asList(new Integer[] {3,4,4,3,2,3,2,2,5,4,5,3}));
     notStartingMoves.add(inverse(Arrays.asList(new Integer[] {3,4,4,3,2,3,2,2,5,4,5,3})));
     notStartingMoves.add(Arrays.asList(new Integer[] {3,3,4,2,5,6,5,5,6,5,1,2,0,1}));
     notStartingMoves.add(inverse(Arrays.asList(new Integer[] {3,3,4,2,5,6,5,5,6,5,1,2,0,1})));
  }

  private List<Integer> inverse(List<Integer> original)
  {
      List<Integer> inverted = new ArrayList<>();
      for (Integer o : original)
      {
          inverted.add(6-o.intValue());
      }
      return inverted;
  }

  private boolean whoStarts()
  {
    if (!startingMoves.isEmpty() && startingMoves.size() >= notStartingMoves.size()) {
      return true;
    } else if (!notStartingMoves.isEmpty()) {
      return false;
    }
    return new Random().nextBoolean();
  }



}
