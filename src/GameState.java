
import java.lang.reflect.Array;
import java.util.Arrays;

//import javax.print.attribute.standard.RequestingUserName;
//import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
//
//import com.google.common.base.CaseFormat;
//import com.google.common.base.FinalizablePhantomReference;
//import com.google.common.base.Service.State;


public class GameState implements IGame, Cloneable {

	private final static int[] blind = { 100, 50 };

	/* current round : preflop = 0; flop = 1; turn = 2; river = 3 */
	private int round;

	/* Action[ r ][ i ] gives the i'th action in round r */
	private ActionType[][] action;

	/*
	 * actingPlayer[ r ][ i ] gives the player who made Action i in round r we
	 * can always figure this out from the Actions taken, but it's easier to
	 * just remember this in multiplayer (because of folds)
	 */
	private int[][] actingPlayer;

	/* numActions[ r ] gives the number of Actions made in round r */
	private int[] numActions;

	/* public cards (including cards which may not yet be visible to players) */
	private int[] boardCards;

	/* private cards */
	public int[][] holeCards;

	/* finished is true if and only if game is over */
	private boolean finished;

	/* playerFolded[ p ] is true if and only if player p has folded. */
	private boolean[] playerFolded;

	/* spent[ p ] gives the total amount put into the pot by player p */
	private int[] spent;

	/* minimum number of chips a player must have spend in total to raise */
	private int minNoLimitRaiseTo;
	/* largest bet so far, including all previous rounds */
	private int maxSpent;
	


	private boolean [] roundIsOver; 
	
	
	
	public GameState() {
		int p, r;
		maxSpent = 0;
		spent = new int[MAX_PLAYERS];
		playerFolded = new boolean[MAX_PLAYERS];
		action = new ActionType[MAX_ROUNDS][MAX_NUM_ACTIONS];
		actingPlayer = new int[MAX_ROUNDS][MAX_NUM_ACTIONS];
		numActions = new int[MAX_ROUNDS];
		boardCards = new int[MAX_BOARD_CARDS];
		holeCards = new int[MAX_PLAYERS][MAX_HOLE_CARDS];

		for (int i : spent)
			i = 0;

		for (boolean i : playerFolded)
			i = false;
		for (p = 0; p < MAX_PLAYERS; p++) {
			spent[0] = BIG_BLIND;
			spent[1] = SMALL_BLIND;
		}
		maxSpent = BIG_BLIND;
		minNoLimitRaiseTo = maxSpent * 2;

		round = 0;
		finished = false;
		roundIsOver = new boolean[4];
		for( boolean i : roundIsOver)
			i = false;
	}

	public boolean getRoundIsOver(int round){
		return roundIsOver[round];
	}
	
	public void initState() {
		// round
		round = 0;
		// Action[r][i]
		for (ActionType[] i : action) {
			Arrays.fill(i,null);
		}
		
		// actingPlayer[r][i]
		for (int[] i : actingPlayer) {
			Arrays.fill(i, 0);
		}
		
		// numActions[r]

		Arrays.fill(numActions, 0);
		// boardCards[i]

		Arrays.fill(boardCards, 0);
		// holeCards[p][i]
		for (int[] i : holeCards) {
			Arrays.fill(i, 0);
		}
		
		// finish
		finished = false;
		// playerFolded[p]

		Arrays.fill(playerFolded, false);
		// spent[p]
		for (int p = 0; p < MAX_PLAYERS; p++) {
			spent[0] = BIG_BLIND;
			spent[1] = SMALL_BLIND;
		}
		maxSpent = BIG_BLIND;
		minNoLimitRaiseTo = maxSpent * 2;
	}

	public int[] getNumActions(){
		return numActions;
	}
	
	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean[] getPlayerFolded() {
		return playerFolded;
	}

	public boolean getPlayerFolded(int position) {
		return playerFolded[position];
	}

	public void setPlayerFolded(boolean[] playerFolded) {
		this.playerFolded = playerFolded;
	}

	public int[] getSpent() {
		return spent;
	}
	
	public void setMaxSpent(int maxSpent) {
		this.maxSpent = maxSpent;
	}

