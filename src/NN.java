import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class NN extends Player {
private int[] neurons = new int[] {42, 7};
private Matrix[] weights;
private Matrix[] biases;
private Board board;
private int fitness = 0;
private List<Board> archivedBoards = new ArrayList<>();

public NN(int playerNum, Board board) throws Exception {
        super("NN_" + String.valueOf((new Random()).nextInt()), playerNum, board);
        this.setNN(true);
        this.board = board;
}
public void move(Board board) {
  int col = Matrix.argmax(Matrix.componentMultiply(this.forwardProp(),board.getPossibleMovesVector()));
  try {
    board.nextMove(this.playerNum,col);
  } catch (Exception e) {
    e.printStackTrace();
  }
}

@Override
public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Name: ");
        s.append(this.name);
        s.append("\n");
        s.append("Weights:\n");
        for (Matrix m : this.weights)
        {
                s.append(m.toString());
        }
        return s.toString();
}

public void setNN(boolean randomize)
{
        this.weights = new Matrix[this.neurons.length-1];
        this.biases = new Matrix[this.neurons.length-1];
        for (int i = 0; i < this.neurons.length - 1; i++)
        {
                this.weights[i] = new Matrix(this.neurons[i+1],this.neurons[i],randomize);
                this.biases[i] = new Matrix(this.neurons[i+1],1,randomize);
        }
}

public Matrix forwardProp() {
        Matrix output = new Matrix(1,1,false); //just a place holder
        Matrix input = this.board.getBoardStateLinear();
        for (int i = 0; i < this.weights.length; i++) {
                output = Matrix.multiply(this.weights[i],input);
                output = Matrix.add(this.biases[i],output);
                output.sigmoid();
                input = output;
        }
        return output;
}

public NN mutatedCopy(double mutation) throws Exception
{
  // TO DO !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  NN out = new NN(-1,new Board());
  out.neurons = this.neurons.clone();
  for (int i = 0; i < this.weights.length; i++)
  {
    out.weights[i] = this.weights[i].mutatedCopy(mutation);
    out.biases[i] = this.biases[i].mutatedCopy(mutation);
  }
  return out;
}

public void archiveBoard()
{
  this.archivedBoards.add(this.board);
  this.board = new Board();
  this.setTileType(this.board.popTile());
  this.board.setHashMap(this.playerNum, this.getTileType());
}

public Board getBoard() { return this.board; }

public void increaseFitness(int val) { this.fitness += val; }
public int getFitness() { return this.fitness; }


}
