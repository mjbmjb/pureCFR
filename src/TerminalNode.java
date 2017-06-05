
public class TerminalNode extends BettingNode {
	static int soln_idx;
	static int num_choices;
    static int player;
    static int round;
    BettingNode child;
    
    boolean showdown; /* 0 = end by folding, 1 = end in showdown */
    int[] fold_value = new int[ 2 ]; /* (-1,1) if end by folding and (lose,win), 0 for showdown */
    int money; /* amount of money changing hands at leaf */
    
	public TerminalNode(boolean showdown, int[] fold_value, int money) {
		this.showdown = showdown;
		this.money = money;
		this.fold_value[0] = fold_value[0];
		this.fold_value[1] = fold_value[1];
	}
	/*
	 * 计算utility
	 */
	@Override
	public int evaluate(Hand hand, int position) {
		return (showdown ? hand.showdownValue[position] : fold_value[position]) * money;
	}

}
