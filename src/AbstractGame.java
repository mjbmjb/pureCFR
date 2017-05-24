
public class AbstractGame implements IGame{
	public BettingNode bettingTreeRoot;
	public ActionAbstraction actionAbs;
	public CardAbstarction cardAbs;
	
	public AbstractGame() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * �ݹ�ĸ���entry����Ŀ��������countEntries
	 * @param node : ��ǰ��node
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
		
		// Recurse �ݹ����child������totalNumEntries�м�¼��entry ����Ŀ
		for (int c = 0; c < numChoices; ++c) {
			countEntriesR(child, numEntriesPerBucket, totalNumEntries);
			child = child.getChild();
		}
	}
	
	public void countEntries(int[] numEntriesPerBucket, int[] totalNumEntries) {
		countEntriesR(bettingTreeRoot, numEntriesPerBucket, totalNumEntries);
	}

}
