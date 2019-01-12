import java.util.ArrayList;
import java.util.List;

public class Node {
  private String type = "";
  private int number;
  private List<Connection> ingoing = new ArrayList<>();
  private List<Connection> outgoing = new ArrayList<>();
  private double value = 0.0;

  private boolean valueCalculated = false;
  private boolean visited = false;

  public Node(String type, int number)
  {
    assert (type != "input" && type != "hidden" && type != "output");
    this.type = type;
    this.number = number;
    System.out.println("Created " + this.toString());
  }

  public Node(Node other)
  {
    this.type = other.type;
    this.number = other.number;
    this.value = other.value;
  }

  public void calculateInOutGoing(List<Connection> connections)
  {
    for (Connection c : connections)
    {
      if (c.getFromNode().getNumber() == this.getNumber())
      {
        this.addOutGoing(c);
      } else if (c.getToNode().getNumber() == this.getNumber())
      {
        this.addInGoing(c);
      }
    }
  }

  public int getNumber() { return this.number; }

  public void addInGoing(Connection c)
  {
      if (this.ingoing.contains(c)) { return; }
      this.ingoing.add(c);
  }
  public void addOutGoing(Connection c)
  {
    if (this.outgoing.contains(c)) { return; }
    this.outgoing.add(c);
  }
  public void removeInGoing(Connection c)
  {
    if (!this.ingoing.contains(c))
    {
      System.err.println("Connection " + c + " is not in " + this.ingoing);
      return;
    }
    this.ingoing.remove(c);

  }
  public void removeOutGoing(Connection c)
  {
    if (!this.outgoing.contains(c))
    {
      System.err.println("Connection " + c + " is not in " + this.outgoing);
      return;
    }
    this.outgoing.remove(c);
  }

  public List<Connection> getIngoing() { return this.ingoing; }
  public List<Connection> outIngoing() { return this.outgoing; }

  //public double getValue() { return this.value; }
  public void setValue(double val) { this.value = val; }

  public void setVisited(boolean b) { this.visited = b; }
  public boolean getVisited() { return this.visited; }

  public String getType() { return this.type; }

  public void setValueCalculated(boolean b) { this.valueCalculated = b; }
  public boolean getValueCalculated() { return this.valueCalculated; }

  public double getValue()
  {
    if (this.valueCalculated) { return this.value; };


    System.err.println("value(" + this.number + ")");
    this.ingoing.forEach(c -> System.err.print(c + "; "));
    System.err.println(" ");


    double sum = 0;
    for (Connection c : this.ingoing)
    {
      if (c.getIsExpressed()) {
        Node node = c.getFromNode();
        double weight = c.getWeight();
        sum += node.getValue() * weight;
      }
    }
    sum = 1.0/(1.0+java.lang.Math.exp(-1.0*sum));
    this.value = sum;

    this.valueCalculated = true;

    return this.value;
  }

  public List<Node> last()
  {
    List<Node> out = new ArrayList<>();
    for (Connection c : this.ingoing)
    {
      if (c.getIsExpressed()) { out.add(c.getFromNode()); }
    }
    return out;
  }

  public int getDepth()
  {
    if (this.type == "input") {
      return 0;
    } else if (this.type == "output") {
      return 100;
    }
    int lastDepth = -1;
    for (Connection c : this.ingoing)
    {
      lastDepth = Math.max(c.getFromNode().getDepth(),lastDepth);
    }
    lastDepth++;
    return lastDepth;
  }

  public List<Node> next()
  {
    List<Node> out = new ArrayList<>();
    for (Connection c : this.outgoing)
    {
      out.add(c.getToNode());
    }
    return out;
  }

  public List<Node> nextUnvisited()
  {
    List<Node> out = next();
    out.removeIf(n -> n.getVisited() == true);
    return out;
  }

  @Override
  public String toString() {
          StringBuilder s = new StringBuilder();
          s.append(this.type);
          s.append("_");
          s.append(this.number);
          return s.toString();
  }

}
