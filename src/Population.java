public class Population {
  private int size;
  private NN[] networks;

  public Population(int size) throws Exception {
    this.size = size;
    this.networks = new NN[this.size];
    for (int i = 0; i < this.size; i++) { this.networks[i] = new NN(1,new Board()); }
  }

  public NN[] getNetworks() { return this.networks; }

}
