
public class AbstractGame implements IGame{
	public BettingNode bettingTreeRoot;
	public ActionAbstraction actionAbs;
	public CardAbstarction cardAbs;
	public Game game;
	
	public AbstractGame() {
		// Create Game 
		game = new Game();
		// Create Game end
		
		//Creat action abstraction
		actionAbs = new NullActionAbstraction();
		
		// init numEntriesPerBUcket to zero
		int[] numEntriesPerBucket = new int[MAX_ROUNDS];
		
		//process betting tree
		GameState state = new GameState();
		bettingTreeRoot = new BettingNode().initBettingTree(state, game, actionAbs, numEntriesPerBucket);
		
		// Create card abstraction
		cardAbs = new NullCardAbstraction(game);
		
		
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

		int buckets = cardAbs.numBucket(game, node);
		totalNumEntries[round] += buckets * numChoices;
		
		// Recurse �ݹ����sibling������totalNumEntries�м�¼��entry ����Ŀ
		for (int c = 0; c < numChoices; ++c) {
			countEntriesR(child, numEntriesPerBucket, totalNumEntries);
			child = child.getSibling();
		}
	}
	
	public void countEntries(int[] numEntriesPerBucket, int[] totalNumEntries) {
		countEntriesR(bettingTreeRoot, numEntriesPerBucket, totalNumEntries);
	}

}
