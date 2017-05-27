
public class Game implements IGame {

	public Game() {
		stack[0] = 1000;
		stack[1] = 1000;
	}
		  /* stack sizes for each player */
		  public int[] stack = new int[ MAX_PLAYERS ];

		  /* entry fee for game, per player */
		  public int[] blind = new int[ MAX_PLAYERS ];

		  /* size of fixed raises for limitBetting games */
//		  public int[] raiseSize = new int[ MAX_ROUNDS ];
		  public int[] raiseSize = {1,0,0,0};

		  /* general class of game */
//		  enum BettingType bettingType;

		  /* number of players in the game */
		  public int numPlayers = 2;

		  /* number of betting rounds */
		  public int numRounds = 1;

		  /* first player to act in a round */
		  public int[] firstPlayer = new int[ MAX_ROUNDS ];

		  /* number of bets/raises that may be made in each round */
		  public int[] maxRaises = new int[ MAX_ROUNDS ];

		  /* number of suits and ranks in the deck of cards */
		  public int numSuits = 1;
		  public int numRanks = 3;

		  /* number of private player cards */
		  public int numHoleCards = 1;

		  /* number of shared public cards each round */
		  public int[] numBoardCards = {0,0,0,0};	
		  
		  // betting type unlimit=1 limit = 0
		  public boolean bettingType = false;
	
}
