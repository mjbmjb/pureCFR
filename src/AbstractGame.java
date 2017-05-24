
public class AbstractGame implements IGame{
	public BettingNode bettingTreeRoot;
	public ActionAbstraction actionAbs;
	public CardAbstarction cardAbs;
	
	public AbstractGame() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 递归的更新entry的数目，被用于countEntries
	 * @param node : 当前的node
	 * @param numEntriesPerBucket
	 * @param totalNumEntries
	 */
	protected void countEntriesR(BettingNode node, int[] numEntriesPerBucket, int[] totalNumEntries) {
		BettingNode child = node.getChild();
		
		if (child == null) {
			//Terminal Node
			return ;
		}
		
		int round = node.getRound();
		int numChoices = node.getNumChoices();
		
		// Update entries counts
		numEntriesPerBucket[round] += numChoices;
		int buckets = cardAbs.numBucket(node);
		totalNumEntries[round] += buckets * numChoices;
		
		// Recurse 递归的找child，更新totalNumEntries中记录的entry 的数目
		for (int c = 0; c < numChoices; ++c) {
			countEntriesR(child, numEntriesPerBucket, totalNumEntries);
			child = child.getChild();
		}
	}
	
	public void countEntries(int[] numEntriesPerBucket, int[] totalNumEntries) {
		countEntriesR(bettingTreeRoot, numEntriesPerBucket, totalNumEntries);
	}

}