	public int getSpent(int position) {
		return spent[position];
	}

	public void setSpent(int[] spent) {
		this.spent = spent;
	}

	public void setPlayerFolded(int position) {
		playerFolded[position] = false;
	}

	public void setStateFinished() {
		finished = false;

	}

	public int currentPlayer() {
		int curPlayer = 0;
		/*
		 * if Action has already been made, compute next player from last player
		 */
		if (numActions[round] != 0) {
			//curPlayer=numActions[round]-1;
			curPlayer=actingPlayer[round][numActions[round]-1];
			do {
				curPlayer = (curPlayer + 1) % MAX_PLAYERS;
			} while (playerFolded[curPlayer] == true || spent[curPlayer] >= STACK_SIZE);
		} else {
			if (round == 0)
				return 1;
			else
				return 0;
		}
		return curPlayer;
	}

	public int numRaises() {
		int ret = 0;
		for (int i = 0;i < numActions[getRound()]; ++i) {
			if (action[getRound()][i].getType() == 'r') {
				++ ret;
			}
		}
		return ret;
	}
	
	public int numFolded() {
		int ret = 0;
		for (int p = 0; p < MAX_PLAYERS; p++) {
			if (playerFolded[p])
				++ret;
		}
		return ret;
	}

	public int numCalled() {
		int p, i, ret = 0;
		for (i = numActions[round]; i > 0; --i) {
			p = actingPlayer[round][i - 1];
			if (action[round][i - 1].getType() == 'r') {
				/* player initiated the bet, so they've called it */
				if (spent[p] < STACK_SIZE) {
					/* player is not all-in, so they're still acting */
					++ret;
				}
				/* this is the start of the current bet, so we're finished */
				return ret;
			} else if (action[round][i - 1].getType() == 'c') {
				if (spent[p] < STACK_SIZE) {
					/* player is not all-in, so they're still acting */
					++ret;
				}

			}
		}
		return ret;
	}

	public int numActingPlayers() {
		int p, ret = 0;
		for (p = 0; p < MAX_PLAYERS; ++p) {
			if (playerFolded[p] == false && spent[p] < STACK_SIZE) {
				++ret;
			}
		}
		return ret;
	}

	public void doAction(Game game, ActionType act) {
		int p = currentPlayer();
		assert(act != null);
		action[round][numActions[round]] = (ActionType) act.clone();
		actingPlayer[round][numActions[round]] = p;
		++numActions[round];
		switch (act.getType()) {
		case 'f':
			this.playerFolded[p] = true;
			// finished=true;
			break;
		case 'c':
			if (this.maxSpent > STACK_SIZE) {
				/* calling puts player all-in */
				spent[p] = STACK_SIZE;
			} else {
				/* player matches the bet by spending same amount of money */
				this.spent[p] = this.maxSpent;
			}
			break;
		case 'r': //TODO  Limit Game
			// no limit betting
//			if (act.getSize() + act.getSize() - maxSpent > minNoLimitRaiseTo) {
//				minNoLimitRaiseTo = act.getSize() + act.getSize() - maxSpent;
//			}
//			maxSpent = act.getSize();
//			spent[p] = maxSpent;
//			break;
			
			// limit betting game
//			System.out.print("now round:" + getRound());
//			assert(getRound() <= MAX_ROUNDS);
			if (getMaxSpent() + game.raiseSize[getRound()] > game.stack[p]) {
				setMaxSpent(game.stack[p]);
			}
			spent[p] = getMaxSpent();
			break;
		}
		/* see if the round or game has ended */
		if (numFolded() + 1 >= MAX_PLAYERS) {
			/* only one player left - game is immediately over, no showdown */
			finished = true;
		} else if (numCalled() >= numActingPlayers()) {
			/* >= 2 non-folded players, all acting players have called */
			if (numActingPlayers() > 1) {
				/* there are at least 2 acting players */
				if (round + 1 < MAX_ROUNDS) {
					/* active players move onto next round */
//					switch (round) {
//					case 0:
//						roundIsOver[0] = true;
//						break;
//					case 1:
//						roundIsOver[1] = true;
//						break;
//					case 2:
//						roundIsOver[2] = true;
//						break;
//					case 3:
//						roundIsOver[3] = true;
//						break;
//					default:
//						break;
//					}
//					++round;
					++ round;
					
					
					minNoLimitRaiseTo = 1;
					for (p = 0; p < MAX_PLAYERS; ++p) {
						if (blind[p] > minNoLimitRaiseTo) {
							minNoLimitRaiseTo = blind[p];
						}
					}
					/*
					 * we finished at least one round, so raise-to = raise-by +
					 * maxSpent
					 */
					minNoLimitRaiseTo += maxSpent;
				} else {
					/*
					 * we finished at least one round, so raise-to = raise-by +
					 * maxSpent
					 */
					finished = true;
				}
			} else {
				/*
				 * not enough players for more betting, but still need a
				 * showdown
				 */
				finished = true;
				round = MAX_ROUNDS - 1;
			}
		} else if (numRaises() >= MAX_REISE_NUM[getRound()]){
			//num of raise reach the limitation
			if (round + 1 < MAX_ROUNDS) {
				++ round;
				}
			else {
				finished = true;
			}
		}
	}

