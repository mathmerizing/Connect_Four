public class Population {
private int size;
private NN[] networks;

public Population(int size) throws Exception {
        this.size = size;
        this.networks = new NN[this.size];
        for (int i = 0; i < this.size; i++) { this.networks[i] = new NN(-1,new Board()); }
}

public NN[] getNetworks() {
        return this.networks;
}

public void calculateFitness()
{
  for (NN nn : this.networks)
  {
    nn.setFitness(nn.getBoard().calculateScore());
  }
}

public void playMinimax(int depth) throws Exception
{
  for (NN nn : this.networks)
  {
    Minimax enemy = new Minimax(1,nn.getBoard(),2);
    while (!nn.getBoard().isGameOver())
    {
        nn.move(nn.getBoard());
        if (!nn.getBoard().isGameOver())
        {
            enemy.move(nn.getBoard());
        }
    }
  }
}

}
