import java.lang.Thread.State;


public class BettingNode {
	protected int showdown;
	protected int[] fold_value = new int[2];
	protected int money;
	
	public BettingNode() {
		// TODO Auto-generated constructor stub
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
		actionType[] actions = new actionType[Game.MAX_ABSTRACT_ACTION];
		int numChoices = getActions(state, actions,stage);
        int solnIdx=numEntriesPerBucket[0];
        numEntriesPerBucket[0]+=numChoices;
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
		node = new InfoSetNode(solnIdx, numChoices,state.currentPlayer(), state.getRound(), first_child);
		return node;
	}
	
}
