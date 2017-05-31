
public class NullCardAbstraction extends CardAbstarction implements IGame{
	protected int deckSize;
	protected int[] mNumBuckets = new int[MAX_ROUNDS];
	
	public NullCardAbstraction(Game game) {
		deckSize = game.numSuits * game.numRanks;
		
		/* Precompute number of buckets per round */
		mNumBuckets[0] = 1;
		for (int i = 0;i < game.numHoleCards; ++i) {
			mNumBuckets[0] *= deckSize;
		}
		for (int r = 0; r < MAX_ROUNDS; ++r) {
			if (r < game.numRounds) {
				//后一轮都是   前一轮的  ^ 公共牌数量
				if (r > 0) {
					mNumBuckets[r] = mNumBuckets[r - 1];
				}
				for (int i = 0;i < game.numBoardCards[r]; ++i) {
					mNumBuckets[r] *= deckSize;
				}
			}
			else {
				mNumBuckets[r] = 0;
			}
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public int numBucket(Game game, BettingNode node) {
		return mNumBuckets[node.getRound()];
	}
	
	public int numBucket(Game game,GameState state) {
		return mNumBuckets[state.getRound()];
	}
	
//	public int getBucketInternal(Game game, 
//								 int boardCard[], 
//								 int holeCard[], 
//								 int player, 
//								 int round) {
//		/* Calculate the unique bucket number for this hand */
//		int bucket = 0;
//		for (int i = 0;i < game.numHoleCards; ++i) {
//			if (i > 0) {
//				bucket *= deckSize;
//			}
//		}
//	}
//	
	
	

}
