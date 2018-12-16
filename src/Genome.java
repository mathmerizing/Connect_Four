import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Genome {
  private HashMap<Integer,Node> nodeGenes = new HashMap<>();
  private List<Connection> connectionGenes = new ArrayList<>();
  private int nodeCount = 0;
  private int innovationNumber = 0; //DO I NEED THIS HERE???

  private final int inNodes = 42;
  private final int outNodes = 7;

  public Genome()
  {
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
          this.connectionGenes.add(new Connection(this.nodeGenes.get(i),
                                                  this.nodeGenes.get(j),
                                                  true,true,innovationUp()));
      }
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

  public void mutateAddConnection(int from, int to)
  {
      Node start = this.nodeGenes.get(from);
      Node end = this.nodeGenes.get(to);
      mutateAddConnection(start, end);
  }

  public void mutateAddConnection(Node start, Node end)
  {
      Connection newConnection = new Connection(start,end,true,true,innovationUp());
      this.connectionGenes.add(newConnection);
  }

  public void mutateAddNode(Connection oldConnection)
  {
    Node in = oldConnection.getInNode();
    Node out = oldConnection.getOutNode();
    oldConnection.disable();
    this.nodeCount++;
    Node middle = new Node("hidden",this.nodeCount);
    this.nodeGenes.put(nodeCount,middle);
    this.connectionGenes.add(new Connection(in,middle,true,true,innovationUp()));
    this.connectionGenes.add(new Connection(middle,out,oldConnection.getWeight(),
                                            true,oldConnection.getInnovationNumber()));
  }

  public int innovationUp() { this.innovationNumber++; return this.innovationNumber; }
}
