
public class PureCfrMachine implements Game{
	protected AbstractGame ag;
	protected Entries[] regrets = new Entries[MAX_ROUNDS];
	protected Entries[] avgStrategy = new Entries[MAX_ROUNDS];
	
	public PureCfrMachine() {
		/* count up the number of entries required per round to store regret,
		 * avg_strategy
		 */
		int[] numEntriesPerBucket = new int[MAX_ROUNDS];
		int[] totalNumEntries = new int[MAX_ROUNDS];
		ag.countEntries(numEntriesPerBucket, totalNumEntries);
		
		// ��ʼ�� regret �� avg strategy
		for (int r = 0; r < MAX_ROUNDS; ++r) {
			if (r < MAX_ROUNDS) {// TODO �˴�ӦΪʵ����Ϸ��round��
				// regret
				regrets[r] = new Entries(numEntriesPerBucket[r], totalNumEntries[r]);
				
				// average strategy
				avgStrategy[r] = new Entries(numEntriesPerBucket[r], totalNumEntries[r]);
			}
			else {
				//  round out of range (never hit)
				regrets[r] = null;
				avgStrategy[r] = null; 
			}
		}
		
	}

	/**
	 * ��ʼ��������regret TODO ���� rng_state_t
	 */
	public void doIteration() {
		
	}
	
	public int writeDump(String dumpPrefix) {
		return 0;
		//TODO writeDump
	}
	
	public int loadDump(String dumpPrefix) {
		return 0;
		//TODO loaddump
	}
	
	protected int generateHand(Hand hand) {
		GameState state = new GameState();
		// dealcard TODO ���ƣ��ŵ�GameState����ȥ
		
		// TODO bucket the hand for each player
		
		// Rank the hands
		int[] ranks = new int[MAX_PLAYERS];
		int topRank = -1;
		int numTies = 1;
		
		 /* State must be in the final round for rankHand to work properly */
		state.setRound(MAX_ROUNDS - 1);
		for (int p = 0; p < MAX_PLAYERS; ++p) {
			ranks[p] = rankHand(state);
			if (ranks[p] > topRank) {
				topRank = ranks[p];
				numTies = 1;
			}
			else if (ranks[p] == topRank) {
				++ numTies;
			}
		}
		
		// Set evaluation values
		if (ranks[0] > ranks[1]) {
			/* Player 0 wins in showdown */
		      hand.showdownValue[0] = 1;
		      hand.showdownValue[1] = -1;
	    }
		else if (ranks[0] < ranks[1]) {
		      /* Player 1 wins in showdown */
			  hand.showdownValue[0] = -1;
			  hand.showdownValue[1] = 1;
	    }
	    else {
		      /* Players tie in showdown */
			  hand.showdownValue[0] = 0;
			  hand.showdownValue[1] = 0;
		}
		return 0;
	}
	
	protected double walkPureCfr(int position, BettingNode curNode, Hand hand) {
		double retval = 0.0;
		// ��ΪTerminal node ���� ��� fold ��Ϸ����������utility
		if (curNode.getChild() == null || curNode.didPlayerFold(position)) {
			retval = curNode.evaluate(hand, position);
			return retval;
		}//line 5
		
		/* Grab some values that will be used often */
		int numChoices = curNode.getNumChoices();
		int player = curNode.getPlayer();
		int round = curNode.getRound();
		int solnIdx = curNode.getSolnIdx();
		// TODO precomputer bucket
		
		int bucket;
		bucket = ag.cardAbs.getBucket(curNode, hand.board_cards,hand.hole_cards);
		
		/* Get the positive regrets at this information set */
		double[] posRegrets = new double[numChoices];
		double sumPosRegret = regrets[round].getPosValue(bucket, solnIdx, numChoices, posRegrets);
		
		if (sumPosRegret == 0.0) {
			/* No positive regret, so assume a default uniform random current strategy */
			sumPosRegret = numChoices;
			for (int c = 0;c < numChoices; ++c) {
				posRegrets[c] = 1.0;
			}
		}
		
		// ���ѡȡaction
		java.util.Random r = new java.util.Random();
		double dart = r.nextDouble() * sumPosRegret;
		int choice;
		for (choice = 0; choice < numChoices; ++choice) {
			if (dart < posRegrets[choice]) {
				break;
			}
			dart -= posRegrets[choice];
		}
		
		assert(choice < numChoices);
		assert(posRegrets[choice] > 0);
		
		BettingNode child = curNode.getChild();
		
		if (player != position) {
			// Opponment's node Recurse down the single choice
			// line 9
			for (int c = 0;c < choice; ++c) {
				// ѡ���Ǹ�action��Ӧ�� node
				child = child.getSibling();
			}
			retval = walkPureCfr(position, child, hand);
			
			/* Update the average strategy if we are keeping track of one */
			if (avgStrategy[round].incrementEntry(bucket, solnIdx, choice)) {
		        System.out.print("The average strategy has overflown :(\n");
		        System.exit(0);
		    }
		}
		else {
			/* Current player's node. Recurse down all choices to get the value of each */
			double[] values = new double[numChoices];
			for (int c = 0;c < numChoices; ++c) {
				values[c] = walkPureCfr(position, child, hand);
				child = child.getSibling();
			}
			
		    /* We return the value that the sampled pure strategy attains */
		    retval = values[choice];//10

		    /* Update the regrets at the current node */
		    regrets[round].updateRegret(bucket, solnIdx, numChoices,
		                                  values, retval);//11
		}
		return retval;
	}
	


}
