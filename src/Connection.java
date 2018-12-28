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

  public boolean getIsExpressed() { return this.isExpressed; }

  public void disable() { this.isExpressed = false; }

  public void switchIsExpressed() { this.isExpressed = !this.isExpressed; }

  @Override
  public String toString() {
          StringBuilder s = new StringBuilder();
          if (!this.isExpressed) { s.append("("); }
          s.append(this.fromNode.getNumber());
          s.append("->");
          s.append(this.toNode.getNumber());
          s.append(" [");
          s.append(String.format("%.2f", this.weight));
          s.append("]");
          s.append(" @" + this.innovationNumber);
          if (!this.isExpressed) { s.append(")"); }
          return s.toString();
  }

  public String toText() {
        StringBuilder s = new StringBuilder();
        s.append(this.fromNode.getNumber() + "_");
        s.append(this.toNode.getNumber() + "_");
        s.append(this.weight + "_");
        s.append(this.isExpressed + "_");
        s.append(this.innovationNumber);
        return s.toString();
  }

}
