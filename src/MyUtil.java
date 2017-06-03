
public  class MyUtil implements IGame{

	public MyUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static void pr(String str) {
		System.out.print(str);
	}
	
	public static void prl(String str) {
		System.out.println(str);
	}
	
	public String printState(Game game, GameState state, int maxLen) {
		int c, r;
		
		c = 0;
		
		// STATE
		
	}
	
	public static void printStrategtyR(PlayerModule playerModule,
								  GameState state,
								  AbstractGame ag,
								  int p,
								  int maxRound) {
		if (state.isFinished()|| (state.getRound()>= maxRound)) {
		    /* End of game or we've gone past the rounds we care to print */
		    return;
		}
		
		/* Get the possible actions */
		ActionType[] actions = new ActionType[MAX_ABSTRACT_ACTIONS];
		int numChoices = ag.actionAbs.getActions(ag.game,state, actions);
		
		if (p == state.currentPlayer(ag.game)) {
			/* Get the state info in a string */	
			String stateStr;
			
		}
	}

}
