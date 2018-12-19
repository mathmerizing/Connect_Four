import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class Genome extends Player {
  private HashMap<Integer,Node> nodeGenes = new HashMap<>();
  private List<Connection> connectionGenes = new ArrayList<>();
  private int nodeCount = 0;
  private int innovationNumber = 0; //DO I NEED THIS HERE???

  private final int inNodes = 42;
  private final int outNodes = 7;

  private final int maxNodes = 200;

  private Board board;

  private int fitness = 0;
  private boolean illegalMove = false;


  public Genome(int playerNum, Board board) throws Exception
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

  public void mutateEnableDisableConnection(Connection connection)
  {
    connection.switchIsExpressed();
  }

  public void mutateConnection(Connection connection, double mutationFactor)
  {
    connection.mutateWeight(mutationFactor);
  }

  //check if it creates a cycle !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  public void mutateAddConnection(int from, int to)
  {
      Node start = this.nodeGenes.get(from);
      Node end = this.nodeGenes.get(to);
      if (createsCycle(start,end)) { return; }
      mutateAddConnection(start, end);
  }

  public void mutateAddConnection(Node start, Node end)
  {
      Connection newConnection = new Connection(start,end,true,true,innovationUp());
      this.connectionGenes.add(newConnection);
      start.addOutGoing(newConnection);
      end.addInGoing(newConnection);
  }

  public void mutateAddNode(Connection oldConnection)
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

  public int innovationUp() { this.innovationNumber++; return this.innovationNumber; }

  private void unvisitAllNodes()
  {
    for (Node n: this.nodeGenes.values())
    {
      n.setVisited(false);
    }
  }

  private void resetAllValueCalculated()
  {
    for (Node n: this.nodeGenes.values())
    {
      // "input" nodes' values don't need to be calulated
      n.setValueCalculated((n.getType() == "input") ? true : false);
    }
  }

  public boolean createsCycle(Node start,Node end) {
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


  public void setInput()
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

  public Matrix getOutput()
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
    public void move(Board board)
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

    public boolean getIllegalMove() { return this.illegalMove; }
    public void resetIllegalMove() { this.illegalMove = false; }

    public Board getBoard() { return this.board; }
    public int getFitness() { return this.fitness; }

    public void calculateFitness()
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

}
