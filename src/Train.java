public class Train {

  public static void main(String[] args) throws
  Exception
  {
    int size = 500;
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
  }

}