	public boolean raiseIsValid(int[] raiseSize) {
		if (numActingPlayers() <= 1) {
			/*
			 * last remaining player can't bet if there's no one left to call
			 * (this check is needed if the 2nd last player goes all in, and the
			 * last player has enough stack left to bet)
			 */
			return false;
		}
		int p = currentPlayer();
		raiseSize[0] = minNoLimitRaiseTo;
		raiseSize[1] = STACK_SIZE;

		/* handle case where remaining player stack is too small */
		if (raiseSize[0] > STACK_SIZE) {
			/* can't handle the minimum bet size - can we bet at all? */
			if (maxSpent >= STACK_SIZE) {
				/* not enough money to increase current bet */
				return false;
			} else {
				/* can raise by going all-in */
				raiseSize[0] = raiseSize[1];
				return true;
			}
		}
		return true;
	}

	public int getMaxSpent() {
		// TODO Auto-generated method stub
		return maxSpent;
	}

	public ActionType getAction(int r,int p ){
		return action[r][p];
	}
	
	public boolean isValidAction(Game game, ActionType action) {
		int p = 0;
		int[] raiseSize = { 0, 0 }; // minRaiseSize, maxRaiseSize;
		if (finished) {
			return false;
		}
		p = currentPlayer();
		if (action.getType() == 'r') {
			if (!raiseIsValid(raiseSize)) {
				return false;
			}
			if (game.bettingType) {
				if (action.getSize() < raiseSize[0]) {
					return false;
				} else if (action.getSize() > raiseSize[1]) {
					return false;
				}
			}
		} else if (action.getType() == 'f') {
			if (spent[p] == maxSpent || spent[p] == STACK_SIZE) {
//				if (spent[p] == BIG_BLIND) {
//					return true;
//				} else {
//					return false;
//				}
				// mjb modified
				return false;
				
			}
			if (action.getSize() != 0) {
				action.setSize(0);
			}
		} else {
			if (action.getSize() != 0) {
				action.setSize(0);
			}
		}
		return true;
	}

//	public Object flopClone(){
//		GameState object = null; // = new GameState();
//		try {
//			object = (GameState) super.clone();
//			object.spent = new int[MAX_PLAYERS];
//			object.roundIsOver = new boolean[4];
//			for(int i= 0;i< 4;i++)
//				object.roundIsOver[i] = false;
//			for(int i= 0;i< FLOP;i++)
//				object.roundIsOver[i] = this.roundIsOver[i];
//			
//			object.playerFolded = new boolean[MAX_PLAYERS];
//			object.Action = new actionType[MAX_ROUNDS][MAX_NUM_ACTIONS];
//			object.actingPlayer = new int[MAX_ROUNDS][MAX_NUM_ACTIONS];
//			object.numActions = new int[MAX_ROUNDS];
//			object.boardCards = new int[MAX_BOARD_CARDS];
//			object.holeCards = new int[MAX_PLAYERS][MAX_HOLE_CARDS];
//			for (int i = 0; i < FLOP; i++) {
//				for (int j = 0; j < MAX_NUM_ACTIONS; j++) {
//					if (this.Action[i][j] != null) {
//						object.Action[i][j] = (actionType) this.action[i][j].clone();
//					}
//				}
//			}
//			// spent = new int[MAX_PLAYERS];
//			for (int i = 0; i < MAX_PLAYERS; i++) {
//				object.spent[i] = this.spent[i];
//			}
//			// playerFolded = new boolean[MAX_PLAYERS];
//			for (int i = 0; i < MAX_PLAYERS; i++) {
//				object.playerFolded[i] = this.playerFolded[i];
//			}
//			// actingPlayer = new int[MAX_ROUNDS][MAX_NUM_ACTIONS];
//			for (int i = 0; i < MAX_ROUNDS; i++) {
//				for (int j = 0; j < MAX_NUM_ACTIONS; j++) {
//					object.actingPlayer[i][j] = this.actingPlayer[i][j];
//				}
//			}
//			// numActions = new int[MAX_ROUNDS];
//			for (int i = 0; i < MAX_ROUNDS; i++) {
//				object.numActions[i] = this.numActions[i];
//			}
//			for (int i = 0; i < MAX_BOARD_CARDS; i++) {
//				object.boardCards[i] = this.boardCards[i];
//			}
//			for (int i = 0; i < MAX_PLAYERS; i++) {
//				for (int j = 0; j < MAX_HOLE_CARDS; j++) {
//					object.holeCards[i][j] = this.holeCards[i][j];
//				}
//			}
//		} catch (CloneNotSupportedException e) {
//			// TODO: handle exception
//		}
//		return object;
//	}
//	
	
