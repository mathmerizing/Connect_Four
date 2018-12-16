import java.util.Random;

public class Connection {
  private Node inNode = null;
  private Node outNode = null;
  private double weight = 0.0;
  private boolean isExpressed = true;
  private int innovationNumber = 0;

  public Connection(Node in, Node out, boolean randomWeight, boolean expressed, int innovation)
  {
      this(in,out,new Random().nextGaussian(),expressed,innovation);
  }

  public Connection(Node in, Node out, double w, boolean expressed, int innovation)
  {
    this.inNode = in;
    this.outNode = out;
    this.weight = w;
    this.isExpressed = expressed;
    this.innovationNumber = innovation;
  }

  // mutationFactor should probably be in (0,1]
  public void mutateWeight(double mutationFactor) {
    this.weight += mutationFactor*(new Random().nextGaussian());
  }

  public Node getInNode() { return this.inNode; }

  public Node getOutNode() { return this.outNode; }

  public double getWeight() { return this.weight; }

  public int getInnovationNumber() { return this.innovationNumber; }

  public void disable() { this.isExpressed = false; }

  public void switchIsExpressed() { this.isExpressed = !this.isExpressed; }
}
