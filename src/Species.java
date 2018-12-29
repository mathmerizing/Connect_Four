import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class Species {
  private int speciesNum = -1;
  private List<Genome> genomesList = new ArrayList<>();
  private List<Genome> elite = new ArrayList<>();
  private int N = 0; // size of species
  private double cumulativeFitness = 0;

  private final double COMPATIBILITY_THRESHOLD = 4.0;
  private final double ELITE_PERCENTAGE = 0.4;

  public Species(int num)
  {
    this.speciesNum = num;
  }

  public static List<Species> updateSpecies(List<Species> speciesList, Genome genome)
  {
    boolean added = false;
    for (Species s : speciesList)
    {
      added = s.speciate(genome);
      if (added) { break; }
    }
    if (!added)
    {
      Species newSpecies = new Species(speciesList.size()+1);
      newSpecies.add(genome);
      speciesList.add(newSpecies);
    }
    return speciesList;
  }

  public boolean speciate(Genome newGenome)
  {
    boolean fits = this.fitsToSpecies(newGenome);
    if (fits) { this.add(newGenome); }
    return fits;
  }

  public boolean fitsToSpecies(Genome newGenome)
  {
    if (this.N == 0) { return true; }
    int randIndex = new Random().nextInt(N);
    Genome randGenome = this.genomesList.get(randIndex);
    if (randGenome.compatibilityDistance(newGenome) < this.COMPATIBILITY_THRESHOLD)
    {
      return true;
    } else {
      return false;
    }
  }

  public void add(Genome g)
  {
      this.genomesList.add(g);
      this.N++;
  }

  public void reproduce(double averageFitness) throws Exception
  {
    int N_new;
    if (this.cumulativeFitness == 0)
    {
      this.genomesList.forEach(g -> this.cumulativeFitness += g.getFitness());
    }
    N_new = (int) Math.round(this.cumulativeFitness/averageFitness);
    System.out.println("N_new = " + N_new); // COMMENT THIS OUT !!!!!!!!!!!!!!!!!!

    Collections.sort(this.genomesList, (a,b) -> b.getFitness() - a.getFitness());

    int eliteNum = (int) Math.round(this.N * this.ELITE_PERCENTAGE);
    this.elite = this.genomesList.subList(0,eliteNum);
    if (this.elite.size() == 0) { this.elite.add(this.genomesList.get(0)); eliteNum = 1; }

    // do the reproduction part
    List<Genome> survivingGenomes = new ArrayList<>();
    if (N >= 5) { survivingGenomes.add(new Genome(this.elite.get(0))); N_new--; }
    while (N_new > 0)
    {
      int firstRandIndex = new Random().nextInt(eliteNum);
      int secondRandIndex = new Random().nextInt(eliteNum);
      Genome firstElite = this.elite.get(firstRandIndex);
      Genome secondElite = this.elite.get(secondRandIndex);
      Genome offspring = firstElite.crossover(secondElite);
      offspring.mutate();
      survivingGenomes.add(offspring);
      N_new--;
    }
    this.genomesList = survivingGenomes;
    this.N = this.genomesList.size();
    // do not reuse species in next epoch, variables don't need to be reset
  }

  public List<Genome> getGenomeList() { return this.genomesList; }




}
