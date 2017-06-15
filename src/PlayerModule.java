import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;


public class PlayerModule implements IGame{
	protected AbstractGame ag;
	protected boolean verbose;
	protected Entries[] entries;
	public PlayerModule(File file) {
		this.verbose = true;
		
		/* Open up the player file for reading */
		
		
		/* Initialize abstract game*/
		this.ag = new AbstractGame();
		
		/* Next, count the number of entries required per round to store the entries */
		int[] numEntriesPerBucket = new int[MAX_ROUNDS];
		int[] totalNumEntries = new int[MAX_ROUNDS];
		ag.countEntries(numEntriesPerBucket, totalNumEntries);
		
		/* Finally, build the entries from the dump */
		for (int r = 0; r < MAX_ROUNDS; ++r) {
			if (r < ag.game.numRounds) {
				/* Establish entries for this round and move dump pointer to next set of
			       * entries.
			       */
				Entries entri = new Entries(numEntriesPerBucket[r], totalNumEntries[r]);
				try {
					entri.load(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				entries[r] = entri;
				
				if (entries[r] == null) {
				MyUtil.prl( "Could not load entries for round " + r);
				System.exit(1);
				}
			}
			else {
				entries[r] = null;
			}
		}
	
	}
	
	protected void getDefaultActionProbs(GameState state, double[] actionProbs) {
		/* Default will be always call */
		Arrays.fill(actionProbs, 0.0);
		
		/* Get the abstract actions */
		ActionType[] actions = new ActionType[MAX_ABSTRACT_ACTIONS];
		int numChoices = ag.actionAbs.getActions(ag.game, state, actions);
		
		/* Find the call action */
		for( int a = 0; a < numChoices; ++a ) {
		    if( actions[ a ].getType() == 'c' ) {
		      actionProbs[ a ] = 1.0;
		      return;
		    }
		}

		/* Still haven't returned?  This means we couldn't find a call action,
		   * so we must be dealing with a very weird action abstraction.
		   * Let's just always play the first action then by default.
		   */
		actionProbs[0] = 1.0;
	}
	
	public void getActionProbs(GameState state, double[] actionProbs, int bucket) {
		/* Initialize action probs to the default in case we must abort early
		   * for one of several reasons
		   */  
		getDefaultActionProbs(state, actionProbs);
		
		/* Find the current node from the sequence of actions in state */
		BettingNode node = ag.bettingTreeRoot;
		GameState oldState = new GameState();
		
		if (verbose) {
			MyUtil.prl("Tranlated abstract state:");
			
		}
		
		for (int r = 0; r <= state.getRound(); ++r) {
			for (int a = 0; a <= state.numActions[r]; ++ a) {
				ActionType realAction = state.getAction(r, a);
				ActionType[] abstractAction = new ActionType[MAX_ABSTRACT_ACTIONS];
				int numAction = ag.actionAbs.getActions(ag.game, oldState, abstractAction);
				
				if (numAction != node.getNumChoices()) {
					if (verbose) {
						MyUtil.prl("Number of actions does not match number of choices");
					}
					return;
				}
				int choice;
				if ((ag.game.bettingType == true) && (realAction.getType() == 'r')) {
					// noLimit
					/* First, find the smallest abstract raise greater than or equal to the
					 * real raise size (upper), and the largest abstract raise less than or
					 * equal to the real raise size (lower).
					 */
					
				}//nolimit ifend
				else {
					/* Limit game or non-raise action. Just match the real action. */
					for (choice = 0; choice < numAction; ++ choice) {
						if (abstractAction[choice].getType()== realAction.getType()) {
							break;
						}
					}
					
					if (choice <= numAction) {
						if (verbose) {
							MyUtil.prl("Unable to translate action at round " + 
										state.getRound() + 
										" turn " +
										state.getActingPlayer(r, a));
						}
						return;
					}
					
					/* Move the current node and old_state along */
					node = node.getChild();
					for (int i = 0; i < choice; ++ i) {
						node = node.getSibling();
						if (node == null) {
							if (verbose) {
								MyUtil.prl("Ran out of sibings for choice: " + choice);
							}
							return;
						}
					}
					
					if (node.getChild() == null) {
						if (verbose) {
							MyUtil.prl("Abstract game over");
						}
						return;
					}
					oldState.doAction(ag.game, abstractAction[choice]);
					
					
				}//limit elseend
			}
		}
		
		/* Bucket the cards */
		  if (bucket == -1) {
		    bucket = ag.cardAbs.getBucket(ag.game, node, state.boardCards, state.holeCards);
		  }
		  if (verbose)	{
			  MyUtil.prl(" Bucket=" + bucket);
		  }

		  /* Check for problems */
//		  if (state.getActingPlayer(round, action) != node->get_player()) {
//		    if (verbose)
//		    {
//		      fprintf(stderr, "Abstract player does not match current player\n");
//		    }
//		    return;
//		  }
		  if (state.getRound() != node.getRound()) {
		    if (verbose) {
		      MyUtil.prl("Abstract round does not match current round");
		    }
		    return;
		  }
		  
		  /* Get the positive entries at this information set */
		  int numChoices = node.getNumChoices();
		  int solnIdx = node.getSolnIdx();
		  int round = node.getRound();
		  double[] posEntries = new double[numChoices];
		  double sumPosEntries = this.entries[round].getPosValue(bucket, solnIdx, numChoices, posEntries);
		  
		  /* Get the abstract game action probabilities */
		  if (sumPosEntries == 0.0) {
			  if (verbose) {
				  MyUtil.prl("All positive entries are zero!");
			  }
			  return;
		  }
		  actionProbs = new double[MAX_ABSTRACT_ACTIONS];
		  for (int c = 0; c < numChoices; ++ c) {
			  actionProbs[c] = posEntries[c] / sumPosEntries;
		  }
	}
	
	public ActionType getAction(GameState state) {
		/* Get the abstract game action probabilities */
		double[] actionProbs = new double[MAX_ABSTRACT_ACTIONS];
		getActionProbs(state, actionProbs, -1);
		
		/* Get the corresponding actions */
		ActionType[] actions = new ActionType[MAX_ABSTRACT_ACTIONS];
		int numChoices = ag.actionAbs.getActions(ag.game, state, actions);
		if (verbose) {
			MyUtil.pr("probs;");
			for (int a = 0; a < numChoices; ++a) {
				if ((numChoices < 5) || (actionProbs[a] > 0.001)) {
					//TODO printAction
				}
			}
		}
		
		/* Choose an action */
		Random rd = new Random();
		double dart = rd.nextDouble();
		int a;
		for (a = 0; a < numChoices - 1; ++ a) {
			if (dart < actionProbs[a]) {
				break;
			}
			dart -= actionProbs[a];
		}
		if (verbose) {
			//TODO print action
		}
		
		/* Make sure action is legal */
		if (!state.isValidAction(ag.game, actions[a])) {
			if (verbose) {
				MyUtil.prl("Action chosen is not legal");
			}
		}
		
		return actions[a];
	}
	
}
