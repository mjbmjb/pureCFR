
public class BettingNode implements Game{
	protected int showdown;
	protected int[] fold_value = new int[2];
	protected int money;
	protected BettingNode sibling = null;
	


	public BettingNode() {
		// TODO Auto-generated constructor stub
	}
	
	public int getSolnIdx() {
		return 0;
	}
	
	public int getNumChoices() {
		return 0;
	}

	public BettingNode getChild() {
		return null;
	}
	
	public void setSibling(BettingNode sibing) {
		this.sibling = sibing;
	}
	
	public BettingNode getSibling() {
		return sibling;
	}
	
	public int getRound() {
		return 0;
	}
	
	public int getPlayer() {
		return 0;
	}
	
	public boolean didPlayerFold(int position) {
		return false;
	}

	/**
	 * º∆À„utility
	 * @param hand
	 * @param position
	 * @return
	 */
	public int evaluate(Hand hand,int position) {
		return 0;
	}
	
	private static int getActions(GameState state, ActionType[] act, int stage) {
		int numActions = 0;
		char[] actionName = { 'f', 'c', 'r' };
		for (int a = 0; a < 3; ++a) {
			ActionType action = new ActionType(actionName[a], 0);
			if (action.getType() == 'r') {
				int[] raiseSize = { 0, 0 }; // minRaiseSize and maxRaiseSize
				if (state.raiseIsValid(raiseSize)) {
					/*
					 * Check for pot-size raise being valid. First, get the pot
					 * size.
					 */
					int potSize = 0;
					for (int p = 0; p < MAX_PLAYERS; ++p) {
						potSize += state.getSpent(p);
					}
					/*
					 * Add amount needed to call. This gives the size of a
					 * pot-sized raise
					 */
					int player = state.currentPlayer();
					int amount_to_call = state.getMaxSpent()
							- state.getSpent(player);
					potSize += amount_to_call;
					switch (stage) {
					case 1:
						break;
					case 2:
						break;
					default:
						for (Double value : BetSizes.getRiverActionAbstraction().values()) {
							int potRaiseSize = (int) (value * potSize
									+ state.getSpent(player) + amount_to_call);
							if (potRaiseSize < raiseSize[1]) {
								act[numActions] = new ActionType('r',
										potRaiseSize);
								++numActions;
							}
						}
					}
					/* Now add all-in */
					act[numActions] = new ActionType('r', raiseSize[1]);
					++numActions;
				}
			} else if (state.isValidAction(action)) {
				act[numActions] = new ActionType(action.getType(), 0);
				++numActions;
			}
		}
		return numActions;
	}
	
	public static BettingNode initBettingTree(GameState state, int[] numEntriesPerBucket,int stage) {
	    BettingNode	node;
		if(state.isFinished() || state.getRoundIsOver(stage)){
			// create termial node
			boolean showdown = state.getPlayerFolded(0) || state.getPlayerFolded(1) ? false : true;
			int[] foldValue = new int[2];
			int money = -1;
			for (int p = 0; p < Game.MAX_PLAYERS; ++p) {
				if (state.getPlayerFolded(p)) {
					foldValue[p] = -1;
					money = state.getSpent(p);
				} else if (state.getPlayerFolded(1 - p)) {
					foldValue[p] = 1;
					money = state.getSpent(1 - p);
				} else {
					//no one had fold, so in showdown stage.
					foldValue[p] = 0;
					money = state.getSpent(p);
				}
			}
			node = new TerminalNode(showdown, foldValue, money);
			return node;
		}
		 /* Choice node.  First, compute number of different allowable actions */
		ActionType[] actions = new ActionType[MAX_ABSTRACT_ACTION];
		int numChoices = getActions(state, actions,stage);
        /* Next, grab the index for this node into the regrets and avg_strategy */
        int solnIdx=numEntriesPerBucket[0];
        /* Update number of entries */
        numEntriesPerBucket[0]+=numChoices;
        /* Recurse to create children */       
		BettingNode first_child = null;
		BettingNode last_child = null;
		for (int a = 0; a < numChoices; a++) {
			GameState newState =(GameState)state.clone();
			newState.doAction(actions[a]);
			BettingNode child = initBettingTree(newState, numEntriesPerBucket,stage);
			if (last_child != null) {
				last_child.setSibling(child);
			} else {
				first_child = child;
			}
			last_child = child;
		}
		last_child.setSibling(null);
		node = new InfoSetNode(solnIdx, numChoices, state.currentPlayer(), state.getRound(), first_child);
		return node;
	}
	
}
