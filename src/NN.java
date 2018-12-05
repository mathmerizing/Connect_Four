import java.util.Random;

public class NN extends Player {
private int[] neurons;
private Matrix[] weights;

public NN(int playerNum, Board board) throws Exception {
        super("NN_" + String.valueOf((new Random()).nextInt()), playerNum, board);
        this.neurons = new int[] {42, 7};
        this.setNN(true);
}
public void move(Board board) {
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
        for (int i = 0; i < this.neurons.length - 1; i++)
        {
                this.weights[i] = new Matrix(this.neurons[i+1],this.neurons[i],randomize);
        }
}

public Matrix forwardProp(Board board) {
        Matrix output = new Matrix(1,1,false); //just a place holder
        Matrix input = new Matrix(board.getBoardStateLinear());
        for (Matrix w : weights) {
                output = Matrix.multiply(w,input);
                output.sigmoid();
                input = output;
        }
        return output;
}



}
