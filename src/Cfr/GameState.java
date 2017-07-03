package Cfr;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import MyUtil.MyUtil;
import Para.Game;
import Para.IGame;

//import javax.print.attribute.standard.RequestingUserName;
//import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;
//
//import com.google.common.base.CaseFormat;
//import com.google.common.base.FinalizablePhantomReference;
//import com.google.common.base.Service.State;


public class GameState implements IGame, Cloneable {

	/* hand id*/
	public int handID = 0;
	
	private final static int[] blind = { 100, 50 };

	/* current round : preflop = 0; flop = 1; turn = 2; river = 3 */
	public int round;

	/* Action[ r ][ i ] gives the i'th action in round r */
	private ActionType[][] action;

	/*
	 * actingPlayer[ r ][ i ] gives the player who made Action i in round r we
	 * can always figure this out from the Actions taken, but it's easier to
	 * just remember this in multiplayer (because of folds)
	 */
	private int[][] actingPlayer;



	/* numActions[ r ] gives the number of Actions made in round r */
	public int[] numActions;

	/* public cards (including cards which may not yet be visible to players) */
	public int[] boardCards;

	/* private cards */
	public int[][] holeCards;

	/* finished is true if and only if game is over */
	public boolean finished;

	/* playerFolded[ p ] is true if and only if player p has folded. */
	private boolean[] playerFolded;

	/* spent[ p ] gives the total amount put into the pot by player p */
	private int[] spent;

	/* minimum number of chips a player must have spend in total to raise */
	private int minNoLimitRaiseTo;
	/* largest bet so far, including all previous rounds */
	private int maxSpent;
	


	private boolean [] roundIsOver;
	
	public int viewingPlayer;

	
	
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
	
	public void initState(Game game) {
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
		maxSpent = 0;
		for (int p = 0; p < MAX_PLAYERS; p++) {
			spent[0] = BIG_BLIND;
			spent[1] = SMALL_BLIND;
		}
		maxSpent = (BIG_BLIND > SMALL_BLIND) ? BIG_BLIND : SMALL_BLIND;
		
		if (game.bettingType) {
			/* no-limit games need to keep track of the minimum bet */
			if(maxSpent > 0) {
				minNoLimitRaiseTo = maxSpent * 2;
			}
			else {
				/* need to bet at least one chip, and there are no blinds/ante */
				minNoLimitRaiseTo = 1;
			}
		}//no limit if end
		else {
			/* no need to worry about minimum raises outside of no-limit games */
			 minNoLimitRaiseTo = 0;
		}
		
		for (int p = 0; p <game.numPlayers; ++p) {
			playerFolded[p] = false;
		}		
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

	public int getActingPlayer(int round, int action) {
		return actingPlayer[round][action];
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
		int p = currentPlayer(game);
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
			else {
				/* player raises by the normal limit size */
				setMaxSpent(getMaxSpent() + game.raiseSize[round]);
			}
			spent[p] = getMaxSpent();
			break;
		default:
			// not the case above
			MyUtil.prl("not a vaild action)");
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
				if (round + 1 < game.numRounds) {
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
				round = game.numRounds - 1;
			}
		}
//		else if (numRaises() >= MAX_REISE_NUM[getRound()]){
//			//num of raise reach the limitation
//			if (round + 1 < MAX_ROUNDS) {
//				++ round;
//				}
//			else {
//				finished = true;
//			}
//		}
	}

