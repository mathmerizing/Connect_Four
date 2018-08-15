public abstract class Player
{
    protected final String name;
    protected int playerNum;
    private String tileType;

    public Player(String name, int playerNum, Board board) throws
            Exception
    {
        if (playerNum == 0)
        {
            throw new Exception("playerNum must not be equal to 0!");
        }

        this.name = name;
        this.playerNum = playerNum;
        this.tileType = board.popTile();
        board.setHashMap(this.playerNum, this.tileType);
    }


    public abstract void move(Board board);

    public String getName()
    {
        return this.name;
    }

}
