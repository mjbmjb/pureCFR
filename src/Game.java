
interface Game {
	public static int MAX_PLAYERS = 0;
	public static int MAX_ROUNDS = 4;
	public static int MAX_BOARD_CARDS=7;
	public static int MAX_HOLE_CARDS=3;
	public static int MAX_NUM_ACTIONS=64;
	public static int MAX_SUITS=4;
	public static int MAX_RANKS=13;
//	public static int MAX_LINE_LEN=READBUF_LEN;

	public static int NUM_ACTION_TYPES=3;
	public static int BIG_BLIND = 20;
	public static int SMALL_BLIND = 10;//TODO 大小盲注默认值
	public static int STACK_SIZE = 0;
	public static int MAX_ABSTRACT_ACTIONS = 4;
	public class GameDef {

		  /* stack sizes for each player */
		  public int[] stack = new int[ MAX_PLAYERS ];

		  /* entry fee for game, per player */
		  public int[] blind = new int[ MAX_PLAYERS ];

		  /* size of fixed raises for limitBetting games */
		  public int[] raiseSize = new int[ MAX_ROUNDS ];

		  /* general class of game */
//		  enum BettingType bettingType;

		  /* number of players in the game */
		  public int numPlayers;

		  /* number of betting rounds */
		  public int numRounds;

		  /* first player to act in a round */
		  public int[] firstPlayer = new int[ MAX_ROUNDS ];

		  /* number of bets/raises that may be made in each round */
		  public int[] maxRaises = new int[ MAX_ROUNDS ];

		  /* number of suits and ranks in the deck of cards */
		  public int numSuits;
		  public int numRanks;

		  /* number of private player cards */
		  public int numHoleCards;

		  /* number of shared public cards each round */
		  public int[] numBoardCards = new int[ MAX_ROUNDS ];	
	}
}