	public boolean raiseIsValid(Game game, int[] raiseSize) {
		
		if (numRaises() >= game.maxRaises[round]) {
			return false;
		}
		
		if (numActions[round] + game.numPlayers > MAX_NUM_ACTIONS) {
			/* 1 raise + NUM PLAYERS-1 calls is too many actions */
			MyUtil.prl("WARNING: #actions in round is too close to MAX_NUM_ACTIONS, forcing call/fold");
			return false;
		}
		
		if (numActingPlayers() <= 1) {
			/*
			 * last remaining player can't bet if there's no one left to call
			 * (this check is needed if the 2nd last player goes all in, and the
			 * last player has enough stack left to bet)
			 */
			return false;
		}
		int p = currentPlayer(game);
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
		p = currentPlayer(game);
		if (action.getType() == 'r') {
			if (!raiseIsValid(game, raiseSize)) {
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
			if (spent[p] == maxSpent || spent[p] == game.stack[p]) {
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
    public int dealCard(int[] deck, int numCards, Random rd) {
    	int i, ret;
    	
    	i = rd.nextInt(numCards);
    	ret = deck[i];
    	deck[i] = deck[numCards - 1];
    	
    	return ret;

    }
    
    /**
     * shuffle a deck of cards and deal them out, writing the results to state
     * @param game
     * @param state
     */
    public void dealCards(Game game, GameState state) {
    	int r, s, numCards, i, p;
    	int[] deck = new int[MAX_RANKS * MAX_SUITS];
    	
    	numCards = 0;
    	Random rd = new Random();
    	for (s = 0;s < game.numSuits; ++ s) {
    		for (r = 0; r < game.numRanks; ++ r) {
    			deck[numCards] = r * game.numSuits + s ;
    			++ numCards;
    		}
    	}
    	
    	for (p = 0; p < game.numPlayers; ++p) {
    		for (i = 0; i < game.numHoleCards; ++i) {
    			state.holeCards[p][i] = dealCard(deck, numCards, rd);
    			--numCards;
    	    }
    	}
    	return;
    }
    
    public int sumBoardCards(Game game, int round) {
    	int r, total = 0;
    	for (r = 0; r <= round; ++ r) {
    		total += game.numBoardCards[r];
    	}
    	return total;
    }
    
    public int suitOfCard(Game game, int card) {
    	return card % game.numSuits;
    }
    
    public int rankOfCard(Game game, int card) {
    	return card / game.numSuits;
    }//TODO 放到Game 里
    
	public int rankHand(Game game, int player) {
		int i;
		
		if ((game.numHoleCards == 1) && (sumBoardCards(game, round) == 0)) {
			return rankOfCard(game, holeCards[player][0]);
		}
		
		CardSet c = new CardSet();
		
		for (i = 0; i < game.numHoleCards; ++ i) {
			c.addCard(suitOfCard(game, holeCards[player][i]),
					  rankOfCard(game, holeCards[player][i]));
		}
		
		for (i = 0; i < sumBoardCards(game, round); ++ i) {
			c.addCard(suitOfCard(game, boardCards[i]),
					  rankOfCard(game, boardCards[i]));
		}
		
		return c.rankCardSet();
	}
	
	public int nextPlayer(Game game, int curPlayer) {
		int n = curPlayer;
		
		do {
			n = (n + 1) % game.numPlayers;
		}while (playerFolded[n] || spent[n] >= game.stack[n]);
		//当fold或者筹码用完时跳过该玩家
		
		return n;
	}
	
	public int currentPlayer(Game game) {
		/* if action has already been made, compute next player from last player */
		if (numActions[round] != 0) {
			assert(false);
			return nextPlayer(game, actingPlayer[round][numActions[round]]);
		}
		
		/* first player in a round is determined by the game and round
	     use nextPlayer() because firstPlayer[round] might be unable to act
	     So start from the last player, use nextPlayer() to determine whether the first player can act  */
		return nextPlayer(game, game.firstPlayer[round] + game.numPlayers - 1);
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
	public boolean readAction(Game game, String str, ActionType action) {
		action.setType(str.charAt(0));
		
		if (action.getType() == 'r' && game.bettingType) {
			//TODO no limit bet/raise need to read a size
			
			
		}
		else {
			action.setSize(0);
		}
		return true;
	}
	
	
	public boolean readBetting(Game game, String str) {
		ActionType action = new ActionType('n', 0);
		
		// set action
		// TODO no limit need a size
		for (int c = 0; c < str.length(); ++ c) {
			char actionChar = str.charAt(c);
			if (actionChar == '/') {
				continue;
			}
			action.setType(actionChar);
			action.setSize(0);
			if(!isValidAction(game, action))
				return false;
			doAction(game, action);
		}
		return true;
	}

	public int readCard(Game game, String cardStr) {
		return game.rankType.indexOf(cardStr.substring(0, 1)) +
			   game.suitType.indexOf(cardStr.substring(1)) * game.numRanks;
	}
	
//	public boolean readCards(String str, int maxCards, int[] cards, )
//	
//	
	public void readHoleCards(Game game, String str) {
		int p = str.charAt(0) == '|' ? 1 : 0;
		
		String[] strArr = str.split("\\|");
		

		for (int c = 0; c < game.numHoleCards; ++ c) {
			holeCards[p][c] = readCard(game, strArr[p]);
		}
	}
	
	
	public int readBoardCards(Game game, String[] cardStrArr) {
		int round;
		for (round = 0; round < cardStrArr.length; ++ round) {
			for (int n = 0; n < game.numBoardCards[round]; ++ n) {
				holeCards[round][n] = readCard(game, cardStrArr[round + 1]);//cause the first item are hole cards
			}
		}
		return round;
	}
	
	
	public boolean readState(String serverStr, Game game) {
		// MATCHSTATE(0):position(1):handID(2):action(3):holeCards|/broadCards
		String[] strArr = serverStr.split(":");
		assert(strArr.length == 5);
		
		// read player position
		viewingPlayer = Integer.parseInt(strArr[1]);
		
		// read handID
		handID = Integer.parseInt(strArr[2]);
		
		// read betting
		if (!readBetting(game, strArr[3])) {
			return false;
		}	
		
		// read cards
		String[] cardStrArr = strArr[4].split("/");
		// read hole cards
		readHoleCards(game, cardStrArr[0]);
		// read board cards
		if (cardStrArr.length >= 2) {
			readBoardCards(game, cardStrArr);
		}
		return true;
	}
	
	
	/**
	 * Assume the input string is 
	 * @param str
	 * @param game
	 * @return
	 */
	public boolean readStateCommon(String str, Game game) {
		int handId;
		int c, r;
		
		// HEADER
		c = 0;
		
		/* HEADER:handId: */
		String[] strList = str.split(":");
		MyUtil.prl("(readStateCommon)The split str is:");
		for (String string : strList) {
			MyUtil.pr(string + " ");
		}
		
		handId = Integer.parseInt(strList[1]);
		
		/* HEADER:handId:betting: */
		if(!readBetting(game, strList[2]))
			return false;
		
		/* HEADER:handId:betting:holeCards */
//		if(!readHoleCards(strList[3]))
//			return false;
//		
//		if(!readBoardCards(strList[4])) 
//			return false;
		
		return true;
	}
	
	
//	public int printStateCommon(Game game, int maxLen, StringBuilder strb) {
//		int c, r;
//		// Header
//		c = 0;
//		
//		// Header:handId:
//		r = strb.append(":").length();
//		if (r < 0) {
//			return -1;
//		}
//		c += r;
//		
//		/* HEADER:handId:betting */
//		r = printBetting(game,maxLen - c, strb);
//		if (r < 0)  {
//			return -1;
//		}
//		c += r;
//
//		/* HEADER:handId:betting: */
//		if (c >= maxLen) {
//			return -1;
//		}
//		strb.append(':');
//		++c;
//		return c;
//	}
	
	
	
	
	public int printBetting(Game game, StringBuilder strb) {
		int i, a, c, r = -1;
		// c is the len of strb
		c = 0;
		for (i = 0; i <= round; ++ i) {
			if (i != 0) {
				strb.append('/');
				c ++;
			}
			
			// print betting for round
			for (a = 0; a < numActions[i]; ++ a) {
				r = printAction(game, action[i][a], strb);
				
				if (r < 0) {
					return -1;
				}
				c += r;
			}
			
			if (r < 0) {
				return -1;
			}
			c += r;
		}
		
		return c;
	}
	
	
	public int printAction(Game game, ActionType action,StringBuilder strb) {
		int c, r;
		
//		if (maxLen == 0) {
//			return -1;
//		}
//		
		c = 0;

		strb.append(action.getType());
		++c;
		
		if (game.bettingType && action.getType() == 'r') {
			r = strb.append(action.getSize()).length();
			if (r < 0) {
				return -1;
			}
			c += r;
		}
		
//		if (c >= maxLen) {
//			return -1;
//		}
		
		return c;
	}
	
	public void printCard(Game game, int card, StringBuilder sb) {
		int suit = card / game.numRanks;
		int rank = card % game.numRanks;
		sb.append(game.rankType.charAt(rank) + game.suitType.charAt(suit));
	}
	
	public void printHoleCards(Game game, StringBuilder sb) {
		for(int p = 0;p < game.numPlayers; ++ p) {
			if (p != 0) {
				sb.append("|");
			}
			for (int c = 0; c < game.numHoleCards; ++ c) {
				printCard(game, holeCards[p][c], sb);
			}
		}
	}
	
	public void printBoardCards(Game game, StringBuilder sb) {
		int cardBaseIndex = 0;
		for (int r = 0; r < round; ++ r) {
			// print round separator
			if (r != 0) {
				sb.append("/");
			}
			
			for(int n = 0; n < game.numBoardCards[r]; ++ n) {
				printCard(game, boardCards[cardBaseIndex + n], sb);
			}
			cardBaseIndex += game.numBoardCards[r];
		}
	}
	
	public void printState(Game game, GameState state) {
		// MATCHSTATE:
		StringBuilder sb = new StringBuilder("MATCHSTATE:");
		
		// MATCHSTATE:player
		sb.append(":" + viewingPlayer);
		
		// MATCHSTATE:player:handId:betting
		sb.append(":" + handID);
		printBetting(game, sb);
		
		// MATCHSTATE:player:handId:betting:holecards
		printHoleCards(game, sb);
		
		// MATCHSTATE:player:handId:betting:holecards:boardcards
		printBoardCards(game, sb);
	}
	
	
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
