import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

public class Train {

  public static void main(String[] args) throws
  Exception
  {
    Genome test = new Genome(-1);
    Genome copy = new Genome(test);
    copy.mutateAddNode(copy.getConnectionGenes().get(0));
    System.out.println("test: \n " + test);
    System.out.println("copy: \n " + copy);
    System.out.println("compat:" + test.compatibilityDistance(copy));
    System.out.println("compat2:" + copy.compatibilityDistance(test));
    copy.save();
    Genome loaded = Genome.load(new File("./saved/" + copy.getName() + ".txt"), -1);
    System.out.println("compat3:" + copy.compatibilityDistance(loaded));

    // testing out bash command calls from Java
    Process p;
    try {
      p = Runtime.getRuntime().exec("python3 graphNN.py" + copy.getName() + ".txt"); // add genome path for it to be shown
      p.waitFor();
      String shellOutput = "";
      InputStreamReader ir = new InputStreamReader(p.getInputStream());
      BufferedReader br = new BufferedReader(ir);
      String nextLine = "";
			while ((nextLine = br.readLine())!= null) {
				shellOutput += (nextLine + "\n");
			}
      System.out.println(shellOutput);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // -------------------------------------------------------------------

    int size = 5;
    Population population = new Population(size);
    long startTime = System.currentTimeMillis();
    population.play();
    long endTime = System.currentTimeMillis();
    //population.sortedByFitness().forEach(g -> System.out.println(g.getFitness()));
    population.calculateCumulativeFitness();
    System.out.println("cumulativeFitness: " + population.getCumulativeFitness());
    System.out.println("averageFitness: " + population.getAverageFitness());
    System.out.println((endTime - startTime) + " milli seconds for playing all genomes");
    System.out.println((endTime - startTime)/(0.0 + size) + " milli seconds for playing one genome");

    Genome first = population.getGenomes()[0];
    for (Genome g : population.getGenomes())
    {
      System.out.println(first.compatibilityDistance(g));
      //System.out.println(g);
    }
  }

}
