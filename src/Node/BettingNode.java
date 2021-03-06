package Node;
import Abstraction.ActionAbstraction;
import Cfr.ActionType;
import Cfr.GameState;
import Cfr.Hand;
import Para.Game;
import Para.IGame;


public class BettingNode implements IGame{
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
	 * 计算utility
	 * @param hand
	 * @param position
	 * @return
	 */
	public int evaluate(Hand hand,int position) {
		return 0;
	}
	
//	private static int getActions(GameState state, ActionType[] act, int stage) {
//		int numActions = 0;
//		char[] actionName = { 'f', 'c', 'r' };
//		for (int a = 0; a < 3; ++a) {
//			ActionType action = new ActionType(actionName[a], 0);
//			if (action.getType() == 'r') {
//				int[] raiseSize = { 0, 0 }; // minRaiseSize and maxRaiseSize
//				if (state.raiseIsValid(raiseSize)) {
//					/*
//					 * Check for pot-size raise being valid. First, get the pot
//					 * size.
//					 */
//					int potSize = 0;
//					for (int p = 0; p < MAX_PLAYERS; ++p) {
//						potSize += state.getSpent(p);
//					}
//					/*
//					 * Add amount needed to call. This gives the size of a
//					 * pot-sized raise
//					 */
//					int player = state.currentPlayer();
//					int amount_to_call = state.getMaxSpent()
//							- state.getSpent(player);
//					potSize += amount_to_call;
//					switch (stage) {
//					case 1:
//						break;
//					case 2:
//						break;
//					default:
//						for (Double value : BetSizes.getRiverActionAbstraction().values()) {
//							int potRaiseSize = (int) (value * potSize
//									+ state.getSpent(player) + amount_to_call);
//							if (potRaiseSize < raiseSize[1]) {
//								act[numActions] = new ActionType('r',
//										potRaiseSize);
//								++numActions;
//							}
//						}
//					}
//					/* Now add all-in */
//					act[numActions] = new ActionType('r', raiseSize[1]);
//					++numActions;
//				}
//			} else if (state.isValidAction(action)) {
//				act[numActions] = new ActionType(action.getType(), 0);
//				++numActions;
//			}
//		}
//		return numActions;
//	}
	
	public BettingNode initBettingTree(GameState state, Game game, ActionAbstraction actionAbs, int[] numEntriesPerBucket) {
	    BettingNode	node;
		if(state.isFinished()){
			// create termial node
			boolean showdown = state.getPlayerFolded(0) || state.getPlayerFolded(1) ? false : true;
			int[] foldValue = new int[2];
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
		ActionType[] actions = new ActionType[MAX_ABSTRACT_ACTIONS];
		int numChoices = actionAbs.getActions(game, state, actions);
		
//		System.out.println((actions[0].getType()));
//		System.out.println((actions[1].getType()));
		
        /* Next, grab the index for this node into the regrets and avg_strategy */
        int solnIdx=numEntriesPerBucket[state.getRound()];
        /* Update number of entries */
        numEntriesPerBucket[state.getRound()]+=numChoices;
        /* Recurse to create children */       
		BettingNode first_child = null;
		BettingNode last_child = null;
//		System.out.println(numChoices);
		for (int a = 0; a < numChoices; a++) {
			GameState newState =(GameState)state.clone();
			newState.doAction(game, actions[a]);
			BettingNode child = initBettingTree(newState, game, actionAbs, numEntriesPerBucket);
			if (last_child != null) {
				last_child.setSibling(child);
			} else {
				first_child = child;
			}
			last_child = child;
		}
		assert(last_child != null);
		last_child.setSibling(null);
		node = new InfoSetNode(solnIdx, numChoices, state.currentPlayer(game), state.getRound(), first_child);
		return node;
	}
	
}
