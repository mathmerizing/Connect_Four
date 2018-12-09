abstract class Player
{
    final String name;
    int playerNum;
    private String tileType;

    Player(String name, int playerNum, Board board) throws
            Exception
    {
        if (playerNum == board.getEmptyColor())
        {
            throw new Exception("playerNum must not be equal to " + board.getEmptyColor() + " !");
        }

        this.name = name;
        this.playerNum = playerNum;
        this.tileType = board.popTile();
        board.setHashMap(this.playerNum, this.tileType);
    }


    public abstract void move(Board board);

    String getName()
    {
        return this.name;
    }


}
