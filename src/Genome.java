import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.Collections;

public class Genome extends Player {
  private HashMap<Integer,Node> nodeGenes = new HashMap<>();
  private List<Connection> connectionGenes = new ArrayList<>();
  private int nodeCount = 0;
  private int innovationNumber = 0; //DO I NEED THIS HERE???

  private final int inNodes = 42;
  private final int outNodes = 7;

  private final int maxNodes = 200;

  private final double mu = 1.0;
  private final double lambda = 3.0;

  private Board board;

  private int fitness = 0;
  private boolean illegalMove = false;


  public Genome(int playerNum) throws Exception //--------------------------------------------------------------
  {
    this(playerNum,new Board());
  }


  public Genome(int playerNum, Board board) throws Exception //-------------------------------------------------
  {
    super("Genome_" +  String.valueOf((new Random()).nextInt()), playerNum, board);

    this.board = board;

    for (int i = 1; i <= this.inNodes; i++)
    {
      this.nodeCount++;
      this.nodeGenes.put(this.nodeCount,new Node("input",this.nodeCount));
    }
    for (int j = 1; j <= this.outNodes; j++)
    {
      this.nodeCount++;
      this.nodeGenes.put(this.nodeCount,new Node("output",this.nodeCount));
    }
    for (int i = 1; i <= this.inNodes; i++)
    {
      for (int j = 1; j <= this.outNodes; j++)
      {
          Connection temp = new Connection( this.nodeGenes.get(i),
                                            this.nodeGenes.get(this.inNodes + j),
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
    for (int j = 1; j <= this.outNodes; j++)
    {
      Connection temp = new Connection( bias,this.nodeGenes.get(this.inNodes + j),
                                        true,true,innovationUp() );
      this.connectionGenes.add(temp);
      bias.addOutGoing(temp);
      this.nodeGenes.get(this.inNodes + j).addInGoing(temp);
    }

  }

  //copy constructor
  public Genome(Genome other) throws Exception //----------------------------------------------
  {
    super(other.getName(),other.getPlayerNum(),new Board());

    this.board = new Board();
    this.nodeCount = other.nodeCount;
    this.innovationNumber = other.innovationNumber;

    // deep copy of other.nodeGenes saved to this.nodeGenes
    for (Map.Entry<Integer, Node> entry : other.nodeGenes.entrySet())
    {
        this.nodeGenes.put(entry.getKey(),new Node(entry.getValue()));
    }

    // deep copy of other.conncetion saved to this.connectionGenes
    for (Connection c : other.connectionGenes)
    {
      this.connectionGenes.add(
            new Connection( this.nodeGenes.get(c.getFromNode().getNumber()),
                            this.nodeGenes.get(c.getToNode().getNumber()),
                            c.getWeight(), c.getIsExpressed(), c.getInnovationNumber() ) );
    }

    //set in- and outgoing connections of the nodes
    for (Node n : this.nodeGenes.values())
    {
        n.calculateInOutGoing(this.connectionGenes);
    }

  }

  public void crossover(Genome other) throws Exception //-------------------------------------------
  {
    Genome offspring; // needs to be created though both parents
    Genome strongerParent;
    Genome weakerParent;
    int highestInnovation; // highest Innovation Number of the stronger parent
    int matchingGenes = this.numOfMatchingGenes(other);
    if (this.fitness > other.fitness || (this.fitness == other.fitness && new Random().nextInt(2) == 0) )
    {
      strongerParent = this;
      weakerParent = other;
    } else {
      strongerParent = other;
      weakerParent = this;
    }
    offspring = new Genome(strongerParent);
    highestInnovation = strongerParent.connectionGenes.get(this.connectionGenes.size()-1).getInnovationNumber();

    for (int i = matchingGenes; weakerParent.connectionGenes.get(i).getInnovationNumber() <= highestInnovation; i++)
    {
      boolean inStrongerParent = false;
      int checkInnovation = weakerParent.connectionGenes.get(i).getInnovationNumber();
      for (int j = matchingGenes; j < strongerParent.connectionGenes.size(); j++)
      {
        if (strongerParent.connectionGenes.get(j).getInnovationNumber() == checkInnovation)
        {
          inStrongerParent = true;
          break;
        }
      }
      if (!inStrongerParent)
      {
        Connection weakConnection = weakerParent.connectionGenes.get(i);
        // get fromNode and toNode of this connection
        int fromNum = weakConnection.getFromNode().getNumber();
        int toNum = weakConnection.getToNode().getNumber();

        //getFromNode
        Node from;
        if (offspring.nodeGenes.get(fromNum) != null)
        {
          from = offspring.nodeGenes.get(fromNum);
        } else {
          from = new Node("hidden",fromNum);
          offspring.nodeGenes.put(fromNum,from);
        }

        //getToNode
        Node to;
        if (offspring.nodeGenes.get(toNum) != null)
        {
          to = offspring.nodeGenes.get(toNum);
        } else {
          to = new Node("hidden",toNum);
          offspring.nodeGenes.put(toNum,to);
        }

        Connection clonedConnection = new Connection(from,to,weakConnection.getWeight(),weakConnection.getIsExpressed(),checkInnovation);
        offspring.connectionGenes.add(clonedConnection);
      }
    }
    Collections.sort(offspring.connectionGenes, (a,b) -> a.getInnovationNumber() - b.getInnovationNumber());

  }



  public void mutateEnableDisableConnection(Connection connection) //--------------------------------------------
  {
    connection.switchIsExpressed();
  }

  public void mutateConnection(Connection connection, double mutationFactor) //----------------------------------
  {
    connection.mutateWeight(mutationFactor);
  }

  //check if it creates a cycle !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  public void mutateAddConnection(int from, int to) //------------------------------------------------------------
  {
      Node start = this.nodeGenes.get(from);
      Node end = this.nodeGenes.get(to);
      if (createsCycle(start,end)) { return; }
      mutateAddConnection(start, end);
  }

  public void mutateAddConnection(Node start, Node end) //-------------------------------------------------------
  {
      Connection newConnection = new Connection(start,end,true,true,innovationUp());
      this.connectionGenes.add(newConnection);
      start.addOutGoing(newConnection);
      end.addInGoing(newConnection);
  }

  public void mutateAddNode(Connection oldConnection) //-----------------------------------------------------------
  {
    if (this.nodeGenes.size() == this.maxNodes) { return; }
    Node from = oldConnection.getFromNode();
    Node to = oldConnection.getToNode();
    oldConnection.disable();
    this.nodeCount++;
    Node middle = new Node("hidden",this.nodeCount);
    this.nodeGenes.put(nodeCount,middle);
    this.connectionGenes.add(new Connection(from,middle,true,true,innovationUp()));
    this.connectionGenes.add(new Connection(middle,to,oldConnection.getWeight(),
                                            true,oldConnection.getInnovationNumber()));
  }

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

  public boolean createsCycle(Node start,Node end) { //-----------------------------------------------------
    unvisitAllNodes();
    Stack<Node> stack = new Stack<>();
    stack.push(start);
    while (!stack.empty())
    {
      Node tmp = stack.pop();
      tmp.setVisited(true);
      if (tmp.next().contains(end)) { return true; }
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
    for (int i = 1; i <= currentState.getRows(); i++)
    {
      // set the values of the input nodes
      this.nodeGenes.get(i).setValue(currentState.getEntry(i-1));
    }
  }

  public Matrix getOutput() //----------------------------------------------------------------------------
  {
    Matrix out = new Matrix(this.outNodes,1,false);
    for (int i = 1; i <= this.outNodes; i++)
    {
      out.setEntry(i-1,this.nodeGenes.get(this.inNodes+i).getValue());
    }
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
      try {
        board.nextMove(this.playerNum,Matrix.argmax(getOutput()));
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
        this.fitness = -100 + this.board.getMoves();
      } else if (this.board.getWinner() == this.playerNum) {
        this.fitness = 150 - this.board.getMoves();
      } else if (this.board.getWinner() == -1*this.playerNum) {
        this.fitness = this.board.getMoves();
      } else {
        this.fitness = 75;
      }
    }

    public int numOfMatchingGenes(Genome other) //---------------------------------------------------------
    {
      int num = 0;
      for (int i = 0; i < this.connectionGenes.size(); i++)
      {
        if (this.connectionGenes.get(i).getInnovationNumber() != other.connectionGenes.get(i).getInnovationNumber())
        {
          break;
        }
        num++;
      }
      return num;
    }

    public double compatibilityDistance(Genome other)
    {
      int matchingGenes = this.numOfMatchingGenes(other);
      int numGenes1 = this.connectionGenes.size();
      int numGenes2 = other.connectionGenes.size();
      int N = Math.max(numGenes1,numGenes2);

      double delta = (mu / (N + 0.0)) * (numGenes1+numGenes2-2*matchingGenes);
      double weightDiff = 0.0;
      for (int i = 0; i < matchingGenes; i++)
      {
        weightDiff += Math.abs(this.connectionGenes.get(i).getWeight() - other.connectionGenes.get(i).getWeight());
      }
      delta += lambda * ( weightDiff / (matchingGenes + 0.0));
      return delta;
    }



}
