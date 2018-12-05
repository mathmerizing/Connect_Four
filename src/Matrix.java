import java.util.Random;

public class Matrix{
  private int rows;
  private int columns;
  double[][] content;

  public Matrix(int rows, int columns,boolean randomize)
  {
    this.rows = rows;
    this.columns = columns;
    this.content = new double[rows][columns];
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.columns; j++) {
        this.content[i][j] = (randomize ? new Random().nextGaussian() : 0);
      }
    }
  }

  public Matrix(int[] array)
  {
      this.rows = array.length;
      this.columns = 1;
      this.content = new double[this.rows][this.columns];
      for (int i = 0; i < array.length; i++) { this.content[i][0] = array[i]; }
  }

  public static Matrix multiply(Matrix A, Matrix B)
  {
    Matrix M = new Matrix(A.rows, B.columns,false);
    for (int i = 0; i < M.rows; i++)
    {
       for (int j = 0; j < M.columns; j++)
       {
           for (int k = 0; k < B.rows; k++)
           {
               M.content[i][j] += A.content[i][k] * B.content[k][j];
           }
       }
    }
   return M;
  }

  public void sigmoid()
  {
    for (int i = 0; i < this.rows; i++)
    {
      for (int j = 0; j < this.columns; j++)
      {
        this.content[i][j] = 1.0/(1.0+java.lang.Math.exp(-1.0*this.content[i][j])); 
      }
    }
  }

  @Override
  public String toString()
  {
    StringBuilder s = new StringBuilder();
    for (double[] L : this.content)
    {
        s.append("|");
        for (double d : L)
        {
            s.append(" ");
            s.append(String.format("%.2f", d));
        }
        s.append("|\n");
    }
    s.append(this.rows).append("X").append(this.columns);
    return s.toString();
  }


}
