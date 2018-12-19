import java.util.Random;

public class Matrix {
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

public Matrix(int[][] array)
{
        this.rows = array.length;
        this.columns = array[0].length;
        this.content = new double[this.rows][this.columns];
        for (int i = 0; i < array.length; i++)
        {
          for (int j = 0; j < array.length; j++)
          {
            this.content[i][j] = array[i][j];
          }
        }
}

public static Matrix add(Matrix A, Matrix B)
{
  Matrix out = new Matrix(A.rows,A.columns,false);
  for (int i = 0; i < A.rows; i++)
  {
    for (int j = 0; j < A.columns; j++)
    {
      out.content[i][j] = A.content[i][j] + B.content[i][j];
    }
  }
  return out;
}

public static Matrix scalarMultiply(double scalar, Matrix A)
{
  Matrix out = new Matrix(A.rows,A.columns,false);
  for (int i = 0; i < A.rows; i++)
  {
    for (int j = 0; j < A.columns; j++)
    {
      out.content[i][j] = A.content[i][j]*scalar;
    }
  }
  return out;
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

public static Matrix componentMultiply(Matrix A, Matrix B)
{
  Matrix out = new Matrix(A.rows,A.columns,false);
  for (int i = 0; i < A.rows; i++)
  {
    for (int j = 0; j < A.columns; j++)
    {
      out.content[i][j] = A.content[i][j]*B.content[i][j];
    }
  }
  return out;
}

public double getEntry(int row)
{
  return getEntry(row,0);
}

public double getEntry(int row, int col)
{
  return this.content[row][col];
}

public void setEntry(int row, double val)
{
  setEntry(row,0,val);
}

public void setEntry(int row, int col, double val)
{
  this.content[row][col] = val;
}

public int getRows() { return this.rows; }

public static int argmax(Matrix M)
{
  double highest = Integer.MIN_VALUE;
  int index = 0;
  for (int i = 0; i < M.rows; i ++)
  {
      if (M.content[i][0] > highest)
      {
        highest = M.content[i][0];
        index =  i;
      }
  }
  return index;
}

public Matrix mutatedCopy(double mutation)
{
    Matrix out = new Matrix(this.rows, this.columns, false);
    Random generator = new Random();
    for (int i = 0; i < this.rows; i++)
    {
      for (int j = 0; j < this.columns; j++)
      {
        out.content[i][j] = ((mutation > generator.nextDouble()) ? generator.nextGaussian() : this.content[i][j]);
      }
    }
    return out;
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
