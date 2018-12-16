public class Node {
  private String type = "";
  private int number;

  public Node(String type, int number)
  {
    assert (type != "input" && type != "hidden" && type != "output");
    this.type = type;
    this.number = number;
  }

  public int getNumber() { return this.number; }
}
