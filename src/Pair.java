public class Pair {
    private int first;
    private int second;
    private static final long serialVersionUID = 1L;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return this.first;
    }

    public int getSecond() {
        return this.second;
    }

    @Override
    public String toString() { return "("+ this.first + "," + this.second + ")"; }
}
