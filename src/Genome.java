import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Genome extends Player {
  private HashMap<Integer,Node> nodeGenes = new HashMap<>();
  private List<Connection> connectionGenes = new ArrayList<>();
  private int nodeCount = 0;
  private int innovationNumber = 0; //DO I NEED THIS HERE???

  private final int IN_NODES = 42;
  private final int OUT_NODES = 7;

  private final int MAX_NODES = 200;

  private final double CONNECTION_WEIGHT_MUTATION = 0.8;
  private final double SHIFT_WEIGHT_MUTATION = 0.9;
  private final double NEW_WEIGHT_MUTATION = 0.1;
  private final double SLIGHT_MUTATION = 0.1;
  private final double ADD_NODE_MUTATION = 0.03;
  private final double ADD_CONNECTION_MUTATION = 0.1;
  private final double DISABLE_ENABLE_MUTATION = 0.3;

  private final double MU = 1.0;
  private final double LAMBDA = 3.0;

  private Board board;
  private Population population;

  private int fitness = 0;
  private boolean illegalMove = false;

  public Genome(String name, int playerNum, Board board, Population population) throws Exception
  {
    super(name,playerNum,board);
    this.board = board;
    this.population = population;
    //System.out.println("GENOME_CONSTRUCTOR_1; NUM_OF_GENES: " + this.connectionGenes.size());
  }

  public Genome(int playerNum, Population population) throws Exception //--------------------------------------------------------------
  {
    this(playerNum,new Board(),population);
  }


  public Genome(int playerNum, Board board, Population population) throws Exception //-------------------------------------------------
  {
    super("Genome_" +  String.valueOf((new Random()).nextInt()), playerNum, board);

    this.board = board;
    this.population = population;

    for (int i = 1; i <= this.IN_NODES; i++)
    {
      this.nodeCount++;
      this.nodeGenes.put(this.nodeCount,new Node("input",this.nodeCount));
    }
    for (int j = 1; j <= this.OUT_NODES; j++)
    {
      this.nodeCount++;
      this.nodeGenes.put(this.nodeCount,new Node("output",this.nodeCount));
    }
    for (int i = 1; i <= this.IN_NODES; i++)
    {
      for (int j = 1; j <= this.OUT_NODES; j++)
      {
          Connection temp = new Connection( this.nodeGenes.get(i),
                                            this.nodeGenes.get(this.IN_NODES + j),
                                            true,true,innovationUp() );
          this.connectionGenes.add(temp);
          temp.getFromNode().addOutGoing(temp);
          temp.getToNode().addInGoing(temp);
      }
    }

    //create a bias node, which is being connected to all outputs
    this.nodeCount++;
    Node bias = new Node("input",this.nodeCount);
    this.nodeGenes.put(this.nodeCount,bias);
    for (int j = 1; j <= this.OUT_NODES; j++)
    {
      Connection temp = new Connection( bias,this.nodeGenes.get(this.IN_NODES + j),
                                        true,true,innovationUp() );
      this.connectionGenes.add(temp);
      bias.addOutGoing(temp);
      this.nodeGenes.get(this.IN_NODES + j).addInGoing(temp);
    }

    //System.out.println("GENOME_CONSTRUCTOR_2; NUM_OF_GENES: " + this.connectionGenes.size());

  }

  public Genome(Genome other) throws Exception
  {
    this(other, new Board());
  }

  //copy constructor
  public Genome(Genome other, Board board) throws Exception //----------------------------------------------
  {
    super("Genome_" +  String.valueOf((new Random()).nextInt()),other.getPlayerNum(),board);

    this.board = board;
    this.population = other.population;
    this.nodeCount = other.nodeCount;
    this.innovationNumber = other.innovationNumber;

    // deep copy of other.nodeGenes saved to this.nodeGenes
    for (Map.Entry<Integer, Node> entry : other.nodeGenes.entrySet())
    {
        this.nodeGenes.put(entry.getKey(),new Node(entry.getValue()));
    }

    //System.out.println("There are " + other.connectionGenes.size() + " connnections in the original.");
    //System.out.println("At first there are " + this.connectionGenes.size() + " connections before copying.");

    // deep copy of other.conncetion saved to this.connectionGenes
    for (Connection c : other.connectionGenes)
    {
      this.connectionGenes.add(
            new Connection( this.nodeGenes.get(c.getFromNode().getNumber()),
                            this.nodeGenes.get(c.getToNode().getNumber()),
                            c.getWeight(), c.getIsExpressed(), c.getInnovationNumber() ) );
    }

    //System.out.println("There are " + this.connectionGenes.size() + " connnections in the deep copy.");

    //set in- and outgoing connections of the nodes
    for (Node n : this.nodeGenes.values())
    {
        n.calculateInOutGoing(this.connectionGenes);
    }

    //System.out.println("GENOME_COPY_CONSTRUCTOR; NUM_OF_GENES: " + this.connectionGenes.size());

  }

  public Genome crossover(Genome other) throws Exception //-------------------------------------------
  {
    if (this.nodeCount >= this.MAX_NODES) { return new Genome(this); }

    Genome offspring; // needs to be created though both parents
    Genome strongerParent;
    Genome weakerParent;
    int highestInnovation; // highest Innovation Number of the stronger parent
    if (this.fitness > other.fitness || (this.fitness == other.fitness && new Random().nextInt(2) == 0) )
    {
      strongerParent = this;
      weakerParent = other;
    } else {
      strongerParent = other;
      weakerParent = this;
    }
    offspring = new Genome(strongerParent);
    List<Integer> matchingGenes = strongerParent.matchingGenes(weakerParent);
    //System.out.println("matchingGenes: " + matchingGenes);
    highestInnovation = strongerParent.connectionGenes.get(strongerParent.connectionGenes.size()-1).getInnovationNumber();

    for (Connection c : weakerParent.connectionGenes)
    {
      int innovNum = c.getInnovationNumber();
      if (innovNum <= highestInnovation && !matchingGenes.contains(innovNum))
      {
        // get fromNode and toNode of this connection
        int fromNum = c.getFromNode().getNumber();
        int toNum = c.getToNode().getNumber();

        //getFromNode
        Node from;
        if (offspring.nodeGenes.get(fromNum) != null)
        {
          from = offspring.nodeGenes.get(fromNum);
        } else {
          from = new Node("hidden",fromNum);
          offspring.nodeGenes.put(fromNum,from);
          this.nodeCount++;
        }

        //getToNode
        Node to;
        if (offspring.nodeGenes.get(toNum) != null)
        {
          to = offspring.nodeGenes.get(toNum);
        } else {
          to = new Node("hidden",toNum);
          offspring.nodeGenes.put(toNum,to);
          this.nodeCount++;
        }

        Connection clonedConnection = new Connection(from,to,c.getWeight(),c.getIsExpressed(),innovNum);
        offspring.connectionGenes.add(clonedConnection);
        from.addOutGoing(clonedConnection);
        to.addInGoing(clonedConnection);
      }
    }
    Collections.sort(offspring.connectionGenes, (a,b) -> a.getInnovationNumber() - b.getInnovationNumber());
    return offspring;
  }

  @SuppressWarnings("unchecked")
  public void mutate()
  {
    Random generator = new Random();

    // mutate connection weight
    if (generator.nextDouble() <= this.CONNECTION_WEIGHT_MUTATION)
    {
      // get a random connection and mutate
      int randIndex = generator.nextInt(this.connectionGenes.size());
      Connection randConnection = this.connectionGenes.get(randIndex);
      if (generator.nextDouble() <= this.SHIFT_WEIGHT_MUTATION)
      {
        this.mutateConnectionWeight(randConnection);
      } else {
        this.mutateNewConnectionWeight(randConnection);
      }
    }

    // disable or enable connection
    if (generator.nextDouble() <= this.DISABLE_ENABLE_MUTATION)
    {
      int randIndex = generator.nextInt(this.connectionGenes.size());
      Connection randConnection = this.connectionGenes.get(randIndex);
      this.mutateEnableDisableConnection(randConnection);
    }

    // add new connection
    if (generator.nextDouble() <= this.ADD_CONNECTION_MUTATION)
    {
      List<Integer> nodeNums = new ArrayList<Integer>(this.nodeGenes.keySet());
      int randIndex1 = generator.nextInt(nodeNums.size());
      int randIndex2 = generator.nextInt(nodeNums.size());
      this.mutateAddConnection(nodeNums.get(randIndex1),nodeNums.get(randIndex2));
    }

    // add node
    if (generator.nextDouble() <= this.ADD_NODE_MUTATION)
    {
      int randIndex = generator.nextInt(this.connectionGenes.size());
      Connection randConnection = this.connectionGenes.get(randIndex);
      this.mutateAddNode(randConnection);
    }

  }


  public void mutateEnableDisableConnection(Connection connection) //--------------------------------------------
  {
    connection.switchIsExpressed();
  }

  public void mutateNewConnectionWeight(Connection connection)
  {
    connection.newWeight();
  }

  public void mutateConnectionWeight(Connection connection)
  {
    connection.mutateWeight(this.SLIGHT_MUTATION);
  }

  public void mutateConnectionWeight(Connection connection, double mutationFactor) //----------------------------------
  {
    connection.mutateWeight(mutationFactor);
  }

  //check if it creates a cycle !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  public void mutateAddConnection(int from, int to) //------------------------------------------------------------
  {
      if (from == to) { return; }
      Node start = this.nodeGenes.get(from);
      Node end = this.nodeGenes.get(to);
      if (end.getType() == "input" || connectionExists(start,end) || createsCycle(start,end)) { return; }
      mutateAddConnection(start, end);
  }

  public void mutateAddConnection(Node start, Node end) //-------------------------------------------------------
  {
      updateInnovationNumber();
      int cInnov = this.population.getConnectionInnovation(start.getNumber(),end.getNumber());
      Connection newConnection = new Connection(start,end,true,true,(cInnov == 0) ? innovationUp() : cInnov);
      this.connectionGenes.add(newConnection);
      start.addOutGoing(newConnection);
      end.addInGoing(newConnection);
      this.population.addConnection(newConnection);
  }

  public void mutateAddNode(Connection oldConnection) //-----------------------------------------------------------
  {
    if (this.nodeCount >= this.MAX_NODES) { return; }
    Node from = oldConnection.getFromNode();
    Node to = oldConnection.getToNode();
    oldConnection.disable();
    this.nodeCount++;
    Node middle = new Node("hidden",this.nodeCount);
    this.nodeGenes.put(nodeCount,middle);
    updateInnovationNumber();
    int cInnov1 = this.population.getConnectionInnovation(from.getNumber(),middle.getNumber());
    Connection firstConnection = new Connection(from,middle,true,true,(cInnov1 == 0) ? innovationUp() : cInnov1);
    int cInnov2 = this.population.getConnectionInnovation(middle.getNumber(),to.getNumber());
    Connection secondConnection = new Connection(middle,to,oldConnection.getWeight(),true,(cInnov2 == 0) ? innovationUp() : cInnov2);
    this.connectionGenes.add(firstConnection);
    this.connectionGenes.add(secondConnection);
    from.addOutGoing(firstConnection);
    middle.addInGoing(firstConnection);
    middle.addOutGoing(secondConnection);
    to.addInGoing(secondConnection);
    this.population.addConnection(firstConnection);
    this.population.addConnection(secondConnection);
  }

  public void updateInnovationNumber() { this.innovationNumber = this.population.updateGlobalInnovation(this.innovationNumber); }
  public int innovationUp() { this.innovationNumber++; return this.innovationNumber; } //--------------------

  private void unvisitAllNodes() //-------------------------------------------------------------------------
  {
    for (Node n: this.nodeGenes.values())
    {
      n.setVisited(false);
    }
  }

  private void resetAllValueCalculated() //----------------------------------------------------------------
  {
    for (Node n: this.nodeGenes.values())
    {
      // "input" nodes' values don't need to be calulated
      n.setValueCalculated((n.getType() == "input") ? true : false);
    }
  }

  public boolean connectionExists(Node start, Node end)
  {
    boolean exists = false;
    for (Connection c : this.connectionGenes)
    {
      if (c.getFromNode().getNumber() == start.getNumber() && c.getToNode().getNumber() == end.getNumber())
      {
        exists = true;
        break;
      }
    }
    return exists;
  }

  public boolean createsCycle(Node start,Node end) { //-----------------------------------------------------
    unvisitAllNodes();
    Stack<Node> stack = new Stack<>();
    stack.push(end);
    while (!stack.empty())
    {
      Node tmp = stack.pop();
      tmp.setVisited(true);
      if (tmp.next().contains(start)) { return true; }
      for (Node n : tmp.nextUnvisited())
      {
        stack.push(n);
      }
    }
    return false;
  }


  public void setInput() //------------------------------------------------------------------------------
  {
    // playerNum should be 1 or -1, hence the Matrix is being converted such that
    // the entry 1 stands for this genome and the entry -1 stands for its opponent
    Matrix currentState = Matrix.scalarMultiply(this.playerNum,this.board.getBoardStateLinear());

    // THE COMMENTED OUT CODE BELOW IS ONLY FOR TESTING PURPOSES
    /*
    // FORWARD PROPAGATION IF NO HIDDEN LAYERS AND FULLY CONNECTED -------------------------------
    //System.out.println(this.board);
    //System.out.println("currentState: \n" + currentState);
    Matrix x = new Matrix(43,1,false);
    for (int i = 1; i <= currentState.getRows(); i++)
    {
      x.setEntry(i-1,currentState.getEntry(i-1));
    }
    x.setEntry(42,this.nodeGenes.get(50).getValue());
    System.out.println("x = \n" + x);
    Matrix A = new Matrix(7,43,false);
    for (Connection c : this.connectionGenes)
    {
      int from = c.getFromNode().getNumber();
      int to = c.getToNode().getNumber();
      double val = c.getWeight();

      int row = to - 43;
      int col = (from == 50) ? 42 : from - 1;
      A.setEntry(row,col,val);
    }
    System.out.println("A = \n" + A);
    Matrix b = Matrix.multiply(A,x);
    b.sigmoid();
    System.out.println("\n\n A*x = \n" + b + "\n");
    // END OF FORWARD PROPAGATION -----------------------------------------------------------
    */

    for (int i = 1; i <= currentState.getRows(); i++)
    {
      // set the values of the input nodes
      this.nodeGenes.get(i).setValue(currentState.getEntry(i-1));
    }


  }

  public Matrix getOutput() //----------------------------------------------------------------------------
  {
    Matrix out = new Matrix(this.OUT_NODES,1,false);
    try {
      for (int i = 1; i <= this.OUT_NODES; i++)
      {
        out.setEntry(i-1,this.nodeGenes.get(this.IN_NODES+i).getValue());
      }
      /*
      for (int i = 1; i <= this.OUT_NODES; i++)
      {
        System.out.println(this.getName() + ": " + (this.IN_NODES+i) + " : " + out.getEntry(i-1));
      }
      */
    } catch (Exception e) { /*e.printStackTrace();*/ System.out.println("Genome.getOutput() messed up");}
    return out;
    /*
    OPTIONAL:
    return Matrix.componentMultiply(out,this.board.getPossibleMovesVector());
    //THEN ONLY POSSIBLE MOVES WILL BE MADE BY A GENOME; BUT IT DOESN'T LEARN
    //WHICH MOVES ARE POSSIBLE
    */
  }

    @Override
    public void move(Board board) //-------------------------------------------------------------
    {
      setInput();
      int move = Matrix.argmax(getOutput());
      System.out.println(this.getName() + ": " + move);
      //System.out.println(this);
      resetAllValueCalculated(); //DO I NEED THIS LINE ?
      try {
        board.nextMove(this.playerNum,move);
      } catch (Exception e) {
        // abort game because player failed to make a possible move
        this.illegalMove = true;
        //IF GENOME ONLY MAKES RIGHT MOVES
        //e.printStackTrace();
      }
    }

    public boolean getIllegalMove() { return this.illegalMove; } //----------------------------------------
    public void resetIllegalMove() { this.illegalMove = false; } //----------------------------------------

    public Board getBoard() { return this.board; } //------------------------------------------------------
    public int getFitness() { return this.fitness; } //----------------------------------------------------

    public void calculateFitness() //----------------------------------------------------------------------
    {
      if (this.illegalMove == true) {
        this.fitness = this.board.getMoves() + 1;
      } else if (this.board.getWinner() == this.playerNum) {
        this.fitness = 251 - this.board.getMoves();
      } else if (this.board.getWinner() == -1*this.playerNum) {
        this.fitness = this.board.getMoves() + 101;
      } else {
        this.fitness = 176;
      }
    }

    public List<Integer> matchingGenes(Genome other) //---------------------------------------------------------
    {
      List<Integer> matchingConnections = new ArrayList<>();
      List<Integer> strongConnections = new ArrayList<>();
      List<Integer> weakConnections = new ArrayList<>();
      this.connectionGenes.forEach(c -> strongConnections.add(c.getInnovationNumber()));
      other.connectionGenes.forEach(c -> weakConnections.add(c.getInnovationNumber()));
      Collections.sort(strongConnections);
      Collections.sort(weakConnections);

      //intersection algorithm for two sorted lists
      while (!strongConnections.isEmpty() && !weakConnections.isEmpty())
      {
        if (strongConnections.get(0).compareTo(weakConnections.get(0)) < 0) {
          strongConnections.remove(0);
        } else if (strongConnections.get(0).compareTo(weakConnections.get(0)) == 0) {
          matchingConnections.add(strongConnections.remove(0));
          weakConnections.remove(0);
        } else { weakConnections.remove(0); }
      }

      return matchingConnections;
    }

    public double structuralComplexity()
    {
      int complexity = 0;
      complexity += this.connectionGenes.size() * 3.0;
      for (Connection c : this.connectionGenes)
      {
        if (c.getIsExpressed()) {
          complexity += 1.0;
        } else {
          complexity += 0.01;
        }
      }
      return complexity;
    }

    public double compatibilityDistance(Genome other)
    {
      List<Integer> matchingGenes = this.matchingGenes(other);
      int numGenes1 = this.connectionGenes.size();
      int numGenes2 = other.connectionGenes.size();
      int N = Math.max(numGenes1,numGenes2);

      double delta = (this.MU / (N + 0.0)) * (numGenes1+numGenes2-2*matchingGenes.size());
      double weightDiff = 0.0;
      for (Connection c1 : this.connectionGenes)
      {
        if (matchingGenes.contains(c1.getInnovationNumber()))
        {
          int firstInnov = c1.getInnovationNumber();
          for (Connection c2 : other.connectionGenes)
          {
            if (firstInnov == c2.getInnovationNumber())
            {
              weightDiff += Math.abs(c1.getWeight()-c2.getWeight());
              break;
            }
          }
        }
      }
      delta += this.LAMBDA * ( weightDiff / (matchingGenes.size() + 0.0));
      return delta;
    }

    @Override
    public String toString() {
            StringBuilder s = new StringBuilder();
            s.append(this.getName().toUpperCase() + "\n");
            s.append(this.nodeCount + " nodes" + "\n\n");
            s.append("NODES:" + "\n");
            for (Node n : this.nodeGenes.values())
            {
              s.append(n.toString() + ";");
            }
            s.append("\n\n" + "CONNECTIONS:" + "\n");
            for (Connection c : this.connectionGenes)
            {
              s.append(c.toString() + ";");
            }
            return s.toString();
    }

    public void saveGraph() { this.saveGraph(false,true); }

    public void saveGraph(boolean showWeights, boolean showGraph)
    {
      Process p;
      try {
        System.out.println(this.getName());
        String command = "python3 graphNN.py " + this.getName() + ".txt -saveGraph";
        if (showWeights) { command += " -showWeights"; }
        if (showGraph) { command += " -showGraph"; }
        p = Runtime.getRuntime().exec(command);
        p.waitFor();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void save() throws Exception
    {
      String fileName = "./saved/" + this.getName() + ".txt";
      System.out.println(fileName);
      String[] path = fileName.split("/");
      new File(String.join("/",Arrays.copyOf(path, path.length-1))).mkdirs();
      File f = new File(fileName);
      f.createNewFile();

      StringBuilder s = new StringBuilder();
      s.append(this.getName() + "\n\n");

      //NODES
      for (Node n : this.nodeGenes.values())
      {
        s.append(n.toString() + "\n"); //ex.: input_1;
      }
      //s.deleteCharAt(s.length() - 1); DO I NEED THIS LINE?
      s.append("\n\n");

      //CONNECTIONS
      for (Connection c : this.connectionGenes)
      {
        s.append(c.toText() + "\n"); //ex.: 1_43_1.4029252399344676_false_1
      }

      try {
        FileWriter writer = new FileWriter(f);
        writer.write(s.toString());
        writer.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static Genome load(File fileAddress, int playerNum, Population population) throws Exception
    {
      Genome obj = null;
      String line;
      String out = "";
      try {
        BufferedReader br = new BufferedReader(new FileReader(fileAddress));
        while ((line = br.readLine()) != null)
        {
            out += line + "\n";
        }
        //System.out.println(out);
      } catch (Exception e) {
        e.printStackTrace();
      }

      //convert the String out into a Genome
      String[] parts = out.split("\n\n");

      // part 1: name, part 2: nodes, part 3: connections

      // part 1
      String genomeName = parts[0];
      obj = new Genome(genomeName, playerNum, new Board(), population);

      // part 2
      for (String subPart : parts[1].split("\n"))
      {
        String[] variables = subPart.split("_");
        obj.nodeCount++;
        int nodeNumber = Integer.parseInt(variables[1]);
        obj.nodeGenes.put(nodeNumber,new Node(variables[0],nodeNumber));
      }

      // part 3
      for (String subPart : parts[2].split("\n"))
      {
        String[] variables = subPart.split("_");

        int fromNum = Integer.parseInt(variables[0]);
        int toNum = Integer.parseInt(variables[1]);
        double weight = Double.parseDouble(variables[2]);
        boolean isExpressed = Boolean.parseBoolean(variables[3]);
        int innovation = Integer.parseInt(variables[4]);

        obj.innovationNumber = Math.max(innovation,obj.innovationNumber);
        obj.connectionGenes.add(
              new Connection( obj.nodeGenes.get(fromNum),
                              obj.nodeGenes.get(toNum),
                              weight, isExpressed, innovation ) );
      }

      //set in- and outgoing connections of the nodes
      for (Node n : obj.nodeGenes.values())
      {
          n.calculateInOutGoing(obj.connectionGenes);
      }

      return obj;
    }

    public List<Connection> getConnectionGenes() { return this.connectionGenes; }

    public void setPopulation(Population p) { this.population = p; }



}