	public Object clone() {

		GameState object = null; // = new GameState();
		try {
			object = (GameState) super.clone();
			object.spent = new int[MAX_PLAYERS];
			object.roundIsOver = new boolean[4];
			for(int i= 0;i< 4;i++)
				object.roundIsOver[i] = this.roundIsOver[i];
			object.playerFolded = new boolean[MAX_PLAYERS];
			object.action = new ActionType[MAX_ROUNDS][MAX_NUM_ACTIONS];
			object.actingPlayer = new int[MAX_ROUNDS][MAX_NUM_ACTIONS];
			object.numActions = new int[MAX_ROUNDS];
			object.boardCards = new int[MAX_BOARD_CARDS];
			object.holeCards = new int[MAX_PLAYERS][MAX_HOLE_CARDS];
			for (int i = 0; i < MAX_ROUNDS; i++) {
				for (int j = 0; j < MAX_NUM_ACTIONS; j++) {
					if (this.action[i][j] != null) {
						object.action[i][j] = (ActionType) this.action[i][j].clone();
					}
				}
			}
			// spent = new int[MAX_PLAYERS];
			for (int i = 0; i < MAX_PLAYERS; i++) {
				object.spent[i] = this.spent[i];
			}
			// playerFolded = new boolean[MAX_PLAYERS];
			for (int i = 0; i < MAX_PLAYERS; i++) {
				object.playerFolded[i] = this.playerFolded[i];
			}
			// actingPlayer = new int[MAX_ROUNDS][MAX_NUM_ACTIONS];
			for (int i = 0; i < MAX_ROUNDS; i++) {
				for (int j = 0; j < MAX_NUM_ACTIONS; j++) {
					object.actingPlayer[i][j] = this.actingPlayer[i][j];
				}
			}
			// numActions = new int[MAX_ROUNDS];
			for (int i = 0; i < MAX_ROUNDS; i++) {
				object.numActions[i] = this.numActions[i];
			}
			for (int i = 0; i < MAX_BOARD_CARDS; i++) {
				object.boardCards[i] = this.boardCards[i];
			}
			for (int i = 0; i < MAX_PLAYERS; i++) {
				for (int j = 0; j < MAX_HOLE_CARDS; j++) {
					object.holeCards[i][j] = this.holeCards[i][j];
				}
			}
		} catch (CloneNotSupportedException e) {
			// TODO: handle exception
		}
		return object;
	}
}
