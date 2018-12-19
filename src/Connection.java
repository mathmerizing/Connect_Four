import java.util.Random;

public class Connection {
  private Node fromNode = null;
  private Node toNode = null;
  private double weight = 0.0;
  private boolean isExpressed = true;
  private int innovationNumber = 0;

  public Connection(Node from, Node to, boolean randomWeight, boolean expressed, int innovation)
  {
      this(from,to,new Random().nextGaussian(),expressed,innovation);
  }

  public Connection(Node from, Node to, double w, boolean expressed, int innovation)
  {
    this.fromNode = from;
    this.toNode = to;
    this.weight = w;
    this.isExpressed = expressed;
    this.innovationNumber = innovation;
  }

  // mutationFactor should probably be in (0,1]
  public void mutateWeight(double mutationFactor) {
    this.weight += mutationFactor*(new Random().nextGaussian());
  }

  public Node getFromNode() { return this.fromNode; }

  public Node getToNode() { return this.toNode; }

  public double getWeight() { return this.weight; }

  public int getInnovationNumber() { return this.innovationNumber; }

  public void disable() { this.isExpressed = false; }

  public void switchIsExpressed() { this.isExpressed = !this.isExpressed; }
}
