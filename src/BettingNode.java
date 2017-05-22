import java.lang.Thread.State;


public class BettingNode {
	protected int showdown;
	protected int[] fold_value = new int[2];
	protected int money;
	
	public BettingNode() {
		// TODO Auto-generated constructor stub
	}
	
	public BettingNode init_betting_tree_r(GameState state, Game game, int[] num_entries_per_bucket) {
		BettingNode node;
		if (state.finished) {
		//	Terminal Node
			boolean showdown = (state.playerFolded[0] || state.playerFolded[1] ? false : true);
			int[] fold_value = new int[2];
			int money = -1;
			// 自己fold时，money为对方的spent
			for (int p = 0; p < 2; ++p) {
		        if (state.playerFolded[p])
		        {
		          fold_value[p] = -1;
		          money = state.spent[p];
		        }
		        else if (state.playerFolded[1-p])
		        {
		          fold_value[p] = 1;
		          money = state.spent[1-p];
		        }
		        else
		        {
		          fold_value[p] = 0;
		          money = state.spent[p];
		        }
			}
			node = new TerminalNode(showdown, fold_value, money);
		}//Terminal Node ifend
		// Choice node
		else {
			// TODO 获取合法action数目
			/* Choice node.  First, compute number of different allowable actions */
			int num_choices = 3;//FIXME 暂时制订了action的数目
			
			
		}
	}
	
}
